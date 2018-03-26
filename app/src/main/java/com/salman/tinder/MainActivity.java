package com.salman.tinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private cards cards_data[];
    private arrayAdapter arrayAdapter;
    private DatabaseReference usersDb;


    //private ArrayList<String> al;
    //private ArrayAdapter<String> arrayAdapter;
    private int i;
    FirebaseAuth firebaseAuth;
   ListView listView;
   List<cards> rowItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUserSex();
        firebaseAuth=FirebaseAuth.getInstance();
       usersDb=FirebaseDatabase.getInstance().getReference().child("Users");


        rowItems = new ArrayList<>();


        arrayAdapter = new arrayAdapter(this, R.layout.item,rowItems);

        SwipeFlingAdapterView flingContainer=findViewById(R.id.frame);


        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                cards obj=(cards) dataObject;
                String userId=obj.getUserId();
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child("connections").child(userId).child(oppositeUserSex).child("nope").child(firebaseAuth.getCurrentUser().getUid());
                ref.setValue(true);

                Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj=(cards) dataObject;
                String userId=obj.getUserId();
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex).child(userId).child("connections").child("yeps").child(firebaseAuth.getCurrentUser().getUid());
                ref.setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("connections").child("yeps").child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String key=FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    DatabaseReference refs=FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("connections").child("Matches").child(dataSnapshot.getKey()).child("ChatId");
                    refs.setValue(key);
                    DatabaseReference refss=FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex).child(dataSnapshot.getKey()).child("connections").child("Matches").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ChatId");
                    refss.setValue(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    String userSex;
    String oppositeUserSex;
   public void checkUserSex(){
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
       DatabaseReference maleDb= FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
       maleDb.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               if(dataSnapshot.getKey().equals(user.getUid())){
              userSex="Male";
              oppositeUserSex="Female";
              getOppositeSexUsers();
               }

           }

           @Override
           public void onChildChanged(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onChildRemoved(DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
       DatabaseReference femaleDb= FirebaseDatabase.getInstance().getReference().child("Users").child("Female");
      femaleDb.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               if(dataSnapshot.getKey().equals(user.getUid())){
                   userSex="Female";
                   oppositeUserSex="Male";
                   getOppositeSexUsers();
               }

           }

           @Override
           public void onChildChanged(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onChildRemoved(DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
   }
   public void getOppositeSexUsers(){
       final DatabaseReference oppositeSexDb= FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex);
       oppositeSexDb.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               String profileUrl="default";
               if(!dataSnapshot.child("profileUrl").getValue().equals("default")){
                   profileUrl=dataSnapshot.child("profileUrl").getValue().toString();
               }
               if(dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(firebaseAuth.getCurrentUser().getUid()) && !dataSnapshot.child("connections").child("yeps").hasChild(firebaseAuth.getCurrentUser().getUid())){
                   cards item=new cards(dataSnapshot.getKey(),dataSnapshot.child("name").getValue().toString(),dataSnapshot.child("profileUrl").getValue().toString());

                   rowItems.add(item);
                   arrayAdapter.notifyDataSetChanged();
               }

           }

           @Override
           public void onChildChanged(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onChildRemoved(DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

           }

    public void logoutUser(View view) {
        firebaseAuth.signOut();
        Intent i=new Intent(MainActivity.this,chooseLoginRegistrationActivity.class);
        startActivity(i);
        finish();
    }

    public void goToSettings(View view) {
        Intent i=new Intent(MainActivity.this,SettingsActivity.class);
        i.putExtra("userSex",userSex);
        startActivity(i);
    }

    public void goToMatches(View view) {
       Intent i=new Intent(MainActivity.this,MatchesActivity.class);
        i.putExtra("userSex",userSex);
       startActivity(i);
    }
}
