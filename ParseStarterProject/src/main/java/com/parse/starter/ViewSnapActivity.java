package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class ViewSnapActivity extends AppCompatActivity {
    String username;
    LinearLayout linLayout;
    TextView messageTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        setTitle(username + "'s message");



        linLayout = findViewById(R.id.linLayout);
        messageTextView = findViewById(R.id.messageTextView);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("snap");
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

                        messageTextView.setText(object.getString("message"));
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
    }
}