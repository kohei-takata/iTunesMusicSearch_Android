package com.example.kohei.itunesmusicsearch_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ListActivity extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new LruImageCache());
        mAdapter = new ListAdapter(this, R.layout.list_item);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListenter());

        final EditText editText = (EditText) findViewById(R.id.edit_text);
        editText.setOnKeyListener(new OnKeyListener());
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

    private class OnKeyListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                EditText editText = (EditText) view;

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                String text = editText.getText().toString();
                try {
                    text = URLEncoder.encode(text, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("", e.toString(), e);
                    return true;
                }
                if (!TextUtils.isEmpty(text)) {
                    String url = "https://itunes.apple.com/search?term=" + text + "&country=JP&media=music&lang=ja_jp";
                    mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("", response.toString());

                            mAdapter.clear();

                            JSONArray results = response.optJSONArray("results");
                            if (results != null) {
                                for (int i = 0; i < results.length(); i++) {
                                    mAdapter.add(results.optJSONObject(i));
                                }
                            }
                        }
                    },
                    null));
                }
                return true;
            }
            return false;
        }
    }

    private class OnItemClickListenter implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent intent = new Intent(ListActivity.this, DetailActivity.class);

            JSONObject result = mAdapter.getItem(position);
            intent.putExtra("track_name", result.optString("trackName"));
            intent.putExtra("preview_url", result.optString("previewUrl"));

            startActivity(intent);
        }
    }
}