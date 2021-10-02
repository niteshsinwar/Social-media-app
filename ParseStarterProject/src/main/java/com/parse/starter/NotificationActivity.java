package com.parse.starter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    ArrayList<String> users = new ArrayList<>();
    String username;
    ArrayAdapter adapter;
    String delete="hirob";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setTitle("Notifications");


       //setting up array adaptor
        final ListView listView = findViewById(R.id.nistview);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked,users);
        listView.setAdapter(adapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override//it will open alert box if we long click the item
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                 //alert dialog box
                final int itemToDelete = i;
                new AlertDialog.Builder(NotificationActivity.this)
                        //alert dialog box
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this note?")


                        //it will delete item from database and update array adaptor when we click yes
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                 delete =users.get(itemToDelete);
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("snap");
                                Log.i( "onClick: ", delete);
                                query.whereEqualTo("username",delete);
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        try {
                                            object.delete();
                                        } catch (ParseException parseException) {
                                            parseException.printStackTrace();
                                        }
                                        object.saveInBackground();
                                    }
                                });

                                //deleting item from array then updating list view
                               users.remove(itemToDelete);
                               adapter.notifyDataSetChanged();
                            }
                        })
                        // nothing will happen when we click no
                        .setNegativeButton("No",null)
                        .show();
                return true;
            }
        });


        //it will open view snap activity when we click any notification item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), ViewSnapActivity.class);
                intent.putExtra("username", users.get(i));
                startActivity(intent);
                ParseUser.getCurrentUser().saveInBackground();
            }
        });


        //fetching data from database for current user
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("snap");
        query.whereEqualTo("reciever", ParseUser.getCurrentUser().getUsername());
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        queries.add(query);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override//adding all the data to list view
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject user : objects) {
                        users.add(user.getString("username"));
                    }
                    adapter.notifyDataSetChanged();

                    for (String username : users) {
                        listView.setItemChecked(users.indexOf(username), true);
                    }
                }
            }
        });
    }
}