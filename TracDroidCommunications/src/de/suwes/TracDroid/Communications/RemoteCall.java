package de.suwes.TracDroid.Communications;

import de.suwes.TracDroid.Model.*;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Osiris
 */
public class RemoteCall 
{
    private XMLRPCClient m_client;
    private static RemoteCall m_instance = null;
    private static String m_strServerUrl = "";
    private static String m_strUsername = "";
    private static String m_strPassword = "";
    
    /**
     * Creates a connection to the Trac system
     * @param strServerUrl The url of the server
     * @param strUsername The username for authentication
     * @param strPassword The password for authentication
     * @throws RemoteCallException 
     */
    private RemoteCall(String strServerUrl, String strUsername, String strPassword) throws RemoteCallException
    {
        try {
	        m_strServerUrl = strServerUrl;
	        m_strUsername = strUsername;
	        m_strPassword = strPassword;
			m_client = new XMLRPCClient(new URL(strServerUrl), XMLRPCClient.FLAGS_IGNORE_STATUSCODE | XMLRPCClient.FLAGS_FORWARD | XMLRPCClient.FLAGS_SSL_IGNORE_INVALID_CERT);
			m_client.setLoginData(strUsername, strPassword);
			
		} catch (MalformedURLException e) {
			throw new RemoteCallException(e.getClass(), "Constructor", e.getMessage());
		}
        
    }
    
    public static RemoteCall getInstance(String strServerUrl, String strUsername, String strPassword) throws RemoteCallException
    {
        if (m_instance == null)
        {
            m_instance = new RemoteCall(strServerUrl, strUsername, strPassword);
        }
        else
        {
            if (!m_strServerUrl.equals(strServerUrl) || !m_strUsername.equals(strUsername) || !m_strPassword.equals(strPassword))
            {
                m_instance = new RemoteCall(strServerUrl, strUsername, strPassword);
            }
        }
        return m_instance;
    }
    
    /**
     * Gets the APIVersion
     * @return Object Array with the API Versions
     * @throws RemoteCallException 
     */
    public Object[] getAPIVersion() throws RemoteCallException
    {
        try {
            Object[] result = (Object[]) m_client.call("system.getAPIVersion");
            return result;
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "system.getAPIVersion()", ex.getMessage());
        }
    }
    
    public List<Ticket> getTickets(String strFilter) throws RemoteCallException
    {
        try {
            strFilter = "max=0&" + strFilter;
            Object[] result = (Object[]) m_client.call("ticket.query", strFilter);
            List<Object> ticketIds = new ArrayList<Object>(); 
            ticketIds.addAll(Arrays.asList(result));
            List<Ticket> tickets = new ArrayList<Ticket>();
            
            for (Object id : ticketIds)
            {
                result = (Object[]) m_client.call("ticket.get", id);
                int iID = (Integer) result[0];
                HashMap<String, Object> attributes = (HashMap<String, Object>) result[3];
                List<TicketAction> actions = this.getTicketActions((Integer) id);
                
                
                tickets.add(new Ticket(iID, attributes, actions));
            }
            
            return tickets;
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "ticket.query", ex.getMessage());
        }
    }
    
    public Ticket getTicket(int iID) throws RemoteCallException
    {
        try {
            Object[] result = (Object[]) m_client.call("ticket.get", iID);
            iID = (Integer) result[0];
            HashMap<String, Object> attributes = (HashMap<String, Object>) result[3];
            List<TicketAction> actions = this.getTicketActions(iID);

            return new Ticket(iID, attributes, actions);
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "ticket.get", ex.getMessage());
        }
    }
    
    public Ticket updateTicket(Ticket updateTicket, String strComment, TicketAction action) throws RemoteCallException
    {
        try {
            int iID = updateTicket.getID();
            updateTicket.setAttribute("action", action.getAction());
            
            for (TicketInputField inputField : action.getInputFields())
            {
                updateTicket.setAttribute(inputField.getName(), inputField.getValue());
            }
            
            
            HashMap<String, Object> attributes = new HashMap<String, Object>();
            
            for (Entry<String, Object> attribute : updateTicket.getAttributes().entrySet())
            {
                if (!(attribute.getValue() instanceof Date))
                {
                    attributes.put(attribute.getKey(), attribute.getValue());
                }
            }
            
            List<Object> params = new ArrayList<Object>();
            params.add(iID);
            params.add(strComment);
            params.add(attributes);
            Object[] result = (Object[]) m_client.call("ticket.update", iID, strComment, attributes);
            
            iID = (Integer) result[0];
            attributes = (HashMap<String, Object>) result[3];
            List<TicketAction> actions = this.getTicketActions((Integer) iID);
            
            return new Ticket(iID, attributes, actions);
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "ticket.update", ex.getMessage());
        }
    }
    
    public Ticket createTicket(Ticket createTicket) throws RemoteCallException
    {
        try {
            HashMap<String, Object> attributes = new HashMap<String, Object>();
            
            String strSummary = "";
            String strDescription = "";
            
            for (Entry<String, Object> attribute : createTicket.getAttributes().entrySet())
            {
                if (!(attribute.getValue() instanceof Date))
                {
                    if (attribute.getKey().equals("summary"))
                    {
                        strSummary = attribute.getValue().toString();
                    }
                    else if (attribute.getKey().equals("description"))
                    {
                        strDescription = attribute.getValue().toString();
                    }
                    else
                    {
                        attributes.put(attribute.getKey(), attribute.getValue());
                    }
                }
            }
            
            Object result = (Object) m_client.call("ticket.create", strSummary, strDescription, attributes);
            
            int iID = (Integer) result;
            Ticket retTicket = this.getTicket(iID);
            
            return retTicket;
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "ticket.create", ex.getMessage());
        }
    }
    
    private List<TicketAction> getTicketActions(int iID) throws RemoteCallException
    {
        try 
        {
            List<TicketAction> actions = new ArrayList<TicketAction>();
            Object[] result = (Object[]) m_client.call("ticket.getActions", iID);
            
            for (Object action : result)
            {
                Object[] arrAction = (Object[]) action;
                
                String strAction = (String) arrAction[0];
                String strLabel = (String) arrAction[1];
                String strHint = (String) arrAction[2];
                List<TicketInputField> inputFields = new ArrayList<TicketInputField>();
                
                Object[] objInputFields = (Object[]) arrAction[3];
                for (Object inputField : objInputFields)
                {
                    Object[] arrInputField = (Object[]) inputField;
                    
                    String strName = (String) arrInputField[0];
                    String strValue = (String) arrInputField[1];
                    List<String> options = new ArrayList<String>();
                    
                    Object[] objValues = (Object[]) arrInputField[2];
                    for (Object value : objValues)
                    {
                        options.add((String) value);
                    }
                    
                    inputFields.add(new TicketInputField(strName, strValue, options));
                }
                
                actions.add(new TicketAction(strAction, strLabel, strHint, inputFields));
            }
            
            return actions;
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "ticket.getActions", ex.getMessage());
        }
    }
    
    public List<TicketAttribute> getTicketFields() throws RemoteCallException
    {
        try {
            Object[] result = (Object[]) m_client.call("ticket.getTicketFields");
            List<Object> res = new ArrayList<Object>(); 
            res.addAll(Arrays.asList(result));
            List<TicketAttribute> attributes = new ArrayList<TicketAttribute>();
            
            for (Object objAttribute : res)
            {
                HashMap<String, Object> attribute = (HashMap<String, Object>) objAttribute;
                
                String strName = "";
                String strLabel = "";
                String strType = "";
                String strValue = "";
                Object[] options = null;
                boolean bOptional = false;
                int iOrder = 0;
                boolean bCustom = false;
                String strFormat = "";
                
                if (attribute.get("name") != null)
                    strName = (String) attribute.get("name");
                if (attribute.get("label") != null)
                    strLabel = (String) attribute.get("label");
                if (attribute.get("type") != null)
                    strType = (String) attribute.get("type");
                if (attribute.get("value") != null)
                    strValue = (String) attribute.get("value");
                if (attribute.get("options") != null)
                    options = (Object[]) attribute.get("options");
                if (attribute.get("optional") != null)
                    bOptional = (Boolean) attribute.get("optional");
                if (attribute.get("order") != null)
                    iOrder = (Integer) attribute.get("order");
                if (attribute.get("custom") != null)
                    bCustom = (Boolean) attribute.get("custom");
                if (attribute.get("format") != null)
                    strFormat = (String) attribute.get("format");
                
                List<String> strOptions = new ArrayList<String>();
                if (options != null)
                {
                    for (Object option : options)
                    {
                        strOptions.add((String) option);
                    }
                }
                
                attributes.add(new TicketAttribute(strName, strLabel, strType, strValue, strOptions, bOptional, iOrder, bCustom, strFormat));
            }
            
            
            
            return attributes;
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "ticket.getTicketFields", ex.getMessage());
        }
    }
    
    public List<TicketChange> getTicketHistory(int iID) throws RemoteCallException
    {
        try {
            Object[] result = (Object[]) m_client.call("ticket.changeLog", iID);
            List<Object> res = new ArrayList<Object>(); 
            res.addAll(Arrays.asList(result));
            List<TicketChange> changes = new ArrayList<TicketChange>();
            
            for (Object objAttribute : res)
            {
                Object[] attributes = (Object[]) objAttribute;
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                Date dtTime = (Date) attributes[0];
                String strAuthor = (String) attributes[1];
                String strField = (String) attributes[2];
                String strOldValue = "";
                String strNewValue = "";
                
                if (attributes.length > 3)
                    strOldValue = (String) attributes[3];
                if (attributes.length > 4)
                    strNewValue = (String) attributes[4];
                
                
                changes.add(new TicketChange(dtTime, strAuthor, strField, strOldValue, strNewValue));
            }
            return changes;
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "ticket.changeLog", ex.getMessage());
        }
    }
    
    public List<String> getComponents() throws RemoteCallException
    {
        try {
            Object[] result = (Object[]) m_client.call("ticket.component.getAll");
            List<Object> res = new ArrayList<Object>(); 
            res.addAll(Arrays.asList(result));
            List<String> strComponents = new ArrayList<String>();
            
            for (Object objEntry : res)
            {
                strComponents.add(objEntry.toString());
            }
            
            return strComponents;
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "ticket.component.getAll", ex.getMessage());
        }
    }
    
    public List<String> getMilestones() throws RemoteCallException
    {
        try {
            Object[] result = (Object[]) m_client.call("ticket.milestone.getAll");
            List<Object> res = new ArrayList<Object>(); 
            res.addAll(Arrays.asList(result));
            List<String> strMilestones = new ArrayList<String>();
            
            for (Object objEntry : res)
            {
                strMilestones.add(objEntry.toString());
            }
            
            return strMilestones;
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "ticket.milestone.getAll", ex.getMessage());
        }
    }
    
    public String getWikiPage(String strName) throws RemoteCallException
    {
        if (strName == null)
        {
            return getWikiPage("WikiStart");
        }
        else
        {
            return getWikiPage(strName, -1);
        }
    }
    
    public String getWikiPage(String strName, int iVersion) throws RemoteCallException
    {
        try {
            String strResult;
            if (iVersion == -1)
            {
                strResult = (String) m_client.call("wiki.getPageHTML", strName);
            }
            else
            {
                strResult = (String) m_client.call("wiki.getPageHTML", strName, iVersion);
            }
            
            return strResult;
        } catch (XMLRPCException ex) {
            throw new RemoteCallException(ex.getClass(), "wiki.getPageHTML", ex.getMessage());
        }
    }
}
