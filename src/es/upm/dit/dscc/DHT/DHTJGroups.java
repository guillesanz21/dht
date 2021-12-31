package es.upm.dit.dscc.DHT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.Collection;
//import java.util.Iterator;
import java.util.Set;

import org.jgroups.JChannel;
//import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.Address;

import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class DHTJGroups implements DHTUserInterface {

	private java.util.logging.Logger LOGGER = DHTMain.LOGGER;

	private SendMessagesDHT   sendMessages;     
	private OperationBlocking mutex;
	private TableManager      tableManager;

	public DHTJGroups (
			SendMessagesDHT sendMessages, 
			OperationBlocking mutex,
			TableManager tableManager) {

		this.sendMessages = sendMessages;
		this.mutex        = mutex;
		this.tableManager = tableManager;

	}

	public Integer putMsg(DHT_Map map) {
		return putLocal(map);
	}

	@Override
	public Integer put(DHT_Map map) {
	
		OperationsDHT operation; 
		LOGGER.finest("PUT: Is invoked");
		int value;
	
	
		// Create the array of nodes where map should be stored
		int nodes[] = tableManager.getNodes(map.getKey());
		
		for (int i = 1; i < nodes.length; i++) {
			if (tableManager.isDHTLocalReplica(nodes[i], map.getKey())) {
				LOGGER.fine("PUT: Local replica");
				value = putLocal(map);
			} else {
				LOGGER.fine("PUT: Remote replica");
				sendMessages.sendPut(tableManager.DHTAddress(nodes[i]), map, true); 			
			}
		}
		
		if (tableManager.isDHTLocal(nodes[0])) {
			LOGGER.finest("PUT: The operation is local");
			value = putLocal(map);
		} else {
			sendMessages.sendPut(tableManager.DHTAddress(nodes[0]), map, false);
			operation = mutex.sendOperation();
			LOGGER.finest("Returned value in put: " + operation.getValue());
			return operation.getValue();
		}

		return 0;
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

		java.util.List<Address> DHTReplicas = new java.util.ArrayList<Address>();
		OperationsDHT operation; 

		for (Iterator<Address> iterator = DHTReplicas.iterator(); iterator.hasNext();) {
			Address address = (Address) iterator.next();
			LOGGER.finest("PUT: The operation is replicated");
			if (tableManager.isDHTLocalReplica(key, address)) {
				LOGGER.fine("PUT: Local replica");
				return getLocal(key);
			}
		}

		// Notify the operation to the cluster
		if (tableManager.isDHTLocal(key)) {
			LOGGER.finest("GET: The operation is local");
			return getLocal(key);
		} else {
			sendMessages.sendGet(tableManager.DHTAddress(key), key, false);
			operation = mutex.sendOperation();
			LOGGER.fine("Returned value in get: " + operation.getValue());
			return operation.getValue();
		}
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

		OperationsDHT operation; 
		LOGGER.finest("REMOVE: Is invoked");
		int value;
	
	
		// Create the array of nodes where map should be stored
		int nodes[] = tableManager.getNodes(key);
		
		for (int i = 1; i < nodes.length; i++) {
			if (tableManager.isDHTLocalReplica(nodes[i], key)) {
				LOGGER.fine("PUT: Local replica");
				value = removeLocal(key);
			} else {
				LOGGER.fine("REMOVE: Remote replica");
				sendMessages.sendRemove(tableManager.DHTAddress(nodes[i]), key, true); 			
			}
		}
		
		if (tableManager.isDHTLocal(nodes[0])) {
			LOGGER.finest("PUT: The operation is local");
			return removeLocal(key);
		} else {
			sendMessages.sendRemove(tableManager.DHTAddress(nodes[0]), key, false);
			operation = mutex.sendOperation();
			LOGGER.finest("Returned value in put: " + operation.getValue());
			return operation.getValue();
		}


	}

	private Integer removeLocal(String key) {
		DHTUserInterface  hashMap;
		hashMap = tableManager.getDHT(key);
		
		if (hashMap == null) {
			LOGGER.warning("Error: this sentence should not get here");
		}
		
		return hashMap.remove(key);		
	}
	
	@Override
	public boolean containsKey(String key) {
		Integer isContained = get(key);
		if (isContained == null) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public Set<String> keySet() {
		// Notify the operation to the cluster

		// Update the operation
		return null; //hashMap.keySet();
	}

	@Override
	public ArrayList<Integer> values() {
		// Notify the operation to the cluster

		// Update the operation
		return null;//hashMap.values();

	}

	@Override
	public String toString() {
		
		return tableManager.toString();

	}


}
