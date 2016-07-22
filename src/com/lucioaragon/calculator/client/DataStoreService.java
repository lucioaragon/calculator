package com.lucioaragon.calculator.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lucioaragon.calculator.shared.BinaryNumb;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface DataStoreService extends RemoteService {
	String convertToBinary(String name) throws IllegalArgumentException;
	ArrayList<BinaryNumb> getCurrentNumbers();
}
