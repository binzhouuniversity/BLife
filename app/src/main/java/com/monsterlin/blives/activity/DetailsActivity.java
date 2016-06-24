package com.monsterlin.blives.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.monsterlin.blives.BaseActivity;
import com.monsterlin.blives.R;
import com.monsterlin.blives.constants.DetailType;
import com.monsterlin.blives.entity.Acinforms;
import com.monsterlin.blives.entity.Jobnews;
import com.monsterlin.blives.entity.Offnews;
import com.monsterlin.blives.entity.SchoolNews;
import com.monsterlin.blives.utils.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 新闻详情
 * Created by monsterLin on 2016/4/2.
 */
public class DetailsActivity extends BaseActivity{

    @InjectView(R.id.toolbar)
    Toolbar mToolBar ;

    @InjectView(R.id.toolbar_layout)
     CollapsingToolbarLayout collapsingToolbarLayout ;

    @InjectView(R.id.fab)
     FloatingActionButton fab;

    @InjectView(R.id.tv_date)
     TextView tv_date;

    @InjectView(R.id.tv_content)
    TextView tv_content;

    @InjectView(R.id.iv_img)
     ImageView iv_img;


    private SchoolNews schoolNews; //学校新闻
    private Acinforms acinforms ; //学术新闻
    private Offnews offnews; //教务信息
    private Jobnews jobnews; //就业新闻

    private String newsInfo ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.inject(this);
        initToolBar();
        initData();
        initEvent();
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, newsInfo);
                startActivity(Intent.createChooser(shareIntent, "分享"));
            }
        });
    }

    /**
     * 初始化数据源
     */
    private void initData() {
        int type = getIntent().getBundleExtra("dataExtra").getInt("type");
        switch (type){
            case DetailType.SchoolNews:
                 schoolNews = (SchoolNews) getIntent().getBundleExtra("dataExtra").getSerializable("detail");  //得到上一级传来的数据
                initDetail(schoolNews.getTitle(),schoolNews.getContent(),schoolNews.getNewsdate().getDate(),schoolNews.getNewsimg());
                newsInfo=""+schoolNews.getTitle()+"\n"+schoolNews.getContent()+"\n";
                break;
            case DetailType.Acinforms:
                acinforms= (Acinforms) getIntent().getBundleExtra("dataExtra").getSerializable("detail");
                initDetail(acinforms.getTitle(),acinforms.getContent(),acinforms.getNewsdate().getDate(),acinforms.getNewsimg());
                newsInfo=""+acinforms.getTitle()+"\n"+acinforms.getContent()+"\n";
                break;
            case DetailType.Offnews:
                offnews= (Offnews) getIntent().getBundleExtra("dataExtra").getSerializable("detail");
                initDetail(offnews.getTitle(),offnews.getContent(),offnews.getNewsdate().getDate(),offnews.getNewsimg());
                newsInfo=""+offnews.getTitle()+"\n"+offnews.getContent()+"\n";
                break;
            case DetailType.Jobnews:
                jobnews= (Jobnews) getIntent().getBundleExtra("dataExtra").getSerializable("detail");
                initDetail(jobnews.getTitle(),jobnews.getContent(),jobnews.getNewsdate().getDate(),jobnews.getNewsimg());
                newsInfo=""+jobnews.getTitle()+"\n"+jobnews.getContent()+"\n";
                break;
        }
    }


    /**
     * 初始化详情
     * @param title
     * @param content
     * @param date
     * @param informImg
     */
    public void initDetail(String title , String content , String date, BmobFile informImg){
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        tv_content.setText(content);
        tv_date.setText(stringFormate(date));

        if (informImg!=null){
            iv_img.setTag(informImg.getFileUrl(this));

            new ImageLoader().showImageByAsyncTask(iv_img,informImg.getFileUrl(this));
        }else {
            iv_img.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 格式化时间
     * @param date
     * @return
     */
    private String stringFormate (String date){
        String dateString;
        dateString = date.substring(0,10);
        return dateString ;
    }


    private void initToolBar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //出现返回箭头
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



}
