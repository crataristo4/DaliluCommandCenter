package com.dalilu.commandCenter.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.dalilu.commandCenter.databinding.ImageTypeBinding;
import com.dalilu.commandCenter.databinding.VideoTypeBinding;
import com.dalilu.commandCenter.model.AlertItems;
import com.dalilu.commandCenter.ui.activities.CommentsActivity;
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


    public HomeRecyclerAdapter(ArrayList<AlertItems> data, Context context) {
        this.dataSet = data;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case AppConstants.VIDEO_TYPE:

                return new VideoTypeViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_type, parent, false));

            case AppConstants.IMAGE_TYPE:

                return new ImageTypeViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_type, parent, false));


        }
        return null;


    }

    @SuppressLint({"CheckResult", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int listPosition) {
        //  String uid = MainActivity.uid;

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(DisplayViewUI.getRandomDrawableColor());
        requestOptions.error(DisplayViewUI.getRandomDrawableColor());
        requestOptions.centerCrop();

        AlertItems object = dataSet.get(listPosition);
        if (object != null) {

            switch (object.type) {
                case AppConstants.VIDEO_TYPE:

                    //bind data in xml
                    ((VideoTypeViewHolder) holder).videoTypeBinding.setVideoType(object);
                    //show time
                    ((VideoTypeViewHolder) holder).videoTypeBinding.txtTime.setText(GetTimeAgo.getTimeAgo(object.getTimeStamp()));
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


                    ((ImageTypeViewHolder) holder).numOfComments(((ImageTypeViewHolder) holder).txtComments, object.getId());

                    ((ImageTypeViewHolder) holder).txtComments.setOnClickListener(view -> {

                        Intent commentsIntent = new Intent(view.getContext(), CommentsActivity.class);
                        commentsIntent.putExtra("alertItemId", object.getId());
                        commentsIntent.putExtra("alertPhotoUrl", object.getUrl());
                        commentsIntent.putExtra("address", object.address);
                        commentsIntent.putExtra("datePosted", object.getDateReported());

                        view.getContext().startActivity(commentsIntent);


                    });


                    break;

            }
        }

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



    //view holder for videos
    static class VideoTypeViewHolder extends RecyclerView.ViewHolder {
        final VideoTypeBinding videoTypeBinding;
        final VideoView videoView;
        final FrameLayout frameLayout;


        VideoTypeViewHolder(@NonNull VideoTypeBinding videoTypeBinding) {
            super(videoTypeBinding.getRoot());
            this.videoTypeBinding = videoTypeBinding;
            videoView = videoTypeBinding.videoContentPreview;
            frameLayout = videoTypeBinding.controllerAnchor;


        }


    }

    //view holder for images
    static class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        final ImageTypeBinding imageTypeBinding;
        final TextView txtComments;
        final RippleBackground rippleBackground;
        final MediaPlayer mediaPlayer;


        ImageTypeViewHolder(@NonNull ImageTypeBinding imageTypeBinding) {
            super(imageTypeBinding.getRoot());
            this.imageTypeBinding = imageTypeBinding;
            txtComments = imageTypeBinding.txtComments;
            rippleBackground = imageTypeBinding.rippleContent;
          //  rippleBackground.startRippleAnimation();
            mediaPlayer = MediaPlayer.create(imageTypeBinding.getRoot().getContext(), R.raw.alarm);


        }

        private void numOfComments(TextView txtComments, String postId) {

            CollectionReference commentsCf = FirebaseFirestore.getInstance().collection("Comments").document(postId).collection(postId);

            commentsCf.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    txtComments.setText(MessageFormat.format("{0} Comments", task.getResult().size()));


                }

            });


        }


        private void isCrimeSolved(boolean isSolved){

            if (!isSolved){

                rippleBackground.setVisibility(View.VISIBLE);
                rippleBackground.startRippleAnimation();
                if (!mediaPlayer.isPlaying()){
                    try {
                     //   mediaPlayer.reset();
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }





            }else {

                rippleBackground.setVisibility(View.GONE);
                rippleBackground.stopRippleAnimation();
                try {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.reset();
                        mediaPlayer.stop();

                        mediaPlayer.setLooping(false);
                    }
                }catch (Exception e){

                    e.printStackTrace();
                }




                // mediaPlayer = null;
                //      mediaPlayer.release();


            }


        }


      /*  private void numOfComments(TextView txtComments, String postId) {
            DatabaseReference likesDbRef = FirebaseDatabase.getInstance()
                    .getReference().child("Comments").child(postId);

            likesDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    txtComments.setText(MessageFormat.format("{0} Comments", dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }*/


    }



}
