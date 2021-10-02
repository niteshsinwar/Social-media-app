package com.parse.starter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    LinearLayout linLayout;
    ParseObject object;
    String username;



    //opening device storage for uploading image
    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                //converting selected image from bitmap to array
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                //uploading selected image to database along with username and receiver name
                ParseFile file = new ParseFile("image.png", byteArray);
                object = new ParseObject("snap");
                object.put("image", file);
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("reciever", username);


                //determining end results success/fail
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(DashboardActivity.this, "Image has been shared!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DashboardActivity.this, "There has been an issue uploading the image :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //requesting permission to access device storage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    //snap activity
    public void snap(View view) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoto();
        }
    }

    //chat activity
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
        setTitle(username + "'s Dashboard"); //setting up title


      //setting up user dp
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



      //adding data to list view
         ArrayList<String> tweetData = new ArrayList<>();
         ListView listView = findViewById(R.id.tlistview);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tweetData);
        listView.setAdapter(adapter);

        query = ParseQuery.getQuery("Tweet");
        query.whereEqualTo("username", username);
        query.orderByDescending("createdAt");
        query.setLimit(20);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    for (ParseObject tweet : objects) {
                        tweetData.add(tweet.getString("tweet"));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}