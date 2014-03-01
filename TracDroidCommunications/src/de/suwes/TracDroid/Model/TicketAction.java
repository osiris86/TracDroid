package de.suwes.TracDroid.Model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Osiris
 */
public class TicketAction implements Serializable {
    private String m_strAction;
    private String m_strLabel;
    private String m_strHint;
    private List<TicketInputField> m_inputFields;
    
    public TicketAction(String strAction, String strLabel, String strHint, List<TicketInputField> inputFields)
    {
        m_strAction = strAction;
        m_strLabel = strLabel;
        m_strHint = strHint;
        m_inputFields = inputFields;
    }

    /**
     * @return the m_strAction
     */
    public String getAction() {
        return m_strAction;
    }

    /**
     * @return the m_strLabel
     */
    public String getLabel() {
        return m_strLabel;
    }

    /**
     * @return the m_strHint
     */
    public String getHint() {
        return m_strHint;
    }

    /**
     * @return the m_inputFields
     */
    public List<TicketInputField> getInputFields() {
        return m_inputFields;
    }
}
