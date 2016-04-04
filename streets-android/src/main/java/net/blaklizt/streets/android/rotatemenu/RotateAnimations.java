package net.blaklizt.streets.android.rotatemenu;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class RotateAnimations {

	public static void startAnimation(ViewGroup viewGroup, int duration, int startOffSet) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			viewGroup.getChildAt(i).setVisibility(View.VISIBLE);
			viewGroup.getChildAt(i).setFocusable(true);
			viewGroup.getChildAt(i).setClickable(true);
		}

		Animation animation;

		animation = new RotateAnimation(0, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
		animation.setFillAfter(true);
		animation.setDuration(duration);
		animation.setStartOffset(startOffSet);
		viewGroup.startAnimation(animation);

	}

}
