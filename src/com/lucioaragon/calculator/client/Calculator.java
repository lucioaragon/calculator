package com.lucioaragon.calculator.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.lucioaragon.calculator.shared.BinaryNumb;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Calculator implements EntryPoint {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final DataStoreServiceAsync dataStoreService = GWT.create(DataStoreService.class);
	public static final int BUTTON_WIDTH = 50;
	public static final String SHOW_MESSAGE = "Show stored numbers";
	public static final String HIDE_MESSAGE = "Hide stored numbers";

	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable buttonsLayout = new FlexTable();
	private ArrayList<TextButton> buttons = new ArrayList<TextButton>();
	private ArrayList<String> buttonTexts = new ArrayList<String>();
	private ArrayList<BinaryNumb> results;
	private TextField textInput = new TextField();
	private float currentValue = 0;
	private float storedValue = 0;
	private enum operationType {NONE, ADD, SUB, MUL, DIV, PER, NEG, BIN};
	private operationType operation;
	private boolean deleteInput;
	final Label errorLabel = new Label();
	private FlexTable numbersTable = new FlexTable();
	TextButton showResultsButton;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		reset();
		
		textInput.setWidth(BUTTON_WIDTH * 3);
		showResultsButton = new TextButton("Show stored numbers", onShowButtonClicked);

		//textInput.addKeyPressHandler(onTextInputChange);
		Collections.addAll(buttonTexts,                "C",  "CE",
										"7", "8", "9", "+/-", "%",
										"4", "5", "6", "+",  "-",
										"1", "2", "3", "*",  "/",
										"0", ".",      "Bin", "=");
		
		for (int i = 0; i < buttonTexts.size(); i++) {
			final TextButton newButton = new TextButton(buttonTexts.get(i), onButtonClicked);
			newButton.setWidth(BUTTON_WIDTH);
			buttons.add(newButton);
		}

		buttonsLayout.setWidget(0, 0, textInput);
		buttonsLayout.setWidget(0, 1, buttons.get(0));
		buttonsLayout.setWidget(0, 2, buttons.get(1));
		buttonsLayout.setWidget(1, 0, buttons.get(2));
		buttonsLayout.setWidget(1, 1, buttons.get(3));
		buttonsLayout.setWidget(1, 2, buttons.get(4));
		buttonsLayout.setWidget(1, 3, buttons.get(5));
		buttonsLayout.setWidget(1, 4, buttons.get(6));
		buttonsLayout.setWidget(2, 0, buttons.get(7));
		buttonsLayout.setWidget(2, 1, buttons.get(8));
		buttonsLayout.setWidget(2, 2, buttons.get(9));
		buttonsLayout.setWidget(2, 3, buttons.get(10));
		buttonsLayout.setWidget(2, 4, buttons.get(11));
		buttonsLayout.setWidget(3, 0, buttons.get(12));
		buttonsLayout.setWidget(3, 1, buttons.get(13));
		buttonsLayout.setWidget(3, 2, buttons.get(14));
		buttonsLayout.setWidget(3, 3, buttons.get(15));
		buttonsLayout.setWidget(3, 4, buttons.get(16));
		buttonsLayout.setWidget(4, 0, buttons.get(17));
		buttonsLayout.setWidget(4, 1, buttons.get(18));
		buttonsLayout.setWidget(4, 3, buttons.get(19));
		buttonsLayout.setWidget(4, 4, buttons.get(20));
		
		// text input is 3 cells wide
		FlexCellFormatter cellFormatter = buttonsLayout.getFlexCellFormatter();
		cellFormatter.setColSpan(0, 0, 3);
		
		numbersTable.setVisible(false);
		mainPanel.add(buttonsLayout);
		mainPanel.add(showResultsButton);
		mainPanel.add(numbersTable);
		RootPanel.get("calculator").add(mainPanel);
	}
	
	SelectHandler onButtonClicked = new SelectHandler() {
		@Override
		public void onSelect(SelectEvent event) {
			TextButton source = (TextButton) event.getSource();
			switch (source.getText()) {
			case "0":
			case "1":
			case "2":
			case "3":
			case "4":
			case "5":
			case "6":
			case "7":
			case "8":
			case "9":
			case ".":
				String actualValue = textInput.getValue();
				if (actualValue == "0" || deleteInput)
					actualValue = "";
				deleteInput = false;
				actualValue += source.getText();
				textInput.setValue(actualValue);
				break;
			case "C":
				reset();
				break;
			case "CE":
				currentValue = 0;
				textInput.setValue(String.valueOf(currentValue));
				break;
			case "+":
				makeOperation(operationType.ADD);
				break;
			case "-":
				makeOperation(operationType.SUB);
				break;
			case "*":
				makeOperation(operationType.MUL);
				break;
			case "/":
				makeOperation(operationType.DIV);
				break;
			case "=":
				makeOperation(operationType.NONE);
				break;
			case "%":
				makeOperation(operationType.PER);
				break;
			case "+/-":
				makeOperation(operationType.NEG);
				break;
			case "Bin":
				convertToBinary(currentValue);
				
				break;
			default:
				break;
			}
		}
	};

	SelectHandler onShowButtonClicked = new SelectHandler() {
		@Override
		public void onSelect(SelectEvent event) {
			boolean isVisible = numbersTable.isVisible();
			TextButton source = (TextButton) event.getSource();
			getNumbersFromServer();
			source.setText(isVisible ? SHOW_MESSAGE : HIDE_MESSAGE);
			numbersTable.setVisible(!isVisible);
		}
	};

/*	KeyPressHandler onTextInputChange = new KeyPressHandler() {
		@Override
		public void onKeyPress(KeyPressEvent event) {
			if ((event.getCharCode() < '1' && event.getCharCode() > '0') &&
				(event.getCharCode() != '.'))
				event.stopPropagation();
		}
	};
*/
	private void reset() {
		currentValue = storedValue = 0;
		deleteInput = false;
		operation = operationType.NONE;
		textInput.setValue(String.valueOf(currentValue));
	}

	protected void convertToBinary(float currentValue2) {
		sendNumberToServer();
/*	    String binaryOutput = new String("");
	    int bits = Float.floatToIntBits(currentValue2);
	    // Extract each bit from 'bits' and compare it by '0'
	    for (int i=31; i>=0; --i) {
	    	binaryOutput += (bits & (1 << i)) == 0 ? "0" : "1";
	    }
	    textInput.setValue(binaryOutput);
*/
	}

	protected void makeOperation(operationType op) {
		// get value from text input
		currentValue = Float.parseFloat(textInput.getText());
		
		// "+/-" is an unary operation, which alters the sign of
		// current input value
		if (op == operationType.NEG) {
			currentValue = -currentValue; 
			textInput.setValue(String.valueOf(currentValue));
		}

		// "%" is an binary operation, which sets current input value as
		// percentage of the stored value
		// Though being binary, this operation doesn't alter stored value
		else if (op == operationType.PER) {
			if (storedValue == 0)
				currentValue = 0;
			else
				currentValue = storedValue * currentValue / 100;
			textInput.setValue(String.valueOf(currentValue));
		}
		
		// "+", "-", "*" and "/" make the operation between stored value and
		// current input value.
		// This operation is executed after a second operator is added
		else if (operation == operationType.ADD) {
			storedValue += currentValue; 
		}
		else if (operation == operationType.SUB) {
			storedValue -= currentValue; 
		}
		else if (operation == operationType.MUL) {
			storedValue *= currentValue; 
		}
		else if (operation == operationType.DIV) {
			storedValue /= currentValue; 
		}

		// "%" and "+/-" operations only alter value in text input
		//
		// "+", "-", "*" and "/" operations set stored value, shows it in text
		// input and mark it to be deleted when a new input is added.
		if (op != operationType.PER && op != operationType.NEG) {
			if (storedValue == 0)
				storedValue = currentValue;
			operation = op;
			textInput.setValue(String.valueOf(storedValue));
			deleteInput = true;
		}
	}

	/**
	 * Send the number to the server to convert it to binary
	 * and store it
	 */
	private void sendNumberToServer() {
		// Validate the input. is a number?
		errorLabel.setText("");
		String textToServer = textInput.getText();
		if (Float.isNaN(Float.parseFloat(textToServer))) {
			errorLabel.setText("Please enter a number");
			return;
		}

		// Then, we send the input to the server.
		dataStoreService.convertToBinary(textToServer, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
/*				dialogBox.setText("Remote Procedure Call - Failure");
				serverResponseLabel.addStyleName("serverResponseLabelError");
				serverResponseLabel.setHTML(SERVER_ERROR);
				dialogBox.center();
				closeButton.setFocus(true);
*/			}

			public void onSuccess(String result) {
				getNumbersFromServer();
			}
		});
	}
	
	/**
	 * Send the number to the server to convert it to binary
	 * and store it
	 */
	private void getNumbersFromServer() {
		dataStoreService.getCurrentNumbers(new AsyncCallback<ArrayList<BinaryNumb>>() {
	
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
	
			@Override
			public void onSuccess(ArrayList<BinaryNumb> result) {
				results = result;
				buildNumbersTable();
			}
		});
	}

	private void buildNumbersTable() {
    	numbersTable.clear();
		numbersTable.setText(0, 0, "Date");
		numbersTable.setText(0, 1, "Original Number");
		numbersTable.setText(0, 2, "Binary (IEEE 754 FP bit layout)");
		// Add styles to elements in the stock list table.
    	numbersTable.setCellPadding(6);
    	numbersTable.addStyleName("watchList");
    	numbersTable.getRowFormatter().addStyleName(0, "watchListHeader");

    	Iterator<BinaryNumb> iter = results.iterator();
	    int row = 1;
	    while(iter.hasNext()) {
	    	BinaryNumb item = iter.next();
	    	numbersTable.setText(row, 0, item.getDate());
	    	numbersTable.setText(row, 1, item.getOriginalNumber());
	    	numbersTable.setText(row, 2, item.getConvertedNumber());
	    	numbersTable.getCellFormatter().addStyleName(row, 0, "watchListNumericColumn");
	    	numbersTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
	    	numbersTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
	    	row++;
	    }
	}

}
