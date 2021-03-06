package com.monsterlin.blives.activity.campus;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.monsterlin.blives.BaseActivity;
import com.monsterlin.blives.R;
import com.monsterlin.blives.adapter.campus.CampusAdapter;
import com.monsterlin.blives.bean.Campus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * 我的发布
 * Created by monsterLin on 2016/4/27.
 */
public class MCampusActivity extends BaseActivity{

    private String objectId ;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.rv_campus)
    RecyclerView rv_campus;

    @InjectView(R.id.srl)
    SwipeRefreshLayout srl ;


    BmobQuery<Campus> query ;
    private List<Campus> mList = new ArrayList<>();
    private CampusAdapter adapter ;
    private MaterialDialog mMaterialDialog;

    boolean isLoading ; //监听加载状态

    private int limit =10;		// 每页的数据是8条
    private int curPage = 0;		// 当前页的编号，从0开始

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcampus);
        objectId= BmobUser.getCurrentUser(this).getObjectId();
        ButterKnife.inject(this);
        initToolBar(toolbar,true);
        toolbar.setTitle("我的发布");
        initView();
        initData();

    }


    /**
     * 初始化数据
     */
    private void initData() {

        query= new BmobQuery<Campus>();
        query.order("-updatedAt");
        query.setLimit(limit);
        query.addWhereEqualTo("bUser",BmobUser.getCurrentUser(this).getObjectId());
        query.include("bUser");
        query.setSkip(curPage*limit);
        curPage++;
        query.findObjects(MCampusActivity.this, new FindListener<Campus>() {
            @Override
            public void onSuccess(List<Campus> list) {
                //　新数据的添加
                mList.addAll(list);
                if (null != adapter){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.e("OnError :",s);
            }
        });

    }

    private void getData(int page) {

        // 进行服务器端的分页处理
        BmobQuery<Campus> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.include("bUser");
        query.setSkip(page*limit+1);
        query.setLimit(5);
        curPage++;
        query.findObjects(MCampusActivity.this, new FindListener<Campus>() {
            @Override
            public void onSuccess(List<Campus> list) {
                if(list.size()!=0){
                    mList.addAll(list);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });



    }


    private void initView() {
        srl.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO 刷新的时候获取数据，需要优化
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        srl.setRefreshing(false);
                    }
                }, 1500);

            }
        });


        final LinearLayoutManager layoutManager = new LinearLayoutManager(MCampusActivity.this);
        rv_campus.setLayoutManager(layoutManager);
        adapter = new CampusAdapter(MCampusActivity.this,mList);
        rv_campus.setAdapter(adapter);


        //滑动监听
        rv_campus.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                /**
                 * 当RecyclerView的滑动状态改变时触发
                 * 0： 手指离开屏幕
                 * 1：  手指触摸屏幕
                 * 2： 手指加速滑动并放开，此时滑动状态伴随SCROLL_STATE_IDLE
                 */

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition =layoutManager.findLastVisibleItemPosition(); //最后一个可视的Item
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {

                    boolean isRefreshing = srl.isRefreshing();  //刷新状态

                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        //　加载数据
                        getData(curPage);

                        isLoading = false;
                    }
                }

            }
        });


        adapter.setOnItemClickListener(new CampusAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position, View view) {

            }

            @Override
            public void OnItemLongClick(final int position, final View view) {
                mMaterialDialog=new MaterialDialog(MCampusActivity.this)

                        .setMessage("你是否要删除此次活动？")
                        .setPositiveButton("残忍删除", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Campus campus =adapter.getCampusData(position);
                                String objectId = campus.getObjectId();

                                Campus campus1=new Campus();
                                campus1.setObjectId(objectId);
                                campus1.delete(MCampusActivity.this, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        mList.clear();
                                        BmobQuery<Campus> query = new BmobQuery<>();
                                        query.order("-updatedAt");
                                        query.include("bUser");
                                        query.setLimit(5);
                                        query.findObjects(MCampusActivity.this, new FindListener<Campus>() {
                                            @Override
                                            public void onSuccess(List<Campus> list) {
                                                if(list.size()!=0){
                                                    mList.addAll(list);
                                                    adapter.notifyDataSetChanged();
                                                }

                                            }
                                            @Override
                                            public void onError(int i, String s) {

                                            }
                                        });
                                        showToast("删除成功");
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        showToast("删除失败："+s);
                                    }
                                });

                                mMaterialDialog.dismiss();

                            }
                        })
                        .setNegativeButton("再看一看", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
            }
        });



    }
}
