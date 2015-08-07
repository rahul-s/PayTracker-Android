package com.creativecapsule.paytracker.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creativecapsule.paytracker.Models.Person;
import com.creativecapsule.paytracker.R;

import java.util.ArrayList;

/**
 * Created by rahul on 01/07/15.
 */
public class PersonsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Person> persons;

    public PersonsAdapter(Context context) {
        this.context = context;
        this.persons = new ArrayList<>();
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    @Override
    public int getCount() {
        return this.persons.size();
    }

    @Override
    public Object getItem(int i) {
        return this.persons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PersonViewHolder personViewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_person, viewGroup, false);
            personViewHolder = new PersonViewHolder();

            personViewHolder.tvName = (TextView) view.findViewById(R.id.person_item_name);
            personViewHolder.tvNickname = (TextView) view.findViewById(R.id.person_item_nickname);

            view.setTag(personViewHolder);
        }
        else {
            personViewHolder = (PersonViewHolder) view.getTag();
        }

        Person person = this.persons.get(i);

        personViewHolder.tvName.setText(person.getName());
        personViewHolder.tvNickname.setText(person.getNickName());

        return view;
    }

    public class PersonViewHolder {
        public TextView tvName, tvNickname;
    }
}
