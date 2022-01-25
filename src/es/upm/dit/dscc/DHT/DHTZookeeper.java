package es.upm.dit.dscc.DHT;


import java.util.ArrayList;

//import java.util.Collection;
//import java.util.Iterator;
import java.util.Set;



public class DHTZookeeper implements DHTUserInterface {

	private java.util.logging.Logger LOGGER = DHTMain.LOGGER;
   
	private OperationBlocking mutex;
	private TableManager      tableManager;
	private int nReplica;

	public DHTZookeeper (OperationBlocking mutex, TableManager tableManager, int nReplica) {
		this.mutex        = mutex;
		this.tableManager = tableManager;
		this.nReplica = nReplica;
		
	}
	
	@Override
	public Integer putMsg(DHT_Map map) {		
		return putLocal(map);
	}	
	
	@Override
	public Integer put(DHT_Map map) {
		LOGGER.finest("PUT: Is invoked");
		OperationsDHT operation = prepareOperation(OperationEnum.PUT_MAP, null, map);
		LOGGER.info("Returned value in put: " + operation.getValue());
		return operation.getValue();			
	}
	
	private Integer putLocal(DHT_Map map) {
		DHTUserInterface  hashMap;
		hashMap = tableManager.getDHT(map.getKey());
		
		if (hashMap == null) {
			LOGGER.warning("Error: this sentence should not get here");
		}		
		return hashMap.put(map);
	}

	@Override
	public Integer get(String key) {

		LOGGER.finest("GET: Is invoked");
		OperationsDHT operation = prepareOperation(OperationEnum.GET_MAP, key, null); 
		LOGGER.info("Returned value in get: " + operation.getValue());
		return operation.getValue();
	}
	@Override
	public Integer getMsg(String key) {
		
		return getLocal(key);
	}
	private Integer getLocal(String key) {
		DHTUserInterface  hashMap;
		hashMap = tableManager.getDHT(key);
		
		if (hashMap == null) {
			LOGGER.warning("Error: this sentence should not get here");
		}
		return hashMap.get(key);		
	}
	
	public Integer removeMsg(String key) {
		return removeLocal(key);
	}
	
	@Override
	public Integer remove(String key) {

		LOGGER.finest("REMOVE: Is invoked");
		OperationsDHT operation = prepareOperation(OperationEnum.REMOVE_MAP, key, null);
		LOGGER.info("Returned value in remove: " + operation.getValue());
		return operation.getValue();


	}

	private Integer removeLocal(String key) {
		DHTUserInterface  hashMap;
		hashMap = tableManager.getDHT(key);
		
		if (hashMap != null) {
			return hashMap.remove(key);	
		}
		LOGGER.warning("No se puede realizar");
		return hashMap.remove(key);		
	}
	
	@Override
	public boolean containsKey(String key) {
		Integer isContained = get(key);
		boolean contained; 
		
		if (isContained != null) {
			contained = true;
			return contained;
		} else {
			contained = false; 
			return contained;
		}
	}
	
	@Override
	public Set<String> keySet() {
		return null; 
	}

	@Override
	public ArrayList<Integer> values() {
		return null;

	}
	@Override
	public String toString() {	
		return tableManager.toString();

	}


	/*
	 * Este método se encarga de serializar los datos (número de réplica, servidores implicados y operación) y pasársela a zkOperations para 
	 * que este se encargue de crear los Znodos correspondientes de Zookeeper.
	 */
	public OperationsDHT prepareOperation(OperationEnum Op, String key, DHT_Map map) {
		OperationsDHT operation;
		int nodes[];
		if (key == null) {
			operation = new OperationsDHT(Op, map); 	
			nodes = tableManager.getNodes(map.getKey());		
		} else {
			operation = new OperationsDHT(Op, key);
			nodes = tableManager.getNodes(key);
		}
		
		Data operationData = new Data(nReplica, nodes, operation);
		byte[] dataSerialized = Serializator.serialize(operationData);
		ZkOperation operationZk = new ZkOperation(dataSerialized, mutex);
		LOGGER.finest("Entremos en mutex.sendOperation()");
		operation = mutex.sendOperation();
		return operation;

	}

}
