package bookingsGo;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

public class ViewCars
{
	// Method which outputs the result of the search, depending on the supplier, latitude, longitude, and number of passengers
	// Also takes as parameters: the scanner used to input user's choices, and the table which stores the prices for each car type by each supplier.
	// Returns the updated table, with the prices for all car types provided by supplier passed as parameter to this method.
	public static HashMap<String,HashMap<String,Integer>> supplySearch(String supplier, String latitude, String longitude, Scanner scanner, int noPassengers, HashMap<String,HashMap<String,Integer>> table) throws IOException, InterruptedException
	{
		System.out.println("SUPPLIER:" + supplier);
		String inline = "";
        Gson gson = new Gson(); 
        URL url = new URL("https://techtest.rideways.com/"+supplier+"?pickup="+latitude+","+longitude+"&dropoff="+latitude+","+longitude);
    	

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        TimeUnit.SECONDS.sleep(2);  // Giving it time to get the response
        if(responseCode!=200) // If not successful, tell the user why, and exit the system
        {
        	if(responseCode == 500)
        		System.out.println("Internal Server Error, please try again later.");
        	else System.out.println("Bad Request, please try again later.");
        	System.exit(0);
        }

        System.out.println("Response code is: " +responseCode);
        
        // Scanner functionality will read the JSON data from the stream
        Scanner sc = new Scanner(url.openStream());
        
		while(sc.hasNext()) // Put the JSon data from URL into String inline
		{
			inline+=sc.nextLine();
		}
		System.out.println("\nJSON Response in String format"); 
		System.out.println(inline);
		sc.close(); // Close the stream when reading the data has been finished
		
		// Turn the json string into an object of the format of the Response class
		Response response = gson.fromJson(inline, Response.class);
		
		// Store the options for this particular response
		ArrayList<Option> options = (ArrayList<Option>) response.options;
		Collections.sort(options, Collections.reverseOrder());   // sorts them in descending order of price
		
		// Logic added to display results depending on number of passengers
		ArrayList<String> carTypes = new ArrayList<String>();  // Stores all car types in ArrayList
		carTypes.add("STANDARD");
		carTypes.add("EXECUTIVE");
		carTypes.add("LUXURY");
		carTypes.add("PEOPLE_CARRIER");
		carTypes.add("LUXURY_PEOPLE_CARRIER");
		carTypes.add("MINIBUS");
		
		// Removing car types from ArrayList which do not fit the number of passengers
		if(noPassengers>4)
		{
			carTypes.remove("STANDARD");
			carTypes.remove("EXECUTIVE");
			carTypes.remove("LUXURY");
			if(noPassengers>6)
			{
				carTypes.remove("PEOPLE_CARRIER");
				carTypes.remove("LUXURY_PEOPLE_CARRIER");
				if(noPassengers>16)
				{
					System.out.println("Maximum number of passengers is 16");
	            	System.exit(0);
				} // if
			} // if
		} // if
		
		System.out.println("SEARCH RESULTS IN DESCENDING PRICE ORDER:");
		// Normal output
		for(Option o: options)
		{
			// update the table to contain the price for this supplier for this car type
			HashMap<String,Integer> priceForSupplier = new HashMap<String,Integer>(); 
			priceForSupplier.put(supplier, o.price); 
			table.put(o.carType, priceForSupplier);
			
			if(carTypes.contains(o.carType)) // printing only the prices of the car types which are suitable for number of passengers
			{	
				System.out.println("{" + o.carType + "} - {" + o.price + "}");
			}
		}
		System.out.println("----RESTFUL OUTPUT-----");
		// Restful output
		for(Option o: options)
		{
			if(carTypes.contains(o.carType)) // printing only the prices of the car types which are suitable for number of passengers
			{	
				String jsonString = "{\"car_type\":\"" + o.carType + "\",\"price\":\"" + o.price +"\"}";
				searchResult current = gson.fromJson(jsonString, searchResult.class);
				System.out.println(gson.toJson(current));
			}	
		}
		System.out.println("------------");
		return table;
	}
    public static void main(String[] args)
    {
        try 
        {
        	System.out.println("Enter the latitude");
        	Scanner scanner = new Scanner(System.in);
        	String latitude = scanner.nextLine();
        	
        	System.out.println("Enter the longitude");
        	String longitude = scanner.nextLine();
        	
        	System.out.println("Enter the number of passengers");
        	int noPassengers = scanner.nextInt();
        	
        	ArrayList<String> suppliers = new ArrayList<String>();
        	suppliers.add("dave");
        	suppliers.add("eric");
        	suppliers.add("jeff");
        	
        	HashMap<String,HashMap<String,Integer>> minPerSupplier = new HashMap<String,HashMap<String,Integer>>(); // table to store prices for each model by each supplier
        	HashMap<String,Integer> innerMap = new HashMap<String,Integer>();
        	
        	// initially populating all prices for all models and suppliers to be a very large value
        	for(String supplier : suppliers)
        		innerMap.put(supplier, Integer.MAX_VALUE);
        	
        	minPerSupplier.put("STANDARD", innerMap);
        	minPerSupplier.put("EXECUTIVE", innerMap);
        	minPerSupplier.put("LUXURY", innerMap);
        	minPerSupplier.put("PEOPLE_CARRIER", innerMap);
        	minPerSupplier.put("LUXURY_PEOPLE_CARRIER", innerMap);
        	minPerSupplier.put("MINIBUS", innerMap);
        	
        	try
        	{
        		// call search for all suppliers, updating the table of prices per model per supplier as well.
            	minPerSupplier = supplySearch("dave", latitude, longitude, scanner, noPassengers, minPerSupplier);
            	minPerSupplier = supplySearch("eric", latitude, longitude, scanner, noPassengers, minPerSupplier);
            	minPerSupplier = supplySearch("jeff", latitude, longitude, scanner, noPassengers, minPerSupplier);
        	}
        	catch(IOException ex)
        	{
        		 System.out.println (ex.toString());
        	     System.out.println("Response code was 500; Internal Server Error (Supplier ignored for this search.)");
        	}
        	
        	System.out.println("FOR EACH CAR TYPE, THE CHEAPEST SUPPLIER, AND PRICE");
        	// looping through the entrie in table which stores prices per model per supplier
        	// outer loop equivalent to (for carType)
        	// inner loop equivalent to (for supplier)
        	for(HashMap.Entry<String,HashMap<String,Integer>> entry : minPerSupplier.entrySet())
        	{
        		String typeOfCar = entry.getKey();
        		int currentMin = Integer.MAX_VALUE;
        		String supplierForMin = "";
        		HashMap<String,Integer> pricePerSupplier = entry.getValue();
        		for(HashMap.Entry<String,Integer> entryInner : pricePerSupplier.entrySet())
        		{
        			String supplier = entryInner.getKey();
        			int priceForSupplier = entryInner.getValue();
        			if(priceForSupplier < currentMin)
        			{
        				currentMin = priceForSupplier;
        				supplierForMin = supplier;
        			}
    			if(currentMin != Integer.MAX_VALUE)
    			{
    				System.out.println("{" + typeOfCar + "} - {" + supplierForMin + "} - {" + currentMin + "}"); // Normal output
    				System.out.println("Restful version: ");
    		        Gson gson = new Gson(); 
    				String jsonVersion = "{\"car_type\":\"" + typeOfCar + "\",\"supplier\":\"" + supplierForMin  + "\",\"price\":" + currentMin + "}" ;
//    				System.out.println(jsonVersion);
    				MinimumPerType current = gson.fromJson(jsonVersion, MinimumPerType.class);
    				System.out.println(gson.toJson(current) + "\n");
    			}
    				
        		}
        		
        	}

    		scanner.close();
//        
        } 
        catch (Exception e) 
        {
            e.printStackTrace();

        }
    }// main


}