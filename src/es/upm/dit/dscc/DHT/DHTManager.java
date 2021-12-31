package es.upm.dit.dscc.DHT;

import java.util.ArrayList;
import java.util.Set;

import org.jgroups.Address;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.JChannel;
import org.jgroups.Message;

/**
 * @author aalonso
 * This is the manager of the DHT. It shows the API for clients, 
 * in order to interact with the table.
 */
public class DHTManager extends ReceiverAdapter  implements DHTUserInterface {

	private java.util.logging.Logger LOGGER = DHTMain.LOGGER;

	private int               nServersMax  = 3;
	private int               nReplica     = 2;
	private JChannel          channel;
	private SendMessagesDHT   sendMessages;
	private OperationBlocking mutex;
	private Address           localAddress;
	private TableManager      tableManager;
	private ReceiveMessagesDHT   receiveMessages;
	private boolean           endConfigure = false;
	private DHTUserInterface  dht;
	private ViewManager       viewManager;

	public DHTManager (String cluster) {

		LOGGER.warning("Start of configuration " + cluster);

		try {
			channel = new JChannel().setReceiver(this);
			localAddress = channel.address();
			channel.connect(cluster);
			localAddress = channel.address();
		} catch (Exception e) {
			LOGGER.severe("Error to create the JGroups channel");
		}

		if (!endConfigure) {
			configure();
		}

		LOGGER.finest("End of configuration");
	}

	/**
	 * Configure the main objects in the DHT,
	 */
	private void configure() {
		this.sendMessages    = new SendMessagesDHT(channel);
		this.mutex           = new OperationBlocking();
		if (localAddress == null) {
			channel.address();
		}
		if (localAddress == null) {
			LOGGER.severe("localAddress is null!!!!");
		}
		this.tableManager    = new TableManager(localAddress, nServersMax, nReplica);
		this.dht             = new DHTJGroups(sendMessages, mutex, tableManager);
		this.viewManager     = new ViewManager(localAddress, nServersMax, nReplica,
									sendMessages, tableManager);
		this.receiveMessages = new ReceiveMessagesDHT(sendMessages, dht, mutex, viewManager);
		this.endConfigure    = true;
	}

	
	/**
	 * Receives the views in the processes group. 
	 */
	@Override
	public void viewAccepted(View newView) { 
		
		if (localAddress == null) {
			localAddress = newView.getMembers().get(newView.size() - 1);
		}

		if (!endConfigure) {
			configure();
		}

		try {

			LOGGER.warning("** view: " + newView);

			if (tableManager == null || newView == null) {
				LOGGER.severe("table or view is null");
			} else {
				viewManager.manageView(newView);					
			}

		} catch (Exception e) {
			LOGGER.severe("Unexpected exception in viewAccepted");
			System.err.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * Receives the messages sent for the processes in the group
	 */
	@Override
	public void receive(Message msg) {
		java.lang.Object dhtbdObj = null;

		try {
			dhtbdObj = (java.lang.Object) org.jgroups.util.Util.objectFromByteBuffer(msg.getBuffer());			
			LOGGER.finest("A message received");
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			System.out.println(e.toString());
			System.out.println("Exception objectFromByteBuffer");
		}
		OperationsDHT operation = (OperationsDHT) dhtbdObj;
		
		receiveMessages.handleReceiverMsg(msg.src(), operation);
	}
	
	
	/**
	 * Check whether there is a quorum: There are servers/processes
	 * correct forming the DHT
	 * @return True if there is a quorum
	 */
	public boolean isQuorum() {
		return viewManager.isQuorun();
	}
	
	/**
	 * Adds a map in the DHT 
	 */
	public Integer put(DHT_Map map) {
		return dht.put(map);
	}
	
	public Integer putMsg(DHT_Map map) {
		return null;
	}
	
	/**
	 * Returns the value associated to the provided key
	 */
	public Integer get(String key) {
		return dht.get(key);
	}

	/**
	 * Removes the map associated to the key in the DHT
	 */
	public Integer remove(String key) {
		return dht.remove(key);
	}
	
	public Integer removeMsg(String key) {
		return null;
	}
	
	
	/**
	 * Checks whether the provided key is in the DHT
 	 */
	public boolean containsKey(String key) {
		return dht.containsKey(key);
	}

	
	/**
	 * Returns a Set view of the keys contained in this DHT.
	 */
	public Set<String> keySet() {
		return dht.keySet();
	}

	/**
	 * Returns a List of the values contained in this DHT.
	 */
	public ArrayList<Integer> values() {
		return dht.values();
	}
	
	public String getServers() {
		return tableManager.printDHTServers();
	}
	
	@Override
	public String toString() {
		return dht.toString();
	}



}




