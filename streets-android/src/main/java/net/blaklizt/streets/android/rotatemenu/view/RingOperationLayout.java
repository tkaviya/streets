package net.blaklizt.streets.android.rotatemenu.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.rotatemenu.RotateAnimations;

import java.util.Timer;
import java.util.TimerTask;

public class RingOperationLayout extends LinearLayout {

	private ImageButton mHomeButton;

	private ImageButton mMenuButton;

	private RelativeLayout mLevel1;

	private RelativeLayout mLevel2;

	private RelativeLayout mLevel3;

	private boolean mIsLevel2Show = false;

	private boolean mIsLevel3Show = false;

	public static boolean mLevel2ISRuning = false;

	public static boolean mLevel3ISRuning = false;

	private View mConvertView;

	private LayoutInflater mLayoutInflater = null;

	private CoverRingImageView mDrawImageView;

	private Timer mTimer;

	public static int mLevel1Width;

	public static int mLevel2Width;

	public static int mLevel3Width;

	private Boolean mHasGotWidth = false;

	private static final int WANT_TO_GET_MUNU_WIDTH_MESSAGE = 1;

	public RingOperationLayout(Context context, AttributeSet attrs) {
		super(context);
		initialize(context);

		mMenuButton.setOnClickListener(v -> {

            if (!mLevel3ISRuning) {
                mLevel3ISRuning = true;

                if (mIsLevel3Show) {
                    // mMenuButton.setClickable(false);
                    mDrawImageView.hideOutsideCircle();
                    RotateAnimations.startAnimation(mLevel3, 1500, 0);
                } else {
                    // showCircleLevelOne();
                    mDrawImageView.showOutsideCircle();
                    RotateAnimations.startAnimation(mLevel3, 1500, 0);
                }

                mIsLevel3Show = !mIsLevel3Show;
            }
        });

		mHomeButton.setOnClickListener(v -> {

            if (!mLevel3ISRuning && !mLevel2ISRuning) {
                mLevel2ISRuning = true;

                if (!mIsLevel2Show) {
                    mDrawImageView.showInsideCircle();
                    RotateAnimations.startAnimation(mLevel2, 1500, 0);
                } else {
                    if (mIsLevel3Show) {
                        mDrawImageView.hideOutsideCircle();
                        RotateAnimations.startAnimation(mLevel3, 1500, 0);
                        mDrawImageView.hideInsideCircle();
                        RotateAnimations.startAnimation(mLevel2, 1500, 500);
                        mIsLevel3Show = !mIsLevel3Show;
                    } else {
                        mDrawImageView.hideInsideCircle();
                        RotateAnimations.startAnimation(mLevel2, 1500, 0);
                    }
                }
                mIsLevel2Show = !mIsLevel2Show;
            }
        });

		addView(mConvertView);

		final Handler myHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == WANT_TO_GET_MUNU_WIDTH_MESSAGE) {
					if (mLevel3.getWidth() != 0 && mLevel2.getWidth() != 0 && mLevel1.getWidth() != 0) {
						mLevel1Width = mLevel1.getWidth();
						mLevel2Width = mLevel2.getWidth();
						mLevel3Width = mLevel3.getWidth();
						System.out.println(mLevel3Width + "");
						mTimer.cancel();
						mHasGotWidth = true;
					}
				}
			}
		};

		if (!mHasGotWidth) {
			mTimer = new Timer();
			TimerTask task = new TimerTask() {
				public void run() {
					Message message = new Message();
					message.what = WANT_TO_GET_MUNU_WIDTH_MESSAGE;
					myHandler.sendMessage(message);
				}
			};
			mTimer.schedule(task, 10, 1000);
		}
	}

	private void initialize(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		mConvertView = mLayoutInflater.inflate(R.layout.custom_ring_operation_layout, null);
		mDrawImageView = (CoverRingImageView) mConvertView.findViewById(R.id.drawImageView);

		mHomeButton = (ImageButton) mConvertView.findViewById(R.id.home);
		mMenuButton = (ImageButton) mConvertView.findViewById(R.id.menu);

		mLevel1 = (RelativeLayout) mConvertView.findViewById(R.id.level1);
		mLevel2 = (RelativeLayout) mConvertView.findViewById(R.id.level2);
		mLevel3 = (RelativeLayout) mConvertView.findViewById(R.id.level3);

	}

}
