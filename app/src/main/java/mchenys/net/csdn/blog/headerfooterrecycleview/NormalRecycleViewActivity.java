package mchenys.net.csdn.blog.headerfooterrecycleview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mchenys.net.csdn.blog.headerfooterrecycleview.refreshview.MyItemDecoration;

/**
 * Created by mChenys on 2016/12/22.
 */
public class NormalRecycleViewActivity extends AppCompatActivity {
    private static final String TAG = "Normal";
    private List<String> mData = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        initData();
        initView();

    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.setAdapter(mAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        for (int i = 'a'; i <= 'z'; i++) {
            mData.add(String.valueOf((char) i));
        }
    }

    //RecyclerView Adapter
    private RecyclerView.Adapter mAdapter = new RecyclerView.Adapter<MyViewHolder>() {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder->viewType:" + viewType);
            return new MyViewHolder(getLayoutInflater().inflate(R.layout.item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder->position:" + position);
            holder.tv.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public int getItemViewType(int position) {
            Log.d(TAG, "getItemViewType->position:" + position);
            return super.getItemViewType(position);
        }
    };

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

    public void onRefresh(View view) {
        mData.clear();
        for (int i = 'a'; i <= 'z'; i++) {
            mData.add("new"+String.valueOf((char) i));
        }
        mAdapter.notifyDataSetChanged();
    }
    public void onAddMore(View view) {
        for (int i = 'a'; i <= 'z'; i++) {
            mData.add("load"+String.valueOf((char) i));
        }
        mAdapter.notifyDataSetChanged();
    }

}
