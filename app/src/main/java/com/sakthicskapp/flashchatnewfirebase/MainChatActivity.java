package com.sakthicskapp.flashchatnewfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // Set up the display name and get the Firebase reference
        setupDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        // Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                sendMessage();
                return true;
            }
        });

        // Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    // Retrieve the display name from the Shared Preferences
    private void setupDisplayName(){
        /*SharedPreferences mPrefs = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);

            // Author
        mDisplayName = mPrefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);

        if(mDisplayName == null) mDisplayName = "Anonymous";*/

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDisplayName = user.getDisplayName();
    }


    private void sendMessage() {
        Log.d("FlashChat", "I sent something");

        // Grab the text the user typed in and push the message to Firebase

        String input = mInputText.getText().toString();
        if(!input.equals("")){
            InstantMessage chat = new InstantMessage(input, mDisplayName);
            mDatabaseReference.child("message").push().setValue(chat);
            // clear the text box
            mInputText.setText("");
        }

    }

    // Override the onStart() lifecycle method. Setup the adapter here.
    public void onStart(){
        super.onStart();

        mAdapter = new ChatListAdapter(this, mDatabaseReference, mDisplayName);

        // link the adapter to the view
        mChatListView.setAdapter(mAdapter);
    }


    @Override
    public void onStop() {
        super.onStop();

        // TODO: Remove the Firebase event listener on the adapter.
        mAdapter.cleanup();

    }

}
