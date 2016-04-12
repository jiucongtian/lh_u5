package com.longhuapuxin.u5.pullable;

import java.util.Timer;
import java.util.TimerTask;
import com.longhuapuxin.u5.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PullToRefreshLayout extends RelativeLayout
{
	public static final String TAG = "PullToRefreshLayout";
 
	public static final int INIT = 0;
	 
	public static final int RELEASE_TO_REFRESH = 1;
	 
	public static final int REFRESHING = 2;
	// 闁插﹥鏂侀崝鐘烘祰
	public static final int RELEASE_TO_LOAD = 3;
	// 濮濓絽婀崝鐘烘祰
	public static final int LOADING = 4;
	// 閹垮秳缍旂�瑰本鐦�
	public static final int DONE = 5;
	// 瑜版挸澧犻悩鑸碉拷锟�
	private int state = INIT;
	// 閸掗攱鏌婇崶鐐剁殶閹恒儱褰�
	private OnRefreshListener mListener;
	// 閸掗攱鏌婇幋鎰
	public static final int SUCCEED = 0;
	// 閸掗攱鏌婃径杈Е
	public static final int FAIL = 1;
	// 閹稿绗匶閸ф劖鐖ｉ敍灞肩瑐娑擄拷娑擃亙绨ㄦ禒鍓佸仯Y閸ф劖鐖�
	private float downY, lastY;

	// 娑撳濯洪惃鍕獩缁傛眹锟藉倹鏁為幇蹇ョ窗pullDownY閸滃ullUpY娑撳秴褰查懗钘夋倱閺冩湹绗夋稉锟�0
	public float pullDownY = 0;
	// 娑撳﹥濯洪惃鍕獩缁傦拷
	private float pullUpY = 0;

	// 闁插﹥鏂侀崚閿嬫煀閻ㄥ嫯绐涚粋锟�
	private float refreshDist = 200;
	// 闁插﹥鏂侀崝鐘烘祰閻ㄥ嫯绐涚粋锟�
	private float loadmoreDist = 200;

	private MyTimer timer;
	// 閸ョ偞绮撮柅鐔峰
	public float MOVE_SPEED = 8;
	// 缁楊兛绔村▎鈩冨⒔鐞涘苯绔风仦锟�
	private boolean isLayout = false;
	// 閸︺劌鍩涢弬鎷岀箖缁嬪鑵戝鎴濆З閹垮秳缍�
	private boolean isTouch = false;
	// 閹靛瀵氬鎴濆З鐠烘繄顬囨稉搴濈瑓閹峰銇旈惃鍕拨閸斻劏绐涚粋缁樼槷閿涘奔鑵戦梻缈犵窗闂呭繑顒滈崚鍥у毐閺佹澘褰夐崠锟�
	private float radio = 2;

	// 娑撳濯虹粻顓炪仈閻ㄥ嫯娴�180鎺抽崝銊ф暰
	private RotateAnimation rotateAnimation;
	// 閸у洤瀵戦弮瀣祮閸斻劎鏁�
	private RotateAnimation refreshingAnimation;

	// 娑撳濯烘径锟�
	private View refreshView;
	// 娑撳濯洪惃鍕唲婢讹拷
	private View pullView;
	// 濮濓絽婀崚閿嬫煀閻ㄥ嫬娴橀弽锟�
	private View refreshingView;
	// 閸掗攱鏌婄紒鎾寸亯閸ョ偓鐖�
	private View refreshStateImageView;
	// 閸掗攱鏌婄紒鎾寸亯閿涙碍鍨氶崝鐔稿灗婢惰精瑙�
	private TextView refreshStateTextView;

	// 娑撳﹥濯烘径锟�
	private View loadmoreView;
	// 娑撳﹥濯洪惃鍕唲婢讹拷
	private View pullUpView;
	// 濮濓絽婀崝鐘烘祰閻ㄥ嫬娴橀弽锟�
	private View loadingView;
	// 閸旂姾娴囩紒鎾寸亯閸ョ偓鐖�
	private View loadStateImageView;
	// 閸旂姾娴囩紒鎾寸亯閿涙碍鍨氶崝鐔稿灗婢惰精瑙�
	private TextView loadStateTextView;

	 
	private View pullableView;
 	private int mEvents;
 	private boolean canPullDown = true;
	private boolean canPullUp = true;

	 
	@SuppressLint("HandlerLeak") Handler updateHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			// 閸ョ偛鑴婇柅鐔峰闂呭繋绗呴幏澶庣獩缁傜北oveDeltaY婢х偛銇囬懓灞筋杻婢讹拷
			MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2
					/ getMeasuredHeight() * (pullDownY + Math.abs(pullUpY))));
			if (!isTouch)
			{
				// 濮濓絽婀崚閿嬫煀閿涘奔绗栧▽鈩冩箒瀵帮拷娑撳﹥甯归惃鍕樈閸掓瑦鍋撻崑婊愮礉閺勫墽銇�"濮濓絽婀崚閿嬫煀..."
				if (state == REFRESHING && pullDownY <= refreshDist)
				{
					pullDownY = refreshDist;
					timer.cancel();
				} else if (state == LOADING && -pullUpY <= loadmoreDist)
				{
					pullUpY = -loadmoreDist;
					timer.cancel();
				}

			}
			if (pullDownY > 0)
				pullDownY -= MOVE_SPEED;
			else if (pullUpY < 0)
				pullUpY += MOVE_SPEED;
			if (pullDownY < 0)
			{
				// 瀹告彃鐣幋鎰礀瀵拷
				pullDownY = 0;
				pullView.clearAnimation();
				// 闂呮劘妫屾稉瀣婢跺瓨妞傞張澶婂讲閼冲�熺箷閸︺劌鍩涢弬甯礉閸欘亝婀佽ぐ鎾冲閻樿埖锟戒椒绗夐弰顖涱劀閸︺劌鍩涢弬鐗堟閹靛秵鏁奸崣妯煎Ц閹拷
				if (state != REFRESHING && state != LOADING)
					changeState(INIT);
				timer.cancel();
				requestLayout();
			}
			if (pullUpY > 0)
			{
				// 瀹告彃鐣幋鎰礀瀵拷
				pullUpY = 0;
				pullUpView.clearAnimation();
				// 闂呮劘妫屾稉濠冨婢跺瓨妞傞張澶婂讲閼冲�熺箷閸︺劌鍩涢弬甯礉閸欘亝婀佽ぐ鎾冲閻樿埖锟戒椒绗夐弰顖涱劀閸︺劌鍩涢弬鐗堟閹靛秵鏁奸崣妯煎Ц閹拷
				if (state != REFRESHING && state != LOADING)
					changeState(INIT);
				timer.cancel();
				requestLayout();
			}
			Log.d("handle", "handle");
			// 閸掗攱鏌婄敮鍐ㄧ湰,娴兼俺鍤滈崝銊ㄧ殶閻⑩暙nLayout
			requestLayout();
			// 濞屸剝婀侀幏鏍ㄥ閹存牞锟藉懎娲栧鐟扮暚閹达拷
			if (pullDownY + Math.abs(pullUpY) == 0)
				timer.cancel();
		}

	};

	public void setOnRefreshListener(OnRefreshListener listener)
	{
		mListener = listener;
	}

	public PullToRefreshLayout(Context context)
	{
		super(context);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context)
	{
		if(isInEditMode())return;
		timer = new MyTimer(updateHandler);
		rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				context, R.anim.reverse_anim);
		refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				context, R.anim.rotating);
	 
		LinearInterpolator lir = new LinearInterpolator();
		rotateAnimation.setInterpolator(lir);
		refreshingAnimation.setInterpolator(lir);
	}

	private void hide()
	{
		timer.schedule(5);
	}

	public void PreventPullUp(){
		((PullableScrollView)pullableView).PreventPullUp();
		
	}
	public void AllowPull(){
		((PullableScrollView)pullableView).AllowPull();
		
	}
	public void refreshFinish(int refreshResult)
	{
		refreshingView.clearAnimation();
		refreshingView.setVisibility(View.GONE);
		switch (refreshResult)
		{
		case SUCCEED:
			 
			//refreshStateImageView.setVisibility(View.VISIBLE);
			//refreshStateTextView.setText(R.string.refresh_succeed);
			//refreshStateImageView.setBackgroundResource(R.drawable.refresh_succeed);
			break;
		case FAIL:
		default:
			 
			refreshStateImageView.setVisibility(View.VISIBLE);
			refreshStateTextView.setText(R.string.refresh_fail);
			refreshStateImageView
					.setBackgroundResource(R.drawable.refresh_failed);
			break;
		}
		if (pullDownY > 0)
		{
		 
			new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					changeState(DONE);
					hide();
				}
			}.sendEmptyMessageDelayed(0, 10);
		} else
		{
			changeState(DONE);
			hide();
		}
	}

	 
	public void loadmoreFinish(int refreshResult)
	{
		loadingView.clearAnimation();
		loadingView.setVisibility(View.GONE);
		switch (refreshResult)
		{
		case SUCCEED:
			 
			//loadStateImageView.setVisibility(View.VISIBLE);
			//loadStateTextView.setText(R.string.load_succeed);
			//loadStateImageView.setBackgroundResource(R.drawable.load_succeed);
			break;
		case FAIL:
		default:
		 
			loadStateImageView.setVisibility(View.VISIBLE);
			loadStateTextView.setText(R.string.load_fail);
			loadStateImageView.setBackgroundResource(R.drawable.load_failed);
			break;
		}
		if (pullUpY < 0)
		{
		 
			new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					changeState(DONE);
					hide();
				}
			}.sendEmptyMessageDelayed(0, 10);
		} else
		{
			changeState(DONE);
			hide();
		}
	}

	private void changeState(int to)
	{
		state = to;
		switch (state)
		{
		case INIT:
			 
			refreshStateImageView.setVisibility(View.GONE);
			refreshStateTextView.setText(R.string.pull_to_refresh);
			pullView.clearAnimation();
			pullView.setVisibility(View.VISIBLE);
			 
			loadStateImageView.setVisibility(View.GONE);
			loadStateTextView.setText(R.string.pullup_to_load);
			pullUpView.clearAnimation();
			pullUpView.setVisibility(View.VISIBLE);
			break;
		case RELEASE_TO_REFRESH:
			 
			refreshStateTextView.setText(R.string.release_to_refresh);
			pullView.startAnimation(rotateAnimation);
			break;
		case REFRESHING:
			 
			pullView.clearAnimation();
			refreshingView.setVisibility(View.VISIBLE);
			pullView.setVisibility(View.INVISIBLE);
			refreshingView.startAnimation(refreshingAnimation);
			refreshStateTextView.setText(R.string.refreshing);
			break;
		case RELEASE_TO_LOAD:
			 
			loadStateTextView.setText(R.string.release_to_load);
			pullUpView.startAnimation(rotateAnimation);
			break;
		case LOADING:
			 
			pullUpView.clearAnimation();
			loadingView.setVisibility(View.VISIBLE);
			pullUpView.setVisibility(View.INVISIBLE);
			loadingView.startAnimation(refreshingAnimation);
			loadStateTextView.setText(R.string.loading);
			break;
		case DONE:
			 
			break;
		}
	}

	/**
	 * 娑撳秹妾洪崚鏈电瑐閹峰鍨ㄦ稉瀣
	 */
	private void releasePull()
	{
		canPullDown = true;
		canPullUp = true;
	}

	/*
	 * 閿涘牓娼� Javadoc閿涘鏁遍悥鑸靛付娴犺泛鍠呯�规碍妲搁崥锕�鍨庨崣鎴滅皑娴犺绱濋梼鍙夘剾娴滃娆㈤崘鑼崐
	 * 
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		switch (ev.getActionMasked())
		{
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			lastY = downY;
			timer.cancel();
			mEvents = 0;
			releasePull();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_UP:
			// 鏉╁洦鎶ゆ径姘卞仯鐟欙妇顫�
			mEvents = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mEvents == 0)
			{
				if (pullDownY > 0
						|| (((Pullable) pullableView).canPullDown()
								&& canPullDown && state != LOADING))
				{
					// 閸欘垯浜掓稉瀣閿涘本顒滈崷銊ュ鏉炶姤妞傛稉宥堝厴娑撳濯�
					// 鐎电懓鐤勯梽鍛拨閸斻劏绐涚粋璇蹭粵缂傗晛鐨敍宀勶拷鐘冲灇閻€劌濮忛幏澶屾畱閹扮喕顫�
					pullDownY = pullDownY + (ev.getY() - lastY) / radio;
					if (pullDownY < 0)
					{
						pullDownY = 0;
						canPullDown = false;
						canPullUp = true;
					}
					if (pullDownY > getMeasuredHeight())
						pullDownY = getMeasuredHeight();
					if (state == REFRESHING)
					{
						// 濮濓絽婀崚閿嬫煀閻ㄥ嫭妞傞崐娆捫曢幗鍝バ╅崝锟�
						isTouch = true;
					}
				} else if (pullUpY < 0
						|| (((Pullable) pullableView).canPullUp() && canPullUp && state != REFRESHING))
				{
					// 閸欘垯浜掓稉濠冨閿涘本顒滈崷銊ュ煕閺傜増妞傛稉宥堝厴娑撳﹥濯�
					pullUpY = pullUpY + (ev.getY() - lastY) / radio;
					if (pullUpY > 0)
					{
						pullUpY = 0;
						canPullDown = true;
						canPullUp = false;
					}
					if (pullUpY < -getMeasuredHeight())
						pullUpY = -getMeasuredHeight();
					if (state == LOADING)
					{
						// 濮濓絽婀崝鐘烘祰閻ㄥ嫭妞傞崐娆捫曢幗鍝バ╅崝锟�
						isTouch = true;
					}
				} else
					releasePull();
			} else
				mEvents = 0;
			lastY = ev.getY();
			// 閺嶈宓佹稉瀣鐠烘繄顬囬弨鐟板綁濮ｆ柧绶�
			radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
					* (pullDownY + Math.abs(pullUpY))));
			if (pullDownY > 0 || pullUpY < 0)
				requestLayout();
			if (pullDownY > 0)
			{
				if (pullDownY <= refreshDist
						&& (state == RELEASE_TO_REFRESH || state == DONE))
				{
			 		changeState(INIT);
				}
				if (pullDownY >= refreshDist && state == INIT)
				{
			 		changeState(RELEASE_TO_REFRESH);
				}
			} else if (pullUpY < 0)
			{
				 
				if (-pullUpY <= loadmoreDist
						&& (state == RELEASE_TO_LOAD || state == DONE))
				{
					changeState(INIT);
				}
			 
				if (-pullUpY >= loadmoreDist && state == INIT)
				{
					changeState(RELEASE_TO_LOAD);
				}

			}
		 
			if ((pullDownY + Math.abs(pullUpY)) > 8)
			{
			 
				ev.setAction(MotionEvent.ACTION_CANCEL);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (pullDownY > refreshDist || -pullUpY > loadmoreDist)
		 	{
				isTouch = false;
			}
			if (state == RELEASE_TO_REFRESH)
			{
				changeState(REFRESHING);
		 
				if (mListener != null)
					mListener.onRefresh(this);
			} else if (state == RELEASE_TO_LOAD)
			{
				changeState(LOADING);
		 
				if (mListener != null)
					mListener.onLoadMore(this);
			}
			hide();
		default:
			break;
		}
	 
		super.dispatchTouchEvent(ev);
		return true;
	}

	 
	private class AutoRefreshAndLoadTask extends
			AsyncTask<Integer, Float, String>
	{

		@Override
		protected String doInBackground(Integer... params)
		{
			while (pullDownY < 4 / 3 * refreshDist)
			{
				pullDownY += MOVE_SPEED;
				publishProgress(pullDownY);
				try
				{
					Thread.sleep(params[0]);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			changeState(REFRESHING);
		 
			if (mListener != null)
				mListener.onRefresh(PullToRefreshLayout.this);
			hide();
		}

		@Override
		protected void onProgressUpdate(Float... values)
		{
			if (pullDownY > refreshDist)
				changeState(RELEASE_TO_REFRESH);
			requestLayout();
		}

	}

	 
	public void autoRefresh()
	{
		AutoRefreshAndLoadTask task = new AutoRefreshAndLoadTask();
		task.execute(20);
	}

	/**
	 * 閼奉亜濮╅崝鐘烘祰
	 */
	public void autoLoad()
	{
		pullUpY = -loadmoreDist;
		requestLayout();
		changeState(LOADING);
		// 閸旂姾娴囬幙宥勭稊
		if (mListener != null)
			mListener.onLoadMore(this);
	}

	private void initView()
	{
		// 閸掓繂顫愰崠鏍︾瑓閹峰绔风仦锟�
		pullView = refreshView.findViewById(R.id.pull_icon);
		refreshStateTextView = (TextView) refreshView
				.findViewById(R.id.state_tv);
		refreshingView = refreshView.findViewById(R.id.refreshing_icon);
		refreshStateImageView = refreshView.findViewById(R.id.state_iv);
		// 閸掓繂顫愰崠鏍︾瑐閹峰绔风仦锟�
		pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
		loadStateTextView = (TextView) loadmoreView
				.findViewById(R.id.loadstate_tv);
		loadingView = loadmoreView.findViewById(R.id.loading_icon);
		loadStateImageView = loadmoreView.findViewById(R.id.loadstate_iv);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		Log.d("Test", "Test");
		if (!isLayout)
		{
		 
			refreshView = getChildAt(0);
			pullableView = getChildAt(1);
			loadmoreView = getChildAt(2);
			isLayout = true;
			initView();
			refreshDist = ((ViewGroup) refreshView).getChildAt(0)
					.getMeasuredHeight();
			loadmoreDist = ((ViewGroup) loadmoreView).getChildAt(0)
					.getMeasuredHeight();
		}
	 	refreshView.layout(0,
				(int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
				refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
		pullableView.layout(0, (int) (pullDownY + pullUpY),
				pullableView.getMeasuredWidth(), (int) (pullDownY + pullUpY)
						+ pullableView.getMeasuredHeight());
		loadmoreView.layout(0,
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
				loadmoreView.getMeasuredWidth(),
				(int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight()
						+ loadmoreView.getMeasuredHeight());
	}

	class MyTimer
	{
		private Handler handler;
		private Timer timer;
		private MyTask mTask;

		public MyTimer(Handler handler)
		{
			this.handler = handler;
			timer = new Timer();
		}

		public void schedule(long period)
		{
			if (mTask != null)
			{
				mTask.cancel();
				mTask = null;
			}
			mTask = new MyTask(handler);
			timer.schedule(mTask, 0, period);
		}

		public void cancel()
		{
			if (mTask != null)
			{
				mTask.cancel();
				mTask = null;
			}
		}

		class MyTask extends TimerTask
		{
			private Handler handler;

			public MyTask(Handler handler)
			{
				this.handler = handler;
			}

			@Override
			public void run()
			{
				handler.obtainMessage().sendToTarget();
			}

		}
	}

 
	public interface OnRefreshListener
	{ 
		void onRefresh(PullToRefreshLayout pullToRefreshLayout);
 
		void onLoadMore(PullToRefreshLayout pullToRefreshLayout);
	}

}