package com.salman.tinder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by SALMAN on 3/25/2018.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchesViewHoder> {
    private ArrayList<MatchesObject> matchesObjects;
    private Context context;
    public MatchesAdapter(ArrayList<MatchesObject> matchesObjects, Context context) {
        this.matchesObjects = matchesObjects;
        this.context = context;
    }


    @Override
    public MatchesViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li=(LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=li.inflate(R.layout.item_matches,parent,false);
        return new MatchesViewHoder(view);
    }

    @Override
    public void onBindViewHolder(MatchesViewHoder holder, int position) {
        MatchesObject match=matchesObjects.get(position);
        Log.d("MatchMe", "onBindViewHolder: "+match.getName());
        String name=match.getName();
        String id=match.getUserId();
        holder.matchName.setText(name);
        holder.matchId.setText(id);
        if(!match.getProfileUrl().equalsIgnoreCase("default")){
        Glide.with(context).load(match.getProfileUrl()).into(holder.profileImage);}

    }

    @Override
    public int getItemCount() {
        return matchesObjects.size();
    }

    class MatchesViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView profileImage;
        TextView matchName,matchId;

        public MatchesViewHoder(View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.profileUrl);
            matchName=itemView.findViewById(R.id.matchName);
            matchId=itemView.findViewById(R.id.matchId);
            itemView.setOnClickListener(this);




        }

        @Override
        public void onClick(View view) {
            Intent i=new Intent(view.getContext(),ChatActivity.class);
            i.putExtra("matchId",matchId.getText().toString());
            view.getContext().startActivity(i);

        }
    }
}
