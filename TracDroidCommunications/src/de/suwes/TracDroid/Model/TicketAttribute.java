package de.suwes.TracDroid.Model;

import java.util.List;

/**
 *
 * @author Osiris
 */
public class TicketAttribute 
{
    public static int TYPE_TEXT = 0;
    public static int TYPE_CHECKBOX = 1;
    public static int TYPE_SELECT = 2;
    public static int TYPE_RADIO = 3;
    public static int TYPE_TEXTAREA = 4;
    
    private String m_strName;
    private String m_strLabel;
    private int m_iType;
    private String m_strValue;
    private List<String> m_options;
    private boolean m_bOptional;
    private int m_iOrder;
    private boolean m_bCustom;
    private String m_strFormat;
    
    public TicketAttribute(String strName, String strLabel, String strType, 
            String strValue, List<String> options, boolean bOptional, 
            int iOrder, boolean bCustom, String strFormat)
    {
        m_strName = strName;
        m_strLabel = strLabel;
        if (strType.equals("text"))
            m_iType = TYPE_TEXT;
        else if (strType.equals("checkbox"))
            m_iType = TYPE_CHECKBOX;
        else if (strType.equals("select"))
            m_iType = TYPE_SELECT;
        else if (strType.equals("radio"))
            m_iType = TYPE_RADIO;
        else if (strType.equals("textarea"))
            m_iType = TYPE_TEXTAREA;
        m_strValue = strValue;
        m_options = options;
        m_bOptional = bOptional;
        m_iOrder = iOrder;
        m_bCustom = bCustom;
        m_strFormat = strFormat;
    }

    /**
     * @return the m_strName
     */
    public String getName() {
        return m_strName;
    }

    /**
     * @return the m_strLabel
     */
    public String getLabel() {
        return m_strLabel;
    }

    /**
     * @return the m_iType
     */
    public int getType() {
        return m_iType;
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
     * @return the m_bOptional
     */
    public boolean isOptional() {
        return m_bOptional;
    }

    /**
     * @return the m_iOrder
     */
    public int getOrder() {
        return m_iOrder;
    }

    /**
     * @return the m_bCustom
     */
    public boolean isCustom() {
        return m_bCustom;
    }

    /**
     * @return the m_strFormat
     */
    public String getFormat() {
        return m_strFormat;
    }
}
