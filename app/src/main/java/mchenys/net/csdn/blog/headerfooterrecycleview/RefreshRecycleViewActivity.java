package mchenys.net.csdn.blog.headerfooterrecycleview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mChenys on 2016/12/21.
 */
public class RefreshRecycleViewActivity extends AppCompatActivity {
    private RefreshRecycleView mRecycleView;
    private List<String> mData = new ArrayList<>();
    private MyAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecycleView = new RefreshRecycleView(this);
        setContentView(mRecycleView);
        initData();
        initView();
        initListener();
    }


    private void initData() {
        for (int i = 'a'; i <= 'z'; i++) {
            mData.add(String.valueOf((char) i));
        }
    }

    private void initView() {
        mRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.addItemDecoration(new MyItemDecoration());
        mAdapter = new MyAdapter();
        mRecycleView.setAdapter(mAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListener() {
        mRecycleView.setOnRefreshListener(new RefreshRecycleView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mData.clear();
                for (int i = 1; i <= 26; i++) {
                    mData.add("new Data-" + (i + 1));
                }
                mAdapter.resetData(mData);
                mRecycleView.setRefreshComplete();
            }

            @Override
            public void onLoadMore() {
                for (int i = 1; i <= 26; i++) {
                    mData.add("load Data-" + (i + 1));
                }
                mAdapter.notifyDataSetChanged();
                mRecycleView.setLoadMoreComplete();
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(getLayoutInflater().inflate(R.layout.item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void resetData(List<String> data) {
            mData = data;
            notifyDataSetChanged();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_info);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
