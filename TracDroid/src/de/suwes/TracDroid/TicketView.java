/**
 *
 * @author Osiris
 */
package de.suwes.TracDroid;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.suwes.TracDroid.Adapters.TicketAdapter;
import de.suwes.TracDroid.Communications.RemoteCall;
import de.suwes.TracDroid.Communications.RemoteCallException;
import de.suwes.TracDroid.Database.FilterProvider;
import de.suwes.TracDroid.Database.InstanceProvider;
import de.suwes.TracDroid.Helpers.Comparators.ComponentComparator;
import de.suwes.TracDroid.Helpers.Comparators.MilestoneComparator;
import de.suwes.TracDroid.Helpers.Comparators.StatusComparator;
import de.suwes.TracDroid.Model.Ticket;
import de.suwes.TracDroid.free.R;

/**
 *
 * @author Osiris
 */
public class TicketView extends SherlockFragment implements AdapterView.OnItemClickListener
{
    private View m_Context = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main, container, false);
    }    
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {   
        m_Context = view;
        setHasOptionsMenu(true);
        

        loadTickets();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
    {
        inflater.inflate(R.menu.tickets, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.select_instance)
        {
            ((MainActivity) getActivity()).onMenuSelectInstance(this);
            return true;
        }
        else if (item.getItemId() == R.id.ticket_add)
        {
            onMenuAdd();
            return true;
        }
        else if (item.getItemId() == R.id.filter)
        {
            onMenuFilter();
            return true;
        }
        else if (item.getItemId() == R.id.refresh)
        {
            onMenuRefresh();
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
    
    private void onMenuAdd()
    {
        Intent i = new Intent(m_Context.getContext(), SingleTicketView.class);
        startActivityForResult(i, 0);
    }
    
    private void onMenuFilter()
    {
        Intent i = new Intent(m_Context.getContext(), FilterView.class);
        startActivityForResult(i, 0);
    }
    
    private void onMenuRefresh()
    {
        loadTickets();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data != null &&
            data.getExtras() != null &&
            data.getExtras().getString("Filter") != null)
        {
            MainActivity.CURRENT_FILTER = data.getExtras().getString("Filter");
            MainActivity.CURRENT_GROUP = data.getExtras().getInt("GroupBy");
            getSherlockActivity().getSupportActionBar().setTitle(data.getExtras().getString("FilterName"));
        }
        
        // Aktuelle Instanz laden
        Cursor c = m_Context.getContext().getContentResolver().query(
                InstanceProvider.getContentURI(), 
                InstanceProvider.COLUMNS, 
                "defaultinstance = ?", 
                new String[] { "1" }, 
                null);
        try {
            MainActivity.CURRENT_INSTANCE = InstanceProvider.getListFromCursor(c).get(0);
        } catch (ParseException ex) {
            Toast.makeText(m_Context.getContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
            getSherlockActivity().setProgressBarIndeterminateVisibility(false);
        }
        
        loadTickets();
    }
    
    public void loadTickets()
    {
        getSherlockActivity().setProgressBarIndeterminateVisibility(true);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    if ((MainActivity.CURRENT_FILTER == null || MainActivity.CURRENT_FILTER.length() == 0) && MainActivity.CURRENT_INSTANCE != null)
                    {
                        Cursor c = m_Context.getContext().getContentResolver().query(
                                FilterProvider.getContentURI(), 
                                FilterProvider.COLUMNS, 
                                "id = ?", 
                                new String[] { Integer.toString(MainActivity.CURRENT_INSTANCE.getDefaultFilter()) }, 
                                null);
                        
                        final de.suwes.TracDroid.Database.Models.Filter f = FilterProvider.getListFromCursor(c).get(0);
                        handler.post(new Runnable() {
                            public void run() {
                                getSherlockActivity().getSupportActionBar().setTitle(f.getName());
                            }
                        });
                        MainActivity.CURRENT_FILTER = f.getFilterString(m_Context.getContext());
                        MainActivity.CURRENT_GROUP = f.getGroupBy();
                    }
                    
                    
                    RemoteCall rc = TicketView.this.getRemoteCall(handler);
                    if (rc == null)
                    {
                        return;
                    }
                    
                    if (MainActivity.TICKET_ATTRIBUTES == null)
                    {
                        MainActivity.TICKET_ATTRIBUTES = rc.getTicketFields();
                    }
                    
                    final List<Ticket> tickets = rc.getTickets(MainActivity.CURRENT_FILTER);
                    
                    if (MainActivity.CURRENT_GROUP == de.suwes.TracDroid.Database.Models.Filter.GROUPBY_MILESTONE)
                    {
                        Collections.sort(tickets, new MilestoneComparator());
                    }
                    else if (MainActivity.CURRENT_GROUP == de.suwes.TracDroid.Database.Models.Filter.GROUPBY_COMPONENT)
                    {
                        Collections.sort(tickets, new ComponentComparator());
                    }
                    else if (MainActivity.CURRENT_GROUP == de.suwes.TracDroid.Database.Models.Filter.GROUPBY_STATUS)
                    {
                        Collections.sort(tickets, new StatusComparator());
                    }
                    
                    handler.post(new Runnable() {
                        public void run() {
                            addTicketsToList(tickets);

                            if (tickets.isEmpty())
                            {
                                Toast.makeText(m_Context.getContext(), m_Context.getContext().getString(R.string.error_noTickets), Toast.LENGTH_LONG).show();
                            }
                            if (getSherlockActivity() != null)
                            {
                                getSherlockActivity().setProgressBarIndeterminateVisibility(false);
                            }
                        }
                    });
                } catch (ParseException ex) {
                    handler.post(new Runnable() {
                        public void run() {                            
                            Toast.makeText(m_Context.getContext(), R.string.error_loadFilter, Toast.LENGTH_LONG).show();
                            getSherlockActivity().setProgressBarIndeterminateVisibility(false);
                        }
                    });
                } catch (final RemoteCallException ex) {
                    handler.post(new Runnable() {
                        public void run() {
                            if (!ex.getMessage().contains("Socket closed") &&
                                !ex.getMessage().contains("Request aborted"))
                            {
                                Toast.makeText(m_Context.getContext(), R.string.error_loadTickets, Toast.LENGTH_LONG).show();
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
    
    public int getFirstVisiblePosition()
    {
        ListView lv = (ListView) m_Context.findViewById(R.id.ListViewTickets);
        return lv.getFirstVisiblePosition();
    }

    public void onItemClick(AdapterView<?> av, View view, int iPosition, long l) 
    {
        Ticket clicked = (Ticket)av.getItemAtPosition(iPosition);
        
        Intent i = new Intent(m_Context.getContext(), SingleTicketView.class);
        i.putExtra("Ticket", clicked);
        startActivityForResult(i, 0);
    }
    
    private void addTicketsToList(List<Ticket> tickets) 
    {
        ListView lvTickets = (ListView) m_Context.findViewById(R.id.ListViewTickets);

        lvTickets.setOnItemClickListener(this);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(m_Context.getContext());
        String strTopLeft = settings.getString("field_topleft", "type");
        String strTopRight = settings.getString("field_topright", "owner");
        String strBottomLeft = settings.getString("field_bottomleft", "status");
        String strBottomRight = settings.getString("field_bottomright", "milestone");
        TicketAdapter la = new TicketAdapter(m_Context.getContext(), R.layout.row_ticket, tickets, MainActivity.TICKET_ATTRIBUTES, strTopLeft, strTopRight, strBottomLeft, strBottomRight, MainActivity.CURRENT_GROUP);
        lvTickets.setAdapter(la);
    }
}

