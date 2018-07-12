package com.jovan_ristic.streetsmart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jovan_ristic.streetsmart.R;
import com.jovan_ristic.streetsmart.activity.GalleryActivity;
import com.jovan_ristic.streetsmart.helpers.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mGalleryImages;
    private GalleryActivity mGalleryActivity;


    public GalleryAdapter(Context context, ArrayList<String> galleryImages, GalleryActivity gA)
    {
        mContext=context;
        mGalleryImages=galleryImages;
        this.mGalleryActivity = gA;
    }

    public void updateData(ArrayList<String> galleryImages)
    {
        mGalleryImages=galleryImages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        clearAdapter(holder);
    }

    protected void clearAdapter(ViewHolder holder) {

        try{
            GlideApp.with(mContext).clear(holder.itemView.findViewById(R.id.image));
        }
        catch(Exception|OutOfMemoryError outOfMemoryError)
        {
            //
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ImageView imageView= holder.itemView.findViewById(R.id.image);
        final String url= mGalleryImages.get(position);
        if(imageView!=null) {
            try {
                GlideApp.with(mContext).load(url).thumbnail(0.2f).centerCrop().into(imageView);
            } catch (Exception | OutOfMemoryError outOfMemoryError) {
                Log.v("TAG", "OOM happened");
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        mGalleryActivity.pickedFromGallery(url);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
       if(mGalleryImages!=null)
           return mGalleryImages.size();
       else
           return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
