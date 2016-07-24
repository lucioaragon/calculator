/**
 * DataStoreServiceImpl.jar
 * 
 * The server-side implementation of the RPC service.
 */

package com.lucioaragon.calculator.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import com.lucioaragon.calculator.client.DataStoreService;
import com.lucioaragon.calculator.shared.BinaryNumb;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DataStoreServiceImpl extends RemoteServiceServlet implements DataStoreService {
	
	PersistenceManager pm = PMF.get().getPersistenceManager();

	/**
	 * Converts the input argument to binary, and also stores it in server
	 */
	public String convertToBinary(String input) throws IllegalArgumentException {
		float number = Float.parseFloat(input);
		
		// Verify that the input is valid. 
		if (Float.isNaN(number)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException("You must send a number");
		}

		Date actualDate = new Date();
		String binaryOutput = new String("");
	    int bits = Float.floatToIntBits(number);
	    // Extract each bit from 'bits' and compare it by '0'
	    for (int i=31; i>=0; --i) {
	    	binaryOutput += (bits & (1 << i)) == 0 ? "0" : "1";
	    }
        BinaryNumb newNumber = new BinaryNumb(binaryOutput, input, actualDate.toString());
	    
	    try {
	    	pm.makePersistent(newNumber);
	    } finally {
	        //pm.close();
	    }
	    
		return binaryOutput;
	}
	
	/**
	 * Returns all currently stored numbers
	 */
	public ArrayList<BinaryNumb> getCurrentNumbers(){
		Extent<BinaryNumb> numbers = null;
		ArrayList<BinaryNumb> numbersList = new ArrayList<BinaryNumb>();
		
	    //Query for all stored values
	    numbers = pm.getExtent(BinaryNumb.class);
	    Iterator<BinaryNumb> iter = numbers.iterator();
	    while(iter.hasNext()) {
	    	BinaryNumb item = iter.next();
	    	numbersList.add(item);
	    }
	    
		return numbersList;
	}
}
