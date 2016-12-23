package mchenys.net.csdn.blog.headerfooterrecycleview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mchenys.net.csdn.blog.headerfooterrecycleview.refreshview.MyItemDecoration;
import mchenys.net.csdn.blog.headerfooterrecycleview.refreshview.WrapperRecyclerView;

public class MainActivity extends AppCompatActivity {
    private WrapperRecyclerView mRecycleView;
    private List<String> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        for (int i = 'a'; i <= 'z'; i++) {
            mData.add(String.valueOf((char) i));
        }
    }

    private void initView() {
        mRecycleView = (WrapperRecyclerView) findViewById(R.id.wrc);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.addItemDecoration(new MyItemDecoration());
        View header1 = View.inflate(this, R.layout.layout_header1, null);
        View header2 = View.inflate(this, R.layout.layout_header2, null);
        View footer1 = View.inflate(this, R.layout.layout_footer1, null);
        View footer2 = View.inflate(this, R.layout.layout_footer2, null);
        mRecycleView.addHeaderView(header1);
        mRecycleView.addHeaderView(header2);
        mRecycleView.addFooterView(footer1);
        mRecycleView.addFooterView(footer2);

        mRecycleView.setAdapter(mAdapter);
    }

    //RecyclerView Adapter
    private RecyclerView.Adapter mAdapter = new RecyclerView.Adapter<MyViewHolder>() {


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
    };

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_info);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "RefreshRecycleViewActivity");
        menu.add(0, 1, 1, "NormalRecycleViewActivity");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==0) {
            startActivity(new Intent(this,RefreshRecycleViewActivity.class));
            return true;
        }else if (item.getItemId() ==1) {
            startActivity(new Intent(this,NormalRecycleViewActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
