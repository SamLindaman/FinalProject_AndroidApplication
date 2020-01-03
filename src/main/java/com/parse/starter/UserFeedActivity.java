package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

//This is the most complicated class with many nested loops
//this is also where all of the user's photos are shared
//I do not have an android phone, so unfortunately i could not
//share any photos other than the ones that are able to be taken by
//the android studio emulator, so the photos are boring. However
//the implementation works as is expected although the photos are not
//as interesting as they would be with user's posting photos of their lives


public class UserFeedActivity extends AppCompatActivity {

    boolean isDoubleClicked=false;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        TextView title = new TextView(getApplicationContext());
        title.setGravity(Gravity.CENTER);
        title.setText("Double tap to like a picture");
        linearLayout.addView(title);

        //set title for the username's photos
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        setTitle(username+"'s Photos");

        //opens a query for all of the images with a search for the current user's username
        //as an attribute
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("image");
        query.whereEqualTo("username",username);
        query.orderByDescending("createdAt");   // show newest pictures first on feed

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override //callback
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size()>0){                       //if there is no errors and there is at least 1 photo in the database, continue
                    for(final ParseObject object : objects){            // loop through all images
                        ParseFile file = (ParseFile) object.get("image");       //open parse file for the current image
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {       //data must be converted to bytes and bitmaps for images
                                if(e==null&&data!=null){
                                    int count = 0;
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);

                                    final TextView numberOfLikes = new TextView(getApplicationContext());
                                    final ImageView imageview = new ImageView(getApplicationContext());
                                    //programmatically add the images and textviews to the listlayout defined in the xml file
                                    imageview.setLayoutParams( new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));
                                    //this sets the image tag = to the unique objectID in the parse database
                                    //this allows us to recall and store the correct number of likes based on the picture
                                    //being double tapped.The number of likes is stored in the database as well as in the
                                    //objects ID
                                    imageview.setImageBitmap(bitmap);
                                    imageview.setPadding(10,20,10,10);
                                    imageview.setId(count);
                                    imageview.setClickable(true);
                                    numberOfLikes.setText("Number of likes: " + object.get("numberoflikes"));
                                    numberOfLikes.setGravity(Gravity.CENTER);
                                    imageview.setTag(object.getObjectId().toString());
                                    linearLayout.addView(imageview);
                                    linearLayout.addView(numberOfLikes);
                                    count++;

                                    //the following runnable code is to check for a double click scenario
                                    final Handler handler=new Handler();

                                    final Runnable r=new Runnable(){
                                        @Override
                                        public void run(){
                                            //Actions when Single Clicked
                                            isDoubleClicked=false;
                                        }
                                    };

                                    imageview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(isDoubleClicked){
                                                //Actions when double Clicked

                                                Log.i("Clicked Image","It Works!");

                                                ParseQuery queryImage = ParseQuery.getQuery("image");
                                                queryImage.whereEqualTo("objectId",imageview.getTag());

                                                //calculates the new number of likes, and sets it as the ID and stores to database
                                                int likes = imageview.getId();
                                                likes++;

                                                imageview.setId(likes);
                                                numberOfLikes.setText("Number of likes: "+likes);

                                                object.put("numberoflikes",likes);
                                                //save the changes made to the parse object
                                                object.saveInBackground();


                                                isDoubleClicked=false;
                                                //remove callbacks for Handlers
                                                handler.removeCallbacks(r);
                                            }else{
                                                //if not double clicked, wait for 500 milliseconds for the second click, or exit.
                                                isDoubleClicked=true;
                                                handler.postDelayed(r,500);
                                            }
                                        }
                                    });{


                                    }
                                }
                            }
                        });
                    }
                }
            }
        });











    }
}
