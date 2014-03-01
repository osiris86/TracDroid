/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Model;

import java.util.Date;

/**
 *
 * @author Osiris
 */
public class TicketChange 
{
    private Date m_dtTime;
    private String m_strAuthor;
    private String m_strField;
    private String m_strOldValue;
    private String m_strNewValue;
    
    public TicketChange(Date dtTime, String strAuthor, String strField, String strOldValue, String strNewValue)
    {
        m_dtTime = dtTime;
        m_strAuthor = strAuthor;
        m_strField = strField;
        m_strOldValue = strOldValue;
        m_strNewValue = strNewValue;
    }

    /**
     * @return the m_dtTime
     */
    public Date getTime() {
        return m_dtTime;
    }

    /**
     * @return the m_strAuthor
     */
    public String getAuthor() {
        return m_strAuthor;
    }

    /**
     * @return the m_strField
     */
    public String getField() {
        return m_strField;
    }

    /**
     * @return the m_strOldValue
     */
    public String getOldValue() {
        return m_strOldValue;
    }

    /**
     * @return the m_strNewValue
     */
    public String getNewValue() {
        return m_strNewValue;
    }
}
