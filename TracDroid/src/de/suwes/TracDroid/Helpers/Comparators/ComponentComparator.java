/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Helpers.Comparators;

import de.suwes.TracDroid.Model.Ticket;
import java.util.Comparator;

/**
 *
 * @author Osiris
 */
public class ComponentComparator implements Comparator<Ticket> {
    public int compare(Ticket ticket1, Ticket ticket2) {
        String strComponent1 = ticket1.getAttribute("component");
        String strComponent2 = ticket2.getAttribute("component");
        
        return strComponent1.compareTo(strComponent2);
    }
}
