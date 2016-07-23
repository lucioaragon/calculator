/**
 * BinaryNumberProperties.jar
 * 
 * Provides properties to show objects of type BinaryNumb in a grid
 */

package com.lucioaragon.calculator.client;

import com.google.gwt.editor.client.Editor.Path;
import com.lucioaragon.calculator.shared.BinaryNumb;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface BinaryNumberProperties extends PropertyAccess<BinaryNumb> {
	
	@Path("convertedNumber")
	ModelKeyProvider<BinaryNumb> key();
	
	ValueProvider<BinaryNumb, String> date();
	ValueProvider<BinaryNumb, String> originalNumber();
	ValueProvider<BinaryNumb, String> convertedNumber();
}
