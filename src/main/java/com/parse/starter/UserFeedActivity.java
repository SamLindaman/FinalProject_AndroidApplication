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
        //get image from parse database

        //set title for the username's photos
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        setTitle(username+"'s Photos");

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("image");
        query.whereEqualTo("username",username);
        query.orderByDescending("createdAt");   // show newest pictures first on feed

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects.size()>0){
                    for(ParseObject object : objects){
                        ParseFile file = (ParseFile) object.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e==null&&data!=null){
                                    int count = 0;
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                    final TextView numberOfLikes = new TextView(getApplicationContext());
                                    //numberOfLikes.setTextColor(000);



                                    final ImageView imageview = new ImageView(getApplicationContext());
                                    imageview.setLayoutParams( new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));
                                    imageview.setTag("0");
                                    imageview.setImageBitmap(bitmap);
                                    imageview.setPadding(10,20,10,10);
                                    imageview.setId(count);
                                    imageview.setClickable(true);
                                    numberOfLikes.setText("Number of likes: " + imageview.getTag().toString());
                                    numberOfLikes.setGravity(Gravity.CENTER);
                                    linearLayout.addView(imageview);
                                    linearLayout.addView(numberOfLikes);
                                    count++;

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
                                                ParseObject objectImage = new ParseObject("image");



                                                //objectImage.increment("numberoflikes");
                                                String likesString =imageview.getTag().toString();
                                                int likes = Integer.parseInt(likesString);
                                                likes++;

                                                imageview.setTag(likes);
                                                numberOfLikes.setText("Number of likes: "+likes);
                                                objectImage.saveInBackground();


                                                isDoubleClicked=false;
                                                //remove callbacks for Handlers
                                                handler.removeCallbacks(r);
                                            }else{
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
