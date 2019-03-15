//package com.devstudiosng.yg.smooop.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
//import com.bumptech.glide.request.RequestOptions;
//import com.devstudiosng.yg.smooop.R;
//import com.devstudiosng.yg.smooop.helpers.SharedPref;
//import com.devstudiosng.yg.smooop.model.Album;
//
//import java.util.List;
//
///**
// * Created by Apple on 09/01/16.
// */
//public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ContactItemViewHolder> {
//
//    private static final String TAG = AlbumAdapter.class.getSimpleName();
//    private List<Album> mAlbum;
//    private Context mContext;
//    private LayoutInflater mLayoutInflater;
//    private RequestOptions requestOptions;
//    private boolean isHorizontal = false;
//    private SharedPref sharedPref;
//
//    public AlbumAdapter(List<Album> album, Context context) {
//        mAlbum = album;
//        mContext = context;
//        mLayoutInflater = LayoutInflater.from(context);
//        requestOptions = new RequestOptions().fitCenter();
//    }
//
//    public AlbumAdapter(List<Album> album, Context context, boolean isHorizontal) {
//        mAlbum = album;
//        mContext = context;
//        mLayoutInflater = LayoutInflater.from(context);
//        requestOptions = new RequestOptions().fitCenter();
//        this.isHorizontal = isHorizontal;
//        sharedPref = new SharedPref(mContext);
//    }
//
//    class ContactItemViewHolder extends RecyclerView.ViewHolder {
//
//        TextView mTitleTextView;
//        ImageView mImageView;
//        RadioButton mRadioButton;
//
//        ContactItemViewHolder(View itemView) {
//            super(itemView);
//
//            mTitleTextView = itemView.findViewById(R.id.titleTextView);
//            if (isHorizontal) {
//                mRadioButton = itemView.findViewById(R.id.radio_button);
//                mImageView = itemView.findViewById(R.id.photo);
//
//                mRadioButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (mRadioButton.isChecked())
//                            sharedPref.setAutoChangeAlbum(mAlbum.get(getPosition()));
//                    }
//                });
//            } else {
//                mImageView = itemView.findViewById(R.id.wallpaper_imageview);
//            }
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (!isHorizontal) {
//                        Intent intent = new Intent(mContext, AlbumDetailActivity.class);
//                        intent.putExtra(WallpaperApp.ALBUM, mAlbum.get(getPosition()));
//                        mContext.startActivity(intent);
//                    }
//                }
//            });
//
//
//        }
//    }
//
//    @Override
//    public ContactItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view;
//        if (isHorizontal) {
//            view = mLayoutInflater.inflate(R.layout.album_horizontal_layout, parent, false);
//        } else {
//            view = mLayoutInflater.inflate(R.layout.category_layout, parent, false);
//        }
//        return new ContactItemViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ContactItemViewHolder holder, int position) {
//        Album Album = mAlbum.get(position);
//
//        if (Album.getFiles() != null && Album.getFiles().size() > 0) {
//            Glide.with(mContext).load(Uri.parse(Album.getFiles().get(0))) //file uri
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .apply(requestOptions)
//                    .into(holder.mImageView);
//        }
//        holder.mTitleTextView.setText(Album.getName());
//    }
//
//    @Override
//    public int getItemCount() {
//        if (mAlbum != null)
//            return mAlbum.size();
//        else
//            return 0;
//    }
//
//
//}
