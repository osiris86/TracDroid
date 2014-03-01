/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Database.Models;

import android.content.Context;
import android.database.Cursor;
import de.suwes.TracDroid.Database.FilterfieldsProvider;
import java.text.ParseException;
import java.util.List;

/**
 *
 * @author Osiris
 */
public class Filter {
    
    public static final int GROUPBY_NOTHING = 0;
    public static final int GROUPBY_MILESTONE = 1;
    public static final int GROUPBY_COMPONENT = 2;
    public static final int GROUPBY_STATUS = 3;
    
    private int m_iID;
    private String m_strName;
    private boolean m_bDefault;
    private int m_iGroupBy;
    private int m_iInstance;
    
    /**
     * Konstruktor
     * @param iID
     * @param strName 
     */
    public Filter(int iID, String strName, boolean bDefault, int iGroupBy, int iInstance)
    {
        m_iID = iID;
        m_strName = strName;
        m_bDefault = bDefault;
        m_iGroupBy = iGroupBy;
        m_iInstance = iInstance;
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
     * @return the m_strName
     */
    public String getName() {
        return m_strName;
    }

    /**
     * @param m_strName the m_strName to set
     */
    public void setName(String m_strName) {
        this.m_strName = m_strName;
    }

    /**
     * @return the m_bDefault
     */
    public boolean isDefault() {
        return m_bDefault;
    }

    /**
     * @param m_bDefault the m_bDefault to set
     */
    public void setDefault(boolean m_bDefault) {
        this.m_bDefault = m_bDefault;
    }
    
    public String getFilterString(Context ctx) throws ParseException
    {
        Cursor c = ctx.getContentResolver().query(
                FilterfieldsProvider.getContentURI(), 
                FilterfieldsProvider.COLUMNS, 
                "filterid = ?", 
                new String[] { Integer.toString(m_iID) }, 
                null);

        List<Filterfield> filterfields = FilterfieldsProvider.getListFromCursor(c);

        String strFilter = "";

        for (Filterfield filterfield : filterfields)
        {
            if (strFilter.length() > 0)
            {
                strFilter += "&";
            }

            strFilter += filterfield.getField();
            strFilter += filterfield.getOperator();
            strFilter += filterfield.getValue();
        }
        
        return strFilter;
    }

    /**
     * @return the m_iGroupBy
     */
    public int getGroupBy() {
        return m_iGroupBy;
    }

    /**
     * @param m_iGroupBy the m_iGroupBy to set
     */
    public void setGroupBy(int m_iGroupBy) {
        this.m_iGroupBy = m_iGroupBy;
    }

    /**
     * @return the m_iInstance
     */
    public int getInstance() {
        return m_iInstance;
    }

    /**
     * @param m_iInstance the m_iInstance to set
     */
    public void setInstance(int m_iInstance) {
        this.m_iInstance = m_iInstance;
    }
}
