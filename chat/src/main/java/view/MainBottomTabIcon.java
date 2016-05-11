package view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.chat.R;


/**
 * Created by Administrator on 2015/12/19.
 */
//public class MainBottomTabIcon extends LinearLayout{
//    private ViewPager mViewPager;
//    int mTextNormalColor, mTextSelectedColor;
//    private View[] mIconLayouts;
//
//    private String mTitles[] = {"微信", "通讯录", "发现", "我"};
//    private int mIconRes[][] = {
//            {R.drawable.icon_main_home_normal, R.drawable.icon_main_home_selected},
//            {R.drawable.icon_main_category_normal, R.drawable.icon_main_category_selected},
//            {R.drawable.icon_main_service_normal, R.drawable.icon_main_service_selected},
//            {R.drawable.icon_main_mine_normal, R.drawable.icon_main_mine_selected}
//    };
//
//    public MainBottomTabIcon(Context context) {
//        this(context, null);
//    }
//
//    public MainBottomTabIcon(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public MainBottomTabIcon(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context, attrs, defStyleAttr);
//    }
//
//    public void init(Context context,AttributeSet attrs,int defStyleAttr){
//        mTextNormalColor = getResources().getColor(R.color.main_bottom_tab_textcolor_normal);
//        mTextSelectedColor = getResources().getColor(R.color.main_bottom_tab_textcolor_selected);
//    }
//
//    public void setViewPager(ViewPager viewPager) {
//        removeAllViews();
//        mViewPager = viewPager;
//        if (viewPager != null && viewPager.getAdapter() != null) {
//            populateLayout();
//        }
//    }
//    private void populateLayout(){
//        final PagerAdapter adapter = mViewPager.getAdapter();
//        mIconLayouts = new View[adapter.getCount()];
//
//        for (int i = 0;i < adapter.getCount();i++){
//            final View tabView = LayoutInflater.from(getContext()).inflate(R.layout.layout_mainbottom_tab,this,false);
//            mIconLayouts[i] = tabView;
//            TabIconView iconView = (TabIconView)tabView.findViewById(R.id.main_bottom_tab_icon);
//            iconView.init(mIconRes[i][0],mIconRes[i][1]);
//            TextView textView = (TextView)tabView.findViewById((R.id.main_bottom_tab_text));
//            textView.setText(mTitles[i]);
//
//            if (tabView == null) {
//                throw new IllegalStateException("tabView is null.");
//            }
//
//            LayoutParams lp = (LayoutParams)tabView.getLayoutParams();
//            lp.width = 0;
//            lp.weight = 1;
//            addView(tabView);
//        }
//    }
//}


import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by wuyexiong on 4/25/15.
 */
public class MainBottomTabIcon extends LinearLayout {

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private ArgbEvaluator mColorEvaluator;
    int mTextNormalColor, mTextSelectedColor;

    private int mLastPosition;
    private int mSelectedPosition;
    private float mSelectionOffset;

    private String mTitles[] = {"微信", "通讯录", "发现", "我"};
    private int mIconRes[][] = {
            {R.drawable.icon_main_home_normal, R.drawable.icon_main_home_selected},
            {R.drawable.icon_main_category_normal, R.drawable.icon_main_category_selected},
            {R.drawable.icon_main_service_normal, R.drawable.icon_main_service_selected},
            {R.drawable.icon_main_mine_normal, R.drawable.icon_main_mine_selected}
    };

    private View[] mIconLayouts;

    public MainBottomTabIcon(Context context) {
        this(context, null);
    }

    public MainBottomTabIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainBottomTabIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mColorEvaluator = new ArgbEvaluator();
        mTextNormalColor = getResources().getColor(R.color.main_bottom_tab_textcolor_normal);
        mTextSelectedColor = getResources().getColor(R.color.main_bottom_tab_textcolor_selected);
    }

    public void setViewPager(ViewPager viewPager) {
        removeAllViews();
        mViewPager = viewPager;
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabLayout();
        }
    }

    private void populateTabLayout() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();
        mIconLayouts = new View[adapter.getCount()];

        for (int i = 0; i < adapter.getCount(); i++) {

            final View tabView = LayoutInflater.from(getContext()).inflate(R.layout.layout_mainbottom_tab, this, false);
            mIconLayouts[i] = tabView;
            TabIconView iconView = (TabIconView) tabView.findViewById(R.id.main_bottom_tab_icon);
            iconView.init(mIconRes[i][0], mIconRes[i][1]);
            TextView textView = (TextView) tabView.findViewById(R.id.main_bottom_tab_text);
            textView.setText(mTitles[i]);

            if (tabView == null) {
                throw new IllegalStateException("tabView is null.");
            }

            LayoutParams lp = (LayoutParams) tabView.getLayoutParams();
            lp.width = 0;
            lp.weight = 1;

            tabView.setOnClickListener(tabClickListener);
            addView(tabView);

            if (i == mViewPager.getCurrentItem()) {
                iconView.transformPage(0);
                //tabView.setSelected(true);
                textView.setTextColor(mTextSelectedColor);
            }
        }
    }

    public class TabClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            for(int i = 0;i < getChildCount();i++){
                if(v == getChildAt(i)){
                    mViewPager.setCurrentItem(i, false);
                }
            }
        }
    }

    public class InternalViewPagerListener implements ViewPager.OnPageChangeListener{
        int mScrollState;
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            onViewPagerPageChanged(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < getChildCount(); i++) {
                ((TabIconView) mIconLayouts[i].findViewById(R.id.main_bottom_tab_icon))
                        .transformPage(position == i ? 0 : 1);
                ((TextView) mIconLayouts[i].findViewById(R.id.main_bottom_tab_text))
                        .setTextColor(position == i ? mTextSelectedColor : mTextNormalColor);
            }
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                onViewPagerPageChanged(position, 0f);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }
    }

    private void onViewPagerPageChanged(int position, float positionOffset){
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        if(mSelectionOffset == 0f && mSelectedPosition != mLastPosition){
            mLastPosition = mSelectedPosition;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int childCount = getChildCount();
        if (childCount > 0) {
            if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {

                View selectedTab = getChildAt(mSelectedPosition);
                View nextTab = getChildAt(mSelectedPosition + 1);

                View selectedIconView = ((LinearLayout) selectedTab).getChildAt(0);
                View nextIconView = ((LinearLayout) nextTab).getChildAt(0);

                View selectedTextView = ((LinearLayout) selectedTab).getChildAt(1);
                View nextTextView = ((LinearLayout) nextTab).getChildAt(1);

                //draw icon alpha
                if (selectedIconView instanceof TabIconView && nextIconView instanceof TabIconView) {
                    ((TabIconView) selectedIconView).transformPage(mSelectionOffset);
                    ((TabIconView) nextIconView).transformPage(1 - mSelectionOffset);
                }

                //draw text color
                Integer selectedColor = (Integer) mColorEvaluator.evaluate(mSelectionOffset,
                        mTextSelectedColor,
                        mTextNormalColor);
                Integer nextColor = (Integer) mColorEvaluator.evaluate(1 - mSelectionOffset,
                        mTextSelectedColor,
                        mTextNormalColor);

                if (selectedTextView instanceof TextView && nextTextView instanceof TextView) {
                    ((TextView) selectedTextView).setTextColor(selectedColor);
                    ((TextView) nextTextView).setTextColor(nextColor);
                }
            }
        }
    }
}
