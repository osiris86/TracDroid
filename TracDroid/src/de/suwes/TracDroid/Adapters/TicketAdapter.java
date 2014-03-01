package de.suwes.TracDroid.Adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.suwes.TracDroid.Database.Models.Filter;
import de.suwes.TracDroid.Model.Ticket;
import de.suwes.TracDroid.Model.TicketAttribute;
import de.suwes.TracDroid.free.R;

public class TicketAdapter extends ArrayAdapter<Ticket> 
{
    private List<Ticket> m_arrayList = null;
    private Activity m_activity = null;
    private int m_iTextViewResourceID = 0;
    private int m_iGroupBy = 0;
    private String m_strTopLeft = "";
    private String m_strTopRight = "";
    private String m_strBottomLeft = "";
    private String m_strBottomRight = "";
    private String m_strTopLeftLabel = "";
    private String m_strTopRightLabel = "";
    private String m_strBottomLeftLabel = "";
    private String m_strBottomRightLabel = "";
	
    /**
        * Erstellt einen neuen TicketAdapter
        * @param context Der Context in welchem der Adapter erstellt wird
        * @param arr Die ArrayList mit den Einträgen
        * @param textViewResourceID Die ID des Row-Eintrages
        */
    public TicketAdapter(Context context, int textViewResourceID, List<Ticket> arr, List<TicketAttribute> attributes, String strTopLeft, String strTopRight, String strBottomLeft, String strBottomRight, int iGroupBy) 
    {
        super(context, textViewResourceID, arr);
		
        m_iTextViewResourceID = textViewResourceID;
        m_iGroupBy = iGroupBy;
        m_activity = (Activity) context;
        m_arrayList = arr;        
        m_strTopLeft = strTopLeft;
        m_strTopRight = strTopRight;
        m_strBottomLeft = strBottomLeft;
        m_strBottomRight = strBottomRight;
        
        for (TicketAttribute attr : attributes)
        {
            if (attr.getName().equals(strTopLeft))
            {
                m_strTopLeftLabel = attr.getLabel();
            }
            if (attr.getName().equals(strTopRight))
            {
                m_strTopRightLabel = attr.getLabel();
            }
            if (attr.getName().equals(strBottomLeft))
            {
                m_strBottomLeftLabel = attr.getLabel();
            }
            if (attr.getName().equals(strBottomRight))
            {
                m_strBottomRightLabel = attr.getLabel();
            }
        }
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
        
        TextView seperator = (TextView) row.findViewById(R.id.RowTicket_seperator);
        
        String strOld = "";
        String strNew = "";
        
        if (m_iGroupBy == Filter.GROUPBY_MILESTONE)
        {
            strNew = m_arrayList.get(position).getAttribute("milestone");
        }
        else if (m_iGroupBy == Filter.GROUPBY_COMPONENT)
        {
            strNew = m_arrayList.get(position).getAttribute("component");
        }
        else if (m_iGroupBy == Filter.GROUPBY_STATUS)
        {
            strNew = m_arrayList.get(position).getAttribute("status");
        }
        
        if (position > 0)
        {
            if (m_iGroupBy == Filter.GROUPBY_MILESTONE)
            {
                strOld = m_arrayList.get(position-1).getAttribute("milestone");
            }
            else if (m_iGroupBy == Filter.GROUPBY_COMPONENT)
            {
                strOld = m_arrayList.get(position-1).getAttribute("component");
            }
            else if (m_iGroupBy == Filter.GROUPBY_STATUS)
            {
                strOld = m_arrayList.get(position-1).getAttribute("status");
            }
        }
        
        if (m_iGroupBy != 0 && !strOld.equals(strNew))
        {
            seperator.setText(strNew);
            seperator.setVisibility(View.VISIBLE);
        }
        else
        {
            seperator.setVisibility(View.GONE);
        }

        TextView label=(TextView)row.findViewById(R.id.RowTicket_label);
        label.setText(m_arrayList.get(position).getAttribute("summary"));
        
        TextView id = (TextView)row.findViewById(R.id.RowTicket_id);
        id.setText("#" + m_arrayList.get(position).getID());
        
        TextView topleftlabel = (TextView)row.findViewById(R.id.RowTicket_topleft_label);
        topleftlabel.setText(m_strTopLeftLabel);
        TextView topleft = (TextView)row.findViewById(R.id.RowTicket_topleft);
        topleft.setText(m_arrayList.get(position).getAttribute(m_strTopLeft));
        
        TextView toprightlabel = (TextView)row.findViewById(R.id.RowTicket_topright_label);
        toprightlabel.setText(m_strTopRightLabel);
        TextView topright = (TextView)row.findViewById(R.id.RowTicket_topright);
        topright.setText(m_arrayList.get(position).getAttribute(m_strTopRight));
        
        TextView bottomleftlabel = (TextView)row.findViewById(R.id.RowTicket_bottomleft_label);
        bottomleftlabel.setText(m_strBottomLeftLabel);
        TextView bottomleft = (TextView)row.findViewById(R.id.RowTicket_bottomleft);
        bottomleft.setText(m_arrayList.get(position).getAttribute(m_strBottomLeft));
        
        TextView bottomrightlabel = (TextView)row.findViewById(R.id.RowTicket_bottomright_label);
        bottomrightlabel.setText(m_strBottomRightLabel);
        TextView bottomright = (TextView)row.findViewById(R.id.RowTicket_bottomright);
        bottomright.setText(m_arrayList.get(position).getAttribute(m_strBottomRight));
	    
        return(row);
    }
}
