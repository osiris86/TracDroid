/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Database.Models;

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

/**
 *
 * @author Osiris
 */
public class Instance {
    private int m_iID;
    private String m_strName;
    private String m_strUrl;
    private String m_strUsername;
    private String m_strPassword;
    private boolean m_bDefault;
    private String m_strTopleft;
    private String m_strTopright;
    private String m_strBottomleft;
    private String m_strBottomright;
    private int m_iDefaultFilter;
    
    private static String algorithm = "DES";
    private static Key key = null;
    private static Cipher cipher = null;
    
    public Instance(int iID, String strName, String strUrl, String strUsername, boolean bPasswordEncrypted, String strPassword, boolean bDefault, String strTopleft, String strTopright, String strBottomleft, String strBottomright, int iDefaultFilter)
    {
        // TODO: Implement default values für Felder
        
        
        try {
            DESKeySpec keySpec = new DESKeySpec("asdfasdfaer5235dhj".getBytes("UTF8")); 
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            key = keyFactory.generateSecret(keySpec);
            cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            ex.printStackTrace();
        }
        
        m_iID = iID;
        m_strName = strName;
        m_strUrl = strUrl;
        m_strUsername = strUsername;
        m_strPassword = bPasswordEncrypted ? decrypt(strPassword) : strPassword;
        m_bDefault = bDefault;
        m_strTopleft = strTopleft;
        m_strTopright = strTopright;
        m_strBottomleft = strBottomleft;
        m_strBottomright = strBottomright;
        m_iDefaultFilter = iDefaultFilter;
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
     * @return the m_strUrl
     */
    public String getUrl() {
        return m_strUrl;
    }

    /**
     * @param m_strUrl the m_strUrl to set
     */
    public void setUrl(String m_strUrl) {
        this.m_strUrl = m_strUrl;
    }

    /**
     * @return the m_strUsername
     */
    public String getUsername() {
        return m_strUsername;
    }

    /**
     * @param m_strUsername the m_strUsername to set
     */
    public void setUsername(String m_strUsername) {
        this.m_strUsername = m_strUsername;
    }

    /**
     * @return the m_strPassword
     */
    public String getPassword() {
        return m_strPassword;
    }

    /**
     * @param m_strPassword the m_strPassword to set
     */
    public void setPassword(boolean bEncrypted, String m_strPassword) {
        this.m_strPassword = bEncrypted ? decrypt(m_strPassword) : m_strPassword;
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
    
    public String getEncryptedPassword()
    {
        return encrypt(m_strPassword);
    }
    
    public void setDefaultFilter(int iDefaultFilter) {
    	this.m_iDefaultFilter = iDefaultFilter;
    }
    
    public int getDefaultFilter() {
    	return m_iDefaultFilter;
    }
    
    private String encrypt(String str) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] inputBytes = str.getBytes("UTF8");
            String encrypedPwd = Base64.encodeToString(cipher.doFinal(inputBytes), Base64.DEFAULT);
            //encrypedPwd = encrypedPwd.substring(0, encrypedPwd.length() - 1);
            return encrypedPwd;
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return "";
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
            return "";
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
            return "";
        } catch (BadPaddingException ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    private String decrypt(String str) {
        try {
            //str += "=";
            byte[] encrypedPwdBytes = Base64.decode(str, Base64.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] recoveredBytes = 
                cipher.doFinal(encrypedPwdBytes);
            String recovered = 
                new String(recoveredBytes);
            return recovered;
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
            return "";
        } catch (BadPaddingException ex) {
            ex.printStackTrace();
            return "";
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * @return the m_strTopleft
     */
    public String getTopleft() {
        return m_strTopleft;
    }

    /**
     * @param m_strTopleft the m_strTopleft to set
     */
    public void setTopleft(String m_strTopleft) {
        this.m_strTopleft = m_strTopleft;
    }

    /**
     * @return the m_strTopright
     */
    public String getTopright() {
        return m_strTopright;
    }

    /**
     * @param m_strTopright the m_strTopright to set
     */
    public void setTopright(String m_strTopright) {
        this.m_strTopright = m_strTopright;
    }

    /**
     * @return the m_strBottomleft
     */
    public String getBottomleft() {
        return m_strBottomleft;
    }

    /**
     * @param m_strBottomleft the m_strBottomleft to set
     */
    public void setBottomleft(String m_strBottomleft) {
        this.m_strBottomleft = m_strBottomleft;
    }

    /**
     * @return the m_strBottomright
     */
    public String getBottomright() {
        return m_strBottomright;
    }

    /**
     * @param m_strBottomright the m_strBottomright to set
     */
    public void setBottomright(String m_strBottomright) {
        this.m_strBottomright = m_strBottomright;
    }
    
    @Override
    public String toString()
    {
        return this.m_strName;
    }
}
