package com.creativecapsule.paytracker.UI.CustomViews.SectionHeaderListView;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by rahul on 24/12/14.
 */
public class SectionHeaderListView extends ViewGroup implements AbsListView.OnScrollListener, View.OnTouchListener {

    /**
     *
     */
    public ListView mListView;
    private View mSectionHeader;
    SectionHeaderBaseAdapter mAdapter;

    private static final String DEBUG_TAG = "ExtendedSectionListView";
    private int mListTopStart, mListTop, mTouchStartY;
    private int mTopSection;
    private static boolean isListViewAdded = false;
    private boolean headerFreeze = true;

    public SectionHeaderBaseAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(SectionHeaderBaseAdapter mAdapter) {
        this.mListView.setAdapter(mAdapter);
        if (mAdapter.numberOfSections()>0)
        this.mSectionHeader = mAdapter.getSectionHeaderView(0, null, null);
        this.mAdapter = mAdapter;
    }

    public void setHeaderFreeze(boolean headerFreeze) {
        this.headerFreeze = headerFreeze;
    }

    public SectionHeaderListView(Context context) {
        super(context);
        this.mListView = new ListView(context, null);
        isListViewAdded = false;
        this.mListView.setOnScrollListener(this);
        this.mListView.setOnTouchListener(this);
        this.mListView.setDivider(null);
        this.mListView.setDividerHeight(0);
        this.mSectionHeader = new View(context);
    }

    public SectionHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mListView = new ListView(context, attrs);
        isListViewAdded = false;
        this.mListView.setOnScrollListener(this);
        this.mListView.setOnTouchListener(this);
        this.mListView.setDivider(null);
        this.mListView.setDividerHeight(0);
        this.mSectionHeader = new View(context, attrs);
    }

    public SectionHeaderListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mListView = new ListView(context, attrs, defStyleAttr);
        isListViewAdded = false;
        this.mListView.setOnScrollListener(this);
        this.mListView.setOnTouchListener(this);
        this.mListView.setDivider(null);
        this.mListView.setDividerHeight(0);
        this.mSectionHeader = new View(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mAdapter == null) {
            return;
        }
        //removeAllViews();
        addAndMeasureListView();
        addAndMeasureSectionHeader();

    }

    private void addAndMeasureListView() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) this.mListView.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        int itemWidth = getWidth();
        int itemHeight = getHeight();
        params.gravity = Gravity.TOP;
        //this.mListView.setLayoutParams(params);
        this.mListView.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.EXACTLY | itemHeight);
        if (!isListViewAdded) {
            addViewInLayout(this.mListView, -1, params, true);
            isListViewAdded = true;
        }
        positionListView();
    }

    /**
     * Positions the ListView at the "correct" positions
     */
    private void positionListView() {
        int top = this.mListView.getTop();
        int width = this.mListView.getMeasuredWidth();
        int height = this.mListView.getMeasuredHeight();
        int left = (getWidth() - width) / 2;

        this.mListView.layout(left, top, left + width, top + height);
    }

    private void updateSectionHeader() {
        //Remove previous view
        removeView(this.mSectionHeader);
        this.mSectionHeader = mAdapter.getSectionHeaderView(mTopSection, null, null);
        int itemWidth = getWidth();
        int itemHeight = (int)dpToPx((float)this.mAdapter.getHeightForHeaderView());
        this.mSectionHeader.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.EXACTLY | itemHeight);
        //addAndMeasureSectionHeader();

        positionSectionHeader();
    }

    private void addAndMeasureSectionHeader() {
        if (this.mSectionHeader != null) {
            removeView(this.mSectionHeader);
        }
        LayoutParams params = this.mSectionHeader.getLayoutParams();
        int itemHeight = (int)dpToPx((float)this.mAdapter.getHeightForHeaderView());
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, itemHeight);
        }
        int itemWidth = getWidth();
        this.mSectionHeader.setLayoutParams(params);
        //addView(this.mSectionHeader);
        this.mSectionHeader.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.EXACTLY | itemHeight);
        addViewInLayout(this.mSectionHeader, -1, params, true);
        positionSectionHeader();
    }

    /**
     * Positions the ListView at the "correct" positions
     */
    private void positionSectionHeader() {
        // update top based on scroll position
        int top = this.mSectionHeader.getTop();
        int width = this.mSectionHeader.getMeasuredWidth();
        int height = (int)dpToPx((float)this.mAdapter.getHeightForHeaderView());
        int left = (getWidth() - width) / 2;

        this.mSectionHeader.layout(left, top, left + width, top + height);
    }

    private void updateSectionHeaderPosition() {
        // find the top of the next top section.
        if (mTopSection == mAdapter.numberOfSections() - 1) {
            //Last section header in view.
            return;
        }

        int nextSection = mTopSection + 1;

        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int lastVisiblePosition = mListView.getLastVisiblePosition();
        int nextSectionPosition = mAdapter.rowsUptoSection(nextSection);
        if (!(nextSectionPosition >= firstVisiblePosition && nextSectionPosition <= lastVisiblePosition)) {
            //Next section is out ouf visible region
            return;
        }

        nextSectionPosition = nextSectionPosition - firstVisiblePosition;


        View nextSectionHeader = mListView.getChildAt(nextSectionPosition);
        int nextSectionHeaderTop = nextSectionHeader.getTop();
        Log.d(DEBUG_TAG, "current Section:" + mTopSection + "nextSection:" + nextSection + ", nextSectionHeaderTop:" + nextSectionHeaderTop + ", header height:" + mSectionHeader.getHeight());

        if (nextSectionHeaderTop > this.mListView.getDividerHeight() && nextSectionHeaderTop < (this.mListView.getDividerHeight() + mSectionHeader.getHeight())) {
            //Next section header overlaps. hence section header needs to be moved.
            mSectionHeader.setTop(nextSectionHeaderTop - mSectionHeader.getHeight());
            positionSectionHeader();
        }
        else {
            mSectionHeader.setTop(0);
            positionSectionHeader();
        }

        if (!this.headerFreeze) {
            this.mSectionHeader.setVisibility(GONE);
        }
    }

    //Scroll delegates
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //Log.d(DEBUG_TAG,"scrolled to:"+firstVisibleItem);
        // based on first visible item determine the section header.
        if (mAdapter == null) {
            return;
        }
        int topSection = mAdapter.getSection(firstVisibleItem);
        if (topSection != mTopSection) {
            mTopSection = topSection;
            updateSectionHeader();
        }
        //
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            {
                mTouchStartY = (int) event.getY();
                mListTopStart = getChildAt(0).getTop();
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                int scrolledDistance = (int) event.getY() - mTouchStartY;
                mListTop = mListTopStart + scrolledDistance;
                updateSectionHeaderPosition();
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        updateSectionHeaderPosition();
                    }
                }, 200);
                break;
            }

            default:
                break;
        }
        return false;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }
}
