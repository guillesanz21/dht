package es.upm.dit.dscc.DHT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jgroups.Address;
import org.jgroups.View;

public class ViewManager {

	private java.util.logging.Logger LOGGER = DHTMain.LOGGER;
	
	private int       nReplica;
	private int       nServersMax;
	// Number of the servers that have integrated in the DHT
	private int       nServers;
	private boolean   isQuorum       = false;
	private boolean   firstQuorum    = false;
	private boolean   pendingReplica = false;
	private View      previousView   = null;
	private SendMessagesDHT   sendMessages;   
	private Address   failedServerTODO;
	private Address   localAddress;
	private TableManager tableManager;


	public ViewManager(
			Address localAddress,
			int     nServersMax, 
			int     nReplica,
			SendMessagesDHT sendMessages,
			TableManager tableManager) {
		this.nServers     = 0;
		this.localAddress = localAddress;
		this.nServersMax  = nServersMax;
		this.nReplica     = nReplica;
		this.sendMessages = sendMessages;
		this.tableManager = tableManager;
	}
	
	/**
	 * Add a new server in the DHT
	 * @param address The new server
	 * @return The DHT table with servers addresses
	 */
	public HashMap<Integer, Address>  addServer(Address address) {

		HashMap<Integer, Address> DHTServers = tableManager.getDHTServers();
		HashMap<Integer, DHTUserInterface> DHTTables = tableManager.getDHTTables();

		// The quorum of servers is sufficient
		if (nServers >= nServersMax) {
			LOGGER.fine("The quorum is already created. This server is not required" + nServers);
			return null;
		} else {
			// Find a hole. Selects the first free.
			// Integrate it in DHT
			for (int i = 0; i < nServersMax; i ++) {
				if (DHTServers.get(i) == null) {
					DHTServers.put(i, address);
					if (DHTTables.get(i) == null) {
						DHTTables.put(i, new DHTHashMap());
					}	
					nServers++;
					//sendMessages.sendServers(address, DHTServers);
					LOGGER.finest("Added a server. NServers: " + nServers);
					return DHTServers;
				}
			}
		}
		LOGGER.warning("Error: This sentence shound not run");
		return null;
	}

	
	/**
	 * Remove the local information of a process/server that has failed.
	 * @param address Address of the failed process
	 * @return The identifier of the failed process
	 */
	public Integer deleteServer(Address address) {
		HashMap<Integer, Address> DHTServers = tableManager.getDHTServers();
		
		for (int i = 0; i < nServersMax; i ++) {
			if (address.equals(DHTServers.get(i))) {
				DHTServers.remove(i);
				return i;
			}
		}
		LOGGER.warning("This sentence should no be run");
		return null;
	}

	/**
	 * Manages a new view. It should assign a table to the new server, 
	 * if there is no quorum currently
	 * @param newView the new accepted view
	 * @return true if the server has been integrated
	 */
	public boolean manageView(View newView) {

		Address address = null;

		// There are enough servers: nServers = nServersMax
		if (newView.size() > nServersMax) {
			if (newView.getMembers().get(newView.size() - 1) == localAddress) {
				LOGGER.warning("The server is exited. The quorum is already created!!!!!");
				System.exit(0);
			} else {
				return false;
			}
		}

		// TODO: Handle if on servers fails before creating the first quorum
		// TODO: Currently supports one failure. Check if there are more than 1 fail
		//       Supposed that no server fails until there are quorum

		// Check whether a server/process fails and it is not in the view
		if (previousView != null && newView.size() < previousView.size()) {
			LOGGER.warning("A server has failed. There is no quorum!!!!!");
			// A server has failed
			Address failedServer = crashedServer(previousView, newView);
			deleteServer(failedServer);
			nServers--;
			isQuorum       = false;
			pendingReplica = true;
			previousView   = newView;
			return false;
		}

		
		if (newView.size() > nServers) {

			// Initial quorum. Add new servers.
			if (nServers == 0 && newView.size()>0) {
				for (Iterator<Address> iterator = newView.iterator(); iterator.hasNext();) {

					Address itAddress = (Address) iterator.next();
					addServer(itAddress);
					LOGGER.fine("Added a server. NServers: " + nServers +  
							"Server: " + itAddress + ".");
					if (!itAddress.equals(localAddress)) {
						sendMessages.sendInit(itAddress);
					}
					if (nServers == nServersMax) {
						isQuorum    = true;
						firstQuorum = true;
					}
				}

			} else {
				if (newView.size() > nServers) {
					HashMap<Integer, Address> DHTServers;
					address = newView.getMembers().get(newView.size() - 1);
					addServer(address);
					LOGGER.fine("Added a server. NServers: " + nServers 
							+ ". Server: " + address);
					if (nServers == nServersMax) {
						isQuorum    = true;
						// A server crashed and is a new one
						if (firstQuorum) {
							// A previous quorum existed. Then tolerate the fail
							// Add the new one in the DHTServer							
							Address failedServer = newServer(newView);
							failedServerTODO     = failedServer;
							// Add the server in DHTServer
							DHTServers = addServer(failedServer);
							if (DHTServers == null) {
								LOGGER.warning("DHTServers is null!!");
							} else {
								sendMessages.sendServers(failedServer, DHTServers);
							}
							// Send the Replicas 
							//transferData(failedServer);
							pendingReplica = true;
						} else {
							firstQuorum = true;
						}
					}
				}
			}
		}
		LOGGER.fine(tableManager.printDHTServers());
		previousView = newView;
		return true;
	}

	
	/** 
	 * Get the address of the server/process that has failed
	 * @param previousView the previous view
	 * @param newView the current view
	 * @return the addres of the failed process
	 */
	public Address crashedServer(View previousView, View newView) {

		for (int k = 0; k < newView.size(); k++) {
			if (previousView.getMembers().get(k).
					equals(newView.getMembers().get(k))) {
			} else {
				return previousView.getMembers().get(k);
			}

		}

		return previousView.getMembers().get(previousView.size() - 1);

	}

	/**
	 * Get the address of the new server/process
	 * @param view the view
	 * @return the address of the new server
	 */
	public Address newServer(View view) {
		return view.getMembers().get(view.getMembers().size() - 1);
	}
	
	/**
	 * Send the DHT servers table and the tables that are local to the new
	 * server/process
	 * @param address the address of the new server/process
	 */
	public void transferData(Address address) {
		HashMap<Integer, Address> DHTServers = tableManager.getDHTServers();
		HashMap<Integer, DHTUserInterface> DHTTables = tableManager.getDHTTables();

		if (pendingReplica) {
			pendingReplica = false;
		} else {
			return;
		}

		if (!address.equals(failedServerTODO)) {
			LOGGER.severe("!!!!!!! address != failedServerTODO");			
		}

		//address = failedServerTODO;
		// Send the DHT servers to the new address
		sendMessages.sendServers(address, DHTServers);

		int i   = 0;
		int posNew = 0;
		for (i = 0; i < nServersMax; i++){
			//if (address.equals(DHTServers.get(i))) {
			//			Address repAddres = DHTServers.get(i);
			if (address.equals(DHTServers.get(i)) ) {
				posNew = i;
				break;
			}
		}

		int posLocal = 0;
		for (i = 0; i < nServersMax; i++){
			if (localAddress.equals(DHTServers.get(i))) {
				posLocal = i;
				break;
			}
		}

		LOGGER.fine("Check whether sending table (-1) from " + posLocal + " to " + posNew);

		int posNext = (posNew + 1) % nServersMax;
		if (posLocal == posNext) {
			LOGGER.fine("pos: " + posNew + " local: " + posLocal + " address: " + address);		
			//			sendMessages.sendReplicaData(address, dhts.get(posLocal), posLocal, posNew);
			Set <String> hashMap = DHTTables.get(posNew).keySet();
			for (Iterator<String> iterator = hashMap.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				LOGGER.fine("posNew + " + posNew+ " key: " + key);
				sendMessages.sendPut(address, new DHT_Map(key, DHTTables.get(posNew).get(key)), true);				
			}

		}

		LOGGER.fine("Check whether sending table (0) from " + posLocal + " to " + posNew);
		// send the second replica of the previous
		for (int j = 1; j < nReplica; j++) {
			int posPrev = (posNew - j) % nServersMax;
			if (posPrev < 0) {posPrev = posPrev + nServersMax;}
			if (posLocal == posPrev) {
				LOGGER.fine("replica: " + posNext + " address: "  + address);
				LOGGER.fine("replica: " + posNext + " address: " + address);
				Set <String> hashMap = DHTTables.get(posLocal).keySet();
				//sendMessages.sendReplicaData(address, dhts.get(posLocal), posLocal, posNew);
				for (Iterator<String> iterator = hashMap.iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					LOGGER.fine("posLocal + " + posLocal+ " key: " + key);
					sendMessages.sendPut(address, new DHT_Map(key, DHTTables.get(posLocal).get(key)), true);	
				}

			}
		}

	}

	
	/**
	 * Add a replica in the DHT Tables in the DHT table
	 * @param posReplica the position of the replica
	 * @param dht the table to integrate
	 */
	public void putReplica (int posReplica, DHTUserInterface dht) {		
		HashMap<Integer, DHTUserInterface> DHTTables = tableManager.getDHTTables();
		DHTTables.put(posReplica, dht);
	}
	
	
	/** Copy the DHT servers in the parameter in the local DHT Servers 
	 * @param newDHTServers The DHT servers
	 */
	public void putDHTServers(HashMap <Integer, Address> newDHTServers) {
		HashMap<Integer, Address> DHTServers = tableManager.getDHTServers();
		for (int i = 0; i < nServersMax; i++) {
			DHTServers.put(i, newDHTServers.get(i));
		}

		LOGGER.fine(tableManager.printDHTServers());
	}

	/**
	 * Check whether there is quorum
	 * @return true if there is quorum
	 */
	public boolean isQuorun( ) {
		return isQuorum;
	}

	
}
