package com.creativecapsule.paytracker.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creativecapsule.paytracker.Models.Outing;
import com.creativecapsule.paytracker.R;

import java.util.ArrayList;

/**
 * Created by rahul on 01/07/15.
 */
public class OutingsAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Outing> outings;

    public OutingsAdapter(Context context) {
        this.context = context;
        this.outings = new ArrayList<>();
    }

    public void setOutings(ArrayList<Outing> outings) {
        this.outings = outings;
    }

    @Override
    public int getCount() {
        return outings.size();
    }

    @Override
    public Object getItem(int i) {
        return outings.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        OutingViewHolder outingViewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_outing, viewGroup, false);
            outingViewHolder = new OutingViewHolder();

            outingViewHolder.tvName = (TextView) view.findViewById(R.id.outing_item_title);
            outingViewHolder.tvDescription = (TextView) view.findViewById(R.id.outing_item_desc);

            view.setTag(outingViewHolder);
        }
        else {
            outingViewHolder = (OutingViewHolder) view.getTag();
        }

        Outing outing = outings.get(i);

        outingViewHolder.tvName.setText(outing.getTitle());
        outingViewHolder.tvDescription.setText( "With " + outing.getBuddiesText());

        return view;
    }

    public class OutingViewHolder {
        TextView tvName, tvDescription;
    }
}
