/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Window;

import de.suwes.TracDroid.Communications.RemoteCall;
import de.suwes.TracDroid.Communications.RemoteCallException;
import de.suwes.TracDroid.Helpers.ISummaryListener;
import de.suwes.TracDroid.Model.TicketAttribute;
import de.suwes.TracDroid.free.R;

/**
 *
 * @author Osiris
 */
public class Preferences extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener, ISummaryListener
{
    protected Method mLoadHeaders = null;
    protected Method mHasHeaders = null;
    private boolean m_bShowedError = false;
    protected static List<TicketAttribute> m_TicketAttributes = null;
    
    /**
    * Checks to see if using new v11+ way of handling PrefsFragments.
    * @return Returns false pre-v11, else checks to see if using headers.
    */
    public boolean isNewV11Prefs() {
        if (mHasHeaders!=null && mLoadHeaders!=null) {
            try {
                return (Boolean)mHasHeaders.invoke(this);
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
        return false;
    }
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        //onBuildHeaders() will be called during super.onCreate()
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        String strTitle = getString(R.string.title_preferences);
        strTitle += " - ";
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Preferences.this);
        strTitle += settings.getString("name", "");
        getSupportActionBar().setTitle(strTitle);
        
        try {
            mLoadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class );
            mHasHeaders = getClass().getMethod("hasHeaders");
        } catch (NoSuchMethodException e) {
        }
        super.onCreate(savedInstanceState);
        if (!isNewV11Prefs()) {
            addPreferencesFromResource(R.xml.preference_login);
            addPreferencesFromResource(R.xml.preference_fields);
            ListPreference topLeft = (ListPreference) findPreference("field_topleft");
            ListPreference topRight = (ListPreference) findPreference("field_topright");
            ListPreference bottomLeft = (ListPreference) findPreference("field_bottomleft");
            ListPreference bottomRight = (ListPreference) findPreference("field_bottomright");
            createFieldsPreference(topLeft, topRight, bottomLeft, bottomRight, getPreferenceScreen().getSharedPreferences(), this);
        }
        
        getSherlock().setProgressBarIndeterminateVisibility(false);
    }
    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> aTarget) {
        try {
            mLoadHeaders.invoke(this,new Object[]{R.xml.preference_headers,aTarget});
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }   
    }
    
    public void createFieldsPreference(
            final ListPreference topLeft, 
            final ListPreference topRight, 
            final ListPreference bottomLeft, 
            final ListPreference bottomRight, 
            final SharedPreferences sp,
            final ISummaryListener summaryListener)
    {
        topLeft.setEnabled(false);
        topRight.setEnabled(false);
        bottomLeft.setEnabled(false);
        bottomRight.setEnabled(false);
        getSherlock().setProgressBarIndeterminateVisibility(true);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Preferences.this);
                    String strUrl = settings.getString("url", "");
                    String strUsername = settings.getString("username", "");
                    String strPassword = settings.getString("password", "");
                    RemoteCall rc = RemoteCall.getInstance(strUrl, strUsername, strPassword);
                    
                    m_TicketAttributes = rc.getTicketFields();

                    final List<String> fields = new ArrayList<String>();
                    final List<String> values = new ArrayList<String>();
                    
                    for (TicketAttribute attr : m_TicketAttributes)
                    {
                        if (attr.getName().equals("summary") ||
                            attr.getName().equals("description"))
                        {
                            continue;
                        }
                        fields.add(attr.getLabel());
                        values.add(attr.getName());
                    }
                    
                    handler.post(new Runnable() {
                        public void run() {
                            topLeft.setEntries(fields.toArray(new String[fields.size()])); 
                            topLeft.setEntryValues(values.toArray(new String[values.size()]));
                            topRight.setEntries(fields.toArray(new String[fields.size()])); 
                            topRight.setEntryValues(values.toArray(new String[values.size()]));
                            bottomLeft.setEntries(fields.toArray(new String[fields.size()])); 
                            bottomLeft.setEntryValues(values.toArray(new String[values.size()]));
                            bottomRight.setEntries(fields.toArray(new String[fields.size()])); 
                            bottomRight.setEntryValues(values.toArray(new String[values.size()]));
                            
                            topLeft.setEnabled(true);
                            topRight.setEnabled(true);
                            bottomLeft.setEnabled(true);
                            bottomRight.setEnabled(true);
                            getSherlock().setProgressBarIndeterminateVisibility(false);
                            summaryListener.setSummaries(sp);
                        }
                    });
                } catch (final RemoteCallException ex) {
                    handler.post(new Runnable() {
                        public void run() {
                            if (!m_bShowedError)
                            {
                                Toast.makeText(Preferences.this, R.string.error_loadAvailableFields, Toast.LENGTH_LONG).show();
                                m_bShowedError = true;
                            }
                            getSherlock().setProgressBarIndeterminateVisibility(false);
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (key.equals("field_topleft") || 
            key.equals("field_topright") || 
            key.equals("field_bottomleft") ||
            key.equals("field_bottomright")) 
        {
            setSummaries(sp);
        }
        else
        {
            ListPreference topLeft = (ListPreference) findPreference("field_topleft");
            ListPreference topRight = (ListPreference) findPreference("field_topright");
            ListPreference bottomLeft = (ListPreference) findPreference("field_bottomleft");
            ListPreference bottomRight = (ListPreference) findPreference("field_bottomright");
            createFieldsPreference(topLeft, topRight, bottomLeft, bottomRight, getPreferenceScreen().getSharedPreferences(), this);
        }
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
        
        if (getPreferenceScreen() != null)
        {
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        if (getPreferenceScreen() != null)
        {
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

            setSummaries(sp);

            sp.registerOnSharedPreferenceChangeListener(this);
        }
    }
    
    public void setSummaries(SharedPreferences sp)
    {
        String strTopLeft = "";
        String strTopRight = "";
        String strBottomLeft = "";
        String strBottomRight = "";
        
        if (m_TicketAttributes == null)
                return;

        for (TicketAttribute attr : m_TicketAttributes)
        {
            if (attr.getName().equals(sp.getString("field_topleft", "type")))
            {
                strTopLeft = attr.getLabel();
            }
            if (attr.getName().equals(sp.getString("field_topright", "owner")))
            {
                strTopRight = attr.getLabel();
            }
            if (attr.getName().equals(sp.getString("field_bottomleft", "status")))
            {
                strBottomLeft = attr.getLabel();
            }
            if (attr.getName().equals(sp.getString("field_bottomright", "milestone")))
            {
                strBottomRight = attr.getLabel();
            }
        }

        ListPreference pref = (ListPreference) findPreference("field_topleft");
        pref.setSummary(strTopLeft);
        pref = (ListPreference) findPreference("field_topright");
        pref.setSummary(strTopRight);
        pref = (ListPreference) findPreference("field_bottomleft");
        pref.setSummary(strBottomLeft);
        pref = (ListPreference) findPreference("field_bottomright");
        pref.setSummary(strBottomRight);
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class LoginDetails extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_login);
        }
    }
    
    /**
     * This fragment shows the preferences for the first header.
     */
    public static class Fields extends PreferenceFragment implements OnSharedPreferenceChangeListener, ISummaryListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_fields);
        }
        
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState)
        {
            Preferences p = (Preferences) getActivity();
            
            ListPreference prefTopLeft = (ListPreference) findPreference("field_topleft");
            ListPreference prefTopRight = (ListPreference) findPreference("field_topright");
            ListPreference prefBottomLeft = (ListPreference) findPreference("field_bottomleft");
            ListPreference prefBottomRight = (ListPreference) findPreference("field_bottomright");
            
            p.createFieldsPreference(prefTopLeft, prefTopRight, prefBottomLeft, prefBottomRight, getPreferenceScreen().getSharedPreferences(), this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
            if (key.equals("field_topleft") || 
                key.equals("field_topright") || 
                key.equals("field_bottomleft") ||
                key.equals("field_bottomright")) 
            {
                setSummaries(sp);
            }
        }
        
        @Override
        public void onPause()
        {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume()
        {
            super.onResume();
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            
            setSummaries(sp);
            
            sp.registerOnSharedPreferenceChangeListener(this);
        }
        
        public void setSummaries(SharedPreferences sp)
        {
            String strTopLeft = "";
            String strTopRight = "";
            String strBottomLeft = "";
            String strBottomRight = "";
            
            if (m_TicketAttributes == null)
                return;
            
            for (TicketAttribute attr : m_TicketAttributes)
            {
                if (attr.getName().equals(sp.getString("field_topleft", "type")))
                {
                    strTopLeft = attr.getLabel();
                }
                if (attr.getName().equals(sp.getString("field_topright", "owner")))
                {
                    strTopRight = attr.getLabel();
                }
                if (attr.getName().equals(sp.getString("field_bottomleft", "status")))
                {
                    strBottomLeft = attr.getLabel();
                }
                if (attr.getName().equals(sp.getString("field_bottomright", "milestone")))
                {
                    strBottomRight = attr.getLabel();
                }
            }
            
            ListPreference pref = (ListPreference) findPreference("field_topleft");
            pref.setSummary(strTopLeft);
            pref = (ListPreference) findPreference("field_topright");
            pref.setSummary(strTopRight);
            pref = (ListPreference) findPreference("field_bottomleft");
            pref.setSummary(strBottomLeft);
            pref = (ListPreference) findPreference("field_bottomright");
            pref.setSummary(strBottomRight);
        }
    }
}
