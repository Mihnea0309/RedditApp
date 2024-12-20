package com.example.redditapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.redditapp.Account.LoginActivity;
import com.example.redditapp.Comments.CommentsActivity;
import com.example.redditapp.model.Feed;
import com.example.redditapp.model.entry.Entry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    URLS urls = new URLS();

    private Button btnRefreshFeed;
    private EditText mFeedName;
    private String currentFeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting.");
        btnRefreshFeed = (Button) findViewById(R.id.btnRefreshFeed);
        mFeedName = (EditText) findViewById(R.id.etFeedName);
        currentFeed = "androiddev";

        setupToolbar();

        init();

        btnRefreshFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedName = mFeedName.getText().toString();
                if(!feedName.equals("")){
                    currentFeed = feedName;
                    init();
                }
                else{
                    init();
                }
            }
        });

    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: clicked menu item: " + item);

                if (item.getItemId() == R.id.navLogin) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

    }

    private void init(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urls.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed(currentFeed);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                List<Entry> entrys = response.body().getEntrys();
                Log.d(TAG, "onResponse: entrys: " + response.body().getEntrys());

                final ArrayList<Post> posts = new ArrayList<>();
                for (int i = 0; i < entrys.size(); i++) {
                    ExtractXML extractXML1 = new ExtractXML(entrys.get(i).getContent(), "<a href=");
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML(entrys.get(i).getContent(), "<img src=");
                    try {
                        List<String> thumbnailContent = extractXML2.start();
                        if (thumbnailContent.size() > 0) {
                            postContent.add(thumbnailContent.get(0));
                        } else {
                            postContent.add(null);
                        }
                    } catch (NullPointerException | IndexOutOfBoundsException e) {
                        postContent.add(null);
                        Log.e(TAG, "onResponse: Exception(thumbnail): " + e.getMessage());
                    }

                    if (postContent.size() > 0) {
                        int lastPosition = postContent.size() - 1;
                        try {
                            posts.add(new Post(
                                    entrys.get(i).getTitle(),
                                    entrys.get(i).getAuthor().getName(),
                                    entrys.get(i).getUpdated(),
                                    postContent.get(0),
                                    postContent.get(lastPosition),
                                    entrys.get(i).getId()
                            ));
                        } catch (NullPointerException e) {
                            posts.add(new Post(
                                    entrys.get(i).getTitle(),
                                    "None",
                                    entrys.get(i).getUpdated(),
                                    postContent.get(0),
                                    postContent.get(lastPosition),
                                    entrys.get(i).getId()
                            ));
                            Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());
                        }
                    } else {
                        Log.e(TAG, "onResponse: postContent is empty for entry index " + i);
                    }
                }

                for (Post post : posts) {
                    Log.d(TAG, "onResponse: \n " +
                            "PostURL: " + post.getPostURL() + "\n " +
                            "ThumbnailURL: " + post.getThumbnailURL() + "\n " +
                            "Title: " + post.getTitle() + "\n " +
                            "Author: " + post.getAuthor() + "\n " +
                            "updated: " + post.getDate_updated() + "\n " +
                            "id: " + post.getId() + "\n ");
                }

                ListView listView = findViewById(R.id.listView);
                CustomListAdapter customListAdapter = new CustomListAdapter(MainActivity.this, R.layout.card_layout_main, posts);
                listView.setAdapter(customListAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d(TAG, "onItemClick: Clicked: " + posts.get(i).toString());
                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                        intent.putExtra("@string/post_url", posts.get(i).getPostURL());
                        intent.putExtra("@string/post_thumbnail", posts.get(i).getThumbnailURL());
                        intent.putExtra("@string/post_title", posts.get(i).getTitle());
                        intent.putExtra("@string/post_author", posts.get(i).getAuthor());
                        intent.putExtra("@string/post_updated", posts.get(i).getDate_updated());
                        intent.putExtra("@string/post_id", posts.get(i).getId());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(MainActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }
}