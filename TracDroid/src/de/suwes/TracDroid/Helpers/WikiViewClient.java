/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Helpers;

import java.io.UnsupportedEncodingException;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import de.suwes.TracDroid.WikiView;

/**
 *
 * @author Osiris
 */
public class WikiViewClient extends WebViewClient {
    WikiView m_baseClass = null;
    
    public WikiViewClient(WikiView wv)
    {
        m_baseClass = wv;
    }
    
    @Override
    public boolean shouldOverrideUrlLoading (WebView view, String url)
    {
        try {
            String strName = url.substring(url.lastIndexOf("/") + 1);
            strName = java.net.URLDecoder.decode(strName, "UTF-8");
            m_baseClass.loadWikiPage(strName, url, false);
            return true;
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
