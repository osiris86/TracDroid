/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid;

import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import de.suwes.TracDroid.Adapters.TicketActionAdapter;
import de.suwes.TracDroid.Communications.RemoteCall;
import de.suwes.TracDroid.Communications.RemoteCallException;
import de.suwes.TracDroid.Model.Ticket;
import de.suwes.TracDroid.Model.TicketAction;
import de.suwes.TracDroid.Model.TicketAttribute;
import de.suwes.TracDroid.Model.TicketInputField;
import de.suwes.TracDroid.free.R;

/**
 *
 * @author Osiris
 */
public class SingleTicketViewTicket extends SherlockFragment 
{
    private View m_Context = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ticket, container, false);
    }    
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {   
        m_Context = view;
        Ticket ticket = null;
        if (this.getActivity().getIntent().getExtras() != null)
        {
            ticket = (Ticket) this.getActivity().getIntent().getExtras().getSerializable("Ticket");
        }
        
        loadActions(ticket);  
    }
    
    
    
    private void loadActions(final Ticket ticket)
    {
        if (ticket != null)
        {
            String str = SingleTicketViewTicket.this.getString(R.string.title_ticket_edit);
            str += " - #";
            str += ticket.getID();
            SingleTicketViewTicket.this.getSherlockActivity().getSupportActionBar().setTitle(str);
        }
        else
        {
            SingleTicketViewTicket.this.getSherlockActivity().getSupportActionBar().setTitle(R.string.title_ticket_new);
        }
        
        SingleTicketView ticketView = (SingleTicketView) getActivity();
        List<TicketAttribute> attributes = ticketView.getAttributes();
        
        LinearLayout ll = (LinearLayout) m_Context.findViewById(R.id.Ticket_Layout);
        
        for (TicketAttribute attribute : attributes)
        {
            if (attribute.getName().equals("status") ||
                attribute.getName().equals("resolution") ||
                attribute.getName().equals("time") ||
                attribute.getName().equals("changetime") ||
                attribute.getName().equals("reporter"))
            {
                continue;
            }

            if (attribute.getName().equals("owner") && ticket != null)
            {
                continue;                                        
            }

            LinearLayout row = new LinearLayout(m_Context.getContext());
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setBaselineAligned(false);
            row.setGravity(Gravity.CENTER_VERTICAL);
            //row.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvLabel = new TextView(m_Context.getContext());
            tvLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f));
            tvLabel.setText(attribute.getLabel());

            TextView tvName = new TextView(m_Context.getContext());
            tvName.setVisibility(View.GONE);
            tvName.setText(attribute.getName());

            if (attribute.getType() == TicketAttribute.TYPE_CHECKBOX)
            {
                LinearLayout llWrapper = new LinearLayout(m_Context.getContext());
                llWrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                llWrapper.setGravity(Gravity.LEFT);

                CheckBox chb = new CheckBox(m_Context.getContext());
                chb.setGravity(Gravity.LEFT);

                if (ticket != null)
                {
                    boolean bChecked = false;
                    if (ticket.getAttribute(attribute.getName()).equals("1"))
                    {
                        bChecked = true;
                    }
                    chb.setChecked(bChecked);
                }

                row.addView(tvLabel);
                row.addView(tvName);
                llWrapper.addView(chb);
                row.addView(llWrapper);
            }
            else if (attribute.getType() == TicketAttribute.TYPE_RADIO)
            {
                Spinner spinner = new Spinner(m_Context.getContext());
                spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(m_Context.getContext(), android.R.layout.simple_spinner_item);
                for (String s : attribute.getOptions())
                {
                    adapter.add(s);
                }
                spinner.setAdapter(adapter);

                int iPosition = 0;

                for (String str : attribute.getOptions())
                {

                    if (ticket != null)
                    {
                        if (ticket.getAttribute(attribute.getName()).equals(str))
                        {
                            break;
                        }
                    }
                    else
                    {
                        if (str.equals(attribute.getValue()))
                        {
                            break;
                        }
                    }
                    iPosition++;
                }

                if (iPosition < adapter.getCount())
                {
                    spinner.setSelection(iPosition);
                }

                row.addView(tvLabel);
                row.addView(tvName);
                row.addView(spinner);
            }
            else if (attribute.getType() == TicketAttribute.TYPE_SELECT)
            {
                Spinner spinner = new Spinner(m_Context.getContext());
                spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(m_Context.getContext(), android.R.layout.simple_spinner_item);
                if (!attribute.getName().equals("type") && !attribute.getName().equals("priority"))
                {
                    adapter.add("");
                }
                for (String s : attribute.getOptions())
                {
                    adapter.add(s);
                }
                spinner.setAdapter(adapter);

                int iPosition = 0;

                for (String str : attribute.getOptions())
                {
                    if (ticket != null)
                    {
                        if (ticket.getAttribute(attribute.getName()).equals(str))
                        {
                            break;
                        }
                    }
                    else
                    {
                        if (str.equals(attribute.getValue()))
                        {
                            break;
                        }
                    }
                    iPosition++;
                }

                if (iPosition < adapter.getCount())
                {
                    spinner.setSelection(iPosition);
                }

                row.addView(tvLabel);
                row.addView(tvName);
                row.addView(spinner);
            }
            else if (attribute.getType() == TicketAttribute.TYPE_TEXT)
            {
                EditText edit = new EditText(m_Context.getContext());
                edit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));

                if (ticket != null)
                {
                    edit.setText(ticket.getAttribute(attribute.getName()));
                }
                else
                {
                    if (attribute.getName().equals("reporter"))
                    {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(m_Context.getContext());
                        String strUsername = settings.getString("username", "");
                        edit.setText(strUsername);
                    }
                    else
                    {
                        edit.setText(attribute.getValue());
                    }
                }


                row.addView(tvLabel);
                row.addView(tvName);
                row.addView(edit);
            }
            else if (attribute.getType() == TicketAttribute.TYPE_TEXTAREA)
            {
                LinearLayout llVertical = new LinearLayout(m_Context.getContext());
                llVertical.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                llVertical.setOrientation(LinearLayout.VERTICAL);

                EditText edit = new EditText(m_Context.getContext());
                edit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
                edit.setMinLines(5);
                edit.setMaxLines(5);
                if (ticket != null)
                {
                    edit.setText(ticket.getAttribute(attribute.getName()));
                }
                else
                {
                    edit.setText(attribute.getValue());
                }
                edit.setGravity(Gravity.TOP);

                llVertical.addView(tvLabel);
                llVertical.addView(tvName);
                llVertical.addView(edit);

                row.addView(llVertical);
            }


            ll.addView(row);
        }
                            
        if (ticket != null)
        {
            // Überschrift
            TextView tv = new TextView(m_Context.getContext());
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            tv.setText(R.string.ticketview_change);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0, 10, 0, 0);
            ll.addView(tv);

            LinearLayout row = new LinearLayout(m_Context.getContext());
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout llVertical = new LinearLayout(m_Context.getContext());
            llVertical.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            llVertical.setOrientation(LinearLayout.VERTICAL);

            TextView tvLabel = new TextView(m_Context.getContext());
            tvLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f));
            tvLabel.setText(R.string.label_comment);

            EditText edit = new EditText(m_Context.getContext());
            edit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
            edit.setMinLines(5);
            edit.setMaxLines(5);
            edit.setGravity(Gravity.TOP);

            llVertical.addView(tvLabel);
            llVertical.addView(edit);

            row.addView(llVertical);
            ll.addView(row);

            final LinearLayout row2 = new LinearLayout(m_Context.getContext());
            row2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row2.setOrientation(LinearLayout.HORIZONTAL);

            Spinner spinner = new Spinner(m_Context.getContext());
            spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f));
            TicketActionAdapter adapter = new TicketActionAdapter(m_Context.getContext(), android.R.layout.simple_spinner_item, ticket.getActions());
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                    SingleTicketViewTicket.this.onActionChanged(row2, av, i, ticket);
                }

                public void onNothingSelected(AdapterView<?> av) {
                    SingleTicketViewTicket.this.onActionChanged(row2, av, -1, ticket);
                }
            });
            row2.addView(spinner);

            TextView tvSecond = new TextView(m_Context.getContext());
            tvSecond.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
            tvSecond.setText("");
            row2.addView(tvSecond);

            ll.addView(row2);
        }

        Button btn = new Button(m_Context.getContext());
        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (ticket != null)
        {
            btn.setText(R.string.btn_ticket_change);
        }
        else
        {
            btn.setText(R.string.btn_ticket_new);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBtnSave(ticket);
            }
        });

        ll.addView(btn);

        getSherlockActivity().setProgressBarIndeterminateVisibility(false);
    }
    
    private void onActionChanged(LinearLayout row, AdapterView<?> av, int iPosition, Ticket ticket)
    {
        if (iPosition == -1)
        {
            System.out.println("Nothing selected");
            return;
        }
        
        TicketAction action = (TicketAction) av.getItemAtPosition(iPosition);
        
        
        row.removeViewAt(1);
        
        LinearLayout llRight = new LinearLayout(m_Context.getContext());
        llRight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f));
        llRight.setOrientation(LinearLayout.VERTICAL);
        llRight.setGravity(Gravity.CENTER_VERTICAL);

        if (action.getInputFields().size() > 0)
        {
            for (TicketInputField input : action.getInputFields())
            {
                if (input.getOptions().size() > 0)
                {
                    Spinner sp = new Spinner(m_Context.getContext());
                    sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(m_Context.getContext(), android.R.layout.simple_spinner_item);
                    for (String s : input.getOptions())
                    {
                        adapter.add(s);
                    }
                    sp.setAdapter(adapter);
                    
                    iPosition = 0;

                    for (String str : input.getOptions())
                    {
                        if (str.equals(input.getValue()))
                        {
                            break;
                        }
                        iPosition++;
                    }

                    if (iPosition < adapter.getCount())
                    {
                        sp.setSelection(iPosition);
                    }
                    
                    llRight.addView(sp);
                }
                else
                {
                    EditText et = new EditText(m_Context.getContext());
                    et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    et.setGravity(Gravity.CENTER_VERTICAL);
                    et.setText(input.getValue());
                    llRight.addView(et);
                }
            }
        }
        else
        {
            TextView tv = new TextView(m_Context.getContext());
            tv.setText(action.getHint());
            tv.setGravity(Gravity.CENTER_VERTICAL);
            llRight.addView(tv);
        }

        row.addView(llRight);
    }
    
    private void onBtnSave(Ticket changeTicket)
    {
        // Save
        LinearLayout ll = (LinearLayout) m_Context.findViewById(R.id.Ticket_Layout);
        Ticket ticket;
        if (changeTicket != null)
        {
            ticket = changeTicket;
        }
        else
        {
            ticket = new Ticket();
        }
        
        boolean bTicketChange = false;
        TicketAction action = null;
        String strComment = "";
        
        for (int i = 0 ; i < ll.getChildCount() - 1 ; i++)
        {
            if (ll.getChildAt(i) instanceof TextView)
            {
                // Ticket ändern teil
                bTicketChange = true;
                continue;
            }
            
            if (bTicketChange)
            {
                LinearLayout llChange = (LinearLayout) ll.getChildAt(i);
                
                if (llChange.getChildAt(0) instanceof LinearLayout)
                {
                    // Kommentar
                    LinearLayout llComment = (LinearLayout) llChange.getChildAt(0);
                    EditText etComment = (EditText) llComment.getChildAt(1);
                    strComment = etComment.getText().toString();
                    continue;
                }
                
                Spinner spAction = (Spinner) llChange.getChildAt(0);
                action = (TicketAction) spAction.getSelectedItem();
                
                LinearLayout llRight = (LinearLayout) llChange.getChildAt(1);
                
                int iCount = 0;
                for (TicketInputField field : action.getInputFields())
                {
                    if (field.getOptions().size() > 0)
                    {
                        Spinner sp = (Spinner) llRight.getChildAt(iCount);
                        field.setValue((String)sp.getSelectedItem());
                    }
                    else
                    {
                        EditText et = (EditText) llRight.getChildAt(iCount);
                        field.setValue(et.getText().toString());
                    }
                    
                    iCount++;
                }
                
                break;
            }
            
            LinearLayout llchild = (LinearLayout) ll.getChildAt(i);
            
            if (llchild.getChildCount() == 1)
            {
                llchild = (LinearLayout) ((ViewGroup) llchild).getChildAt(0);
            }
            
            TextView viewName = (TextView) llchild.getChildAt(1);
            String name = viewName.getText().toString();
            
            View viewValue = llchild.getChildAt(2);
            String value = "";
            
            if (viewValue instanceof EditText)
            {
                value = ((EditText) viewValue).getText().toString();
            }
            else if (viewValue instanceof Spinner)
            {
                value = ((Spinner) viewValue).getSelectedItem().toString();
            }
            else if (viewValue instanceof LinearLayout)
            {
                LinearLayout llChb = (LinearLayout) viewValue;
                CheckBox chb = (CheckBox) llChb.getChildAt(0);
                if (chb.isChecked())
                {
                    value = "1";
                }
                else
                {
                    value = "0";
                }
            }
            
            ticket.setAttribute(name, value);
        }
        
        if (bTicketChange)
        {
            saveTicket(ticket, strComment, action);
        }
        else
        {
            saveTicket(ticket);
        }
    }
    
    private void saveTicket(Ticket ticket)
    {
        saveTicket(ticket, "", null);
    }
    
    private void saveTicket(final Ticket ticket, final String strComment, final TicketAction action) 
    {
        getSherlockActivity().setProgressBarIndeterminateVisibility(true);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String strUrl = MainActivity.CURRENT_INSTANCE.getUrl();
                    String strUsername = MainActivity.CURRENT_INSTANCE.getUsername();
                    String strPassword = MainActivity.CURRENT_INSTANCE.getPassword();
                    RemoteCall rc = RemoteCall.getInstance(strUrl, strUsername, strPassword);
                    
                    if (ticket.getID() != -1)
                    {
                        rc.updateTicket(ticket, strComment, action);
                    }
                    else
                    {
                        ticket.setAttribute("reporter", strUsername);
                        rc.createTicket(ticket);
                    }

                    handler.post(new Runnable() {
                        public void run() {
                            getSherlockActivity().setProgressBarIndeterminateVisibility(false);
                            getSherlockActivity().finish();
                        }
                    });
                } catch (final RemoteCallException ex) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(m_Context.getContext(), R.string.error_saveTicket, Toast.LENGTH_LONG).show();
                            getSherlockActivity().setProgressBarIndeterminateVisibility(false);
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }
}
