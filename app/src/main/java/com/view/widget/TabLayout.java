package com.view.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.C;
import com.base.util.DensityUtil;
import com.base.util.ImageUtil;
import com.data.entity._User;
import com.ui.main.R;
import com.ui.user.UserActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baixiaokang on 16/12/1.
 */

public class TabLayout extends LinearLayout {
    public static ViewPager mPager;
    View view;
    Context context;
    public int currIndex;
    private int screenW;

    List<_User> m_Users;
    LinearLayout ll_tabs;

    private List<TabView> mTabViews;
    private List<Integer> mHeights;
    private List<Integer> mOldHeights;

    SlowScrollView hsrcoll;
    int width = 0;
    int oldposition = 0;
    int oldwidth = 0;
    boolean isRight = true;
    int speed = 1;
    float oldx = 0;

    int defaultH = DensityUtil.dip2px(33);

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (isRight) {
                oldwidth = oldwidth + speed;
                if (oldwidth <= width) {
                    hsrcoll.smoothScrollTo(oldwidth, 0);
                    handler.postDelayed(this, 10);
                } else {
                    oldwidth = width;
                }

            } else {
                oldwidth = oldwidth - speed;
                if (oldwidth >= width) {
                    hsrcoll.smoothScrollTo(oldwidth, 0);
                    handler.postDelayed(this, 10);
                } else {
                    oldwidth = width;
                }
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    public TabLayout(Context context) {
        super(context);
        initView(context);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.tabs_layout, null);
        addView(view);
        init();

    }


    private void init() {
        ll_tabs = (LinearLayout) view.findViewById(R.id.ll_tabs);
        hsrcoll = (SlowScrollView) view.findViewById(R.id.rl_bottom);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenW = dm.widthPixels;
        m_Users = new ArrayList<>();

    }

    public void setM_Users(List<_User> users) {
        m_Users = users;
        InitTabViews();
        InitViewPager();
    }


    private void InitTabViews() {
        mTabViews = new ArrayList<>();
        mOldHeights = new ArrayList<>();
        mHeights = new ArrayList<>();
        for (int i = 0; i < m_Users.size(); i++) {
            mTabViews.add(setTabView(i));
            ll_tabs.addView(mTabViews.get(i));
            mOldHeights.add(defaultH);
            mHeights.add(defaultH);
            mTabViews.get(i).setOnTouchListener(new MyTouchListener(i));
        }
    }


    public void Touched(int index) {
        currIndex = index;
        mHeights = new ArrayList<>();
        for (int i = 0; i < mTabViews.size(); i++) {
            if (i == index) {
                Animation translateAnimation = new TranslateAnimation(0, 0,
                        mOldHeights.get(i), 0);
                translateAnimation.setFillAfter(true);
                translateAnimation
                        .setInterpolator(AnimationUtils
                                .loadInterpolator(
                                        context,
                                        android.R.anim.accelerate_decelerate_interpolator));
                translateAnimation.setDuration(150);
                mTabViews.get(i).startAnimation(translateAnimation);
                mHeights.add(0);
            } else if (Math.abs(i - currIndex) < 7) {
                mHeights.add(5 + 9 * Math.abs(i - currIndex));
                Animation translateAnimation = new TranslateAnimation(0, 0,
                        mOldHeights.get(i), mHeights.get(i));
                translateAnimation.setFillAfter(true);
                translateAnimation.setInterpolator(AnimationUtils
                        .loadInterpolator(context,
                                android.R.anim.overshoot_interpolator));
                translateAnimation.setDuration(150);
                mTabViews.get(i).startAnimation(translateAnimation);
            } else {
                mHeights.add(defaultH);
            }
        }
        mOldHeights = mHeights;
    }

    public void Selected(int index) {

        currIndex = index;
        mPager.setCurrentItem(index, true);
        for (int i = 0; i < mTabViews.size(); i++) {
            if (i == index) {
                Animation translateAnimation = new TranslateAnimation(0, 0,
                        mOldHeights.get(i), 0);
                translateAnimation.setFillAfter(true);
                translateAnimation
                        .setInterpolator(AnimationUtils
                                .loadInterpolator(
                                        context,
                                        android.R.anim.accelerate_decelerate_interpolator));
                translateAnimation.setDuration(150);
                mTabViews.get(i).startAnimation(translateAnimation);
                mTabViews.get(i).setImage(m_Users.get(i).face);
            } else if (Math.abs(i - currIndex) < 7) {
                mTabViews.get(i).setImage(m_Users.get(i).face);
                Animation translateAnimation = new TranslateAnimation(0, 0,
                        mOldHeights.get(i), defaultH);
                translateAnimation.setFillAfter(true);
                translateAnimation.setInterpolator(AnimationUtils
                        .loadInterpolator(context,
                                android.R.anim.overshoot_interpolator));
                translateAnimation.setDuration(150);
                mTabViews.get(i).startAnimation(translateAnimation);
                mHeights.add(defaultH);
            } else {
                mTabViews.get(i).releaseImage();
                mHeights.add(defaultH);
            }
        }
        scrollTo();
    }

    private void scrollTo() {
        width = (screenW / 7) * (currIndex - 3);
        if (width > oldposition) {
            speed = (width - oldposition) / (screenW / 7) + 1;
            isRight = true;
            oldposition = width;
        } else if (width < oldposition) {
            speed = (oldposition - width) / (screenW / 7) + 1;
            isRight = false;
            oldposition = width;
        }
        handler.post(runnable);
    }

    private TabView setTabView(int i) {
        TabView mTabView = new TabView(context, null);
        LinearLayout.LayoutParams lp;
        lp = new LinearLayout.LayoutParams(screenW / 7, screenW * 3 / 7);
        lp.setMargins(0, 0, 0, -screenW / 7);
        mTabView.setLayoutParams(lp);
        return mTabView;
    }

    public void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(new ViewPagerAdapter());
        mPager.setCurrentItem(0);
        Selected(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class ViewPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = LayoutInflater.from(context).inflate(R.layout.viewpager_item, null);
            ImageView image = (ImageView) v.findViewById(R.id.image);
            ImageUtil.loadImg(image, m_Users.get(position).face);
            image.setOnClickListener(m ->
                    ActivityCompat.startActivity((Activity) context, new Intent(context, UserActivity.class).putExtra(C.HEAD_DATA, m_Users.get(position))
                            , ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, image, C.TRANSLATE_VIEW).toBundle())
            );
            container.addView(v);
            return v;
        }

        @Override
        public int getCount() {
            return m_Users.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }


    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPx) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageSelected(int i) {
            Selected(i);
        }
    }

    public class MyTouchListener implements OnTouchListener {
        private int index = 0;

        public MyTouchListener(int i) {
            index = i;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldx = event.getX();
                    if (oldx % (screenW / 7) != (screenW / 7) / 2) {
                        oldx = oldx - oldx % (screenW / 7) + (screenW / 7) / 2;
                    }
                    currIndex = index;
                    Touched(currIndex);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float newx = event.getX();
                    currIndex = index + (int) (newx - oldx) / (screenW / 7);
                    Touched(currIndex);
                    return true;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    Selected(currIndex);
                    return true;
                default:
                    return false;
            }

        }
    }

}
