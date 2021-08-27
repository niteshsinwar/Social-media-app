package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    LinearLayout linLayout;
    String username;
    public void snap(View view) {
        Intent intent = new Intent(getApplicationContext(), ShareSnapActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void chat(View view) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        setTitle(username + "'s Dashboard");
        //setting up title


        linLayout = findViewById(R.id.linLayout);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");
        //selecting class name

        query.whereEqualTo("username", username);
        //selecting objects by username
        query.orderByDescending("createdAt");
        //order objects by date created
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                //function used to finding the object on parse server(background activity)

                if (e == null && objects.size() > 0) {
                    //if everything is ok
                    for (ParseObject object : objects) {
                        //looping through objects
                        ParseFile file = (ParseFile) object.get("image");
                        //getting parse object

                        file.getDataInBackground(new GetDataCallback() {
                            //function used to download file
                            @Override
                            public void done(byte[] data, ParseException e) {
                                //after collecting byte data by converting bitmap into image
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
                                    //converting array to bitmap
                                    ImageView imageView = new ImageView(getApplicationContext());
                                    //defining imageview

                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));//setting up image view parameter to its actual size

                                    imageView.setImageBitmap(bitmap);
                                    //setting up image view to bitmap
                                    linLayout.addView(imageView);
                                    //adding imageview to layout
                                }
                            }
                        });
                    }
                }
            }
        });




        final ListView listView = findViewById(R.id.tlistview);

        final List<Map<String,String>> tweetData = new ArrayList<>();

         query = ParseQuery.getQuery("Tweet");
        query.whereEqualTo("username", username);
        query.orderByDescending("createdAt");
        query.setLimit(20);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject tweet : objects) {
                        Map<String,String> tweetInfo = new HashMap<>();
                        tweetInfo.put("content",tweet.getString("tweet"));

                        tweetData.add(tweetInfo);
                    }
                    SimpleAdapter simpleAdapter = new SimpleAdapter(DashboardActivity.this, tweetData,android.R.layout.simple_list_item_2, new String[] {"content"}, new int[] {android.R.id.text1});

                    listView.setAdapter(simpleAdapter);
                }
            }
        });


    }
}