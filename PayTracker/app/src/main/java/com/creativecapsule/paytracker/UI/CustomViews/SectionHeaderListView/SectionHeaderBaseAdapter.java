package com.creativecapsule.paytracker.UI.CustomViews.SectionHeaderListView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by rahul on 24/12/14.
 */
public abstract class SectionHeaderBaseAdapter extends BaseAdapter {


    abstract public int numberOfSections();
    abstract public int numberOfRowsInSection(int section);
    abstract public int getHeightForHeaderView();
    abstract public View getSectionHeaderView(int section, View convertView, ViewGroup parent);
    abstract public View getRowView(int section, int row, View convertView, ViewGroup parent);
    abstract public int getRowViewType(int section, int row);
    abstract public int getRowViewTypeCount();
    abstract public Object getRowItem(int section,int row);


    private static final String DEBUG_TAG = "SectionHeaderBaseAdapter";

    @Override
    public int getCount() {
        int numRows = 0;
        int numSections = numberOfSections();
        for (int i =0 ; i< numSections ; i++) {
            numRows += numberOfRowsInSection(i);
        }
        return numRows+numSections;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return getRowViewTypeCount() + 1;   // Types of rows and section header
    }

    @Override
    public int getItemViewType(int position) {
        if (isSectionHeader(position)) {
            return 0;
        }
        else {
            int sectionIndex = getSection(position);
            int rowIndex = getRow(position);
            return getRowViewType(sectionIndex, rowIndex);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionIndex = getSection(position);
        if (isSectionHeader(position)) {
            // header row
            //Log.d(DEBUG_TAG,"cell at "+position+":section header");
            convertView = getSectionHeaderView(sectionIndex,convertView,parent);
        }
        else {
            // cell row
            //Log.d(DEBUG_TAG,"cell at "+position+":row");
            int rowIndex = getRow(position);
            convertView = getRowView(sectionIndex,rowIndex,convertView,parent);
        }

        return convertView;
    }

    /**
     * returns the section index to which the position belongs
     * @param position - row index
     * @return - section index
     */
    public int getSection(int position){
        int section = 0;
        int cellCounter = 0;
        do {
            cellCounter += numberOfRowsInSection(section);
            section++;
            cellCounter++;
        }while (cellCounter <= position && section < numberOfSections());
        return section - 1;
    }

    /**
     * returns the row index for the position
     * @param position - row index
     * @return - row index within the section
     */
    private int getRow(int position) {
        int section = getSection(position);
        int row = position - rowsUptoSection(section);
        return row - 1;
    }

    /**
     * check if the given position is the section header
     * @param position - row index
     * @return - true if section header, false otherwise
     */
    private boolean isSectionHeader (int position) {
        int section = getSection(position);
        //Log.d(DEBUG_TAG,"position:"+position+", section:"+section + ", rowsUptoSection:"+rowsUptoSection(section));
        return position == rowsUptoSection(section);
    }

    /**
     * get number of cells (header & rows) before section
     * @param section - section index
     * @return - number of rows
     */
    public int rowsUptoSection(int section) {
        int count = 0;
        if (section >= numberOfSections())
            return count;

        for (int i = 0; i < section; i++) {
            count += numberOfRowsInSection(i);
            count++;
        }
        return count;
    }

    /**
     * get the Top section to freeze
     * @return - top section
     */
    public View getTopSectionView() {
        int sectionIndex = 0;

        return getSectionHeaderView(sectionIndex,null,null);
    }
}
