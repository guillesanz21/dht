package es.upm.dit.dscc.DHT;

import java.util.HashMap;

class Tester {
    private java.util.logging.Logger LOGGER = DHTMain.LOGGER;
	HashMap<String, Integer> tabla = new HashMap<String, Integer>();
	String[] keys = new String[]{"Angel", "Bernardo", "Carlos", "Daniel", "Eugenio", "Zamorano"};
	Integer DHTAnswer = null;
	Integer testAnswer = null;
	DHTManager dhtTest;

    public void test(DHTManager dht) {
    	dhtTest = dht;
    	
    	boolean test = testGet();
    	String output = test ? "Test de la función Get correcto" : "Test de la función Get incorrecto";
    	System.out.println(output);
    	
    	test = testPut();
    	output = test ? "Test de la función Put correcto" : "Test de la función Put incorrecto";
    	System.out.println(output);
    	
    	
    		
    }
    public boolean testGet() {
    	for (int i = 1; i>7;i++) 
			if (dhtTest.get(keys[i])!=i)
				return false;
    	return true;
    }

    public boolean testPut() {
		dhtTest.put(new DHT_Map("Javier", 8));
		if (dhtTest.get("Javier")!=8)
			return false;
    	return true;
    }
    
 }
