package mchenys.net.csdn.blog.headerfooterrecycleview.refreshview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import mchenys.net.csdn.blog.headerfooterrecycleview.R;

/**
 * Created by mChenys on 2016/12/22.
 */
public class RefreshFooterView extends FrameLayout {
    private View mFooterContent;

    public RefreshFooterView(Context context) {
        this(context,null);
    }

    public RefreshFooterView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFooterContent = View.inflate(context, R.layout.layout_footer_view, null);
        addView(mFooterContent,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(context, 60)));
    }

    public void show() {
        mFooterContent.setVisibility(VISIBLE);
    }

    public void hide() {
        mFooterContent.setVisibility(GONE);
    }

    public static int dip2px(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }
}
