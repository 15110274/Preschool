package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView CommentsList;
    private ImageButton PostCommentButton;
    private EditText CommentInputText;
    private DatabaseReference UsersRef,PostsRef,LikesRef;
    private ImageView PostImage;
    private TextView PostDescription,PostName;
    private CircleImageView PostProfileImage;
    private TextView PostTime;
    private TextView LikeButton,CommentButton;

    private DatabaseReference clickPostRef,CommentsRef;

    private String Post_Key, current_user_id;
    private FirebaseAuth mAuth;
    private String postimage,profileimage,description,time,postname;
    Boolean LikeChecker=false;

    private TextView DisplayNoOfLikes,DisplayNoOfComments;
    int countLikes,countComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        PostName=findViewById(R.id.post_user_name);
        PostImage=findViewById(R.id.post_image);
        PostDescription=findViewById(R.id.post_description);
        PostProfileImage=findViewById(R.id.post_profile_image);
        PostTime=findViewById(R.id.post_minute);
        LikeButton=findViewById(R.id.like_button);
        CommentButton=findViewById(R.id.comment_button);
        DisplayNoOfLikes=findViewById(R.id.display_no_of_likes);


        mAuth= FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        Post_Key=getIntent().getExtras().get("PostKey").toString();
        PostsRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");



        clickPostRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key);
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        CommentsList=findViewById(R.id.comments_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        CommentInputText=findViewById(R.id.comment_input);
        PostCommentButton=findViewById(R.id.post_comment_btn);

        DisplayNoOfComments=findViewById(R.id.display_no_of_comments);

        CommentsRef=FirebaseDatabase.getInstance().getReference().child("Posts");

        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String userName=dataSnapshot.child("username").getValue().toString();
                            ValidateComment(userName);
                            CommentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        clickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    setLikeButtonStatus(Post_Key);
                    setCommentPostButtonStatus(Post_Key);
                    postname=dataSnapshot.child("fullname").getValue().toString();
                    description=dataSnapshot.child("description").getValue().toString();
                    postimage=dataSnapshot.child("postimage").getValue().toString();
                    profileimage=dataSnapshot.child("profileimage").getValue().toString();
                    time=dataSnapshot.child("time").getValue().toString();

                    PostName.setText(postname);
                    PostDescription.setText(description);
                    Picasso.get().load(postimage).resize(600,0).into(PostImage);
                    Picasso.get().load(profileimage).resize(100,0).into(PostProfileImage);
                    PostTime.setText(time);
                    CommentButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommentInputText.requestFocus();
                        }
                    });
                    LikeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LikeChecker=true;
                            LikesRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(LikeChecker.equals(true)){
                                        if(dataSnapshot.child(Post_Key).hasChild(current_user_id)){
                                            LikesRef.child(Post_Key).child(current_user_id).removeValue();
                                            LikeChecker=false;
                                        }
                                        else{
                                            LikesRef.child(Post_Key).child(current_user_id).setValue(true);
                                            LikeChecker=false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void setCommentPostButtonStatus(final String PostKey){
        CommentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(PostKey).child("Comments").hasChild(current_user_id)){
                    countComments=(int)dataSnapshot.child(PostKey).child("Comments").getChildrenCount();
                    DisplayNoOfComments.setText((Integer.toString(countComments)+" Comments"));
                }
                else{
                    countComments=(int)dataSnapshot.child(PostKey).child("Comments").getChildrenCount();
                    DisplayNoOfComments.setText((Integer.toString(countComments)+" Comments"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setLikeButtonStatus(final String PostKey){

        LikesRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(PostKey).hasChild(current_user_id)){
                    countLikes=(int)dataSnapshot.child(PostKey).getChildrenCount();
                    LikeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_25dp,0,0,0);
                    DisplayNoOfLikes.setText((Integer.toString(countLikes)+" Likes"));
                    LikeButton.setTextColor(Color.parseColor("#FF5722"));
                }
                else{
                    countLikes=(int)dataSnapshot.child(PostKey).getChildrenCount();
                    LikeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_25dp,0,0,0);
                    DisplayNoOfLikes.setText((Integer.toString(countLikes)+" Likes"));
                    LikeButton.setTextColor(Color.parseColor("#959292"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(PostsRef, Comments.class).build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder commentsViewHolder, int position, @NonNull Comments comments) {
                commentsViewHolder.setUsername(comments.getUsername());
                commentsViewHolder.setComment(comments.getComment());
                commentsViewHolder.setDate(comments.getDate());
                commentsViewHolder.setTime(comments.getTime());

            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comments_layout,parent,false);
                return new CommentsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        CommentsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CommentsViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        public void setComment(String comment) {
            TextView myComment=mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }


        public void setDate(String date) {
            TextView myDate=mView.findViewById(R.id.comment_date);
            myDate.setText(" Date: "+date);
        }



        public void setTime(String time) {
            TextView myTime=mView.findViewById(R.id.comment_time);
            myTime.setText(" Time: "+time);
        }

        public void setUsername(String username) {
            TextView myUserName=mView.findViewById(R.id.comment_username);
            myUserName.setText("@"+username+ "   " );
        }
    }

    private void ValidateComment(String userName) {
        String commentText=CommentInputText.getText().toString();
        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(this,"please write text to comment...",Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calFordDate=Calendar.getInstance();
            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MM-yyyy");
            final String saveCurrentDate=currentDate.format(calFordDate.getTime());

            Calendar calFordTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss");
            final String saveCurrentTime=currentTime.format(calFordDate.getTime());

            final String RandomKey=current_user_id+saveCurrentDate+saveCurrentTime;
            HashMap commentsMap=new HashMap();
            commentsMap.put("uid",current_user_id);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",saveCurrentDate);
            commentsMap.put("time",saveCurrentTime);
            commentsMap.put("username",userName);
            PostsRef.child(RandomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CommentsActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(CommentsActivity.this,"Error",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
}
