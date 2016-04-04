package net.blaklizt.streets.android.sidemenu.util;

import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.StreetsAbstractView;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.sidemenu.animation.FlipAnimation;
import net.blaklizt.streets.android.sidemenu.interfaces.Resourceble;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class ViewAnimator<T extends Resourceble> {
    private final int ANIMATION_DURATION = 175;
    public static final int CIRCULAR_REVEAL_ANIMATION_DURATION = 500;

    private static final String TAG = StreetsCommon.getTag(ViewAnimator.class);
    private AppCompatActivity appCompatActivity;
    private List<T> list;

    private List<View> viewList = new ArrayList<>();
    private StreetsAbstractView streetsFragment;
    private DrawerLayout drawerLayout;
    private ViewAnimatorListener animatorListener;

    public ViewAnimator(AppCompatActivity activity,
                        List<T> items,
                        StreetsAbstractView streetsFragment,
                        final DrawerLayout drawerLayout,
                        ViewAnimatorListener animatorListener) {
        this.appCompatActivity = activity;
        this.list = items;
        this.streetsFragment = streetsFragment;
        this.drawerLayout = drawerLayout;
        this.animatorListener = animatorListener;
    }

    public void showMenuContent() {
        Log.i(TAG, "Showing menu content");
        setViewsClickable(false);
        viewList.clear();
        double size = list.size();
        for (int i = 0; i < size; i++) {
            View viewMenu = appCompatActivity.getLayoutInflater().inflate(R.layout.menu_list_item, null);
            Log.i(TAG, format("Processing menu item %s/%s. Name: %s", i, size, viewMenu.getTag()));
            final int finalI = i;
            viewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, format("Executing onClick for viewMenu %s", view.getTag()));
                    int[] location = {0, 0};
                    view.getLocationOnScreen(location);
                    switchItem(list.get(finalI), location[1] + view.getHeight() / 2);
                }
            });
            ((ImageView) viewMenu.findViewById(R.id.menu_item_image)).setImageResource(list.get(i).getImageRes());
            viewMenu.setVisibility(View.GONE);
            viewMenu.setEnabled(false);
            viewList.add(viewMenu);
            animatorListener.addViewToContainer(viewMenu);
            final double position = i;
            final double delay = 3 * ANIMATION_DURATION * (position / size);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (position < viewList.size()) {
                        animateView((int) position);
                    }
                    if (position == viewList.size() - 1) {
                        Log.i(TAG, format("Executing postDelayed for streetsFragment %s", streetsFragment.getClassName()));
                        setViewsClickable(true);
                    }
                }
            }, (long) delay);
        }

    }

    private void hideMenuContent() {

        Log.i(TAG, "Hide menu content0");
        setViewsClickable(false);
        double size = list.size();
        for (int i = list.size(); i >= 0; i--) {
            final double position = i;
            final double delay = 3 * ANIMATION_DURATION * (position / size);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (position < viewList.size()) {
                        animateHideView((int) position);
                    }
                }
            }, (long) delay);
        }

    }

    private void setViewsClickable(boolean clickable) {
        animatorListener.disableHomeButton();
        for (View view : viewList) {
            view.setEnabled(clickable);
        }
    }

    private void animateView(int position) {
        final View view = viewList.get(position);
        view.setVisibility(View.VISIBLE);
        FlipAnimation rotation =
                new FlipAnimation(90, 0, 0.0f, view.getHeight() / 2.0f);
        rotation.setDuration(ANIMATION_DURATION);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(rotation);
    }

    private void animateHideView(final int position) {
        final View view = viewList.get(position);
        FlipAnimation rotation =
                new FlipAnimation(0, 90, 0.0f, view.getHeight() / 2.0f);
        rotation.setDuration(ANIMATION_DURATION);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.INVISIBLE);
                if (position == viewList.size() - 1) {
                    animatorListener.enableHomeButton();
                    drawerLayout.closeDrawers();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(rotation);
    }

    private void switchItem(Resourceble slideMenuItem, int topPosition) {

        Log.i(TAG, "Switch Item");
        this.streetsFragment = animatorListener.onSwitch(slideMenuItem, streetsFragment, topPosition);
        hideMenuContent();
    }

    public interface ViewAnimatorListener {

        StreetsAbstractView onSwitch(Resourceble slideMenuItem, StreetsAbstractView streetsFragment, int position);

        void disableHomeButton();

        void enableHomeButton();

        void addViewToContainer(View view);

    }
}
