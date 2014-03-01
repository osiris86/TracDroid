/**
 *
 * @author Osiris
 */
package de.suwes.TracDroid;

import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.suwes.TracDroid.Communications.RemoteCall;
import de.suwes.TracDroid.Communications.RemoteCallException;
import de.suwes.TracDroid.Helpers.WikiViewClient;
import de.suwes.TracDroid.free.R;

/**
 *
 * @author Osiris
 */
public class WikiView extends SherlockFragment
{
    private View m_Context = null;
    private List<String> m_History = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        clearHistory();
        return inflater.inflate(R.layout.wiki, container, false);
    }    
    
    public void clearHistory()
    {
        m_History = new ArrayList<String>();
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {   
        m_Context = view;
        setHasOptionsMenu(true);
        
        
        Button btn = (Button) m_Context.findViewById(R.id.ViewWiki_ButtonBack);
        btn.setEnabled(false);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goBack();
            }
        });

        loadWikiPage();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
    {
        inflater.inflate(R.menu.wiki, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.select_instance)
        {
            ((MainActivity) getActivity()).onMenuSelectInstance(this);
            return true;
        }
        else if (item.getItemId() == R.id.preferences)
        {
            ((MainActivity) getActivity()).onMenuPreferences();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
    
    public RemoteCall getRemoteCall(Handler handler)
    {
        try {
            if (MainActivity.CURRENT_INSTANCE == null)
            {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(m_Context.getContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
                        getSherlockActivity().setProgressBarIndeterminateVisibility(false);
                    }
                });
                return null;
            }
            String strUrl = MainActivity.CURRENT_INSTANCE.getUrl();
            String strUsername = MainActivity.CURRENT_INSTANCE.getUsername();
            String strPassword = MainActivity.CURRENT_INSTANCE.getPassword();
            return RemoteCall.getInstance(strUrl, strUsername, strPassword);
        } catch (final RemoteCallException ex) {
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(m_Context.getContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
                    getSherlockActivity().setProgressBarIndeterminateVisibility(false);
                }
            });
            return null;
        } catch (final Exception ex) {
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(m_Context.getContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
                    getSherlockActivity().setProgressBarIndeterminateVisibility(false);
                }
            });
            return null;
        }
        
        
    }
    
    public void loadWikiPage()
    {
        loadWikiPage(null, null, false);
    }

    public void loadWikiPage(final String strPage, final String strUrl, final boolean bHistory) {
        getSherlockActivity().setProgressBarIndeterminateVisibility(true);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    
                    RemoteCall rc = WikiView.this.getRemoteCall(handler);
                    if (rc == null)
                    {
                        return;
                    }
                    
                    if (!bHistory)
                    {
                        if (strPage == null)
                        {
                            m_History.add("WikiStart");
                        }
                        else
                        {
                            m_History.add(strPage);
                        }
                    }
                    final String strHtml = rc.getWikiPage(strPage);
                    
                    handler.post(new Runnable() {
                        public void run() {
                            WebView wvWiki = (WebView) m_Context.findViewById(R.id.ViewWiki);
                            wvWiki.setWebViewClient(new WikiViewClient(WikiView.this));
                            WebSettings webSettings = wvWiki.getSettings();
                            webSettings.setDefaultFontSize(12);
                            webSettings.setJavaScriptEnabled(true);
                            wvWiki.loadData(strHtml, "text/html; charset=UTF-8", null);
                            wvWiki.loadData(strHtml, "text/html; charset=UTF-8", null);
                            if (getSherlockActivity() != null)
                            {
                                getSherlockActivity().setProgressBarIndeterminateVisibility(false);
                            }
                            Button btn = (Button) m_Context.findViewById(R.id.ViewWiki_ButtonBack);
                            if (m_History.size() <= 1)
                            {
                                btn.setEnabled(false);
                            }
                            else
                            {
                                btn.setEnabled(true);
                            }
                            TextView tv = (TextView) m_Context.findViewById(R.id.ViewWiki_CurrentPage);
                            String strText = getString(R.string.wikiview_current_page);
                            strText += " ";
                            if (strPage == null)
                            {
                                strText += "WikiStart";
                            }
                            else
                            {
                                strText += strPage;
                            }
                            tv.setText(strText);
                        }
                    });
                } catch (final RemoteCallException ex) {
                    handler.post(new Runnable() {
                        public void run() {
                            if (ex.getMessage().contains("404"))
                            {
                                try
                                {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, 
                                    Uri.parse(strUrl));
                                    startActivity(intent);
                                    m_History.remove(m_History.size()-1);
                                }
                                catch(ActivityNotFoundException ex)
                                {
                                    String strMsg = getString(R.string.error_noActivity);
                                    strMsg += " " + strUrl;
                                    Toast.makeText(m_Context.getContext(), strMsg, Toast.LENGTH_LONG).show();
                                }
                                return;
                            }
                            if (!ex.getMessage().contains("Socket closed") &&
                                !ex.getMessage().contains("Request aborted"))
                            {
                                Toast.makeText(m_Context.getContext(), R.string.error_loadWiki, Toast.LENGTH_LONG).show();
                            }
                            if (getSherlockActivity() != null)
                            {
                                getSherlockActivity().setProgressBarIndeterminateVisibility(false);
                            }
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }
    
    private void goBack() {
        String strUrl = m_History.get(m_History.size()-2);
        m_History.remove(m_History.get(m_History.size()-1));

        
        loadWikiPage(strUrl, null, true);
    }
}

