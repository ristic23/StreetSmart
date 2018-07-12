package com.jovan_ristic.streetsmart.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jovan_ristic.streetsmart.R;
import com.jovan_ristic.streetsmart.adapter.GalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class GalleryActivity extends AppCompatActivity
{

    private RecyclerView mRecyclerViewGallery;
    private GalleryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View loadingIndicator;
    private ArrayList<String> galleryImages;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_gallery);
        }
        catch(Exception | OutOfMemoryError e)
        {
            Toast.makeText(this, getResources().getString(R.string.errorMsg), Toast.LENGTH_SHORT).show();
            finish();
        }
        mActivity = this;
        loadingIndicator=findViewById(R.id.loadingIndicator);
        mRecyclerViewGallery = findViewById(R.id.recyclerViewGallery);
        if(mRecyclerViewGallery != null) {
            initRecyclerViewGallery();
        }
        openGallery();
    }

        private void openGallery()
    {
        new GetGalleryImages().execute();
    }

    private void initRecyclerViewGallery()
    {
        mLayoutManager = new GridLayoutManager(GalleryActivity.this, 4);
        mRecyclerViewGallery.setLayoutManager(mLayoutManager);
        mAdapter = new GalleryAdapter(GalleryActivity.this, galleryImages, GalleryActivity.this);
        mRecyclerViewGallery.setAdapter(mAdapter);
        mRecyclerViewGallery.scrollToPosition(0);
    }

    public void pickedFromGallery(String link) {
        try {
            File chosenImage = new File(link);
            SharedPreferences sharedPrefPicture = GalleryActivity.this.getSharedPreferences("PHOTO_DATA", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefPicture.edit();
            editor.putString("PhotoPath", chosenImage.getAbsolutePath());
            editor.putBoolean("CameraSet", false);
            editor.apply();
            setResult(RESULT_OK, getIntent());
            finish();
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Error occurred",Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, getIntent());
        finish();
    }

    public ArrayList<String> getFilePaths() throws Exception
    {

        ArrayList<String> resultIAV = new ArrayList<String>();
        if(mActivity!=null) {
            Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.ImageColumns.DATA};
            Cursor c = null;
            SortedSet<String> dirList = new TreeSet<String>();


            String[] directories = null;
            if (u != null) {
                c = mActivity.getContentResolver()
                        .query(u, projection, null, null, null);
            }

            if ((c != null) && (c.moveToFirst())) {
                do {
                    try {
                        String tempDir = c.getString(0);
                        tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                        dirList.add(tempDir);
                    } catch (Exception e) {
                        Log.v("TAG",e.getMessage());
                        break;
                    }
                }
                while (c.moveToNext());


                directories = new String[dirList.size()];
                dirList.toArray(directories);
                c.close();
            }

            for (int i = 0; i < dirList.size(); i++) {
                assert directories != null;
                File imageDir = new File(directories[i]);
                File[] imageList = imageDir.listFiles();
                if (imageList == null)
                    continue;
                for (File imagePath : imageList) {
                    try {

                        if (imagePath.isDirectory()) {
                            imageList = imagePath.listFiles();

                        }
                        if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                                || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                                || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG"))
                        {
                            String path = imagePath.getAbsolutePath();
                            resultIAV.add(path);

                        }
                    }
                    //  }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return resultIAV;
    }

    private class GetGalleryImages extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(loadingIndicator!=null)
                loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                galleryImages = getFilePaths();
                return null;
            }
            catch (Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(loadingIndicator!=null)
                loadingIndicator.setVisibility(View.INVISIBLE);

            mAdapter.updateData(galleryImages);
            mAdapter.notifyDataSetChanged();
            if(mRecyclerViewGallery != null)
                mRecyclerViewGallery.setVisibility(View.VISIBLE);

        }
    }
}
