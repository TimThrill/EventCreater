package com.cong.eventcreater.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.cong.eventcreater.R;
import com.cong.eventcreater.UI.MainActivity;
import com.cong.eventcreater.model.Event;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 21/08/2016.
 */
public class EventListAdapter extends ArrayAdapter<Event> {
    private Context context;
    public EventListAdapter(Context context, int resource, List<Event> list) {
        super(context, resource, list);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.events_list_item, parent, false);
        }
        TextView txv = (TextView)convertView.findViewById(R.id.eventText);
        ImageButton btEdit = (ImageButton)convertView.findViewById(R.id.editEvent);
        ImageButton btDelete = (ImageButton)convertView.findViewById(R.id.deleteEvent);

        final Event event = getItem(position);
        txv.setText(event.toString());

        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getContext()).editEvent(event);
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getContext()).deleteEvent(event.getId());
            }
        });

        return convertView;
    }
}
