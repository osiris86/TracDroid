package de.suwes.TracDroid.Adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.suwes.TracDroid.Database.Models.Instance;
import de.suwes.TracDroid.free.R;

public class InstancesAdapter extends ArrayAdapter<Instance> 
{
    private List<Instance> m_arrayList = null;
    private Activity m_activity = null;
    private int m_iTextViewResourceID = 0;
	
    /**
        * Erstellt einen neuen TicketAdapter
        * @param context Der Context in welchem der Adapter erstellt wird
        * @param arr Die ArrayList mit den Einträgen
        * @param textViewResourceID Die ID des Row-Eintrages
        */
    public InstancesAdapter(Context context, int textViewResourceID, List<Instance> arr) 
    {
        super(context, textViewResourceID, arr);
		
        m_iTextViewResourceID = textViewResourceID;
        m_activity = (Activity) context;
        m_arrayList = arr;        
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
        if (m_arrayList.get(position).isDefault())
        {
            strText += " ";
            strText += m_activity.getString(R.string.label_default);
        }
        label.setText(strText);
	    
        return(row);
    }
}
