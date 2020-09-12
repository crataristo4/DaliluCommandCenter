package com.dalilu.commandCenter.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dalilu.commandCenter.Dalilu;
import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.databinding.ImageTypeGridBinding;
import com.dalilu.commandCenter.databinding.VideoTypeGridBinding;
import com.dalilu.commandCenter.model.AlertItems;
import com.dalilu.commandCenter.ui.activities.CommentsActivity;
import com.dalilu.commandCenter.ui.activities.ImageViewActivity;
import com.dalilu.commandCenter.utils.AppConstants;
import com.dalilu.commandCenter.utils.DisplayViewUI;
import com.dalilu.commandCenter.utils.GetTimeAgo;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skyfishjy.library.RippleBackground;

import java.text.MessageFormat;
import java.util.ArrayList;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<AlertItems> dataSet;
    Context context;


    public HomeRecyclerAdapter(ArrayList<AlertItems> data, Context context) {
        this.dataSet = data;
        this.context = context;

    }

    public static void numOfComments(TextView txtComments, String postId) {

        CollectionReference commentsCf = FirebaseFirestore.getInstance().collection("Comments").document(postId).collection(postId);

        commentsCf.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                txtComments.setText(MessageFormat.format("{0} Comments", task.getResult().size()));


            }

        });


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case AppConstants.VIDEO_TYPE:

                return new VideoTypeViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_type_grid, parent, false));

            case AppConstants.IMAGE_TYPE:

                return new ImageTypeViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_type_grid, parent, false));


        }
        return null;


    }


    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).type) {
            case 0:
                return AppConstants.VIDEO_TYPE;
            case 1:
                return AppConstants.IMAGE_TYPE;
           /* case 2:
                return AppConstants.AUDIO_TYPE;*/
            default:
                return -1;

        }


    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    @SuppressLint({"CheckResult", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int listPosition) {
        //  String uid = MainActivity.uid;

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(DisplayViewUI.getRandomDrawableColor());
        requestOptions.error(DisplayViewUI.getRandomDrawableColor());
        requestOptions.centerCrop();

        Intent commentsIntent = new Intent(context, CommentsActivity.class);


        AlertItems object = dataSet.get(listPosition);
        if (object != null) {

            commentsIntent.putExtra("id", object.getId());
            commentsIntent.putExtra("url", object.getUrl());
            commentsIntent.putExtra("address", object.address);
            commentsIntent.putExtra("datePosted", object.getDateReported());

            switch (object.type) {
                case AppConstants.VIDEO_TYPE:

                    //bind data in xml
                    ((VideoTypeViewHolder) holder).videoTypeBinding.setVideoType(object);
                    //show time
                    ((VideoTypeViewHolder) holder).videoTypeBinding.txtTime.setText(GetTimeAgo.getTimeAgo(object.getTimeStamp()));
                    //check if video is seen by police
                    ((VideoTypeViewHolder) holder).isCrimeSolved(object.isSolved());

                    numOfComments(((VideoTypeViewHolder) holder).txtComments, object.getId());

                    //load users images into views
                    Glide.with(((VideoTypeViewHolder) holder).videoTypeBinding.getRoot().getContext())
                            .load(object.getUserPhotoUrl())
                            .thumbnail(0.5f)
                            .error(((VideoTypeViewHolder) holder).videoTypeBinding.getRoot().getResources().getDrawable(R.drawable.photo))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(((VideoTypeViewHolder) holder).videoTypeBinding.imgUserPhoto);

                    HttpProxyCacheServer proxy = Dalilu.getProxy(((VideoTypeViewHolder) holder).videoTypeBinding.getRoot().getContext());
                    String proxyUrl = proxy.getProxyUrl(object.getUrl());


                    ((VideoTypeViewHolder) holder).videoView.setVideoPath(proxyUrl);
                    //   ((VideoTypeViewHolder) holder).videoView.requestFocusFromTouch();

                    MediaController mediaController = new MediaController(((VideoTypeViewHolder) holder).videoTypeBinding.getRoot().getContext());
                    ((VideoTypeViewHolder) holder).videoView.setMediaController(mediaController);
                    mediaController.setAnchorView(((VideoTypeViewHolder) holder).videoView);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.BOTTOM;
                    mediaController.setLayoutParams(lp);
                    ((ViewGroup) mediaController.getParent()).removeView(mediaController);

                    ((VideoTypeViewHolder) holder).frameLayout.addView(mediaController);

                    //comment on click
                    ((VideoTypeViewHolder) holder).txtComments.setOnClickListener(view -> {

                        commentsIntent.putExtra("type", AppConstants.VIDEO_EXTENSION);
                        view.getContext().startActivity(commentsIntent);

                    });


                    break;
                case AppConstants.IMAGE_TYPE:
                    //bind data in xml
                    ((ImageTypeViewHolder) holder).imageTypeBinding.setImageType(object);
                    ((ImageTypeViewHolder) holder).imageTypeBinding.txtTime.setText(GetTimeAgo.getTimeAgo(object.getTimeStamp()));
                    //load user photo
                    Glide.with(((ImageTypeViewHolder) holder).imageTypeBinding.getRoot().getContext())
                            .load(object.getUserPhotoUrl())
                            .thumbnail(0.5f)
                            .error(((ImageTypeViewHolder) holder).imageTypeBinding.getRoot().getResources().getDrawable(R.drawable.photo))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(((ImageTypeViewHolder) holder).imageTypeBinding.imgUserPhoto);

                    //load images
                    Glide.with(((ImageTypeViewHolder) holder).imageTypeBinding.getRoot().getContext())
                            .load(object.getUrl())
                            .thumbnail(0.5f)
                            .apply(requestOptions)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                    if (isFirstResource) {
                                        ((ImageTypeViewHolder) holder).imageTypeBinding.progressBar.setVisibility(View.GONE);

                                    }
                                    ((ImageTypeViewHolder) holder).imageTypeBinding.progressBar.setVisibility(View.VISIBLE);

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((ImageTypeViewHolder) holder).imageTypeBinding.progressBar.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            }).transition(DrawableTransitionOptions.withCrossFade()).optionalCenterCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into((((ImageTypeViewHolder) holder).imageTypeBinding.imgContentPhoto));

                    ((ImageTypeViewHolder) holder).isCrimeSolved(object.isSolved());


                    //shows the number of comments on the post
                    numOfComments(((ImageTypeViewHolder) holder).txtComments, object.getId());

                    ((ImageTypeViewHolder) holder).imgAlertPhoto.setOnClickListener(view -> {
                        Intent img = new Intent(view.getContext(), ImageViewActivity.class);
                        img.putExtra(AppConstants.UID, object.getId());
                        img.putExtra(AppConstants.OBJECT_URL, object.getUrl());
                        img.putExtra(AppConstants.KNOWN_LOCATION, object.address);
                        img.putExtra(AppConstants.IS_SOLVED, object.isSolved);
                        img.putExtra(AppConstants.DATE_TIME, object.getDateReported());
                        img.putExtra(AppConstants.USER_NAME, object.getUserName());

                        view.getContext().startActivity(img);

                    });

                    ((ImageTypeViewHolder) holder).txtComments.setOnClickListener(view -> {
                        commentsIntent.putExtra("type", AppConstants.IMAGE_EXTENSION);


                       /* Intent commentsIntent = new Intent(view.getContext(), CommentsActivity.class);
                        commentsIntent.putExtra("id", object.getId());
                        commentsIntent.putExtra("type", AppConstants.IMAGE_TYPE);
                        commentsIntent.putExtra("url", object.getUrl());
                        commentsIntent.putExtra("address", object.address);
                        commentsIntent.putExtra("datePosted", object.getDateReported());*/

                        view.getContext().startActivity(commentsIntent);


                    });


                    break;

            }
        }

    }

    //view holder for videos
    static class VideoTypeViewHolder extends RecyclerView.ViewHolder {
        final VideoTypeGridBinding videoTypeBinding;
        final VideoView videoView;
        final FrameLayout frameLayout;
        final RippleBackground rippleBackground;
        //  final MediaPlayer mediaPlayer;
        final ImageView imgChecked;
        final TextView txtComments;

        VideoTypeViewHolder(@NonNull VideoTypeGridBinding videoTypeBinding) {
            super(videoTypeBinding.getRoot());
            this.videoTypeBinding = videoTypeBinding;
            videoView = videoTypeBinding.videoContentPreview;
            frameLayout = videoTypeBinding.controllerAnchor;
            rippleBackground = videoTypeBinding.rippleContent;
            imgChecked = videoTypeBinding.imgChecked;
            txtComments = videoTypeBinding.txtComments;
            //   mediaPlayer = MediaPlayer.create(videoTypeBinding.getRoot().getContext(), R.raw.alarm);

        }

        public void isCrimeSolved(boolean isSolved) {

            if (!isSolved) {

                rippleBackground.setVisibility(View.VISIBLE);
                imgChecked.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                rippleBackground.startRippleAnimation();
                videoView.setVisibility(View.GONE);

/*
                if (!mediaPlayer.isPlaying()) {
                    try {
                        //   mediaPlayer.reset();
                        mediaPlayer.start();
                        //   mediaPlayer.setLooping(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
*/

            } else {

                rippleBackground.setVisibility(View.GONE);
                imgChecked.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.VISIBLE);
                rippleBackground.stopRippleAnimation();
                videoView.setVisibility(View.VISIBLE);

/*
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.reset();
                        mediaPlayer.stop();
                        //   mediaPlayer.setLooping(false);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
*/

            }


        }


    }

    //view holder for images
    static class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        final ImageTypeGridBinding imageTypeBinding;
        final TextView txtComments;
        final ImageView imgAlertPhoto, imgChecked;
        final RippleBackground rippleBackground;
        //  final MediaPlayer mediaPlayer;


        ImageTypeViewHolder(@NonNull ImageTypeGridBinding imageTypeBinding) {
            super(imageTypeBinding.getRoot());
            this.imageTypeBinding = imageTypeBinding;
            txtComments = imageTypeBinding.txtComments;
            rippleBackground = imageTypeBinding.rippleContent;
            imgAlertPhoto = imageTypeBinding.imgContentPhoto;
            imgChecked = imageTypeBinding.imgChecked;
            //   mediaPlayer = MediaPlayer.create(imageTypeBinding.getRoot().getContext(), R.raw.alarm);


        }


        public void isCrimeSolved(boolean isSolved) {

            if (!isSolved) {

                rippleBackground.setVisibility(View.VISIBLE);
                imgChecked.setVisibility(View.GONE);
                rippleBackground.startRippleAnimation();
                //    imgAlertPhoto.setVisibility(View.GONE);

/*
                if (!mediaPlayer.isPlaying()) {
                    try {
                        //   mediaPlayer.reset();
                        mediaPlayer.start();
                     //   mediaPlayer.setLooping(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
*/

            }else {

                rippleBackground.setVisibility(View.GONE);
                imgChecked.setVisibility(View.VISIBLE);
                rippleBackground.stopRippleAnimation();
                // imgAlertPhoto.setVisibility(View.VISIBLE);

/*
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.reset();
                        mediaPlayer.stop();
                        //   mediaPlayer.setLooping(false);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
*/

            }


        }


    }


}
