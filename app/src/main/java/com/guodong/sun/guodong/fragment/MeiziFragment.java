package com.guodong.sun.guodong.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.guodong.sun.guodong.R;
import com.guodong.sun.guodong.adapter.MeiziAdapter;
import com.guodong.sun.guodong.base.AbsBaseFragment;
import com.guodong.sun.guodong.entity.meizi.Meizi;
import com.guodong.sun.guodong.presenter.presenterImpl.MeiziPresenterImpl;
import com.guodong.sun.guodong.uitls.AppUtil;
import com.guodong.sun.guodong.uitls.SnackbarUtil;
import com.guodong.sun.guodong.view.IMeiziView;
import com.guodong.sun.guodong.widget.CustomEmptyView;
import com.guodong.sun.guodong.widget.WrapContentLinearLayoutManager;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Administrator on 2016/10/9.
 */

public class MeiziFragment extends AbsBaseFragment implements IMeiziView
{
    @BindView(R.id.meizi_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.meizi_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.meizi_fb)
    FloatingActionButton mFButton;

    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    //RecycleView是否正在刷新
    private boolean isRefreshing = false;
    private boolean isLoading;
    private int page = 1;

    private MeiziAdapter mAdapter;
    private MeiziPresenterImpl mMeiziPresenter;
    private ObjectAnimator mAnimator;

    public static MeiziFragment newInstance()
    {
        return new MeiziFragment();
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.fragment_meizi;
    }

    @Override
    protected void lazyLoad()
    {
        if (!isPrepared || !isVisible)
            return;
        showProgressBar();
        initRecyclerView();
        initFButton();
        isPrepared = false;
        mMeiziPresenter.getMeiziData(1);
    }

    private void initFButton()
    {
        mFButton.attachToRecyclerView(mRecyclerView);
        mFButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isRefreshing || isLoading)
                    return;
                mAnimator = ObjectAnimator.ofFloat(v, "rotation", 0F, 360F);
                mAnimator.setDuration(500);
                mAnimator.setInterpolator(new LinearInterpolator());
                mAnimator.setRepeatCount(ValueAnimator.INFINITE);
                mAnimator.setRepeatMode(ValueAnimator.RESTART);
                mAnimator.start();
                mRecyclerView.scrollToPosition(0);
                isRefreshing = true;
                mMeiziPresenter.getMeiziData(1);
            }
        });
    }

    private void initRecyclerView()
    {
        mAdapter = new MeiziAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //向下滚动
                {
                    WrapContentLinearLayoutManager llm = (WrapContentLinearLayoutManager) recyclerView.getLayoutManager();
                    int visibleItemCount = llm.getChildCount();
                    int totalItemCount = llm.getItemCount();
                    int firstVisiblesItemPos = llm.findFirstVisibleItemPosition();

                    if (!isLoading && (visibleItemCount + firstVisiblesItemPos) >= totalItemCount)
                    {
                        isLoading = true;
                        page++;
                        loadMoreDate();
                    }
                }
            }
        });
    }

    private void loadMoreDate()
    {
        mAdapter.onLoadStart();
        mMeiziPresenter.getMeiziData(page);
    }

    @Override
    public void finishCreateView(Bundle state)
    {
        isPrepared = true;
        mMeiziPresenter = new MeiziPresenterImpl(getContext(), this);
        lazyLoad();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mMeiziPresenter.unsubcrible();
    }

    @Override
    public void updateMeiziData(ArrayList<Meizi> list)
    {
        hideEmptyView();
        mAdapter.addLists(list);
        mAdapter.onLoadFinish();
        isLoading = false;
    }

    @Override
    public void showProgressBar()
    {
        isRefreshing = true;
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                isRefreshing = true;
//                mAdapter.clearMeiziList();
                mMeiziPresenter.getMeiziData(1);
            }
        });
    }

    @Override
    public void hidProgressBar()
    {
        if (mAnimator != null)
            mAnimator.cancel();
        mSwipeRefreshLayout.setRefreshing(false);
        isRefreshing = false;
    }

    @Override
    public void showError(String error)
    {
        initEmptyView();
    }

    public void hideEmptyView()
    {
        mCustomEmptyView.setVisibility(View.GONE);
    }

    public void initEmptyView()
    {
        if (!AppUtil.isNetworkConnected())
        {
            SnackbarUtil.showMessage(mRecyclerView, getString(R.string.noNetwork));
        }
        else
        {
            mSwipeRefreshLayout.setRefreshing(false);
            mCustomEmptyView.setVisibility(View.VISIBLE);
            mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
            mCustomEmptyView.setEmptyText(getString(R.string.loaderror));
            SnackbarUtil.showMessage(mRecyclerView, getString(R.string.noNetwork));
            mCustomEmptyView.reload(new CustomEmptyView.ReloadOnClickListener()
            {
                @Override
                public void reloadClick()
                {
                    mMeiziPresenter.getMeiziData(1);
                }
            });
        }
    }
}