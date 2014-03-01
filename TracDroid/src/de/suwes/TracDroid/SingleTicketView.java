/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

import de.suwes.TracDroid.Communications.RemoteCall;
import de.suwes.TracDroid.Communications.RemoteCallException;
import de.suwes.TracDroid.Helpers.TabListener;
import de.suwes.TracDroid.Model.Ticket;
import de.suwes.TracDroid.Model.TicketAttribute;
import de.suwes.TracDroid.Model.TicketChange;
import de.suwes.TracDroid.free.R;

/**
 *
 * @author Osiris
 */
public class SingleTicketView extends SherlockFragmentActivity
{
    private List<TicketAttribute> m_Attributes = null;
    private List<TicketChange> m_Changes = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        Ticket ticket = null;
        
        if (getIntent().getExtras() != null)
        {
            ticket = (Ticket) getIntent().getExtras().getSerializable("Ticket");
        }
        
        loadActions(ticket);
    }
    
    private void loadActions(final Ticket ticket)
    {
        getSherlock().setProgressBarIndeterminateVisibility(true);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String strUrl = MainActivity.CURRENT_INSTANCE.getUrl();
                    String strUsername = MainActivity.CURRENT_INSTANCE.getUsername();
                    String strPassword = MainActivity.CURRENT_INSTANCE.getPassword();
                    
                    RemoteCall rc = RemoteCall.getInstance(strUrl, strUsername, strPassword);
                    m_Attributes = rc.getTicketFields();
                    if (ticket != null)
                    {
                        m_Changes = rc.getTicketHistory(ticket.getID());
                    }

                    handler.post(new Runnable() {
                        public void run() {
                            
                            ActionBar actionBar = getSupportActionBar();
                            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

                            // Aktuelles Tab
                            ActionBar.Tab tabTicket = actionBar.newTab()
                                .setText(R.string.title_ticket)
                                .setTabListener(new TabListener<SingleTicketViewTicket>(SingleTicketView.this, "ticket", SingleTicketViewTicket.class 
                                        ));
                            actionBar.addTab(tabTicket);

                            // Mensaorte Tab
                            if (SingleTicketView.this.m_Changes != null)
                            {
                                ActionBar.Tab tabHistory = actionBar.newTab()
                                    .setText(R.string.title_history)
                                    .setTabListener(new TabListener<SingleTicketViewHistory>(SingleTicketView.this, "history", SingleTicketViewHistory.class
                                            ));
                                actionBar.addTab(tabHistory);
                            }
                              
                            if (ticket != null)
                            {
                                String str = SingleTicketView.this.getString(R.string.title_ticket_edit);
                                str += " - #";
                                str += ticket.getID();
                                SingleTicketView.this.getSupportActionBar().setTitle(str);
                            }
                            else
                            {
                                SingleTicketView.this.getSupportActionBar().setTitle(R.string.title_ticket_new);
                            }
                            
                            getSherlock().setProgressBarIndeterminateVisibility(false);
                        }
                    });
                } catch (final RemoteCallException ex) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(SingleTicketView.this, R.string.error_loadTicket, Toast.LENGTH_LONG).show();
                            getSherlock().setProgressBarIndeterminateVisibility(false);
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * @return the m_Attributes
     */
    public List<TicketAttribute> getAttributes() {
        return m_Attributes;
    }
    
    /**
     * @return the m_Changes
     */
    public List<TicketChange> getChanges() {
        return m_Changes;
    }
}
