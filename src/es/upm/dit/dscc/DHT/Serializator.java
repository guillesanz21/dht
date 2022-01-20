package es.upm.dit.dscc.DHT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Serializator {

	private static java.util.logging.Logger LOGGER = DHTMain.LOGGER;

	public Serializator() {
		super();
	}
	public static byte[] serialize(Data data) {
		byte[] serializedData = null;
		
		LOGGER.fine("Serializing data: " + data.toString());
		ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
		
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayStream);
			outputStream.writeObject(data);
			outputStream.flush();
			serializedData = byteArrayStream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serializedData;

		
	}
	
	public static Data deserialize(byte[] data) {
		LOGGER.fine("Deserializing data: " + data.toString());
		Data operation = null;
		// Deserialize: Convert an array of Bytes in an operation.
		if (data != null) {
			try {
				ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(data);
				ObjectInputStream     inputStream = new ObjectInputStream(byteArrayStream);
				operation  = (Data) inputStream.readObject();
			}
			catch (Exception e) {
				System.out.println("Error while deserializing object");
			}

		}
		return operation;

	}

	
	
}

