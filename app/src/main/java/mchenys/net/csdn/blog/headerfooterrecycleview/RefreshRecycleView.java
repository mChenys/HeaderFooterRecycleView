package mchenys.net.csdn.blog.headerfooterrecycleview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * http://blog.csdn.net/Notzuonotdied/article/details/52430416?locationNum=1&fps=1
 * Created by mChenys on 2016/12/21.
 */
public class RefreshRecycleView extends WrapperRecyclerView {
    private static final String TAG = "RefreshRecycleView";
    /**
     * 正在刷新
     */
    public final static int STATE_REFRESHING = 2;
    /**
     * 下拉刷新
     */
    private final static int STATE_NORMAL = 0;
    /**
     * 松开刷新
     */
    private final static int STATE_READY = 1;
    /**
     * 判断是否需要进行刷新
     */
    private boolean isRefresh;
    /**
     * 判断是否需要加载更多
     */
    private boolean isLoadMore;
    /**
     * 按下X坐标
     */
    private int lastX;
    /**
     * 按下Y坐标
     */
    private int lastY;
    /**
     * 定义头部的View
     */
    private View headerView;
    /**
     * 定义尾部的View
     */
    private View footerView;
    /**
     * 头部的View的高度
     */
    private int headerViewHeight;
    /**
     * 默认的状态
     */
    private int mState = STATE_NORMAL;
    /**
     * 显示头部文本信息的TextView
     */
    private TextView headerStateTv;
    private String[] headerStatus = {"下拉刷新", "松开刷新", "正在刷新"};
    /**
     * 设置响应事件
     */
    private OnRefreshListener mOnRefreshListener;

    public RefreshRecycleView(Context context) {
        this(context, null);
    }

    public RefreshRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        headerViewHeight = dip2px(context, 50);
        headerView = View.inflate(context, R.layout.layout_header_view, null);
        footerView = View.inflate(context, R.layout.layout_footer_view, null);
        headerView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 0));
        footerView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, headerViewHeight));
        addHeaderView(headerView);
        addFooterView(footerView);

        headerStateTv = (TextView) headerView.findViewById(R.id.tv_header_state);

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
                this.headerStateTv.setText(headerStatus[STATE_READY]);
                break;
            case STATE_NORMAL:
                this.headerStateTv.setText(headerStatus[STATE_NORMAL]);
                break;
            case STATE_REFRESHING:
                this.headerStateTv.setText(headerStatus[STATE_REFRESHING]);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        Log.d(TAG, "action:" + e.getAction());
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // 判断是否滑动到了头部
                int deltaY = y - lastY;
                int deltaX = x - lastX;
                lastY = y;
                lastX = x;
                if (!canScrollVertically(-1)) {
                    Log.d(TAG, "滑动到了头部");
                    if (Math.abs(deltaY) > Math.abs(deltaX)) {
                        isRefresh = true;
                        updateHeaderHeightAndState(deltaY);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isRefresh = false;
                scrollBackHeaderView();
                break;
        }


        return super.onTouchEvent(e);
    }

    private Handler mHandler = new Handler();

    /**
     * 下拉刷新回滚的属性动画效果
     */
    private void scrollBackHeaderView() {
        // 当前的高度
        int currentHeight = headerView.getHeight();
        // View拖动释放后所在的高度
        int targetHeight = 0;

        if (mState == STATE_READY) {
            targetHeight = headerViewHeight;
            setHeaderState(STATE_REFRESHING);
        }
        // 属性动画
        ValueAnimator valueAnimator = ValueAnimator.ofInt(currentHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animateValue = (int) valueAnimator.getAnimatedValue();
                headerView.getLayoutParams().height = animateValue;
                headerView.requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mState == STATE_READY || mState == STATE_REFRESHING) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnRefreshListener != null) {
                                mOnRefreshListener.onRefresh();
                            }
                        }
                    }, 1000);
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 更新高度
     */
    private void updateHeaderHeightAndState(int changeHeight) {
        Log.d(TAG, "updateHeaderHeightAndState:" + changeHeight);
        headerView.getLayoutParams().height += changeHeight;
        headerView.requestLayout();
        int newHeight = headerView.getHeight();
        Log.d(TAG, "newHeight:" + newHeight);
        if (newHeight < headerViewHeight) {
            setHeaderState(STATE_NORMAL);
        } else if (newHeight > headerViewHeight) {
            setHeaderState(STATE_READY);
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);

        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isRefresh) {
                    return;
                }

                if (mState != STATE_NORMAL) {
                    return;
                }

                // 判断是否是最后一个item
                LayoutManager layoutManager = getLayoutManager();

                // 可见的item数目
                int visibleChildCount = layoutManager.getChildCount();
                if (visibleChildCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && !isLoadMore) {
                    View lastVisibleView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    int lastVisiblePosition = recyclerView.getChildLayoutPosition(lastVisibleView);
                    if (lastVisiblePosition >= layoutManager.getItemCount() - 1) {
                        footerView.setVisibility(VISIBLE);
                        isLoadMore = true;
                        if (mOnRefreshListener != null) {
                            mOnRefreshListener.onLoadMore();
                        } else {
                            footerView.setVisibility(GONE);
                        }
                    }
                }
            }
        });
    }

    /**
     * Sets load more complete.
     */
    public void setLoadMoreComplete() {
        footerView.setVisibility(GONE);
        isLoadMore = false;
    }

    /**
     * Sets refresh complete.
     */
    public void setRefreshComplete() {
        setHeaderState(STATE_NORMAL);
        scrollBackHeaderView();
    }

    /**
     * 对外接口
     */
    public interface OnRefreshListener {
        /**
         * On refresh.
         */
        void onRefresh();

        /**
         * On load more.
         */
        void onLoadMore();
    }

    /**
     * Gets my recycle view listener.
     *
     * @return the my recycle view listener
     */
    public OnRefreshListener getMyRecycleViewListener() {
        return mOnRefreshListener;
    }

    /**
     * Sets my recycle view listener.
     *
     * @param l
     */
    public void setOnRefreshListener(OnRefreshListener l) {
        this.mOnRefreshListener = l;
    }

    public static int dip2px(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }
}
