package com.lucioaragon.calculator.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lucioaragon.calculator.shared.BinaryNumb;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DataStoreServiceAsync {
	void convertToBinary(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
	void getCurrentNumbers(AsyncCallback<ArrayList<BinaryNumb>> asyncCallback);
}
