package de.suwes.TracDroid.Communications;

/**
 *
 * @author Osiris
 */
public class RemoteCallException extends Exception {
    private Class m_thrownException;
    private String m_strRemoteMethod;
    private String m_strMessage;
    
    /**
     * Create a RemoteCallException instance
     * @param thrownException The exception that was initially raised
     * @param strMethod The method on which the exception occured
     * @param strMessage The message that was raised
     */
    public RemoteCallException(Class thrownException, String strMethod, String strMessage)
    {
        this.m_thrownException = thrownException;
        this.m_strRemoteMethod = strMethod;
        this.m_strMessage = strMessage;
    }
    
    /**
     * Returns the exception which was initially thrown
     * @return the class name
     */
    public String getInitalException()
    {
        return m_thrownException.getName();
    }
    
    /**
     * Returns the method on which the exception was thrown
     * @return the method
     */
    public String getRemoteMethod()
    {
        return m_strRemoteMethod;
    }
    
    /**
     * Returns the thrown message
     * @return the message
     */
    @Override
    public String getMessage()
    {
        return m_strMessage;
    }
}
