package de.suwes.TracDroid.Adapters;

import java.text.ParseException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import de.suwes.TracDroid.Database.InstanceProvider;
import de.suwes.TracDroid.Database.Models.Filter;
import de.suwes.TracDroid.Database.Models.Instance;
import de.suwes.TracDroid.free.R;

public class FilterAdapter extends ArrayAdapter<Filter> 
{
    private List<Filter> m_arrayList = null;
    private Activity m_activity = null;
    private int m_iTextViewResourceID = 0;
    private int m_iInstanceId = 0;
	
    /**
        * Erstellt einen neuen TicketAdapter
        * @param context Der Context in welchem der Adapter erstellt wird
        * @param arr Die ArrayList mit den Einträgen
        * @param textViewResourceID Die ID des Row-Eintrages
        */
    public FilterAdapter(Context context, int textViewResourceID, List<Filter> arr, int iInstanceId) 
    {
        super(context, textViewResourceID, arr);
		
        m_iTextViewResourceID = textViewResourceID;
        m_activity = (Activity) context;
        m_arrayList = arr;        
        m_iInstanceId = iInstanceId;
    }

    /**
    * Gibt die View zurück
    * @param position Irgendeine Position
    * @param convertView Irgendeine View
    * @param parent Irgendeine ViewGroup
    * @return ne View
    */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        View row=convertView;

        if (row==null) 
        {
            LayoutInflater inflater = m_activity.getLayoutInflater();
            row=inflater.inflate(m_iTextViewResourceID, parent, false);
        }

        TextView label=(TextView)row.findViewById(android.R.id.text1);
        String strText = m_arrayList.get(position).getName();
        
        Cursor c = m_activity.getContentResolver().query(
        		InstanceProvider.getContentURI(), 
        		InstanceProvider.COLUMNS, 
        		"id = ?", 
        		new String[] { Integer.toString(m_iInstanceId) }, 
        		null);

		Instance instance;
        try {
			instance = InstanceProvider.getListFromCursor(c).get(0);
		} catch (ParseException e) {
            Toast.makeText(m_activity, R.string.error_loadFilters, Toast.LENGTH_LONG).show();
            return null;
		}
        
        if (instance.getDefaultFilter() == m_arrayList.get(position).getID())
        {
            strText += " ";
            strText += m_activity.getString(R.string.label_default);
        }
        label.setText(strText);
	    
        return(row);
    }
}
