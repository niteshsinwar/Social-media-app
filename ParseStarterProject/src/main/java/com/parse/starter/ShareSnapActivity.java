package com.parse.starter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class ShareSnapActivity extends AppCompatActivity {
String username;
    ParseObject object;
    EditText chatEditText;
    String messageContent;
    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //the
                Log.i("Image Selected", "Good work");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.png", byteArray);

                 object = new ParseObject("snap");

                object.put("image", file);

                object.put("username", ParseUser.getCurrentUser().getUsername());

                object.put("reciever", username);

                object.put("message", messageContent);


                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(ShareSnapActivity.this, "Image has been shared!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ShareSnapActivity.this, "There has been an issue uploading the image :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }



    public  void  upload (View view){
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            chatEditText = (EditText) findViewById(R.id.messageEditText);
            messageContent = chatEditText.getText().toString();
            getPhoto();

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_snap);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        setTitle(username + "'s Dashboard");
    }
}