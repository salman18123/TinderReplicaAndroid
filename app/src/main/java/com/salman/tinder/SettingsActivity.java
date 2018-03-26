package com.salman.tinder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    EditText name,phone;
    ImageView profileImage;
    Uri profileUri;
    Button confirm,back;
    FirebaseAuth auth;
    DatabaseReference ref;

    String userSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userSex=getIntent().getExtras().getString("userSex");
        Log.d("usersSex", "onCreate: "+userSex);


        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        confirm=findViewById(R.id.confirm);
        back=findViewById(R.id.back);
        profileImage=findViewById(R.id.profileImage);
        //getUserInfo();
        auth=FirebaseAuth.getInstance();
        String userId=auth.getCurrentUser().getUid();
        //Intent i=getIntent();
        ref= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);

        getUserInfo();
        Log.d("userSex", "onCreate: "+userSex);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,121);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
                if(profileUri!=null){
                    StorageReference store= FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    try {
                        Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),profileUri);
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
                        byte[] data=baos.toByteArray();
                        UploadTask uploadTask=store.putBytes(data);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri url=taskSnapshot.getDownloadUrl();
                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Map map=new HashMap();
                                map.put("profileUrl",url.toString());
                                ref.updateChildren(map);
                                finish();
                              }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }



            }
        });
    }

    private void getUserInfo() {
         ref.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){
                     Map<String,Object> map=(Map<String,Object>) dataSnapshot.getValue();
                     if(map.get("name")!=null){
                         name.setText(map.get("name").toString());
                     }
                     if(map.get("phone")!=null){
                         phone.setText(map.get("phone").toString());
                     }
                     Glide.clear(profileImage);
                     if(map.get("profileUrl")!=null){
                         switch (map.get("profileUrl").toString()){
                             case "default":
                                 Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(profileImage);
                                 break;
                             default:
                                 Glide.with(getApplication()).load(map.get("profileUrl").toString()).into(profileImage);
                         }
                     }
                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }

    private void saveUserInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Map map=new HashMap();
        map.put("name",name.getText().toString());
        map.put("phone",phone.getText().toString());
        ref.updateChildren(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==121&&resultCode== Activity.RESULT_OK){
            Uri uri=data.getData();
            profileImage.setImageURI(uri);
            profileUri=uri;
        }
    }
}
