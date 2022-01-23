
package es.upm.dit.dscc.DHT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import java.util.logging.ConsoleHandler;
//import java.util.logging.Filter;
//import java.util.logging.Handler;
import java.util.logging.Level;
//import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class DHTMain {

	static {
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"[%1$tF %1$tT][%4$-7s] [%5$s] [%2$-7s] %n");

		//    		"[%1$tF %1$tT] [%2$-7s] %3$s %n");
		//           "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		//   "%4$s: %5$s [%1$tc]%n");
		//    "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%n");
	}

	static final Logger LOGGER = Logger.getLogger(DHTMain.class.getName());

	public DHTMain() {
		configureLogger();
	}

	//////////////////////////////////////////////////////////////////////////

	public void configureLogger() {
		ConsoleHandler handler;
		handler = new ConsoleHandler(); 
		handler.setLevel(Level.WARNING); 
		LOGGER.addHandler(handler); 
		LOGGER.setLevel(Level.WARNING);
	}

	//////////////////////////////////////////////////////////////////////////

	public void initMembers(DHTUserInterface dht) {

		//if (!dht.containsKey("Angel")) {
			dht.put(new DHT_Map("Angel", 1));
		//}
		//if (!dht.containsKey("Bernardo")) {
			dht.put(new DHT_Map("Bernardo", 2));
		//}
		//if (!dht.containsKey("Carlos")) {
			dht.put(new DHT_Map("Carlos", 3));
		//}
		//if (!dht.containsKey("Daniel")) {
			dht.put(new DHT_Map("Daniel", 4));
		//}
		//if (!dht.containsKey("Eugenio")) {
			dht.put(new DHT_Map("Eugenio", 5));
		//}
		//if (!dht.containsKey("Zamorano")) {
			dht.put(new DHT_Map("Zamorano", 6));
		//}
	}

	//////////////////////////////////////////////////////////////////////////

	public DHT_Map putMap(Scanner sc) {
		String  key     = null;
		Integer value   = 0;

		System. out .print(">>> Enter name (String) = ");
		key = sc.next();


		System. out .print(">>> Enter account number (int) = ");
		if (sc.hasNextInt()) {
			value = sc.nextInt();
		} else {
			System.out.println("The provised text provided is not an integer");
			sc.next();
			return null;
		}

		return new DHT_Map(key, value);
	}

	//////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {

		System.out.println("DHT working with zookeeper");



		boolean correct = false;
		int     menuKey = 0;
		boolean exit    = false;
		Scanner sc      = new Scanner(System.in);
		// String cluster  = "DHT";


		String   key    = null;
		Integer value   = 0;

		// if (args.length == 0) {
		// 	System.out.println("Incorrect arguments: dht.Main <Number_Group>");
		// 	//sc.close();
		// 	//return;
		// } else {
		// 	cluster = args[0];
		// } 

		DHTManager        dht        = new DHTManager();
		DHTMain           mainDHT    = new DHTMain();

		while (!exit) {
			try {
				correct = false;
				menuKey = 0;
				while (!correct) {
					System. out .println(">>> Enter option: 1) Put. 2) Get. 3) Remove. 4) ContainKey  5) Values 7) Init 0) Exit");				
					if (sc.hasNextInt()) {
						menuKey = sc.nextInt();
						correct = true;
					} else {
						sc.next();
						System.out.println("The provised text provided is not an integer");
					}
					
				}

				/*if (!dht.isQuorum()) {
					System.out.println("No hay quorum. No es posible ejecutar su elección");
					continue;
				}*/
				
				switch (menuKey) {
				case 1: // Put
					dht.put(mainDHT.putMap(sc));
					break;

				case 2: // Get
					System. out .print(">>> Enter key (String) = ");
					key    = sc.next();
					value  = dht.get(key);
					if (value != null) {
						System.out.println(value);							
					} else {
						System.out.println("The key: " + key + " does not exist");
					}

					break;
				case 3: // Remove
					System. out .print(">>> Enter key (String) = ");
					key    = sc.next();
					//if (dht.containsKey(key)) {
					value  = dht.remove(key);
					if (value != null) {
						System.out.println(value);							
					} else {
						System.out.println("The key: " + key + " does not exist");
					}					
					break;
				case 4: // ContainKey
					System. out .print(">>> Enter key (String) = ");
					key    = sc.next();
					if (dht.containsKey(key)) {
						System.out.println("This key is contained");						
					} else {
						System.out.println("The option is not contained");						
					}
					break;
				case 5:
					//ArrayList<Integer> list = new ArrayList<Integer>();
					System.out.println("List of values in the DHT:");
					System.out.println(dht.toString());
					break;
				case 6:
					//Set<String> set = new HashSet<String>();
					//set = dht.keySet();
					//for (Iterator iterator = set.iterator(); iterator.hasNext();) {
					//	String string = (String) iterator.next();
					//	Integer valueSet = dht.get(string);
					//	System.out.print("["+ string + ", "  + valueSet+ "] ");
					//}
					System.out.println("The option is not available");
					break;

				case 7:
					mainDHT.initMembers(dht);
					break;

				case 0:
					exit = true;	
					//dht.close();
				default:
					break;
				}
			} catch (Exception e) {
				System.out.println("Exception at Main. Error read data");
				System.err.println(e);
				e.printStackTrace();
			}

		}

		sc.close();
	}
}