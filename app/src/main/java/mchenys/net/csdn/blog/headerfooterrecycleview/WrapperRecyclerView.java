package mchenys.net.csdn.blog.headerfooterrecycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by mChenys on 2016/12/21.
 */
public class WrapperRecyclerView extends RecyclerView {

    private ArrayList<View> mHeaderViews = new ArrayList<>();

    private ArrayList<View> mFooterViews = new ArrayList<>();
    private Adapter mAdapter;


    public WrapperRecyclerView(Context context) {
        super(context);
    }

    public WrapperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapperRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderViewRecycleAdapter)) {
                mAdapter = new HeaderViewRecycleAdapter(mHeaderViews, mFooterViews, mAdapter);
            }
        }
    }

    public void addFooterView(View view) {
        mFooterViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderViewRecycleAdapter)) {
                mAdapter = new HeaderViewRecycleAdapter(mHeaderViews, mFooterViews, mAdapter);
            }
        }
    }

    public boolean removeHeaderView(View v) {
        if (mHeaderViews.size() > 0) {
            return mAdapter != null && ((HeaderViewRecycleAdapter) mAdapter).removeHeader(v);
        }
        return false;
    }

    public boolean removeFooterView(View v) {
        if (mFooterViews.size() > 0) {
            return mAdapter != null && ((HeaderViewRecycleAdapter) mAdapter).removeFooter(v);
        }
        return false;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeaderViews.isEmpty() && mHeaderViews.isEmpty()) {
            super.setAdapter(adapter);
        } else {
            adapter = new HeaderViewRecycleAdapter(mHeaderViews, mFooterViews, adapter);
            super.setAdapter(adapter);
        }
        mAdapter = adapter;
    }

}
