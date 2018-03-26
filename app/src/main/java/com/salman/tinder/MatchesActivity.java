package com.salman.tinder;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference matchesRef;
    Intent i;
    String userSex;
    String notUserSex;
    ArrayList<MatchesObject> matchesObjects;
    MatchesAdapter matchesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        recyclerView=findViewById(R.id.recyclerView);
        matchesObjects=new ArrayList<>();
        i=getIntent();
        userSex=i.getStringExtra("userSex");
       // userSex="Male";
        if(userSex.equalsIgnoreCase("male")){
            notUserSex="Female";
        }
        else{
            notUserSex="Male";
        }
          matchesAdapter=new MatchesAdapter(matchesObjects,MatchesActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MatchesActivity.this));
        recyclerView.setAdapter(matchesAdapter);
        getMatches();


    }

    private void getMatches() {
        matchesRef= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("connections").child("Matches");
          matchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  if(dataSnapshot.exists()){
                      for(DataSnapshot match:dataSnapshot.getChildren()){
                          fetchMatchInfo(match);
                      }
                  }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });

    }

    private void fetchMatchInfo(DataSnapshot match) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(notUserSex).child(match.getKey());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userId=dataSnapshot.getKey();
                    Map<String,Object> map=(Map<String,Object>) dataSnapshot.getValue();
                    String userName=null,profileUrl=null,phone=null;
                    if(map.get("name")!=null){
                        userName=map.get("name").toString();
                    }
                    if(map.get("phone")!=null){
                        phone=map.get("phone").toString();
                    }
                    if(map.get("profileUrl")!=null){
                        profileUrl=map.get("profileUrl").toString();
                    }
                    MatchesObject match=new MatchesObject(userId,profileUrl,userName,phone);
                    matchesObjects.add(match);
                    matchesAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
