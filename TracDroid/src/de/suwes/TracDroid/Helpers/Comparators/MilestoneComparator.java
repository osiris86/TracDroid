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
public class MilestoneComparator implements Comparator<Ticket> {
    public int compare(Ticket ticket1, Ticket ticket2) {
        String strMilestone1 = ticket1.getAttribute("milestone");
        String strMilestone2 = ticket2.getAttribute("milestone");
        
        return strMilestone1.compareTo(strMilestone2);
    }
}
