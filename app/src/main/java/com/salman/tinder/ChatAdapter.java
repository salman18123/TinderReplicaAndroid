package com.salman.tinder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SALMAN on 3/25/2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.chatViewHolder> {
    ArrayList<ChatObject> chatObjects;
    Context context;

    public ChatAdapter(ArrayList<ChatObject> chatObjects, Context context) {
        this.chatObjects = chatObjects;
        this.context = context;
    }

    @Override
    public chatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li= (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=li.inflate(R.layout.item_chat,parent,false);

        return new chatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(chatViewHolder holder, int position) {
        holder.message.setText(chatObjects.get(position).getMessage());
        if(chatObjects.get(position).getCurrentUser()){
            holder.message.setGravity(Gravity.END);
            Log.d("user", "onBindViewHolder: "+chatObjects.get(position).getCurrentUser());
            holder.message.setTextColor(Color.parseColor("#404040"));
            holder.container.setBackgroundColor(Color.parseColor("#F4F4F4"));
        }
        else {
            holder.message.setGravity(Gravity.START);
            holder.message.setTextColor(Color.parseColor("#404040"));
            holder.container.setBackgroundColor(Color.parseColor("#F4F4F4"));
        }

    }

    @Override
    public int getItemCount() {
        return chatObjects.size();
    }

    class chatViewHolder extends RecyclerView.ViewHolder{
            TextView message;
            LinearLayout container;

        public chatViewHolder(View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.message);
            container=itemView.findViewById(R.id.container);
        }
    }
}
