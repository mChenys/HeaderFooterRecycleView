package mchenys.net.csdn.blog.headerfooterrecycleview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by mChenys on 2016/12/21.
 */
public interface WrapperRecycleAdapter {
    /**
     * Returns the adapter wrapped by this HeaderViewRecycleAdapter.
     * @return
     */
    RecyclerView.Adapter getWrappedAdapter();
}
