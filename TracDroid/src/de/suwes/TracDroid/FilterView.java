/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid;

import java.text.ParseException;
import java.util.List;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import de.suwes.TracDroid.Adapters.FilterAdapter;
import de.suwes.TracDroid.Database.FilterProvider;
import de.suwes.TracDroid.Database.FilterfieldsProvider;
import de.suwes.TracDroid.Database.InstanceProvider;
import de.suwes.TracDroid.Database.Models.Filter;
import de.suwes.TracDroid.free.R;

/**
 *
 * @author Osiris
 */
public class FilterView extends SherlockActivity implements AdapterView.OnItemClickListener {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.filterlist);
        
        loadFilters(); 
    }
    
    private void loadFilters()
    {
        try {
            Cursor cursor = getContentResolver().query(
                    FilterProvider.getContentURI(),
                    FilterProvider.COLUMNS,
                    "instance = ? OR instance = ?",
                    new String[] { "0", Integer.toString(MainActivity.CURRENT_INSTANCE.getID()) },
                    null);

            // Sind noch keine Daten vorhanden, wird ein Wartedialog angezeigt
            // Passiert eigentlich nur beim ersten Start der Anwendung
            List<Filter> filterList = FilterProvider.getListFromCursor(cursor);
            
            ListView lvFilters = (ListView) findViewById(R.id.ListViewFilters);
            //lvTickets.setOnItemClickListener(MainActivity.this);
            FilterAdapter adapter = new FilterAdapter(this, android.R.layout.simple_list_item_1, filterList, MainActivity.CURRENT_INSTANCE.getID());

            lvFilters.setAdapter(adapter);
            lvFilters.setOnItemClickListener(this);
            registerForContextMenu(lvFilters);
        } 
        catch (ParseException ex) 
        {
            Toast.makeText(this, R.string.error_loadFilters, Toast.LENGTH_LONG).show();
        }
    }

    public void onItemClick(AdapterView<?> av, View view, int iPosition, long l) {
        try {
            Filter f = (Filter) av.getItemAtPosition(iPosition);
            
            String strFilter = f.getFilterString(this);
            
            Intent intent = this.getIntent();
            intent.putExtra("Filter", strFilter);
            intent.putExtra("FilterName", f.getName());
            intent.putExtra("GroupBy", f.getGroupBy());
            intent.putExtra("Instance", f.getInstance());
            this.setResult(RESULT_OK, intent);
            finish();
        } catch (ParseException ex) {
            Toast.makeText(this, R.string.error_loadFilterfields, Toast.LENGTH_LONG).show();
        }        
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_filterlist, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
        ListView lvFilters = (ListView) findViewById(R.id.ListViewFilters);
        FilterAdapter adapter = (FilterAdapter) lvFilters.getAdapter();
        Filter f = adapter.getItem(info.position);
        
        if (item.getItemId() == R.id.edit) 
        {
            onMenuEditFilter(f);
            return true;
        }
        else if (item.getItemId() == R.id.delete)
        {
            onMenuDeleteFilter(f);
            return true;
        }
        else if (item.getItemId() == R.id.asdefault)
        {
            onMenuDefaultFilter(f);
            return true;
        }
        else
        {
            return super.onContextItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) 
    {
        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.filterlist, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.filter_add) 
        {
            onMenuAdd();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void onMenuEditFilter(Filter f)
    {
        Intent i = new Intent(this, SingleFilterView.class);
        i.putExtra("FilterID", f.getID());
        startActivityForResult(i, 0);
    }
    
    private void onMenuDeleteFilter(Filter f)
    {
    	if (f.getInstance() == 0) {
	    	// Es muss immer mindestens ein globaler Filter vorhanden sein
	    	Cursor c = getContentResolver().query(
	    			FilterProvider.getContentURI(), 
	    			FilterProvider.COLUMNS, 
	    			"instance = ?", 
	    			new String[] { "0" }, 
	    			null);
	    	
	    	try {
				List<Filter> vorhandeneFilter = FilterProvider.getListFromCursor(c);
				if (vorhandeneFilter.size() <= 1) {
					Toast.makeText(this, R.string.error_leave_one_global_filter, Toast.LENGTH_LONG).show();
					return;
				}
			} catch (ParseException e) {
				Toast.makeText(this, R.string.error_deleting_filter, Toast.LENGTH_LONG).show();
				return;
			}
    	}

		if (f.getID() == MainActivity.CURRENT_INSTANCE.getDefaultFilter()) {
			Toast.makeText(this, R.string.error_default_filter_cant_delete, Toast.LENGTH_LONG).show();
			return;
		}
    	
        String strFilterID = Integer.toString(f.getID());
        
        getContentResolver().delete(
                FilterfieldsProvider.getContentURI(),
                "filterid = ?",
                new String[] { strFilterID });
        
        getContentResolver().delete(
                FilterProvider.getContentURI(), 
                "id = ?", 
                new String[] { strFilterID });
        
        loadFilters();
    }
    
    private void onMenuDefaultFilter(Filter f)
    {
        // Remove all default markers
        
        // Den fraglichen Filter default setzen
    	ContentValues cv = new ContentValues();
    	cv.put("defaultfilter", f.getID());
        f.setDefault(true);
        getContentResolver().update(
                InstanceProvider.getContentURI(), 
                cv, 
                "id = ?", 
                new String[] { Integer.toString(MainActivity.CURRENT_INSTANCE.getID()) });
        
        loadFilters();
    }
    
    private void onMenuAdd()
    {
        Intent i = new Intent(this, SingleFilterView.class);
        i.putExtra("FilterID", -1);
        startActivityForResult(i, 0);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        loadFilters();
    }
}
