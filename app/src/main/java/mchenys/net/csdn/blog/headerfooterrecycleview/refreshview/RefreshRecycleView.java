package mchenys.net.csdn.blog.headerfooterrecycleview.refreshview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * http://blog.csdn.net/Notzuonotdied/article/details/52430416?locationNum=1&fps=1
 * Created by mChenys on 2016/12/21.
 */
public class RefreshRecycleView extends WrapperRecyclerView {
    private static final String TAG = "RefreshRecycleView";
    //是否是上拉
    private boolean isPullingUp;
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


    //header
    private RefreshHeaderView mHeaderView;
    //footer
    private RefreshFooterView mFooterView;
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
        mHeaderView = new RefreshHeaderView(context);
        mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mFooterView = new RefreshFooterView(context);
        mFooterView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addHeaderView(mHeaderView);
        addFooterView(mFooterView);

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
                isPullingUp = deltaY < 0;
                if (Math.abs(deltaY) > Math.abs(deltaX)) {
                    if (!canScrollVertically(-1)) {
                        Log.d(TAG, "滑动到了头部");
                        mHeaderView.updateHeaderHeightAndState(deltaY);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHeaderView.scrollBackHeaderView(mOnRefreshListener);
                break;
        }


        return super.onTouchEvent(e);
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);

        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                // 判断是否是最后一个item
                LayoutManager layoutManager = getLayoutManager();

                // 可见的item数目
                int visibleChildCount = layoutManager.getChildCount();
                if (isPullingUp && visibleChildCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && !isLoadMore) {
                    View lastVisibleView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    int lastVisiblePosition = recyclerView.getChildLayoutPosition(lastVisibleView);
                    if (lastVisiblePosition >= layoutManager.getItemCount() - 1) {
                        mFooterView.show();
                        isLoadMore = true;
                        if (mOnRefreshListener != null) {
                            mOnRefreshListener.onLoadMore();
                        } else {
                            mFooterView.hide();
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
        mFooterView.hide();
        isLoadMore = false;
        removeFooterView(0);
    }



    /**
     * Sets refresh complete.
     */
    public void setRefreshComplete() {
        mHeaderView.setHeaderState(RefreshHeaderView.STATE_NORMAL);
        mHeaderView.scrollBackHeaderView(mOnRefreshListener);
    }

    public void notifyDataRefresh() {
        super.getAdapter().notifyDataSetChanged();
    }

    public void notifyDataLoaded() {
        addFooterView(mFooterView);
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
     * Sets my recycle view listener.
     *
     * @param l
     */
    public void setOnRefreshListener(OnRefreshListener l) {
        this.mOnRefreshListener = l;
    }


}
