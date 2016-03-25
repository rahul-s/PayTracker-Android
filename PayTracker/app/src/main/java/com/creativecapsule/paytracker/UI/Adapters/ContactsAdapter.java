package com.creativecapsule.paytracker.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativecapsule.paytracker.Models.Misc.ContactItem;
import com.creativecapsule.paytracker.R;

import java.util.ArrayList;

/**
 * Created by rahul on 27/12/15.
 */
public class ContactsAdapter extends BaseAdapter implements Filterable{

    private Context context;
    private ArrayList<ContactItem> contactItemsAll, contactItemsFiltered, contactsSelected;

    public ContactsAdapter(ArrayList<ContactItem> contactItems, Context context) {
        this.contactItemsAll = contactItems;
        this.contactItemsFiltered = contactItems;
        this.contactsSelected = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return contactItemsFiltered.size();
    }

    @Override
    public Object getItem(int i) {
        return contactItemsFiltered.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ContactViewHolder contactViewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_contact, viewGroup, false);
            contactViewHolder = new ContactViewHolder();
            contactViewHolder.tvContactName = (TextView) view.findViewById(R.id.contact_person_name);
            contactViewHolder.tvContactSelectedNumber = (TextView) view.findViewById(R.id.contact_person_number);
            contactViewHolder.ivContactSelected = (ImageView) view.findViewById(R.id.contact_item_selected);
            view.setTag(contactViewHolder);
        }
        else {
            contactViewHolder = (ContactViewHolder) view.getTag();
        }

        ContactItem contact = (ContactItem) this.getItem(i);
        contactViewHolder.tvContactName.setText(contact.getContactName());
        if (contactsSelected.contains(contact)) {
            contactViewHolder.tvContactSelectedNumber.setText(contact.getSelectedContactNumber());
            contactViewHolder.ivContactSelected.setImageResource(R.drawable.ic_checkbox_checked);
        }
        else {

            contactViewHolder.tvContactSelectedNumber.setText("");
            contactViewHolder.ivContactSelected.setImageResource(R.drawable.ic_checkbox_unchecked);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter contactsFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<ContactItem> FilteredArrList = new ArrayList<ContactItem>();

                if (contactItemsAll == null) {
                    contactItemsAll = new ArrayList<ContactItem>(contactItemsFiltered); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = contactItemsAll.size();
                    results.values = contactItemsAll;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < contactItemsAll.size(); i++) {
                        String data = contactItemsAll.get(i).getContactName();
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(contactItemsAll.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactItemsFiltered = (ArrayList<ContactItem>) filterResults.values;
            }
        };
        return contactsFilter;
    }

    public class ContactViewHolder {
        public TextView tvContactName, tvContactSelectedNumber;
        public ImageView ivContactSelected;
    }
}
