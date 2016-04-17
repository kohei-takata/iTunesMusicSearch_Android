package com.example.kohei.itunesmusicsearch_android;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ListActivity extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private android.widget.ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new LruImageCache());
        mAdapter = new ListAdapter(this, R.layout.list_item);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);

        setContentView(R.layout.activity_list);
    }

    private class ListAdapter extends ArrayAdapter<JSONObject> {
        public ListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, null);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
            TextView tractTextView = (TextView) convertView.findViewById(R.id.track_text_view);
            TextView artistTextView = (TextView) convertView.findViewById(R.id.artist_text_view);

            ImageLoader.ImageContainer imageContainer = (ImageLoader.ImageContainer) imageView.getTag();
            if (imageContainer != null) {
                imageContainer.cancelRequest();
            }
            imageView.setImageBitmap(null);

            JSONObject result = getItem(position);

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
            imageView.setTag(mImageLoader.get(result.optString("artworkUrl100"), listener));

            tractTextView.setText(result.optString("trackName"));
            artistTextView.setText(result.optString("artistName"));

            return convertView;
        }
    }
}