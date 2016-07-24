/**
 * NumbersGrid.java
 * 
 * Implements the class which will show all stored numbers in a Grid
 */

package com.lucioaragon.calculator.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.lucioaragon.calculator.shared.BinaryNumb;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.state.client.GridStateHandler;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class NumbersGrid implements IsWidget {
	public static final int HEIGHT = 320;
	
	private ArrayList<BinaryNumb> items;
	
	private static final BinaryNumberProperties numbers = GWT.create(BinaryNumberProperties.class);
	private ListStore<BinaryNumb> store = new ListStore<BinaryNumb>(numbers.key());
	private Grid<BinaryNumb> grid;	
	private ContentPanel panel;
	
	@Override
	public Widget asWidget() {

		if (panel == null) {
			ColumnConfig<BinaryNumb, String> dateCol = new ColumnConfig<BinaryNumb, String> (numbers.date(), 200, "Date");
			ColumnConfig<BinaryNumb, String> originalCol = new ColumnConfig<BinaryNumb, String> (numbers.originalNumber(), 120, "Original Number");
			ColumnConfig<BinaryNumb, String> binaryCol = new ColumnConfig<BinaryNumb, String> (numbers.convertedNumber(), 280, "Binary (IEEE 754 FP bit layout)");

			List<ColumnConfig<BinaryNumb, ?>> columns = new ArrayList<ColumnConfig<BinaryNumb,?>>();
			columns.add(dateCol);
			columns.add(originalCol);
			columns.add(binaryCol);
			
			ColumnModel<BinaryNumb> cm = new ColumnModel<BinaryNumb>(columns);
			
			store.addAll(items);
			
			grid = new Grid<BinaryNumb>(store, cm);
			grid.setAllowTextSelection(true);
			grid.getView().setAutoExpandColumn(dateCol);
			grid.getView().setAutoExpandColumn(originalCol);
			grid.getView().setAutoExpandColumn(binaryCol);
			grid.getView().setColumnLines(true);
			grid.setBorders(false);
			grid.setColumnReordering(true);
			
			grid.setStateful(true);
			grid.setStateId("Stored numbers");
			
			GridStateHandler<BinaryNumb> state = new GridStateHandler<BinaryNumb>(grid);
			state.loadState();
			
			VerticalLayoutContainer con = new VerticalLayoutContainer();
			con.add(grid, new VerticalLayoutData(1, 1));
			
			panel = new ContentPanel();
			panel.setHeading("Stored numbers");
			panel.setCollapsible(true);
			panel.add(con);
			panel.setHeight(String.valueOf(HEIGHT));
		}

		return panel;
	}

	/**
	 * Sets the lists of numbers to be shown in the grid
	 * @param items
	 */
	public void setData(ArrayList<BinaryNumb> items) {
		this.items = items;
		
		// If store already has items, replace them, and refresh grid
		if (store.size() > 0) {
			store.replaceAll(items);
			grid.getView().refresh(false);
		}
	}
}
