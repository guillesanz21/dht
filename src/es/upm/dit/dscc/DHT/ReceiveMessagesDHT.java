package es.upm.dit.dscc.DHT;

import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

/**
 * @author aalonso
 * This class handles the messages received. 
 */
public class ReceiveMessagesDHT {

	private java.util.logging.Logger LOGGER = DHTMain.LOGGER;

	private SendMessages      sendMessages;
	private DHTUserInterface  dht;
	private OperationBlocking mutex;
	private ViewManager       viewManager;
	//private TableManager      tableManager;

	/**
	 * Constructor 
	 * @param sendMessages The object por connecting with servers 
	 * @param dht A table
	 * @param mutex Used for synchronization. 
	 * @param viewManager The manager of the views and some central operations
	 */
	public ReceiveMessagesDHT (SendMessages sendMessages,
			DHTUserInterface dht,
			OperationBlocking mutex,
			ViewManager viewManager) {

		this.sendMessages = sendMessages;
		this.dht          = dht;
		this.mutex        = mutex;
		this.viewManager  = viewManager;
//		this.tableManager = tableManager;
	}

	// Receive messages

	/**
	 * It handles the messages received. 
	 * @param address The origin server
	 * @param operation The operation that should be processed
	 */
	public void handleReceiverMsg(Address address, OperationsDHT operation) {
		LOGGER.fine("Operation in a message: " + operation.getOperation());
		Integer value;
		boolean status;
		switch (operation.getOperation()) {
		case PUT_MAP:
			LOGGER.fine(operation.getOperation() + " Key: " + operation.getMap().getKey());
			value = dht.putMsg(operation.getMap());
			if (!operation.isReplica()) {
				sendMessages.returnValue(address, value);
			}
			//LOGGER.fine(operation.getOperation() + "Mesage sent");
			break;
		case GET_MAP:
			value = dht.get(operation.getKey());
			LOGGER.fine(operation.getOperation() + " Value: " + value);
			if (!operation.isReplica()) {
				sendMessages.returnValue(address, value);
			}
			break;
		case REMOVE_MAP:
			value = dht.removeMsg(operation.getKey());
			LOGGER.fine(operation.getOperation() + " Value: " + value);
			if (!operation.isReplica()) {
				// Due to previous comment CHECK IT
				sendMessages.returnValue(address, value);
			}
			break;
		case CONTAINS_KEY_MAP:
			status = dht.containsKey(operation.getKey());
			if (!operation.isReplica()) {
				sendMessages.returnStatus(address, status);
			}
			break;
		case KEY_SET_HM:
			//System.out.println(operation.getClientDB().toString());
			//System.out.println("------------------------------");
			//clientDB.createBank(operation.getClientDB());
			break;
//		case VALUES_HM :
//			break;
		case RETURN_VALUE :
			mutex.receiveOperation(operation);
			break;

		case RETURN_STATUS :
			mutex.receiveOperation(operation);
			break;

		case INIT:
			LOGGER.fine("Received INIT:" + operation.getPosReplica());
			//viewManager.putDHTServers(operation.getDHTServers());
			viewManager.transferData(address);
			break;
			
		case DHT_REPLICA :
			LOGGER.fine("Received servers (DHTServers):" + operation.getPosReplica());
			viewManager.putDHTServers(operation.getDHTServers());
			break;
			
//		case DATA_REPLICA : //TODO: no se está usando
//			LOGGER.fine("Received data replica:" + operation.getPosReplica());
//			viewManager.putReplica(operation.getPosReplica(), operation.getDHT());
//			break;
		
		default:
			break;
		}
	}

}
