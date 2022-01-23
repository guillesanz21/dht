 package es.upm.dit.dscc.DHT;

import java.util.ArrayList;
import java.util.Set;


/**
 * @author aalonso
 * This is the manager of the DHT. It shows the API for clients, 
 * in order to interact with the table.
 */
public class DHTManager implements DHTUserInterface {

	private java.util.logging.Logger LOGGER = DHTMain.LOGGER;

	private int               nServersMax  = 3;
	private int               nReplica     = 2;
	
	private OperationBlocking mutex;
	
	private String           localAddress;
	
	private TableManager      tableManager;
	
	private boolean           endConfigure = false;
	private DHTUserInterface  dht;
	private ZkManager 		  zkManager;
	

	public DHTManager() {

		LOGGER.warning("Starting the server ... ");
		if (!endConfigure) {
			configure();
		}
		this.localAddress = zkManager.getLocalAddress();
		this.tableManager.setLocalAddress(this.localAddress);
		LOGGER.finest("End of configuration. ");
		LOGGER.fine("Direccion: " + this.localAddress);
	}


	/**
	 * Configure the main objects in the DHT,
	 */
	private void configure() {
		this.mutex           = new OperationBlocking();
		if (localAddress == null) {
			LOGGER.severe("localAddress is null!!!!");
		}
		this.tableManager    = new TableManager(localAddress, nServersMax, nReplica);
		this.dht             = new DHTZookeeper(mutex, tableManager, nReplica);
		this.zkManager        = new ZkManager(nServersMax, nReplica, tableManager, dht);
		this.endConfigure    = true;
	}
	
	public boolean isQuorum() {
		return zkManager.isQuorum();
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

	@Override
	public Integer getMsg(String key) {
		// TODO Auto-generated method stub
		return null;
	}
}




