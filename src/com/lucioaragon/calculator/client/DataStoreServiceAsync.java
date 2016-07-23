/**
 * DataStoreService.jar
 * 
 * The async counterpart of <code>DataStoreService</code>.
 */
package com.lucioaragon.calculator.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lucioaragon.calculator.shared.BinaryNumb;

public interface DataStoreServiceAsync {
	void convertToBinary(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
	void getCurrentNumbers(AsyncCallback<ArrayList<BinaryNumb>> asyncCallback);
}
