package com.sakthicskapp.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

// Adapter act as a bridge between list view and data

// ChatListAdapter will act as bridge between firebase and chat view

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapshotarraylist;
    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mSnapshotarraylist.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ChatListAdapter(Activity activity, DatabaseReference ref, String name){
        mActivity = activity;
        mDatabaseReference = ref.child("message");
        mDisplayName = name;

        mDatabaseReference.addChildEventListener(mListener);

        mSnapshotarraylist = new ArrayList<>();
    }

    static class ViewHolder{
        TextView mAuthor;
        TextView mMessageBody;
        LinearLayout.LayoutParams mParams;
    }

    @Override
    public int getCount() {
        return mSnapshotarraylist.size();
    }

    @Override
    public InstantMessage getItem(int i) {

        DataSnapshot snapshot = mSnapshotarraylist.get(i);

        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null){
            /* Since view is null, need to create the new row from layout file
            To create the view, we need LayoutInflator. */
            LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_msg_row, viewGroup, false);

            //Now, its view holder to start to shine
            final ViewHolder holder = new ViewHolder();

            holder.mAuthor = view.findViewById(R.id.author);
            holder.mMessageBody = view.findViewById(R.id.message);
            holder.mParams = (LinearLayout.LayoutParams) holder.mAuthor.getLayoutParams();

            view.setTag(holder);   //Temporary store in view
        }

        final InstantMessage message = getItem(i);

        final ViewHolder holder = (ViewHolder)view.getTag();

        boolean selfuser = message.getAuthor().equals(mDisplayName);
        setChatRowAppearance(selfuser, holder);

        String author = message.getAuthor();
        holder.mAuthor.setText(author);

        String msg = message.getMessage();
        holder.mMessageBody.setText(msg);

        return view;
    }

    public void setChatRowAppearance(boolean selfuser, ViewHolder holder){
        if(selfuser){
                // If it self user, change the layout to right side
            holder.mParams.gravity = Gravity.END;
            holder.mAuthor.setTextColor(Color.GREEN);
            holder.mMessageBody.setBackgroundResource(R.drawable.bubble2);
        } else {
            holder.mParams.gravity = Gravity.START;
            holder.mAuthor.setTextColor(Color.BLUE);
            holder.mMessageBody.setBackgroundResource(R.drawable.bubble1);
        }

        holder.mAuthor.setLayoutParams(holder.mParams);
        holder.mMessageBody.setLayoutParams(holder.mParams);
    }

    public void cleanup(){
        mDatabaseReference.removeEventListener(mListener);
    }

}
