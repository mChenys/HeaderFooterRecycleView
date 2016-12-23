package mchenys.net.csdn.blog.headerfooterrecycleview.refreshview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import mchenys.net.csdn.blog.headerfooterrecycleview.R;

/**
 * Created by mChenys on 2016/12/22.
 */
public class RefreshHeaderView extends FrameLayout {
    /**
     * 下拉刷新
     */
    public final static int STATE_NORMAL = 0;
    /**
     * 松开刷新
     */
    public final static int STATE_READY = 1;
    /**
     * 正在刷新
     */
    public final static int STATE_REFRESHING = 2;

    private static final String TAG = "RefreshHeaderView";
    /**
     * 默认的状态
     */
    private int mState = STATE_NORMAL;
    /**
     * 状态文本和加载圈
     */
    private TextView mHeaderStateTv;
    private ProgressBar mLoadingPb;

    private String[] mHeaderStatus = {"下拉刷新", "松开刷新", "正在刷新"};
    /**
     * 头部的View的高度
     */
    private int mOriginHeight;
    /**
     * 定义头部的View
     */
    private View mHeaderContent;
    private Handler mHandler = new Handler();


    public RefreshHeaderView(Context context) {
        this(context, null);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOriginHeight = dip2px(context, 60);
        mHeaderContent = View.inflate(context, R.layout.layout_header_view, null);
        mHeaderStateTv = (TextView) mHeaderContent.findViewById(R.id.tv_header_state);
        mLoadingPb = (ProgressBar) mHeaderContent.findViewById(R.id.pb_loading);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        addView(mHeaderContent, lp);
    }

    /**
     * On status change.
     *
     * @param status the status
     */
    public void setHeaderState(int status) {
        mState = status;
        switch (status) {
            case STATE_READY:
                this.mHeaderStateTv.setText(mHeaderStatus[STATE_READY]);
                this.mLoadingPb.setVisibility(View.GONE);
                break;
            case STATE_NORMAL:
                this.mHeaderStateTv.setText(mHeaderStatus[STATE_NORMAL]);
                this.mLoadingPb.setVisibility(View.GONE);
                break;
            case STATE_REFRESHING:
                this.mHeaderStateTv.setText(mHeaderStatus[STATE_REFRESHING]);
                this.mLoadingPb.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 更新高度
     */
    public void updateHeaderHeightAndState(int changeHeight) {
        Log.d(TAG, "updateHeaderHeightAndState:" + changeHeight);
        mHeaderContent.getLayoutParams().height += changeHeight;
        mHeaderContent.requestLayout();
        int newHeight = mHeaderContent.getHeight();
        Log.d(TAG, "newHeight:" + newHeight);
        if (newHeight < mOriginHeight) {
            setHeaderState(STATE_NORMAL);
        } else if (newHeight > mOriginHeight) {
            setHeaderState(STATE_READY);
        }
    }

    public static int dip2px(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public boolean isRefreshReady() {
        return mState == STATE_READY;
    }

    public int getOriginHeight() {
        return mOriginHeight;
    }

    /**
     * 下拉刷新回滚的属性动画效果
     */
    public void scrollBackHeaderView(final RefreshRecycleView.OnRefreshListener l) {
        // 当前的高度
        int currentHeight = getHeight();
        // View拖动释放后所在的高度
        int targetHeight = 0;

        if (isRefreshReady()) {
            targetHeight = getOriginHeight();
            setHeaderState(RefreshHeaderView.STATE_REFRESHING);
        }
        // 属性动画
        ValueAnimator valueAnimator = ValueAnimator.ofInt(currentHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animateValue = (int) valueAnimator.getAnimatedValue();
                mHeaderContent.getLayoutParams().height = animateValue;
                mHeaderContent.requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mState == STATE_REFRESHING) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (l != null) {
                                l.onRefresh();
                            }
                        }
                    }, 1000);
                }
            }
        });
        valueAnimator.start();
    }
}
