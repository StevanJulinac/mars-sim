/**
 * Mars Simulation Project
 * InventoryTabPanel.java
 * @version 2.75 2003-07-16
 * @author Scott Davis
 */

package org.mars_sim.msp.ui.standard.unit_window;

import org.mars_sim.msp.simulation.*;
import org.mars_sim.msp.simulation.equipment.*;
import org.mars_sim.msp.ui.standard.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/** 
 * The InventoryTabPanel is a tab panel for displaying inventory information.
 */
public class InventoryTabPanel extends TabPanel implements ListSelectionListener {
    
    private ResourceTableModel resourceTableModel;
    private EquipmentTableModel equipmentTableModel;
    private JTable equipmentTable;
    
    /**
     * Constructor
     *
     * @param unit the unit to display.
     * @param desktop the main desktop.
     */
    public InventoryTabPanel(Unit unit, MainDesktopPane desktop) { 
        // Use the TabPanel constructor
        super("Inventory", null, "Inventory", unit, desktop);
 
        Inventory inv = unit.getInventory();
 
        // Create inventory label panel.
        JPanel inventoryLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topContentPanel.add(inventoryLabelPanel);
        
        // Create inventory label
        JLabel inventoryLabel = new JLabel("Inventory", JLabel.CENTER);
        inventoryLabelPanel.add(inventoryLabel);
        
        // Create inventory content panel
        JPanel inventoryContentPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        centerContentPanel.add(inventoryContentPanel, BorderLayout.CENTER);
        
        // Create resources panel
        JScrollPane resourcesPanel = new JScrollPane();
        resourcesPanel.setBorder(new MarsPanelBorder());
        inventoryContentPanel.add(resourcesPanel);
        
        // Create resources table model
        resourceTableModel = new ResourceTableModel(inv);
            
        // Create resources table
        JTable resourcesTable = new JTable(resourceTableModel);
        resourcesTable.setPreferredScrollableViewportSize(new Dimension(200, 75));
        resourcesTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        resourcesTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        resourcesTable.setCellSelectionEnabled(false);
        resourcesTable.setDefaultRenderer(Double.class, new NumberCellRenderer());
        resourcesPanel.setViewportView(resourcesTable);
        
        // Create equipment panel
        JScrollPane equipmentPanel = new JScrollPane();
        equipmentPanel.setBorder(new MarsPanelBorder());
        inventoryContentPanel.add(equipmentPanel);
        
        // Create equipment table model
        equipmentTableModel = new EquipmentTableModel(inv);
        
        // Create equipment table
        equipmentTable = new JTable(equipmentTableModel);
        equipmentTable.setPreferredScrollableViewportSize(new Dimension(200, 75));
        equipmentTable.setCellSelectionEnabled(true);
        equipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        equipmentTable.getSelectionModel().addListSelectionListener(this);
        equipmentPanel.setViewportView(equipmentTable);
    }
    
    /**
     * Updates the info on this panel.
     */
    public void update() {
        resourceTableModel.update();
        equipmentTableModel.update();
    }
    
    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e) {
        int index = equipmentTable.getSelectedRow();
        Equipment selectedEquipment = (Equipment) equipmentTable.getValueAt(index, 0);
        if (selectedEquipment != null) desktop.openUnitWindow(selectedEquipment);
    }
    
    /** 
     * Internal class used as model for the resource table.
     */
    private class ResourceTableModel extends AbstractTableModel {
        
        Inventory inventory;
        java.util.Map resources;
        java.util.List keys;
        
        private ResourceTableModel(Inventory inventory) {
            this.inventory = inventory;
            resources = inventory.getAllResources();
            keys = new ArrayList();
            Iterator i = resources.keySet().iterator();
            while (i.hasNext()) {
                Object key = i.next();
                double mass = ((Double) resources.get(key)).doubleValue();
                if (mass > 0D) keys.add(key);
            }
        }
        
        public int getRowCount() {
            return keys.size();
        }
        
        public int getColumnCount() {
            return 2;
        }
        
        public Class getColumnClass(int columnIndex) {
            Class dataType = super.getColumnClass(columnIndex);
            if (columnIndex == 1) dataType = Double.class;
            return dataType;
        }
        
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) return "Resource";
            else if (columnIndex == 1) return "Mass (kg)";
            else return "unknown";
        }
        
        public Object getValueAt(int row, int column) {
            if (column == 0) return keys.get(row);
            else if (column == 1) return resources.get(keys.get(row));
            else return "unknown";
        }
  
        public void update() {
            java.util.Map newResources = inventory.getAllResources();
            if (!resources.equals(newResources)) {
                resources = newResources;
                keys = new ArrayList();
                Iterator i = resources.keySet().iterator();
                while (i.hasNext()) {
                    Object key = i.next();
                    double mass = ((Double) resources.get(key)).doubleValue();
                    if (mass > 0D) keys.add(key);
                }
            
                fireTableDataChanged();
            }
        }
    }
    
    /** 
     * Internal class used as model for the equipment table.
     */
    private class EquipmentTableModel extends AbstractTableModel {
        
        Inventory inventory;
        UnitCollection equipment;
        
        private EquipmentTableModel(Inventory inventory) {
            this.inventory = inventory;
            equipment = inventory.getUnitsOfClass(Equipment.class);
        }
        
        public int getRowCount() {
            return equipment.size();
        }
        
        public int getColumnCount() {
            return 1;
        }
        
        public Class getColumnClass(int columnIndex) {
            Class dataType = super.getColumnClass(columnIndex);
            return dataType;
        }
        
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) return "Equipment";
            else return "unknown";
        }
        
        public Object getValueAt(int row, int column) {
            if (row < equipment.size()) {
                Unit result = null;
                int count = 0;
                UnitIterator i = equipment.iterator();
                while (i.hasNext()) {
                    Unit item = i.next();
                    if (count == row) result = item;
                    count++;
                }
                return result;
            }   
            else return "unknown";
        }
  
        public void update() {
            UnitCollection newEquipment = inventory.getUnitsOfClass(Equipment.class);
            if (!equipment.equals(newEquipment)) {
                equipment = newEquipment;
                fireTableDataChanged();
            }
        }
    } 
}
