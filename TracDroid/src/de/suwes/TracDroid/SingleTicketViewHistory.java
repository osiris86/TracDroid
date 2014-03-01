/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid;

import java.util.List;

import org.ocpsoft.pretty.time.PrettyTime;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import de.suwes.TracDroid.Model.TicketAttribute;
import de.suwes.TracDroid.Model.TicketChange;
import de.suwes.TracDroid.free.R;

/**
 *
 * @author Osiris
 */
public class SingleTicketViewHistory extends SherlockFragment 
{
    private View m_Context = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ticket_history, container, false);
    }    
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {   
        m_Context = view;
        
        loadChanges();
    }
    
    public void loadChanges()
    {
        LinearLayout ll = (LinearLayout) m_Context.findViewById(R.id.TicketHistory_Layout);
        
        SingleTicketView ticketView = (SingleTicketView) getActivity();
        
        List<TicketChange> changes = ticketView.getChanges();
        String strComment = "";
        
        for (TicketChange change : changes)
        {
            if (change.getField().equals("comment"))
            {
                if (strComment.length() > 0)
                {
                    // Den Kommentar zur letzten Änderung drunter setzen, falls vorhanden
                    TextView tvComment = new TextView(m_Context.getContext());
                    tvComment.setPadding(10, 0, 10, 0);
                    tvComment.setText(strComment);
                    ll.addView(tvComment);
                }
                
                if (ll.getChildCount() > 0)
                {
                    TextView tvSep = new TextView(m_Context.getContext());
                    tvSep.setPadding(10, 0, 10, 0);
                    tvSep.setText(" ");
                    ll.addView(tvSep);
                }
                
                // Nächster Änderungssatz
                strComment = change.getNewValue();
                TextView tv = new TextView(m_Context.getContext());
                tv.setPadding(10, 0, 10, 0);
                tv.setBackgroundResource(R.color.history_seperator);
                
                String strText = getString(R.string.label_history_seperator_1);
                strText += " ";
                strText += new PrettyTime().format(change.getTime());
                strText += " ";
                strText += getString(R.string.label_history_seperator_2);
                strText += " ";
                strText += change.getAuthor();
                tv.setText(strText);

                ll.addView(tv);
            }
            else
            {
                TextView tvChange = new TextView(m_Context.getContext());
                tvChange.setPadding(10, 0, 10, 0);
                String strText = "• <b>";
                
                strText += getCleanName(change.getField());
                strText += "</b>";
                if (change.getOldValue().length() > 0)
                {
                    strText += " ";
                    strText += getString(R.string.label_history_change_from);
                    strText += " <b>";
                    strText += change.getOldValue();
                    strText += "</b> ";
                }
                else
                {
                    strText += " ";
                    strText += getString(R.string.label_history_change_set);
                    strText += " ";
                }
                
                strText += " ";
                if (change.getOldValue().length() > 0)
                {
                    strText += getString(R.string.label_history_change_from_to);
                }
                else
                {
                    strText += getString(R.string.label_history_change_set_to);
                }
                strText += " <b>";
                strText += change.getNewValue();
                strText += "</b>";
                
                strText += " ";
                if (change.getOldValue().length() > 0)
                {
                    strText += getString(R.string.label_history_change_from_changed);
                }
                else
                {
                    strText += getString(R.string.label_history_change_set_changed);
                }
                tvChange.setText(Html.fromHtml(strText));

                ll.addView(tvChange);
            }
        }
        
        if (strComment.length() > 0)
        {
            // Den Kommentar zur letzten Änderung drunter setzen, falls vorhanden
            TextView tvComment = new TextView(m_Context.getContext());
            tvComment.setPadding(10, 0, 10, 0);
            tvComment.setText(strComment);
            ll.addView(tvComment);
        }
    }
    
    private String getCleanName(String dirtName)
    {
        SingleTicketView ticketView = (SingleTicketView) getActivity();
        List<TicketAttribute> attributes = ticketView.getAttributes();
        
        for (TicketAttribute attribute : attributes)
        {
            if (attribute.getName().equals(dirtName))
            {
                return attribute.getLabel();
            }
        }
        
        return dirtName;
    }
}
