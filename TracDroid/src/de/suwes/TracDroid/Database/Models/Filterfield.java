/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Database.Models;

/**
 *
 * @author Osiris
 */
public class Filterfield {
    private int m_iID;
    private int m_iFilterID;
    private String m_strField;
    private String m_strOperator;
    private String m_strValue;
    
    public Filterfield(int iID, int iFilterID, String strField, String strOperator, String strValue)
    {
        m_iID = iID;
        m_iFilterID = iFilterID;
        m_strField = strField;
        m_strOperator = strOperator;
        m_strValue = strValue;
    }

    /**
     * @return the m_iID
     */
    public int getID() {
        return m_iID;
    }

    /**
     * @param m_iID the m_iID to set
     */
    public void setID(int m_iID) {
        this.m_iID = m_iID;
    }

    /**
     * @return the m_iFilterID
     */
    public int getFilterID() {
        return m_iFilterID;
    }

    /**
     * @param m_iFilterID the m_iFilterID to set
     */
    public void setFilterID(int m_iFilterID) {
        this.m_iFilterID = m_iFilterID;
    }

    /**
     * @return the m_strField
     */
    public String getField() {
        return m_strField;
    }

    /**
     * @param m_strField the m_strField to set
     */
    public void setField(String m_strField) {
        this.m_strField = m_strField;
    }

    /**
     * @return the m_strOperator
     */
    public String getOperator() {
        return m_strOperator;
    }

    /**
     * @param m_strOperator the m_strOperator to set
     */
    public void setOperator(String m_strOperator) {
        this.m_strOperator = m_strOperator;
    }

    /**
     * @return the m_strValue
     */
    public String getValue() {
        return m_strValue;
    }

    /**
     * @param m_strValue the m_strValue to set
     */
    public void setValue(String m_strValue) {
        this.m_strValue = m_strValue;
    }
}
