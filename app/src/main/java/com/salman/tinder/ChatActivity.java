package com.salman.tinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    RecyclerView chatRecycleview;
    ArrayList<ChatObject>chatObjects;
    String matchId;
    EditText newMessage;
    Button send;
    DatabaseReference ref,dbChat;
    String chatId;
    String userSex;
    String oppositeUserSex;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        checkUserSex();

        chatRecycleview=findViewById(R.id.chatRecyclerview);
        newMessage=findViewById(R.id.newMessage);
        send=findViewById(R.id.send);
        //checkUserSex();
        Log.d("Sex", "onCreate: "+userSex);
        matchId=getIntent().getStringExtra("matchId");
        //ref= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("connections").child("Matches").child(matchId).child("chatId");
        dbChat=FirebaseDatabase.getInstance().getReference().child("Chat");
        //getChatId();
        chatObjects=new ArrayList<>();
        chatAdapter=new ChatAdapter(chatObjects,ChatActivity.this);
        chatRecycleview.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        chatRecycleview.setAdapter(chatAdapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }
    public void checkUserSex(){
        Log.d("message", "checkUserSex: Helloo");
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference maleDb= FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("messagee", "checkUserSex: Helloo1");

                if(dataSnapshot.getKey().equals(user.getUid())){
                    userSex="Male";
                    oppositeUserSex="Female";
                    ref= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("connections").child("Matches").child(matchId).child("ChatId");

                    Log.d("messages", "checkUserSex: Helloo0");
                    Log.d("Sexs", "onCreate: "+userSex);


                    // getOppositeSexUsers();
                    getChatId();

                    return;
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
                    ref= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("connections").child("Matches").child(matchId).child("ChatId");

                    // getOppositeSexUsers();
                    getChatId();

                    return;
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

    private void sendMessage() {
        String msg=newMessage.getText().toString();
        if(!msg.isEmpty()){
            DatabaseReference newmsgdb=dbChat.push();
            Map map=new HashMap();
            map.put("createdByUser",FirebaseAuth.getInstance().getCurrentUser().getUid());
            map.put("text",msg);
            newmsgdb.setValue(map);
        }
        newMessage.setText(null);

    }
    private void getChatId(){

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatId=dataSnapshot.getValue().toString();
                    dbChat=dbChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        dbChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists() &&dataSnapshot.getChildrenCount()>0){
                    String message=null;
                    String createdByUser=null;
                    if(dataSnapshot.child("text").getValue()!=null){
                        message=dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser=dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if(message!=null&&createdByUser!=null){
                        Boolean currentUserBoolean=false;
                        if(createdByUser.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            currentUserBoolean=true;
                        }
                        ChatObject chat=new ChatObject(message,currentUserBoolean);
                        chatObjects.add(chat);
                        chatAdapter.notifyDataSetChanged();

                    }
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
}
