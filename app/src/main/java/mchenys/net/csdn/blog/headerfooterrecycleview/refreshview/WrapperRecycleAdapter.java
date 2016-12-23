package mchenys.net.csdn.blog.headerfooterrecycleview.refreshview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 仿HeaderViewListAdapter 包装RecyclerView.Adapter,实现添加header和footer
 * Created by mChenys on 2016/12/21.
 */
public class WrapperRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HeaderView";
    private final RecyclerView.Adapter mAdapter;
    ArrayList<View> mHeaderViews;
    ArrayList<View> mFooterViews;
    static final ArrayList<View> EMPTY_INFO_LIST = new ArrayList<>();

    public WrapperRecycleAdapter(ArrayList<View> headerViews,
                                 ArrayList<View> footerViews,
                                 RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
        if (headerViews == null) {
            this.mHeaderViews = EMPTY_INFO_LIST;
        } else {
            this.mHeaderViews = headerViews;
        }

        if (footerViews == null) {
            this.mFooterViews = EMPTY_INFO_LIST;
        } else {
            this.mFooterViews = footerViews;
        }
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }


    public boolean removeHeader(View v) {
        for (int i = 0; i < mHeaderViews.size(); i++) {
            View view = mHeaderViews.get(i);
            if (view == v) {
                mHeaderViews.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeFooter(View v) {
        for (int i = 0; i < mFooterViews.size(); i++) {
            View view = mFooterViews.get(i);
            if (view == v) {
                mFooterViews.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeFooter(int index) {
        if (mFooterViews.size() > 0) {
            return mFooterViews.remove(index) != null;
        }
        return false;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder->viewType:" + viewType);
        // Header
        int numHeaders = getHeadersCount();
        if (viewType < numHeaders) {
            return new HeaderViewHolder(mHeaderViews.get(viewType));
        }
        // Adapter
        int adjPosition = viewType - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            Log.d(TAG, "adapterCount" + adapterCount);
            if (adjPosition < adapterCount) {
                return mAdapter.onCreateViewHolder(parent, viewType);
            }
        }
        // Footer
        if (getFootersCount() < 0) return null;
        return new HeaderViewHolder(mFooterViews.get(adjPosition - adapterCount));


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder->position:" + position);
        int numHeaders = getHeadersCount();
        if (position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    Log.d(TAG, "onBindViewHolder->显示普通View adjPosition:" + adjPosition + " adapterCount:" + adapterCount);
                    mAdapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mAdapter != null) {
            return getFootersCount() + getHeadersCount() + mAdapter.getItemCount();
        } else {
            return getFootersCount() + getHeadersCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType->position:" + position);

        int numHeaders = getHeadersCount();
        if (position > numHeaders) {
            int adjPosition = position - numHeaders;
            if (mAdapter != null) {
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    int originType = mAdapter.getItemViewType(adjPosition);
                    if (originType < 0 || originType >= mAdapter.getItemCount()) {
                        throw new IllegalStateException("check getItemViewType return value,it must between 0 and adapterCount");
                    }
                }
            }
        }
        return position;


    }

    @Override
    public long getItemId(int position) {
        int numHeaders = getHeadersCount();
        if (mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemId(adjPosition);
            }
        }
        return -1;
    }


    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }


}
