package de.suwes.TracDroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.actionbarsherlock.view.Window;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import de.suwes.TracDroid.Model.TicketAttribute;
import de.suwes.TracDroid.free.R;

import java.text.ParseException;
import java.util.List;

import de.suwes.TracDroid.Database.FilterProvider;
import de.suwes.TracDroid.Database.InstanceProvider;
import de.suwes.TracDroid.Database.Models.Filter;
import de.suwes.TracDroid.Database.Models.Instance;
import de.suwes.TracDroid.Helpers.TabListener;

public class MainActivity extends SherlockFragmentActivity
{    
    public static String CURRENT_FILTER = "";
    public static int CURRENT_GROUP = 0;
    public static List<TicketAttribute> TICKET_ATTRIBUTES = null;
    public static Instance CURRENT_INSTANCE = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);    
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        initialize();
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tabTickets = actionBar.newTab()
            .setText(R.string.title_tickets)
            .setTabListener(new TabListener<TicketView>(MainActivity.this, "tickets", TicketView.class 
                    ));
        actionBar.addTab(tabTickets);
        
        ActionBar.Tab tabWiki = actionBar.newTab()
            .setText(R.string.title_wiki)
            .setTabListener(new TabListener<WikiView>(MainActivity.this, "wiki", WikiView.class 
                    ));
        actionBar.addTab(tabWiki);
    }
    
    private void initialize() {
    	try {
            Cursor c = getContentResolver().query(
                    InstanceProvider.getContentURI(), 
                    InstanceProvider.COLUMNS, 
                    null, 
                    null, 
                    null);
            
            List<Instance> instance = InstanceProvider.getListFromCursor(c);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            String strUrl = settings.getString("url", "");
            
            if ((instance == null || instance.size() == 0) && (strUrl != null && strUrl.length() > 0))
            {
                // Höchst wahrscheinlich wurde Update durchgeführt. Die Settings
                // in Datenbank schreiben
                
                String strName = getString(R.string.default_instance_name);
                String strUsername = settings.getString("username", "");
                String strPassword = settings.getString("password", "");
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
                
                Instance i = new Instance(1, strName, strUrl, strUsername, false, strPassword, true, strTopleft, strTopright, strBottomleft, strBottomright, filter.getID());
                getContentResolver().insert(
                        InstanceProvider.getContentURI(), 
                        InstanceProvider.getContentValues(i));
            }
            else if ((instance == null || instance.size() == 0) && (strUrl == null || strUrl.length() == 0)) {
            	 Intent i = new Intent(this, InstancesView.class);
                 startActivityForResult(i, 0);
                 return;
            }
            
            
            // Aktuelle Instanz laden
            c = getContentResolver().query(
                    InstanceProvider.getContentURI(), 
                    InstanceProvider.COLUMNS, 
                    "defaultinstance = ?", 
                    new String[] { "1" }, 
                    null);
            
            MainActivity.CURRENT_INSTANCE = InstanceProvider.getListFromCursor(c).get(0);      
            
        } catch (ParseException ex) {
            Toast.makeText(this, R.string.error_loadInstances, Toast.LENGTH_LONG).show();
        }
    }
    
    public void onMenuPreferences()
    {
        Intent i = new Intent(this, InstancesView.class);
        startActivity(i);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	initialize();
    }
    
    public void onMenuSelectInstance(final SherlockFragment sender)
    {
        try {
            Cursor c = this.getContentResolver().query(
                    InstanceProvider.getContentURI(), 
                    InstanceProvider.COLUMNS, 
                    null, 
                    null, 
                    null);
            final List<Instance> instances = InstanceProvider.getListFromCursor(c);
            CharSequence[] texts = new CharSequence[instances.size()];
            
            if (instances.isEmpty())
            {
                Toast.makeText(this, R.string.error_noInstances_tickets, Toast.LENGTH_LONG).show();
                return;
            }
            
            for (int i = 0 ; i < instances.size() ; i++)
            {
                texts[i] = instances.get(i).getName();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_select_instance);
            builder.setItems(texts, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    MainActivity.CURRENT_INSTANCE = instances.get(item);
                    if (sender instanceof TicketView)
                    {
                        ((TicketView) sender).loadTickets();
                    }
                    else
                    {
                        ((WikiView) sender).clearHistory();
                        ((WikiView) sender).loadWikiPage();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (ParseException ex) {
            Toast.makeText(this, R.string.error_loadInstances, Toast.LENGTH_LONG).show();
        }
    }
}
