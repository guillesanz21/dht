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
		//OperationsDHT operation = auxOpeMap(OperationEnum.PUT_MAP, map);
		OperationsDHT operation = prepareOperation(OperationEnum.PUT_MAP, null, map);
		LOGGER.finest("Returned value in put: " + operation.getValue());
		System.out.println("El resultado es : " + operation.getValue());
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
		LOGGER.finest("Returned value in get: " + operation.getValue());
		System.out.println("El resultado es : " + operation.getValue());
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
		//OperationsDHT operation = prepareOperation(OperationEnum.REMOVE_MAP, key, null); 	
		LOGGER.finest("Returned value in remove: " + operation.getValue());
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
	 * En este método decidimos como se van a pasar las operaciones entre los servidores. El orden de los datos es: 
	 * 	1) El primer dato sera Nreplica, el array de replicas
	 * 	2) Los nodos a los que afecta la operacion
	 * 	3) la operacion en si
	 * Serializamos los datos y se los pasamos a zkOp que crea los nodos para las operaciones. 
	 * Actualizamos el OprationpBlocking a través del mutex de tal manera de que no se realicen dos operaciones a la vez y haya exclusión mutua.
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
