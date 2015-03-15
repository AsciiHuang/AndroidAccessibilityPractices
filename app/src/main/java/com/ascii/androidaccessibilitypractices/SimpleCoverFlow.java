package com.ascii.androidaccessibilitypractices;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SimpleCoverFlow extends LinearLayout implements View.OnTouchListener, View.OnKeyListener {

	public static final int FLOW_TYPE_NORMAL = 0;
	public static final int FLOW_TYPE_NON_PREVSONG = 1;
	public static final int FLOW_TYPE_NON_NEXTSONG = 2;
	public static final int FLOW_TYPE_SINGLE_ALBUM = 3;
	/** onKey Event Delay 時間 (單位：毫秒) */
	private static final long ON_KEY_DELAY_TIME = 500;

	private final float CUSTOM_ACTION_SPEED = 0.3f;
	private final int POS_TYPE_PREV = -1;
	private final int POS_TYPE_CURR = 0;
	private final int POS_TYPE_NEXT = 1;

	private static Context mContext;
	private OnSimpleCoverFlowCommand mCallback;

	private Handler mHandler = new Handler();
	private Handler mSubHandler = new Handler();
	/** 處理 OnKey() Event 的 Handler */
	private Handler mOnKeyHandler = new Handler();
	private HorizontalScrollView mHScrollView;
	private LinearLayout mSubLayout;
	private Bitmap mBmpDefaultCover;
	private Bitmap mBmpPrevCover;
	private Bitmap mBmpCurrCover;
	private Bitmap mBmpNextCover;
	private ImageView mImgPrevCover;
	private ImageView mImgCurrCover;
	private ImageView mImgNextCover;
	private ImageView mImgCoverBtn;
	private int mLength;
	private int mFlowType;
	private File mFilePath;
	private int mNonScrollLeftLimit; // 視為未換曲的最小值
	private int mScrollMiddle; // 中間圖片的 X 值
	private int mNonScrollRightLimit; // 視為未換曲的最大值
	private int mScrollRightLimit; // 能捲動到的最大值
	private boolean mValidAction; // 每次 Gesture Down 只可以有一次導至上、下首的捲動，以此為 Flag
	private boolean mNotifyStart;
	private int mDownPosition;
	private int mUpPosition;
	private long mDownTime;
	private long mUpTime;

	private boolean isDestroy = false;
	private boolean isInitComponent; // 是否為初始狀態

	private LoadCoverTask prevCoverLoadTask = new LoadCoverTask();
	private LoadCoverTask nextCoverLoadTask = new LoadCoverTask();

	public SimpleCoverFlow(Context context) {
		super(context);
		mContext = context;
		final float scale = getResources().getDisplayMetrics().density;
		initComponent((int)(300 * scale));
	}

	public SimpleCoverFlow(Context context, int size) {
		super(context);
		mContext = context;
		initComponent(size);
	}

	public SimpleCoverFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		final float scale = getResources().getDisplayMetrics().density;
		initComponent((int)(300 * scale));
	}

	private void initComponent(int size) {
		isInitComponent = true;
		int color = 0xff000000;
		mFlowType = FLOW_TYPE_NORMAL;
		mDownPosition = 0;
		mUpPosition = 0;
		mDownTime = 0;
		mUpTime = 0;
		mFilePath = null;
		refreshLengthParam(size);
		mValidAction = false;

		setLayoutParams(new LayoutParams(size * 3, size * 3));
		setBackgroundColor(color);

		mHScrollView = new HorizontalScrollView(this.getContext());
		mHScrollView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mHScrollView.setHorizontalFadingEdgeEnabled(false);
		mHScrollView.setHorizontalScrollBarEnabled(false);
		mHScrollView.setOnTouchListener(this);
		mHScrollView.setOnKeyListener(this);	// ticket.14594 : 接收 D-Pad 滑動 Event

		mSubLayout = new LinearLayout(mHScrollView.getContext());
		mSubLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mSubLayout.setBackgroundColor(color);
		mSubLayout.setOrientation(HORIZONTAL);

		mImgPrevCover = new ImageView(mSubLayout.getContext());
		mImgPrevCover.setLayoutParams(new LayoutParams(size, size));
		mImgPrevCover.setBackgroundColor(color);
		mImgCurrCover = new ImageView(mSubLayout.getContext());
		mImgCurrCover.setLayoutParams(new LayoutParams(size, size));
		mImgCurrCover.setBackgroundColor(color);

		mImgNextCover = new ImageView(mSubLayout.getContext());
		mImgNextCover.setLayoutParams(new LayoutParams(size, size));
		mImgNextCover.setBackgroundColor(color);

		mSubLayout.addView(mImgPrevCover);
		mSubLayout.addView(mImgCurrCover);
		mSubLayout.addView(mImgNextCover);

		mHScrollView.addView(mSubLayout);

		addView(mHScrollView);
		initPosition();
	}

	/**
	 * 重整 Leangth 相關參數
	 * @param size ： [單位 px]
	 */
	private void refreshLengthParam(int size) {
		mLength = size;
		mScrollMiddle = mLength;
		mNonScrollLeftLimit = (int)(mLength * 0.5);
		mNonScrollRightLimit = (int)(mLength * 1.5);
		mScrollRightLimit = mLength * 2;
	}

	/**
	 * 設定 CoverSize
	 * @param size ：[單位dp]
	 */
	public void setCoverSize(int size) {
		final float scale = getResources().getDisplayMetrics().density;
		int scaleSize = (int)(size * scale);
		refreshLengthParam(scaleSize);
		LayoutParams layoutParams = new LayoutParams(scaleSize, scaleSize);
		if (mImgPrevCover != null) {
			mImgPrevCover.setLayoutParams(layoutParams);
		}
		if (mImgCurrCover != null) {
			mImgCurrCover.setLayoutParams(layoutParams);
		}
		if (mImgNextCover != null) {
			mImgNextCover.setLayoutParams(layoutParams);
		}
	}

	/**
	 * 取得 CoverSize
	 * @return Size [單位 px]
	 */
	public int getCoverSize(){
		return mLength;
	}

	public void setImgCoverBtn(ImageView mImgCoverBtn) {
		this.mImgCoverBtn = mImgCoverBtn;
	}

	public void setStoreFilePath(File path) {
		mFilePath = path;
	}

	/**
	 * 設定是否可 scroll cover
	 * @param scrollable : true-可滑動 / false-不可滑動
	 */
	public void setScrollable(boolean scrollable) {
		if (!scrollable) {
			mHScrollView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (mCallback != null) {
						mCallback.onSimpleCoverFlowTouch(v, event);
					}
					return true;
				}
			});

			// ticket.14594: follow 中，不可 D-Pad 滑動換 Cover
			mHScrollView.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					cancelScroll();
					return false;
				}
			});
		}
	}

	public void setDefaultCover(Bitmap bmp) {
		mBmpDefaultCover = bmp;
		if (mBmpDefaultCover != null) {
			setImgPrevBitmap(mBmpDefaultCover);
			setImgCurrBitmap(mBmpDefaultCover);
			setImgNextBitmap(mBmpDefaultCover);
			initPosition();
		}
	}

	public void setFlowType(int type) {
		if (mImgPrevCover != null && mImgNextCover != null) { return; }

		mFlowType = type;
		refreshLengthParam(mLength);

		if (mFlowType == FLOW_TYPE_NON_PREVSONG) {
			setImgPrevBitmap(null);
		} else if (mFlowType == FLOW_TYPE_NON_NEXTSONG) {
			setImgNextBitmap(null);
		} else if (mFlowType == FLOW_TYPE_SINGLE_ALBUM) {
			setImgPrevBitmap(null);
			setImgNextBitmap(null);
		} else {
			mImgPrevCover.setLayoutParams(new LayoutParams(mLength, mLength));
			mImgNextCover.setLayoutParams(new LayoutParams(mLength, mLength));
		}
	}

	public void initPosition() {
		initPosition(false);
	}

	public void initPosition(final boolean notify) {
		if (mHScrollView.getScrollX() == mScrollMiddle) {
			if (notify && mCallback != null) {
				mCallback.onCancelGesture();
			}
			return;
		}

		// 避免快速旋轉初始時會出現上一張圖片, 先設成預設圖
		if(isInitComponent) {
			if (mBmpDefaultCover != null) {
				setImgPrevBitmap(mBmpDefaultCover);
				setImgCurrBitmap(mBmpDefaultCover);
				setImgNextBitmap(mBmpDefaultCover);
			}
			isInitComponent = false;
		}

		mHScrollView.post(new Runnable() {

			public void run() {
				mHScrollView.scrollTo(mScrollMiddle, 0);
			}
		});
		mHandler.postDelayed(new Runnable() {

			public void run() {
				if (mHScrollView.getScrollX() != mScrollMiddle) {
					mHScrollView.scrollTo(mScrollMiddle, 0);
					isInitComponent = false;
				}
				if (notify && mCallback != null) {
					mCallback.onCancelGesture();
				}
			}
		}, 600);
	}

	public void setPrevCover(Bitmap bmp) {
		if (bmp != null) {
			setImgPrevBitmap(bmp);
			mBmpPrevCover = BitmapUtils.checkRecycle(mBmpPrevCover, bmp);
		}
	}

	public void setCurrCover(Bitmap bmp) {
		// ticket.19371:java.lang.RuntimeException: Canvas: trying to use a recycled bitmap
		if (bmp != null && !bmp.isRecycled()) {
			setImgCurrBitmap(bmp);
			mImgCoverBtn.setImageBitmap(bmp); // 解決手勢換歌，歌詞bitmap沒更新發生isRecycle問題
			mCallback.setCurrCover();
			mBmpCurrCover = BitmapUtils.checkRecycle(mBmpCurrCover, bmp);
		}
	}

	public void setNextCover(Bitmap bmp) {
		if (bmp != null) {
			setImgNextBitmap(bmp);
			mBmpNextCover = BitmapUtils.checkRecycle(mBmpNextCover, bmp);
		}
	}

	public void setPrevCoverToDefault() {
		if (mBmpDefaultCover != null) {
			setImgPrevBitmap(mBmpDefaultCover);
		}
		BitmapUtils.recycleBitmap(mBmpPrevCover);
		mBmpPrevCover = null;
	}

	public void setNextCoverToDefault() {
		if (mBmpDefaultCover != null) {
			setImgNextBitmap(mBmpDefaultCover);
		}
		BitmapUtils.recycleBitmap(mBmpNextCover);
		mBmpNextCover = null;
	}

	public void setCurrentCoverToDefault() {
		if (mBmpDefaultCover != null) {
			setImgCurrBitmap(mBmpDefaultCover);
		}
		BitmapUtils.recycleBitmap(mBmpCurrCover);
		mBmpCurrCover = null;
	}

	private void setCoverBtnToDefault() {
		// ticket.19371:java.lang.RuntimeException: Canvas: trying to use a recycled bitmap
		if(mImgCoverBtn != null && mBmpDefaultCover != null && !mBmpDefaultCover.isRecycled()) {
			mImgCoverBtn.setImageBitmap(mBmpDefaultCover);
		}
	}

	public void setPrevCover(String PrevCoverURL, String albumid) {
		try {
			setPrevCover(new URL(PrevCoverURL), albumid);
		} catch (Exception e) {
		}
	}

	public void setNextCover(String NextCoverURL, String albumid) {
		try {
			setNextCover(new URL(NextCoverURL), albumid);
		} catch (MalformedURLException e) {
		}
	}

	public void setPrevCover(URL PrevCoverURL, String albumid) {
		mBmpPrevCover = null;
		if (mImgPrevCover == null) {
			return;
		}
		setImgPrevBitmap(mBmpDefaultCover);
		if (prevCoverLoadTask != null) {
			prevCoverLoadTask.cancel();
		}
		prevCoverLoadTask = new LoadCoverTask(PrevCoverURL, POS_TYPE_PREV, albumid);
		prevCoverLoadTask.execute();
	}

	public void setNextCover(URL NextCoverURL, String albumid) {
		mBmpNextCover = null;
		if (mImgNextCover == null) {
			return;
		}
		setImgNextBitmap(mBmpDefaultCover);
		if (nextCoverLoadTask != null) {
			nextCoverLoadTask.cancel();
		}
		nextCoverLoadTask = new LoadCoverTask(NextCoverURL, POS_TYPE_NEXT, albumid);
		nextCoverLoadTask.execute();
	}

	@Override
	public void computeScroll() {
		if (mValidAction && !mNotifyStart) {
			if (mCallback != null && mHScrollView.getScrollX() != mScrollMiddle) {
				mCallback.onGestureStart();
				mNotifyStart = true;
			}
		}
	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (mCallback != null) {
			mCallback.onSimpleCoverFlowTouch(arg0, arg1);
		}

		// 這裡只會在手指與螢幕觸控時才會收到事件，所以只能相信這裡的 MotionEvent，在此處拿到的 ScrollX 和畫面上顯示的不會相符，必須到 computeScroll 去判斷正確位置
		if (mHScrollView == arg0) {
			if (MotionEvent.ACTION_UP == arg1.getAction()) {
				mUpPosition = mHScrollView.getScrollX();
				mUpTime = System.currentTimeMillis();
				handleGestureUp();
			} else if (MotionEvent.ACTION_DOWN == arg1.getAction()) {
				boolean illegalPrev = ((mHScrollView.getScrollX() < mScrollMiddle) && (mFlowType == FLOW_TYPE_NON_PREVSONG));
				boolean illegalNext = ((mHScrollView.getScrollX() > mScrollMiddle) && (mFlowType == FLOW_TYPE_NON_NEXTSONG));
				mValidAction = !(illegalPrev || illegalNext);
				mNotifyStart = false;
				mDownPosition = mHScrollView.getScrollX();
				mDownTime = System.currentTimeMillis();
			}
		}
		return false;
	}

	/**
	 * 實作 OnKeyListener
	 * ticket.14594 : 解決 NowPlaying 用 D-Pad 滑動 (手機有滾珠控制上下左右) 會出現封面卡一半的問題
	 * 此 Event 可接收 "滾珠控制上下左右"
	 * @param v
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (mHScrollView == v) {
			// 使用 Handler 目的讓 滾動時觸發 OnKey() 不要重覆執行多次，換太多首歌
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				// 滾動向左滑，換下一首歌曲
				mOnKeyHandler.removeCallbacks(getPrevSongCommandRunnable);
				mOnKeyHandler.postDelayed(getPrevSongCommandRunnable, ON_KEY_DELAY_TIME);
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				// 滾動向右滑，換上一首歌曲
				mOnKeyHandler.removeCallbacks(getNextSongCommandRunnable);
				mOnKeyHandler.postDelayed(getNextSongCommandRunnable, ON_KEY_DELAY_TIME);
			}
		}
		return false; // 須回傳 false，表示不觸發此事件
	}

	/**
	 * 取得 Runnable 換上一首歌曲
	 */
	private Runnable getPrevSongCommandRunnable = new Runnable() {
		@Override
		public void run() {
			prevSongCommand(true);
		}
	};

	/**
	 * 取得 Runnable 換下一首歌曲
	 */
	private Runnable getNextSongCommandRunnable = new Runnable() {
		@Override
		public void run() {
			nextSongCommand(true);
		}
	};

	private void handleGestureUp() {
		boolean bToLeft = (mDownPosition >= mUpPosition);
		boolean bToRight = (mDownPosition <= mUpPosition);

		// [例外狀況排除
		// 列出該動作不被允許的情況
		boolean allReject = mFlowType == FLOW_TYPE_SINGLE_ALBUM;
		boolean prevReject = (mFlowType == FLOW_TYPE_NON_PREVSONG && (bToLeft || mDownPosition <= 0 || !mValidAction));
		boolean nextReject = (mFlowType == FLOW_TYPE_NON_NEXTSONG && (bToRight || mDownPosition >= (mScrollMiddle * 2) || !mValidAction));
		if (allReject || prevReject || nextReject) {
			cancelScroll();
			return;
		}

		// 不正常的狀況，拉回中間
		if (mUpTime == 0 || mDownTime == 0 || mUpTime <= mDownTime
				|| ((mUpPosition != 0 && mUpPosition != mScrollRightLimit) && mUpPosition == mDownPosition)) {
			// Log.e("HandleGestureUp", "Down: " + mDownPosition + ", Up: " + mUpPosition + ", Now: " + mHScrollView.getScrollX());
			initPosition(true);
			mValidAction = false;
			return;
		}
		// ]

		// 平均速度 = 位移 / 時間
		float speed = Math.abs(((float)(mDownPosition - mUpPosition)) / ((float)(mUpTime - mDownTime)));
		boolean isFastEnough = speed >= CUSTOM_ACTION_SPEED;
		if (bToLeft && ((mUpPosition <= mNonScrollLeftLimit && mValidAction) || isFastEnough)) {
			// 先檢查 Gesture UP 的位置是否在一半，若非再檢查速度
			// Log.e("PrevSongCommand", "over half" + " => Down: " + mDownPosition + ", Up: " + mUpPosition + ", Type: " + mFlowType);
			// Log.e("PrevSongCommand", "speed" + " => Down: " + mDownPosition + ", Up: " + mUpPosition + ", Type: " + mFlowType);
			prevSongCommand(true);
		} else if (bToRight && ((mUpPosition >= mNonScrollRightLimit && mValidAction) || isFastEnough)) {
			// 先檢查 Gesture UP 的位置是否在一半，若非再檢查速度
			// Log.e("NextSongCommand", "over half" + " => Down: " + mDownPosition + ", Up: " + mUpPosition + ", Type: " + mFlowType);
			// Log.e("NextSongCommand", "speed => " + speed + ", Down: " + mDownPosition + ", Up: " + mUpPosition + ", mValidAction: " + mValidAction);
			nextSongCommand(true);
		} else if (speed >= 0.0f && mValidAction) {
			// Log.e("InitPosition", "speed => " + speed + ", Down: " + mDownPosition + ", Up: " + mUpPosition + ", mValidAction: " + mValidAction);
			initPosition(true);
		}
		mValidAction = false;
	}

	/**
	 * 取消不執行滑動
	 */
	private void cancelScroll() {
		mHScrollView.post(new Runnable() {
			public void run() {
				mHScrollView.smoothScrollTo(mScrollMiddle, 0);
			}
		});
		if (mCallback != null) {
			mCallback.onCancelGesture();
		}
	}

	/**
	 * 上一首歌動作
	 * @param inSimpleCoverFlowCmd 是否由 SimpleCoverFlow 內的 handleGestureUp 驅動
	 */
	public boolean prevSongCommand(final boolean inSimpleCoverFlowCmd) {
		if(inSimpleCoverFlowCmd || !inSimpleCoverFlowCmd && mBmpPrevCover != null) {
			mCallback.prevSongCommand();
			mHScrollView.post(new Runnable() {
				public void run() {
					mHScrollView.fullScroll(FOCUS_LEFT);
				}
			});
			// 手勢換歌時歌詞圖先還原default，以免發生isRecycle
			setCoverBtnToDefault();
			mHandler.postDelayed(new Runnable() {
				public void run() {
					if (mCallback != null) {
						AlbumCover alb = mCallback.onNeedPrevAlbumCover();
						if (alb.mAlbumBmp != null || mBmpPrevCover != null) {
							changeCurrCover(mBmpPrevCover, alb);
							// 原先是調用 UpdateCorrectPosition(true) 但比較慢的機器沒辦法立馬 scrollTo mScrollMiddle 所以在上、下首的情況改用 PostDelay + runnable
							// 後來發現 Post 可以確保 ScrollView 動作…用 PostDelay 反而不保險…所以全面改用 Post
							mHScrollView.post(new Runnable() {
								public void run() {
									mHScrollView.scrollTo(mScrollMiddle, 0);
								}
							});
							mSubHandler.postDelayed(new Runnable() {
								public void run() {
									setPrevCoverToDefault();
									setNextCoverToDefault();
									mCallback.onPrevSong(inSimpleCoverFlowCmd);
								}
							}, 100);
						} else {
							updateCorrectPosition(true);
							if ("".equals(alb.mAlbumId)) {
								setFlowType(SimpleCoverFlow.FLOW_TYPE_NON_PREVSONG);
							}
							mCallback.onPrevSong(inSimpleCoverFlowCmd);
						}
					}
				}
			}, 500);
			return true;
		}
		else {
			mHandler.post(new Runnable() {
				public void run() {
					if (mCallback != null) {
						mCallback.onGestureStart();
						mCallback.prevSongCommand();
						// 手勢換歌時歌詞圖先還原default，以免發生isRecycle
						setCoverBtnToDefault();
						mCallback.refreshCoverFlowNotify(false);
						setCurrentCoverToDefault();
						setPrevCoverToDefault();
						setNextCoverToDefault();
					}
				}
			});
			return false;
		}
	}


	/**
	 * 下一首歌動作
	 * @param inSimpleCoverFlowCmd 是否由 SimpleCoverFlow 內的 handleGestureUp 驅動
	 */
	public boolean nextSongCommand(final boolean inSimpleCoverFlowCmd) {
		if(inSimpleCoverFlowCmd || !inSimpleCoverFlowCmd && mBmpNextCover != null) {
			mCallback.nextSongCommand();
			mHScrollView.post(new Runnable() {
				public void run() {
					mHScrollView.fullScroll(FOCUS_RIGHT);
				}
			});
			// 手勢換歌時歌詞圖先還原default，以免發生isRecycle
			setCoverBtnToDefault();
			mHandler.postDelayed(new Runnable() {

				public void run() {
					if (mCallback != null) {
						AlbumCover alb = mCallback.onNeedNextAlbumCover();
						if (alb.mAlbumBmp != null || mBmpNextCover != null) {
							if(mBmpNextCover == null) {
								mCallback.refreshCoverFlowNotify(false);
							}
							else {
								mCallback.refreshCoverFlowNotify(true);
							}
							changeCurrCover(mBmpNextCover, alb);
							mHScrollView.post(new Runnable() {
								public void run() {
									mHScrollView.scrollTo(mScrollMiddle, 0);
								}
							});
							mSubHandler.postDelayed(new Runnable() {
								public void run() {
									setPrevCoverToDefault();
									setNextCoverToDefault();
									mCallback.onNextSong(inSimpleCoverFlowCmd);
								}
							}, 100);
						} else {
							setCurrentCoverToDefault();
							updateCorrectPosition(true);
							if ("".equals(alb.mAlbumId)) {
								setFlowType(SimpleCoverFlow.FLOW_TYPE_NON_NEXTSONG);
							}
							mCallback.onNextSong(inSimpleCoverFlowCmd);
						}
					}
				}
			}, 500);
			return true;
		}
		else {
			mHandler.post(new Runnable() {
				public void run() {
					if (mCallback != null) {
						mCallback.nextSongCommand();
						// 手勢換歌時歌詞圖先還原default，以免發生isRecycle
						setCoverBtnToDefault();
						mCallback.refreshCoverFlowNotify(false);
						setCurrentCoverToDefault();
						setPrevCoverToDefault();
						setNextCoverToDefault();
					}
				}
			});
			return false;
		}
	}

	private void changeCurrCover(Bitmap bitmap, AlbumCover alb) {
		if (isDestroy) { return; }

		if (bitmap == null || bitmap.isRecycled()) {
			setCurrCover(alb.mAlbumBmp);
		} else {
			setCurrCover(bitmap.copy(Bitmap.Config.RGB_565, false));
		}
	}

	public void updateCorrectPosition(final boolean UpdateCover) // 給上層 call 來回覆正常位置用的
	{
		mHScrollView.post(new Runnable() {

			public void run() {
				mHScrollView.scrollTo(mScrollMiddle, 0);
			}
		});
		if (UpdateCover) {
			setPrevCoverToDefault();
			setNextCoverToDefault();
		}
	}

	public abstract interface OnSimpleCoverFlowCommand {

		void onPrevSong(boolean initCompleted);

		void onNextSong(boolean initCompleted);

		void OnShowCoverCompleted(Bitmap bmp, String albumid);

		AlbumCover onNeedPrevAlbumCover();

		AlbumCover onNeedNextAlbumCover();

		void setCurrCover();

		void prevSongCommand();

		void nextSongCommand();

		void onGestureStart();

		void onCancelGesture(); // 手勢動作不足，通知上層此動作取消

		void onSimpleCoverFlowTouch(View arg0, MotionEvent arg1);

		void refreshCoverFlowNotify(boolean notify);
	}

	public void setOnCommand(OnSimpleCoverFlowCommand callback) {
		mCallback = callback;
	}

	public static class AlbumCover {

		public Bitmap mAlbumBmp;
		public String mAlbumId;

		public AlbumCover() {
			mAlbumBmp = null;
			mAlbumId = "";
		}
	}

	private class LoadCoverTask extends AsyncTask<Void, Void, Bitmap> {

		private final int UNKNOW_POSTYPE = -99;
		private int mCoverPos = UNKNOW_POSTYPE;
		private URL mURL = null;
		private Bitmap mBmpCover = null;
		private File mFileCover = null;
		private String mAlbumId = "";

		private boolean mSkip = false;

		public LoadCoverTask(URL url, int posType, String albumid) {
			mURL = url;
			mCoverPos = posType;
			mFileCover = null;
			mAlbumId = albumid;
		}

		public LoadCoverTask(URL url, int posType, File file) {
			mURL = url;
			mCoverPos = posType;
			mFileCover = file;
			mAlbumId = "";
		}

		public LoadCoverTask() {
			mURL = null;
			mCoverPos = UNKNOW_POSTYPE;
			mFileCover = null;
			mAlbumId = "";
		}

		public void cancel() {
			mSkip = true;
		}

		public Bitmap getURLBitmap(URL url) {
			Bitmap bmpCover = null;
			InputStream isCover = null;
			try {
				if (!mSkip) {
					URLConnection conn = url.openConnection();
					conn.connect();
					isCover = conn.getInputStream();
					bmpCover = BitmapFactory.decodeStream(isCover);
				}
			} catch (Exception e) {
			} finally {
				if(isCover != null) {
					try { isCover.close(); } catch (IOException e) {}
				}
			}
			return bmpCover;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			if (mURL != null && !mSkip) {
				mBmpCover = getURLBitmap(mURL);
				if (mBmpCover != null && !mSkip) { return mBmpCover; }
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bmp) {
			if (bmp != null && !mSkip) {
				boolean bAction = false;
				switch (mCoverPos) {
					case POS_TYPE_PREV:
						setImgPrevBitmap(bmp);
						mBmpPrevCover = bmp;
						bAction = true;
						break;
					case POS_TYPE_CURR:
						setImgCurrBitmap(bmp);
						bAction = true;
						break;
					case POS_TYPE_NEXT:
						setImgNextBitmap(bmp);
						mBmpNextCover = bmp;
						bAction = true;
						break;
				}
				if (bAction) {
					initPosition();
				}
			}
		}
	}

	/** 設定上張圖片*/
	private void setImgPrevBitmap(Bitmap bmp) {
		if(bmp != null && bmp.isRecycled()) {
			return;
		}
		mImgPrevCover.setImageBitmap(bmp);
	}
	/** 設定目前圖片*/
	private void setImgCurrBitmap(Bitmap bmp) {
		if(bmp != null && bmp.isRecycled()) {
			return;
		}
		mImgCurrCover.setImageBitmap(bmp);
	}
	/** 設定下張圖片*/
	private void setImgNextBitmap(Bitmap bmp) {
		if(bmp != null && bmp.isRecycled()) {
			return;
		}
		mImgNextCover.setImageBitmap(bmp);
	}

	public void destroy() {
		isDestroy = true;
		BitmapUtils.recycleBitmap(mBmpPrevCover);
		mBmpPrevCover = null;
		BitmapUtils.recycleBitmap(mBmpNextCover);
		mBmpNextCover = null;
	}

}