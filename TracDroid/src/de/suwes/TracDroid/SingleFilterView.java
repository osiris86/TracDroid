/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import de.suwes.TracDroid.Adapters.InstancesAdapter;
import de.suwes.TracDroid.Adapters.TicketAttributeAdapter;
import de.suwes.TracDroid.Communications.RemoteCall;
import de.suwes.TracDroid.Communications.RemoteCallException;
import de.suwes.TracDroid.Database.FilterProvider;
import de.suwes.TracDroid.Database.FilterfieldsProvider;
import de.suwes.TracDroid.Database.InstanceProvider;
import de.suwes.TracDroid.Database.Models.Filter;
import de.suwes.TracDroid.Database.Models.Filterfield;
import de.suwes.TracDroid.Database.Models.Instance;
import de.suwes.TracDroid.Model.TicketAttribute;
import de.suwes.TracDroid.free.R;

/**
 *
 * @author Osiris
 */
public class SingleFilterView extends SherlockActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        this.setContentView(R.layout.filter);
        
        try {
            Spinner spInstances = (Spinner) findViewById(R.id.Filter_Instance);
            Cursor cursor = getContentResolver().query(
                    InstanceProvider.getContentURI(),
                    InstanceProvider.COLUMNS,
                    null,
                    null,
                    null);

            List<Instance> instanceList = InstanceProvider.getListFromCursor(cursor);
            instanceList.add(0, new Instance(0, getString(R.string.label_all_instances), "", "", false, "", false, "", "", "", "", 0));
            
            InstancesAdapter adapter = new InstancesAdapter(this, android.R.layout.simple_spinner_item, instanceList);
            spInstances.setAdapter(adapter);
        } catch (ParseException ex) {
            Toast.makeText(this, R.string.error_loadInstances, Toast.LENGTH_LONG).show();
        }
        
        Spinner spGroupBy = (Spinner) findViewById(R.id.Filter_GroupBy);
        List<String> strGroupBys = new ArrayList<String>();
        
        String[] strArrGroupBys = getResources().getStringArray(R.array.group_bys);
        strGroupBys.addAll(Arrays.asList(strArrGroupBys));
        ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strGroupBys);
        spGroupBy.setAdapter(arrAdapter);
        
        int iFilterID = this.getIntent().getExtras().getInt("FilterID");
        if (iFilterID == -1)
        {
            this.getSupportActionBar().setTitle(R.string.title_filter_new);
            addRow(true);
        }
        else
        {
            loadFilter(iFilterID);
        }
    }
    
    private void addRow()
    {
        addRow(false);
    }
    
    private void addRow(final boolean bInit)
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
                    final List<TicketAttribute> attributes = rc.getTicketFields();
                    
                    handler.post(new Runnable() {
                        public void run() {
                            LinearLayout row = new LinearLayout(SingleFilterView.this);
                            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                            
                            // Feld
                            final Spinner spinner = new Spinner(SingleFilterView.this);
                            spinner.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.35f));
                            TicketAttributeAdapter adapter = new TicketAttributeAdapter(SingleFilterView.this, android.R.layout.simple_spinner_item, attributes);
                            spinner.setAdapter(adapter);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                                    SingleFilterView.this.onFieldChanged(spinner, av, i);
                                }
                                public void onNothingSelected(AdapterView<?> av) {
                                    SingleFilterView.this.onFieldChanged(spinner, av, -1);
                                }
                            });
                            
                            int iPosition = 0;
                            if (bInit)
                            {
                                for (TicketAttribute attr : attributes)
                                {
                                    if (attr.getName().equals("status"))
                                    {
                                        break;
                                    }
                                    iPosition++;
                                }
                                spinner.setSelection(iPosition);
                            }
                            
                            row.addView(spinner);
                            
                            // Vergleichsoperater
                            Spinner vergleichsSpinner = new Spinner(SingleFilterView.this);
                            vergleichsSpinner.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.15f));
                            ArrayAdapter<String> strings = new ArrayAdapter<String>(SingleFilterView.this, android.R.layout.simple_spinner_item);
                            strings.add(SingleFilterView.this.getString(R.string.operator_equals));
                            strings.add(SingleFilterView.this.getString(R.string.operator_notequal));
                            strings.add(SingleFilterView.this.getString(R.string.operator_greater));
                            strings.add(SingleFilterView.this.getString(R.string.operator_greaterequal));
                            strings.add(SingleFilterView.this.getString(R.string.operator_smaller));
                            strings.add(SingleFilterView.this.getString(R.string.operator_smallerequal));
                            vergleichsSpinner.setAdapter(strings);
                            row.addView(vergleichsSpinner);
                            

                            LinearLayout ll = (LinearLayout) findViewById(R.id.Filter_Layout);
                            ll.addView(row);
                            
                            // Aus allen Reihen den Button durch ein TextView ersetzen
                            for (int i=3 ; i < ll.getChildCount(); i++)
                            {
                                LinearLayout llRow = (LinearLayout) ll.getChildAt(i);
                                
                                if (llRow.getChildCount() >= 4)
                                {
                                    llRow.removeViewAt(3);
                                }
                                
                                TextView tvAnd = new TextView(SingleFilterView.this);
                                tvAnd.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.15f));
                                tvAnd.setGravity(Gravity.CENTER);
                                tvAnd.setText(SingleFilterView.this.getString(R.string.label_and));
                                llRow.addView(tvAnd);
                            }   
                            
                            getSherlock().setProgressBarIndeterminateVisibility(false);
                        }
                    });
                } catch (final RemoteCallException ex) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(SingleFilterView.this, R.string.error_loadFields, Toast.LENGTH_LONG).show();
                            getSherlock().setProgressBarIndeterminateVisibility(false);
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }
    
    private void addRow(final Filterfield field, Handler handler)
    {
        try {
            String strUrl = MainActivity.CURRENT_INSTANCE.getUrl();
            String strUsername = MainActivity.CURRENT_INSTANCE.getUsername();
            String strPassword = MainActivity.CURRENT_INSTANCE.getPassword();

            RemoteCall rc = RemoteCall.getInstance(strUrl, strUsername, strPassword);
            final List<TicketAttribute> attributes = rc.getTicketFields();

            handler.post(new Runnable() {
                public void run() {
                    
                    LinearLayout row = new LinearLayout(SingleFilterView.this);
                    row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                    // Feld
                    final Spinner spinner = new Spinner(SingleFilterView.this);
                    spinner.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.35f));
                    TicketAttributeAdapter adapter = new TicketAttributeAdapter(SingleFilterView.this, android.R.layout.simple_spinner_item, attributes);
                    spinner.setAdapter(adapter);
                    
                    
                    // Richtigen Wert auswählen
                    int iPosition = 0;
                    for (TicketAttribute attr : attributes)
                    {
                        if (attr.getName().equals(field.getField()))
                        {
                            break;
                        }
                        iPosition++;
                    }
                    spinner.setSelection(iPosition);
                    row.addView(spinner);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                            SingleFilterView.this.onFieldChanged(spinner, av, i, field);
                        }
                        public void onNothingSelected(AdapterView<?> av) {
                            SingleFilterView.this.onFieldChanged(spinner, av, -1, field);
                        }
                    });
                    
                    // Vergleichsoperater
                    Spinner vergleichsSpinner = new Spinner(SingleFilterView.this);
                    vergleichsSpinner.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.15f));
                    ArrayAdapter<String> strings = new ArrayAdapter<String>(SingleFilterView.this, android.R.layout.simple_spinner_item);
                    strings.add(SingleFilterView.this.getString(R.string.operator_equals));
                    strings.add(SingleFilterView.this.getString(R.string.operator_notequal));
                    strings.add(SingleFilterView.this.getString(R.string.operator_greater));
                    strings.add(SingleFilterView.this.getString(R.string.operator_greaterequal));
                    strings.add(SingleFilterView.this.getString(R.string.operator_smaller));
                    strings.add(SingleFilterView.this.getString(R.string.operator_smallerequal));
                    vergleichsSpinner.setAdapter(strings);
                    row.addView(vergleichsSpinner);
                    
                    // Richtigen Wert auswählen
                    iPosition = 0;
                    for (int i = 0 ; i < strings.getCount() ; i++)
                    {
                        String strOp = strings.getItem(i);
                        if (strOp.equals(field.getOperator()))
                        {
                            break;
                        }
                        iPosition++;
                    }
                    vergleichsSpinner.setSelection(iPosition);        
                    
                    LinearLayout ll = (LinearLayout) SingleFilterView.this.findViewById(R.id.Filter_Layout);
                    ll.addView(row);
                    
                    // Aus allen Reihen den Button durch ein TextView ersetzen
                    for (int i=3 ; i < ll.getChildCount(); i++)
                    {
                        LinearLayout llRow = (LinearLayout) ll.getChildAt(i);

                        if (llRow.getChildCount() >= 4)
                        {
                            llRow.removeViewAt(3);
                        }

                        TextView tvAnd = new TextView(SingleFilterView.this);
                        tvAnd.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.15f));
                        tvAnd.setGravity(Gravity.CENTER);
                        tvAnd.setText(SingleFilterView.this.getString(R.string.label_and));
                        llRow.addView(tvAnd);
                    }  
                }
            });
        } catch (final RemoteCallException ex) {
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(SingleFilterView.this, R.string.error_loadFields, Toast.LENGTH_LONG).show();
                    getSherlock().setProgressBarIndeterminateVisibility(false);
                }
            });
        }
    }
    
    private void onFieldChanged(Spinner spinner, AdapterView<?> av, int iPosition)
    {
        onFieldChanged(spinner, av, iPosition, null);
    }
    
    private void onFieldChanged(Spinner spinner, AdapterView<?> av, int iPosition, Filterfield field)
    {
        TicketAttribute attr = (TicketAttribute) av.getItemAtPosition(iPosition);
        
        LinearLayout llParent = (LinearLayout) spinner.getParent();
        
        if (llParent.getChildCount() >= 4)
        {
            llParent.removeViewAt(3);
        }
        if (llParent.getChildCount() >= 3)
        {
            llParent.removeViewAt(2);
        }
        
        if (attr.getOptions().size() > 0)
        {
            Spinner optionSpinner = new Spinner(this);
            optionSpinner.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.35f));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
            for (String s : attr.getOptions())
            {
                adapter.add(s);
            }
            optionSpinner.setAdapter(adapter);
            
            if (field != null && attr.getName().equals(field.getField()))
            {
                // Wert setzen 
                int iSelectedPosition = 0;
                for (int i= 0 ; i < adapter.getCount() ; i++)
                {
                    String str = adapter.getItem(i);
                    
                    if (str.equals(field.getValue()))
                    {
                        break;
                    }
                    iSelectedPosition++;
                }
                
                optionSpinner.setSelection(iSelectedPosition);
            }
            
            llParent.addView(optionSpinner);
        }
        else
        {
            EditText et = new EditText(this);
            et.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.35f));
            
            if (field != null && attr.getName().equals(field.getField()))
            {
                // Wert setzen
                et.setText(field.getValue());
            }
            llParent.addView(et);
        }
        
        // Wenn dies die letzte Zeile ist, den Button hinzufügen
        // Ist dies die letzte Zeile?
        LinearLayout llRoot = (LinearLayout) llParent.getParent();
        LinearLayout llCompare = (LinearLayout) llRoot.getChildAt(llRoot.getChildCount() - 1);
        
        if (llParent.equals(llCompare))
        {
            // Das war die letzte Zeile
            Button btn = new Button(this);
            btn.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.15f));
            btn.setText("+");
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    addRow();
                }
            });
            llParent.addView(btn);
        }
        else
        {
            // Das ist nicht die letzte Zeile
            TextView tv = new TextView(this);
            tv.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.15f));
            tv.setGravity(Gravity.CENTER);
            tv.setText(this.getString(R.string.label_and));
            llParent.addView(tv);
        }
    }
    
    private void loadFilter(final int iFilterID)
    {
        getSherlock().setProgressBarIndeterminateVisibility(true);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String strFilterID = Integer.toString(iFilterID);
                    Cursor c = getContentResolver().query(
                            FilterProvider.getContentURI(), 
                            FilterProvider.COLUMNS, 
                            "id = ?", 
                            new String[] { strFilterID }, 
                            null);

                    Filter filter = FilterProvider.getListFromCursor(c).get(0);
                                        
                    final String str = SingleFilterView.this.getString(R.string.title_filter_edit) + " - " + filter.getName();
                    
                    handler.post(new Runnable() {
                        public void run() {   
                            SingleFilterView.this.getSupportActionBar().setTitle(str);
                        }
                    });

                    EditText edtName = (EditText) findViewById(R.id.Filter_Name);
                    edtName.setText(filter.getName());
                    
                    Spinner spInstance = (Spinner) findViewById(R.id.Filter_Instance);
                    if (spInstance != null)
                    {
                        Cursor cursor = getContentResolver().query(
                                InstanceProvider.getContentURI(),
                                InstanceProvider.COLUMNS,
                                null,
                                null,
                                null);

                        List<Instance> instanceList = InstanceProvider.getListFromCursor(cursor);
                        instanceList.add(0, new Instance(0, getString(R.string.label_all_instances), "", "", false, "", false, "", "", "", "", 0));
                        
                        int i = 0;
                        for (Instance inst : instanceList)
                        {
                            if (inst.getID() == filter.getInstance())
                            {
                                break;
                            }
                            i++;
                        }
                        
                        spInstance.setSelection(i);
                    }
                    
                    Spinner spGroup = (Spinner) findViewById(R.id.Filter_GroupBy);
                    spGroup.setSelection(filter.getGroupBy());

                    // Lade Filterfelder aus der Datenbank
                    c = getContentResolver().query(
                            FilterfieldsProvider.getContentURI(), 
                            FilterfieldsProvider.COLUMNS, 
                            "filterid = ?", 
                            new String[] { strFilterID }, 
                            null);

                    final List<Filterfield> filterfields = FilterfieldsProvider.getListFromCursor(c);

                    for (Filterfield f : filterfields)
                    {
                        addRow(f, handler);
                    }
                    
                    handler.post(new Runnable() {
                        public void run() {                            
                            getSherlock().setProgressBarIndeterminateVisibility(false);
                        }
                    });
                } catch (ParseException ex) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(SingleFilterView.this, R.string.error_loadFilter, Toast.LENGTH_LONG).show();
                        }
                    });
                    
                }
            }
        };
        new Thread(runnable).start();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.filter, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.save) 
        {
            onMenuSave();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void onMenuSave()
    {
        try {
            LinearLayout llRoot = (LinearLayout) findViewById(R.id.Filter_Layout);

            int iFilterID;
            
            if (this.getIntent().getExtras().getInt("FilterID") != -1)
            {
                // Ein bestehender Filter wurde editiert
                iFilterID = this.getIntent().getExtras().getInt("FilterID");
                String strFilterID = Integer.toString(iFilterID);
                TextView tvName = (TextView) findViewById(R.id.Filter_Name);
                String strName = tvName.getText().toString();
                
                Spinner spGroup = (Spinner) findViewById(R.id.Filter_GroupBy);
                String strGroup = spGroup.getSelectedItem().toString();
                
                int iInstance = 0;
                Spinner spInstance = (Spinner) findViewById(R.id.Filter_Instance);
                if (spInstance != null)
                {
                    Instance selectedInstance = (Instance) spInstance.getSelectedItem();
                    iInstance = selectedInstance.getID();
                }
                
                int iGroup = 0;
                String[] strArray = getResources().getStringArray(R.array.group_bys);
                
                if (strGroup.equals(strArray[1]))
                {
                    iGroup = Filter.GROUPBY_MILESTONE;
                }
                else if (strGroup.equals(strArray[2]))
                {
                    iGroup = Filter.GROUPBY_COMPONENT;
                }
                else if (strGroup.equals(strArray[3]))
                {
                    iGroup = Filter.GROUPBY_STATUS;
                }
                
                Cursor c = getContentResolver().query(
                        FilterProvider.getContentURI(), 
                        FilterProvider.COLUMNS, 
                        "id = ?", 
                        new String[] { strFilterID }, 
                        null);
                Filter filterNew = FilterProvider.getListFromCursor(c).get(0);
                filterNew.setName(strName);
                filterNew.setGroupBy(iGroup);
                filterNew.setInstance(iInstance);
                
                getContentResolver().update(
                        FilterProvider.getContentURI(), 
                        FilterProvider.getContentValues(filterNew), 
                        "id = ?", 
                        new String[] { strFilterID });
            }
            else
            {
                // Es wurde ein neuer Filter hinzugefügt
                Cursor c = getContentResolver().query(
                        FilterProvider.getContentURI(), 
                        FilterProvider.COLUMNS, 
                        null, 
                        null, 
                        "id DESC");

                int iOldID = 0;
                
                if (c.getCount()> 0)
                {
                    iOldID = FilterProvider.getListFromCursor(c).get(0).getID();
                }

                iFilterID = iOldID + 1;
                TextView tvName = (TextView) findViewById(R.id.Filter_Name);
                String strName = tvName.getText().toString();
                
                Spinner spGroup = (Spinner) findViewById(R.id.Filter_GroupBy);
                String strGroup = spGroup.getSelectedItem().toString();
                
                int iInstance = 0;
                Spinner spInstance = (Spinner) findViewById(R.id.Filter_Instance);
                if (spInstance != null)
                {
                    Instance selectedInstance = (Instance) spInstance.getSelectedItem();
                    iInstance = selectedInstance.getID();
                }
                
                int iGroup = 0;
                String[] strArray = getResources().getStringArray(R.array.group_bys);
                
                if (strGroup.equals(strArray[1]))
                {
                    iGroup = Filter.GROUPBY_MILESTONE;
                }
                else if (strGroup.equals(strArray[2]))
                {
                    iGroup = Filter.GROUPBY_COMPONENT;
                }
                else if (strGroup.equals(strArray[3]))
                {
                    iGroup = Filter.GROUPBY_STATUS;
                }
                
                Filter filterNew = new Filter(iFilterID, strName, iFilterID == 1, iGroup, iInstance);
                getContentResolver().insert(
                        FilterProvider.getContentURI(), 
                        FilterProvider.getContentValues(filterNew));
            }
             
            String strFilterID = Integer.toString(iFilterID);
            
            // Alle Felder für FilterID löschen
            getContentResolver().delete(
                    FilterfieldsProvider.getContentURI(), 
                    "filterid = ?", 
                    new String[] { strFilterID });
            
            Cursor c = getContentResolver().query(
                    FilterfieldsProvider.getContentURI(), 
                    FilterfieldsProvider.COLUMNS, 
                    null, 
                    null, 
                    "id DESC");
            
            int iID = 0;
            if (c.getCount() > 0)
            {
                iID = FilterfieldsProvider.getListFromCursor(c).get(0).getID();
            }
            iID++;

            // Felder neu anlegen
            for (int i=3 ; i < llRoot.getChildCount() ; i++)
            {
                String strField;
                String strOperator;
                String strValue;
                
                LinearLayout llRow = (LinearLayout) llRoot.getChildAt(i);

                Spinner spField = (Spinner) llRow.getChildAt(0);
                Spinner spOperator = (Spinner) llRow.getChildAt(1);

                TicketAttribute attr = (TicketAttribute) spField.getSelectedItem();
                strField = attr.getName();
                strOperator = (String) spOperator.getSelectedItem();

                if (llRow.getChildAt(2) instanceof EditText)
                {
                    EditText edtValue = (EditText) llRow.getChildAt(2);
                    strValue = edtValue.getText().toString();
                }
                else
                {
                    Spinner spValue = (Spinner) llRow.getChildAt(2);
                    strValue = (String) spValue.getSelectedItem();
                }
                
                Filterfield field = new Filterfield(iID, iFilterID, strField, strOperator, strValue);
                getContentResolver().insert(
                        FilterfieldsProvider.getContentURI(), 
                        FilterfieldsProvider.getContentValues(field));
                
                iID++;
            }
            
            finish();
        } catch (ParseException ex) {
            Toast.makeText(this, R.string.error_saveFilter, Toast.LENGTH_LONG).show();
        }
    }
}
