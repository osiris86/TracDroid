/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Osiris
 */
public class TicketInputField implements Serializable
{
    private String m_strName;
    private String m_strValue;
    private List<String> m_options;
    
    public TicketInputField(String strName, String strValue, List<String> options)
    {
        m_strName = strName;
        m_strValue = strValue;
        m_options = options;
    }

    /**
     * @return the m_strName
     */
    public String getName() {
        return m_strName;
    }

    /**
     * @return the m_strValue
     */
    public String getValue() {
        return m_strValue;
    }

    /**
     * @return the m_options
     */
    public List<String> getOptions() {
        return m_options;
    }

    /**
     * @param m_strValue the m_strValue to set
     */
    public void setValue(String m_strValue) {
        this.m_strValue = m_strValue;
    }
}
