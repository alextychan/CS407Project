package com.example.ezclassapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.ezclassapp.Models.Review;
import com.example.ezclassapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubmitReview extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mSubmit_btn;
    private EditText mReviewText;
    private EditText mOpinionText;
    private RatingBar mDiffcultyRating;
    private RatingBar mUsefulRating;
    public static final String ARG_PARAM1 = "courseName";
    public static final String ARG_PARAM2 = "reviewListId";
    private DatabaseReference mDatabase;
    private DatabaseReference reviewReference;
    private DatabaseReference particularCourseReference;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_review);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mReviewText = (EditText)findViewById(R.id.review_input);
        mOpinionText = (EditText)findViewById(R.id.opinion_input);
        mDiffcultyRating = (RatingBar)findViewById(R.id.difficulty_bar);
        mUsefulRating = (RatingBar)findViewById(R.id.useful_bar);
        mSubmit_btn = (Button) findViewById(R.id.submit_btn);
        Bundle currentBundle = getIntent().getExtras();
        final String courseid = currentBundle.getString(ARG_PARAM1);
        final ArrayList<String> reviewListId = currentBundle.getStringArrayList(ARG_PARAM2);
        reviewReference = mDatabase.child(Constants.REVIEW);
        particularCourseReference = mDatabase.child(Constants.COURSE).child(courseid).child(Constants.REVIEWLIST);


        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USER).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name");
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    String name = dataSnapshot.getValue().toString();
                    userName = name;
                } else {
                    userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDiffcultyRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                Toast.makeText(SubmitReview.this, String.valueOf(rating), Toast.LENGTH_SHORT).show();

            }
        });

        mSubmit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // listen at the review --> ForeignKey
                final DatabaseReference foreignKeyReference = mDatabase.child(Constants.REVIEW).child(courseid);
                foreignKeyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        DatabaseReference reviewReference = foreignKeyReference;
                        final String key = reviewReference.push().getKey();
                        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Review review = new Review(key,userName,courseid,mOpinionText.getText().toString(),user_id,
                                mReviewText.getText().toString(),(int)mDiffcultyRating.getRating(),(int)mUsefulRating.getRating());
                        reviewReference.child(key).setValue(review);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        finish();
                    }
                });

            }
        });

    }

}
