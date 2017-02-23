package com.guodong.sun.guodong.entity.picture.singleimage;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.guodong.sun.guodong.R;
import com.guodong.sun.guodong.activity.LongPictureActivity;
import com.guodong.sun.guodong.entity.picture.ContentHolder;
import com.guodong.sun.guodong.entity.picture.PictureFrameProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <p> 描述
 *
 * @author 孙国栋
 * @version 1.0
 * @date 2017-02-22 15:31
 * @see
 */
public class LongImageViewProvider extends PictureFrameProvider<LongImage, LongImageViewProvider.ViewHolder> {

    @Override
    protected ContentHolder onCreateContentViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_picture_long_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindContentViewHolder(@NonNull final ViewHolder holder, @NonNull LongImage content) {

        final String url = content.getMiddle_image().getUrl_list().get(0).getUrl();

        Glide.with(holder.mImageView.getContext())
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.mImageView.setImageBitmap(Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), 300));
                    }
                });

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LongPictureActivity.startActivity(holder.mImageView.getContext(),
                        url);
            }
        });

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LongPictureActivity.startActivity(holder.mImageView.getContext(),
                        url);
            }
        });
    }

    static class ViewHolder extends ContentHolder {

        @BindView(R.id.fragment_picture_item_iv)
        ImageView mImageView;

        @BindView(R.id.fragment_picture_item_ll)
        LinearLayout mLinearLayout;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
