package de.suwes.TracDroid.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Osiris
 */
public class Ticket implements Serializable
{
    private int m_iID;
    private HashMap<String, Object> m_Attributes;
    private List<TicketAction> m_actions;
    
    public Ticket()
    {
        m_iID = -1;
    }
    
    public Ticket(int iID, HashMap<String, Object> attributes, List<TicketAction> actions)
    {
        this.setValues(iID, attributes, actions);
    }
    
    private void setValues(int iID, HashMap<String, Object> attributes, List<TicketAction> actions)
    {
        setID(iID);
        setAttributes(attributes);
        m_actions = actions;
    }
    
    public String getAttribute(String strName)
    {
        if (m_Attributes.get(strName) instanceof Date)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            return sdf.format((Date)m_Attributes.get(strName));
        }
        else
        {
            return (String) m_Attributes.get(strName);
        }
    }
    
    public void setAttribute(String strName, Object value)
    {
        if (m_Attributes == null)
        {
            m_Attributes = new HashMap<String, Object>();
        }
        m_Attributes.put(strName, value);
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
     * @return the m_Attributes
     */
    public HashMap<String, Object> getAttributes() {
        return m_Attributes;
    }

    /**
     * @param m_Attributes the m_Attributes to set
     */
    public void setAttributes(HashMap<String, Object> m_Attributes) {
        this.m_Attributes = m_Attributes;
    }

    /**
     * @return the m_actions
     */
    public List<TicketAction> getActions() {
        return m_actions;
    }
}
