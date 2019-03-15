//package com.devstudiosng.yg.smooop.adapter;
//
//import android.content.Context;
//import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
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
//public class AlbumPhotoAdapter extends RecyclerView.Adapter<AlbumPhotoAdapter.ContactItemViewHolder> {
//
//    private static final String TAG = AlbumPhotoAdapter.class.getSimpleName();
//    private Album mAlbum;
//    private List<String> mPhotos;
//    private Context mContext;
//    private LayoutInflater mLayoutInflater;
//    private RequestOptions requestOptions;
//    private SharedPref sharedPref;
//
//    public AlbumPhotoAdapter(Album album, Context context) {
//        mAlbum = album;
//        mContext = context;
//        mLayoutInflater = LayoutInflater.from(context);
//        requestOptions = new RequestOptions().fitCenter();
//        sharedPref = new SharedPref(mContext);
//    }
//
//    class ContactItemViewHolder extends RecyclerView.ViewHolder {
//
//        TextView mTitleTextView;
//        ImageView mImageView;
//        ImageView mDelete;
//
//
//        ContactItemViewHolder(View itemView) {
//            super(itemView);
//
//            mImageView = itemView.findViewById(R.id.photo);
//            mDelete = itemView.findViewById(R.id.delete);
//
//            mDelete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String albumName = mAlbum.getName();
//                    String filename = mAlbum.getFiles().get(getPosition());
//                    List<Album> allAlbums = sharedPref.getAlbums();
//                    for (Album album : allAlbums) {
//                        if ( albumName.equalsIgnoreCase(album.getName()) ) {
//                            //remove album from shared pref
//                            sharedPref.removeAlbum(album);
//                            for (String file : album.getFiles()) {
//                                if (file.equalsIgnoreCase(filename)) {
//                                    album.getFiles().remove(file);
//                                    mAlbum.setFiles(album.getFiles());
//                                    //add to shared pref
//                                    sharedPref.setAlbum(mAlbum);
//                                    Toast.makeText(mContext, "Photo deleted from album", Toast.LENGTH_LONG).show();
//                                    AlbumPhotoAdapter.this.notifyDataSetChanged();
//                                }
//
//                            }
//                        }
//                    }
//
//                }
//            });
//
//        }
//    }
//
//    @Override
//    public ContactItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = mLayoutInflater.inflate(R.layout.album_photo_layout, parent, false);
//        return new ContactItemViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ContactItemViewHolder holder, int position) {
//        String photo = mAlbum.getFiles().get(position);
//        Log.e(TAG, photo+" ");
//        Glide.with(mContext).load(Uri.parse(photo)) //file uri
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .apply(requestOptions)
//                .into(holder.mImageView);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (mAlbum.getFiles() != null)
//            return mAlbum.getFiles().size();
//        else
//            return 0;
//    }
//
//
//}
