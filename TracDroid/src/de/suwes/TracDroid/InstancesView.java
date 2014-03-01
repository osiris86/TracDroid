/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid;

import java.text.ParseException;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import de.suwes.TracDroid.Adapters.InstancesAdapter;
import de.suwes.TracDroid.Database.FilterProvider;
import de.suwes.TracDroid.Database.InstanceProvider;
import de.suwes.TracDroid.Database.Models.Filter;
import de.suwes.TracDroid.Database.Models.Instance;
import de.suwes.TracDroid.free.R;

/**
 *
 * @author Osiris
 */
public class InstancesView extends SherlockActivity implements AdapterView.OnItemClickListener {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.instancelist);
        getSupportActionBar().setTitle(R.string.title_instances);
        
        loadInstances(); 
    }
    
    private void loadInstances()
    {
        try {
            Cursor cursor = getContentResolver().query(
                    InstanceProvider.getContentURI(),
                    InstanceProvider.COLUMNS,
                    null,
                    null,
                    null);

            // Sind noch keine Daten vorhanden, wird ein Wartedialog angezeigt
            // Passiert eigentlich nur beim ersten Start der Anwendung
            List<Instance> instanceList = InstanceProvider.getListFromCursor(cursor);
            
            ListView lvInstances = (ListView) findViewById(R.id.ListViewInstances);
            //lvTickets.setOnItemClickListener(MainActivity.this);
            InstancesAdapter adapter = new InstancesAdapter(this, android.R.layout.simple_list_item_1, instanceList);
            
            if (adapter.isEmpty())
            {
                Toast.makeText(this, R.string.error_noInstances, Toast.LENGTH_LONG).show();
            }

            lvInstances.setAdapter(adapter);
            lvInstances.setOnItemClickListener(this);
            registerForContextMenu(lvInstances);
        } 
        catch (ParseException ex) 
        {
            Toast.makeText(this, R.string.error_loadInstances, Toast.LENGTH_LONG).show();
        }
    }

    public void onItemClick(AdapterView<?> av, View view, int iPosition, long l) 
    {
        Instance i = (Instance) av.getItemAtPosition(iPosition);
          
        loadSettings(i);
        
        Intent intent = new Intent(this, Preferences.class);
        startActivityForResult(intent, 0);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_instancelist, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
        ListView lvInstances = (ListView) findViewById(R.id.ListViewInstances);
        InstancesAdapter adapter = (InstancesAdapter) lvInstances.getAdapter();
        Instance i = adapter.getItem(info.position);
        
        if (item.getItemId() == R.id.delete)
        {
            onMenuDeleteInstance(i);
            return true;
        }
        else if (item.getItemId() == R.id.asdefault)
        {
            onMenuDefaultInstance(i);
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
        inflater.inflate(R.menu.instancelist, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.instance_add) 
        {
            onMenuAdd();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void onMenuDeleteInstance(Instance i)
    {
        getContentResolver().delete(
                InstanceProvider.getContentURI(), 
                "id = ?", 
                new String[] { Integer.toString(i.getID()) });
        
        loadInstances();
    }
    
    private void onMenuDefaultInstance(Instance i)
    {
        // Remove all default markers
        ContentValues cv = new ContentValues();
        cv.put("defaultinstance", 0);
        getContentResolver().update(
                InstanceProvider.getContentURI(), 
                cv, 
                null, 
                null);
        
        // Die fragliche Instance default setzen
        i.setDefault(true);
        getContentResolver().update(
                InstanceProvider.getContentURI(), 
                InstanceProvider.getContentValues(i), 
                "id = ?", 
                new String[] { Integer.toString(i.getID()) });
        
        loadInstances();
    }
    
    private void onMenuAdd()
    {
        try {
	    	Cursor cursor = getContentResolver().query(
	                InstanceProvider.getContentURI(),
	                InstanceProvider.COLUMNS,
	                null,
	                null,
	                null);
	
	        // Sind noch keine Daten vorhanden, wird ein Wartedialog angezeigt
	        // Passiert eigentlich nur beim ersten Start der Anwendung
			List<Instance> instanceList = InstanceProvider.getListFromCursor(cursor);
    	
	        if (instanceList.size() > 0)
	        {
	            AlertDialog.Builder builder = new AlertDialog.Builder(this);
	            builder.setMessage(R.string.error_only_paid_version_instances)
	                .setPositiveButton(R.string.label_market, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                        String strAppName = "de.suwes.TracDroid.paid";
	                        try {
	                            // Wenn der PlayStore installiert ist, diesen öffnen
	                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+strAppName)));
	                        } catch (android.content.ActivityNotFoundException anfe) {
	                            // Ansonsten über die Webseite
	                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+strAppName)));
	                        }
	                    }
	                })
	                .setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                        // User cancelled the dialog
	                    }
	                });
	            // Create the AlertDialog object and return it
	            AlertDialog alert = builder.create();
	            alert.show();
	        }
	        else
	        {
	            clearSettings();
	            Intent i = new Intent(this, Preferences.class);
	            startActivityForResult(i, 0);
	        }
		} catch (ParseException e) {
            Toast.makeText(this, R.string.error_loadInstances, Toast.LENGTH_LONG).show();
		}
    }
    
    private void clearSettings()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = settings.edit();
        editor.putInt("id", 0);
        editor.putString("name", "");
        editor.putString("url", "");
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putString("topleft", "");
        editor.putString("topright", "");
        editor.putString("bottomleft", "");
        editor.putString("bottomright", "");
        editor.commit();
    }
    
    private void loadSettings(Instance i)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = settings.edit();
        editor.putInt("id", i.getID());
        editor.putString("name", i.getName());
        editor.putString("url", i.getUrl());
        editor.putString("username", i.getUsername());
        editor.putString("password", i.getPassword());
        editor.putString("topleft", i.getTopleft());
        editor.putString("topright", i.getTopright());
        editor.putString("bottomleft", i.getBottomleft());
        editor.putString("bottomright", i.getBottomright());
        editor.commit();
    }
    
    private void saveSettings(Instance i)
    {
        try
        {
            if (i == null)
            {
                // Neu anlegen
                // Es wurde eine neue Instanz hinzugefügt
                Cursor c = getContentResolver().query(
                        InstanceProvider.getContentURI(), 
                        InstanceProvider.COLUMNS, 
                        null, 
                        null, 
                        "id DESC");

                int iOldID = 0;

                if (c.getCount() > 0)
                {
                    iOldID = InstanceProvider.getListFromCursor(c).get(0).getID();
                }

                int iInstanceID = iOldID + 1;

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

                String strName = settings.getString("name", getString(R.string.default_instance_name));
                String strUrl = settings.getString("url", "");
                String strUsername = settings.getString("username", "");
                String strPassword = settings.getString("password", "");
                boolean bDefault = c.getCount() == 0;
                String strTopleft = settings.getString("topleft", "");
                String strTopright = settings.getString("topright", "");
                String strBottomleft = settings.getString("bottomleft", "");
                String strBottomright = settings.getString("bottomright", "");
                
                // Globalen Filter mit niedrigster ID holen
                Cursor cFilter = getContentResolver().query(
                		FilterProvider.getContentURI(), 
                		FilterProvider.COLUMNS, 
                		"instance = ?", 
                		new String[] { "0" }, 
                		"id");
                
                Filter filter = FilterProvider.getListFromCursor(cFilter).get(0);

                Instance newInstance = new Instance(iInstanceID, strName, strUrl, strUsername, false, strPassword, bDefault, strTopleft, strTopright, strBottomleft, strBottomright, filter.getID());
                getContentResolver().insert(
                        InstanceProvider.getContentURI(), 
                        InstanceProvider.getContentValues(newInstance));
            }
            else
            {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                
                String strName = settings.getString("name", getString(R.string.default_instance_name));
                String strUrl = settings.getString("url", "");
                String strUsername = settings.getString("username", "");
                String strPassword = settings.getString("password", "");
                String strTopleft = settings.getString("topleft", "");
                String strTopright = settings.getString("topright", "");
                String strBottomleft = settings.getString("bottomleft", "");
                String strBottomright = settings.getString("bottomright", "");
                
                i.setName(strName);
                i.setUrl(strUrl);
                i.setUsername(strUsername);
                i.setPassword(false, strPassword);
                i.setTopleft(strTopleft);
                i.setTopright(strTopright);
                i.setBottomleft(strBottomleft);
                i.setBottomright(strBottomright);
                
                getContentResolver().update(
                        InstanceProvider.getContentURI(), 
                        InstanceProvider.getContentValues(i), 
                        "id = ?", 
                        new String[] { Integer.toString(i.getID()) });
            }
        }
        catch (ParseException ex)
        {
            Toast.makeText(this, R.string.error_saveInstance, Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            int iID = settings.getInt("id", 0);
            Instance i = null;

            if (iID != 0)
            {

                Cursor c  = getContentResolver().query(
                        InstanceProvider.getContentURI(), 
                        InstanceProvider.COLUMNS, 
                        "id = ?", 
                        new String[] { Integer.toString(iID) },
                        null);

                i = InstanceProvider.getListFromCursor(c).get(0);

            }

            saveSettings(i);

            loadInstances();
        } catch (ParseException ex) {
            Toast.makeText(this, R.string.error_saveInstance, Toast.LENGTH_LONG).show();
        }
    }
}
