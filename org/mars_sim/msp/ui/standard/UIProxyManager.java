/**
 * Mars Simulation Project
 * UIProxyManager.java
 * @version 2.71 2000-10-23
 * @author Scott Davis
 */

package org.mars_sim.msp.ui.standard;

import org.mars_sim.msp.simulation.*;
import java.util.*;
import javax.swing.*;

/** Creates and manages a collection of unit UI proxies. */
public class UIProxyManager {

    // Data members
    private UnitUIProxy[] unitUIProxies;

    /** Constructs a UIProxyManager object 
     *  @units array of units
     */
    public UIProxyManager(Unit[] units) {

        Vector proxies = new Vector();

        for (int x = 0; x < units.length; x++) {

            Unit unit = units[x];

            if (unit instanceof Person)
                proxies.addElement(new PersonUIProxy((Person) unit, this));

            if (unit instanceof Settlement)
                proxies.addElement(new SettlementUIProxy((Settlement) unit, this));

            if (unit instanceof GroundVehicle) {
                if (unit instanceof Rover) {
                    ImageIcon roverIcon = new ImageIcon("images/RoverIcon.gif");
                    proxies.addElement(
                            new GroundVehicleUIProxy((GroundVehicle) unit, this,
                            roverIcon));
                }
            }
        }

        unitUIProxies = new UnitUIProxy[proxies.size()];
        for (int x = 0; x < unitUIProxies.length; x++)
            unitUIProxies[x] = (UnitUIProxy) proxies.elementAt(x);
    }
    
    /** Gets an array of all the UnitUIProxy objects 
     *  @return an array of unit UI proxies
     */
    public UnitUIProxy[] getUIProxies() {
        UnitUIProxy[] result = new UnitUIProxy[unitUIProxies.length];
        for (int x = 0; x < unitUIProxies.length; x++)
            result[x] = unitUIProxies[x];
        return result;
    }

    /** Gets the UnitUIProxy for a given unit 
     *  @param unit the unit
     *  @return the unit's UI proxy
     */
    public UnitUIProxy getUnitUIProxy(Unit unit) {
        UnitUIProxy result = null;
        for (int x = 0; x < unitUIProxies.length; x++) {
            if (unitUIProxies[x].getUnit() == unit)
                result = unitUIProxies[x];
        }
        return result;
    }

    /** Gets an ordered array of people UI proxies 
     *  @return an ordered array of people UI proxies
     */
    public UnitUIProxy[] getOrderedPeopleProxies() {
        Vector peopleProxies = new Vector();

        for (int x = 0; x < unitUIProxies.length; x++) {
            if (unitUIProxies[x] instanceof PersonUIProxy)
                peopleProxies.addElement(unitUIProxies[x]);
        }

        return sortProxies(peopleProxies);
    }

    /** Gets an ordered array of settlement UI proxies 
     *  @return an ordered array of settlement UI proxies
     */
    public UnitUIProxy[] getOrderedSettlementProxies() {
        Vector settlementProxies = new Vector();

        for (int x = 0; x < unitUIProxies.length; x++) {
            if (unitUIProxies[x] instanceof SettlementUIProxy)
                settlementProxies.addElement(unitUIProxies[x]);
        }

        return sortProxies(settlementProxies);
    }

    /** Gets an ordered array of vehicle UI proxies 
     *  @return an ordered array of vehicle UI proxies
     */
    public UnitUIProxy[] getOrderedVehicleProxies() {
        Vector vehicleProxies = new Vector();

        for (int x = 0; x < unitUIProxies.length; x++) {
            if (unitUIProxies[x] instanceof VehicleUIProxy)
                vehicleProxies.addElement(unitUIProxies[x]);
        }

        return sortProxies(vehicleProxies);
    }

    /** Sorts a vector of UI proxies and returns them in an array 
     *  @param unsortedProxies unsorted vector of UI proxies
     *  @return sorted array of UI proxies
     */
    private UnitUIProxy[] sortProxies(Vector unsortedProxies) {

        UnitUIProxy sorterProxy = null;
        UnitUIProxy[] sortedProxies = new UnitUIProxy[unsortedProxies.size()];

        for (int x = 0; x < sortedProxies.length; x++) {
            sorterProxy = (UnitUIProxy) unsortedProxies.elementAt(0);
            for (int y = 0; y < unsortedProxies.size(); y++) {
                UnitUIProxy tempProxy = (UnitUIProxy) unsortedProxies.elementAt(y);
                if (tempProxy.getUnit().getName().compareTo(
                        sorterProxy.getUnit().getName()) <= 0) {
                    sorterProxy = tempProxy;
                }
            }
            sortedProxies[x] = sorterProxy;
            unsortedProxies.removeElement(sorterProxy);
        }

        return sortedProxies;
    }
}
