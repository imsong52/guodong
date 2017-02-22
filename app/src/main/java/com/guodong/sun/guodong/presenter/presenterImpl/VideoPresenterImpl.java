package com.guodong.sun.guodong.presenter.presenterImpl;

import com.google.gson.Gson;
import com.guodong.sun.guodong.api.ApiHelper;
import com.guodong.sun.guodong.api.DuanZiApi;
import com.guodong.sun.guodong.entity.duanzi.NeiHanVideo;
import com.guodong.sun.guodong.entity.duanzi.VideoData;
import com.guodong.sun.guodong.presenter.IVideoPresenter;
import com.guodong.sun.guodong.view.IVideoView;
import com.trello.rxlifecycle.LifecycleTransformer;


import java.util.ArrayList;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/17.
 */

public class VideoPresenterImpl extends BasePresenterImpl implements IVideoPresenter
{
    private IVideoView mVideoView;
    private Gson mGson;
    private LifecycleTransformer bind;

    public VideoPresenterImpl(IVideoView videoView, LifecycleTransformer bind)
    {
        mVideoView = videoView;
        mGson = new Gson();
        this.bind = bind;
    }

    @Override
    public void getVideoData(int page)
    {
        mVideoView.showProgressBar();
        Subscription subscription = ApiHelper.getInstance()
                .getApi(DuanZiApi.class, ApiHelper.DUANZI_BASE_URL).getVideoData(page)
                .compose(bind)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoData>()
                {
                    @Override
                    public void onCompleted()
                    {
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        mVideoView.hideProgressBar();
                        mVideoView.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(VideoData data)
                    {
                        mVideoView.hideProgressBar();
                        ArrayList<NeiHanVideo.DataBean> list = new ArrayList<>();
                        for (NeiHanVideo.DataBean bean : data.getData().getData()) {
                            if (bean.getType() == 1)
                                list.add(bean);
                        }
                        mVideoView.updateVideoData(list);
                    }
                });
        addSubscription(subscription);
    }
}
