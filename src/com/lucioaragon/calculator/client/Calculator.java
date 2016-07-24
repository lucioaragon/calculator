/**
 * Calculator.java
 * 
 * Basic calculator by Lucio Aragón González
 */

package com.lucioaragon.calculator.client;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.RootPanel;
import com.lucioaragon.calculator.shared.BinaryNumb;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Main class and application's entry point
 */
public class Calculator implements EntryPoint {
	public static final int MAX_WIDTH = 600;
	public static final int MAX_HEIGHT = 600;
	public static final int BUTTON_WIDTH = 70;
	public static final int BUTTON_HEIGHT = 30;
	public static final int BUTTON_SPACING = 5;

	// Create a remote service proxy to talk to the server-side Greeting service.
	private final DataStoreServiceAsync dataStoreService = GWT.create(DataStoreService.class);

	// layout elements
	private CenterLayoutContainer mainPanel = new CenterLayoutContainer();
	private VerticalLayoutContainer verticalPanel = new VerticalLayoutContainer();
	private CenterLayoutContainer buttonsPanel2 = new CenterLayoutContainer();
	private ContentPanel buttonsPanel = new ContentPanel();
	private FlexTable buttonsLayout = new FlexTable();
	private ArrayList<TextButton> buttons = new ArrayList<TextButton>();
	private TextField textInput = new TextField();
	NumbersGrid numbersGrid = new NumbersGrid();

	// Data
	// Texts for each calculator button
	private ArrayList<String> buttonTexts = new ArrayList<String>();
	
	// List of numbers stored in server already retrieved
	private ArrayList<BinaryNumb> results;
	
	// Value in text input
	private float currentValue = 0;
	
	// Value stored in memory, which will operate with current input
	private float storedValue = 0;
	
	// Type of operation that can be done
	private enum operationType {NONE, ADD, SUB, MUL, DIV, PER, NEG, BIN};
	
	// Type of operation which will be done
	private operationType operation;
	
	// Mark current input to be deleted after an operation is done and new input
	// is introduced
	private boolean deleteInput;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		buildLayout();
		reset();
	}
	
	/**
	 * Build the layout
	 */
	private void buildLayout() {
		textInput.setWidth(BUTTON_WIDTH * 3 + BUTTON_SPACING * 2);
		textInput.setHeight(BUTTON_HEIGHT);
		//textInput.setDirection(Direction.RTL);

		Collections.addAll(buttonTexts,                "C",  "CE",
										"7", "8", "9", "+/-", "%",
										"4", "5", "6", "+",  "-",
										"1", "2", "3", "*",  "/",
										"0", ".",      "Bin", "=");
		
		for (int i = 0; i < buttonTexts.size(); i++) {
			final TextButton newButton = new TextButton(buttonTexts.get(i), onButtonClicked);
			newButton.setWidth(BUTTON_WIDTH);
			newButton.setHeight(BUTTON_HEIGHT);
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
		
		buttonsPanel2.setSize(String.valueOf(MAX_WIDTH),
				String.valueOf(BUTTON_HEIGHT * 5 + BUTTON_SPACING * 4 + 40) );
		buttonsPanel2.add(buttonsLayout);
		buttonsLayout.setCellSpacing(BUTTON_SPACING);
		buttonsPanel.setHeading("Basic calculator");
		buttonsPanel.add(buttonsPanel2);
		verticalPanel.setSize(String.valueOf(MAX_WIDTH), String.valueOf(MAX_WIDTH));
		verticalPanel.add(buttonsPanel);
		mainPanel.add(verticalPanel);
		RootPanel.get().add(mainPanel);
	}

	/**
	 * Handler triggered when a calculator button is selected
	 */
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
				// [0-9] or '.' buttons update textInput value
				String actualValue = textInput.getValue();
				if ((Float.parseFloat(actualValue) == 0 && !actualValue.contains("."))
						|| deleteInput)
					actualValue = "";
				deleteInput = false;
				if (actualValue == "" && source.getText() == ".")
					actualValue += "0";
				actualValue += source.getText();
				textInput.setValue(actualValue);
				break;
				
			case "C":
				// 'C' button resets the calculator 
				reset();
				break;
			case "CE":
				// 'CE' button clears current input
				currentValue = 0;
				textInput.setValue(String.valueOf(currentValue));
				break;
				
			case "+":
				// '+' sets add operation
				// also performs pending operation
				makeOperation(operationType.ADD);
				break;

			case "-":
				// '-' sets substract operation
				// also performs pending operation
				makeOperation(operationType.SUB);
				break;

			case "*":
				// '*' sets multiply operation
				// also performs pending operation
				makeOperation(operationType.MUL);
				break;

			case "/":
				// '/' sets divide operation
				// also performs pending operation
				makeOperation(operationType.DIV);
				break;

			case "=":
				// '=' makes pending operation
				makeOperation(operationType.NONE);
				break;

			case "%":
				// '%' changes input value as a percentage of stored value
				makeOperation(operationType.PER);
				break;

			case "+/-":
				// '+/-' changes input sign
				makeOperation(operationType.NEG);
				break;

			case "Bin":
				// Converts current input to binary
				// Also stores the number in server,
				// retrieve the current stored numbers
				// and shows them in a grid
				convertToBinary();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * Reset calculator to initial state
	 */
	private void reset() {
		currentValue = storedValue = 0;
		deleteInput = false;
		operation = operationType.NONE;
		textInput.setValue(String.valueOf(currentValue));
	}

	/**
	 * Perform the desired operation.
	 * 
	 * @param op {@link operationType}
	 */
	protected void makeOperation(operationType op) {
		// get value from text input
		currentValue = Float.parseFloat(textInput.getValue());
		if (Float.isNaN(currentValue)) {
			currentValue = 0;
			textInput.setValue(String.valueOf(currentValue));
			return;
		}
		
		// get previous stored value, if it's changed, it will be updated
		// at the end of this method
		float previousValue = storedValue;
		
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
			previousValue = currentValue; // force change of previousValue
			storedValue += currentValue; 
		}
		else if (operation == operationType.SUB) {
			previousValue = currentValue; // force change of previousValue
			storedValue -= currentValue; 
		}
		else if (operation == operationType.MUL) {
			previousValue = currentValue; // force change of previousValue
			storedValue *= currentValue; 
		}
		else if (operation == operationType.DIV) {
			previousValue = currentValue; // force change of previousValue
			storedValue /= currentValue; 
		}

		// "%" and "+/-" operations only alter value in text input
		//
		// "+", "-", "*" and "/" operations set stored value, shows it in text
		// input and mark it to be deleted when a new input is added.
		if (op != operationType.PER && op != operationType.NEG) {
			if (op != operationType.NONE && storedValue == previousValue)
				storedValue = currentValue;
			operation = op;
			textInput.setValue(String.valueOf(storedValue));
			deleteInput = true;
		}
	}

	/**
	 * Send the number to the server to convert it to binary.
	 * Also store the number in the server
	 */
	private void convertToBinary() {
		String textToServer = textInput.getText();

		// Send the input to the server.
		dataStoreService.convertToBinary(textToServer, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				// Get currently stored numbers
				getNumbersFromServer();
			}
		});
	}
	
	/**
	 * Get currently stored numbers from the server
	 */
	private void getNumbersFromServer() {
		dataStoreService.getCurrentNumbers(new AsyncCallback<ArrayList<BinaryNumb>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(ArrayList<BinaryNumb> result) {
				// retrieve the stored numbers, and show them
				results = result;
				buildNumbersTable();
			}
		});
	}

	/**
	 * Builds a grid showing all currently stored numbers
	 */
	private void buildNumbersTable() {
		numbersGrid.setData(results);
		verticalPanel.add(numbersGrid);
	}
}
