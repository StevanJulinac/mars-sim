/**
 * Mars Simulation Project
 * SettlementUnitWindow.java
 * @version 3.1.0 2019-03-02
 * @author Scott Davis
 */

package org.mars_sim.msp.ui.swing.unit_window.structure;

import org.mars_sim.msp.core.Unit;
import org.mars_sim.msp.core.structure.Settlement;
import org.mars_sim.msp.ui.swing.MainDesktopPane;
import org.mars_sim.msp.ui.swing.unit_window.InventoryTabPanel;
import org.mars_sim.msp.ui.swing.unit_window.LocationTabPanel;
import org.mars_sim.msp.ui.swing.unit_window.UnitWindow;
import org.mars_sim.msp.ui.swing.unit_window.structure.building.food.TabPanelCooking;
import org.mars_sim.msp.ui.swing.unit_window.structure.building.food.TabPanelFoodProduction;

/**
 * The SettlementUnitWindow is the window for displaying a settlement.
 */
public class SettlementUnitWindow extends UnitWindow {

	/**
	 * Constructor
	 *
	 * @param desktop the main desktop panel.
	 * @param unit    the unit to display.
	 */
	public SettlementUnitWindow(MainDesktopPane desktop, Unit unit) {
		// Use UnitWindow constructor
		super(desktop, unit, false);

		Settlement settlement = (Settlement) unit;

		addTabPanel(new TabPanelAirComposition(settlement, desktop));

		addTabPanel(new TabPanelAssociatedPeople(settlement, desktop));

		addTabPanel(new TabPanelBots(settlement, desktop));

		addTabPanel(new TabPanelBuildings(settlement, desktop));

		addTabPanel(new TabPanelCooking(settlement, desktop));

		addTabPanel(new TabPanelConstruction(settlement, desktop));

		addTabPanel(new TabPanelCredit(settlement, desktop));

		addTabPanel(new TabPanelFoodProduction(settlement, desktop));

		addTabPanel(new TabPanelGoods(settlement, desktop));

		addTabPanel(new InventoryTabPanel(settlement, desktop));

		addTopPanel(new LocationTabPanel(settlement, desktop));

		addTabPanel(new TabPanelMaintenance(settlement, desktop));

		addTabPanel(new TabPanelManufacture(settlement, desktop));

		addTabPanel(new TabPanelMissions(settlement, desktop));

		addTabPanel(new TabPanelOrganization(settlement, desktop));

		addTabPanel(new TabPanelPopulation(settlement, desktop));

		addTabPanel(new TabPanelPowerGrid(settlement, desktop));

		addTabPanel(new TabPanelResourceProcesses(settlement, desktop));

		addTabPanel(new TabPanelScience(settlement, desktop));

		addTabPanel(new TabPanelThermalSystem(settlement, desktop));

		addTabPanel(new TabPanelVehicles(settlement, desktop));

		addTabPanel(new TabPanelWeather(settlement, desktop));

		sortTabPanels();
	}

	/**
	 * Updates this window.
	 */
	@Override
	public void update() {
		super.update();
	}

}
