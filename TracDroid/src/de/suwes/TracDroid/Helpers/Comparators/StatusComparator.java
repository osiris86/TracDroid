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
public class StatusComparator implements Comparator<Ticket> {
    public int compare(Ticket ticket1, Ticket ticket2) {
        String strStatus1 = ticket1.getAttribute("status");
        String strStatus2 = ticket2.getAttribute("status");
        
        return strStatus1.compareTo(strStatus2);
    }
}
