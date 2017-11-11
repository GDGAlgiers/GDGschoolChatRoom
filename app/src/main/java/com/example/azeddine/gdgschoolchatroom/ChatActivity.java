package com.example.azeddine.gdgschoolchatroom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    // firebase authentication instance
    private FirebaseAuth mAuth;

    // firebase real time database instance
    private FirebaseDatabase mDatabase;

    // firebase analytics instance
    private FirebaseAnalytics mFirebaseAnalytics;

    private Toolbar mToolbar;
    private MessageInput mMessageInput;
    private MessagesList mMessagesList;
    private ImageLoader mImageLoader;
    private MessagesListAdapter<Message> mMessagesAdapter;
    private Message.User mUser ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        /**
         * initialise the firebase features
         */

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // get the current user
        FirebaseUser user = mAuth.getCurrentUser();

        /**
         * if the user is not authenticated we redirect him to
         * the authentication activity
         */
        if(user == null){
            // non authenticated
            Intent intent =new Intent(ChatActivity.this,AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        /**
         * get the string identifier of the user, if the user had signed in
         * with his phone number, we use the phone number as an identifier
         */
        final String email = user.getEmail();

        // now comes the power of Kotlin programming language over Java
        if(email != null){
            mUser = new Message.User(user.getUid(),user.getEmail().split("@")[0]);
        }else {
            Toast.makeText(this, user.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            mUser = new Message.User(user.getUid(),user.getPhoneNumber());
        }

        // set up the app toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);

        // initialize the imported costume widget form the ChatKit library
        mMessageInput = findViewById(R.id.message_input);
        mMessagesList = findViewById(R.id.messages_list);

        /**
         * set up a click listener for the send message button click:
         *      1 - get the text message form the input
         *      2 - create a new reference in the realtime database, and send its key as the message id
         *      4 - instantiate a new message class object and write it to the firebase real time database
         */
        mMessageInput.setInputListener(
                new MessageInput.InputListener() {
                    @Override
                    public boolean onSubmit(CharSequence input) {
                        DatabaseReference reference = mDatabase.getReference("messages").push();
                        Message message = new Message(reference.getKey(),input.toString(),mUser);
                        reference.setValue(message);

                        Bundle params = new Bundle();
                        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"text_message");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, params);

                        return true;
                    }
                });


        //setting up the avatar image loader for the messages
        mImageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Log.d(TAG, "loadImage: ");
                imageView.setImageResource(R.mipmap.emoji_sad);
            }
        };

        //setting up the messages list adapter, passing the sender id and the image loader instance
        mMessagesAdapter = new MessagesListAdapter<>(mUser.getId(),mImageLoader);
        mMessagesList.setAdapter(mMessagesAdapter);

        /**
         * Always, when a change happened in the real time database an event is generated.
         * So we need to listen to this events and make our local changes based on it.
         *
         * - When a message is added:
         *   1 - set up a listener to the firebase real time database messages refrence
         *   2 - create a new message object
         *   3 - add it to the messages list view
         * - When a message is modified:
         *   1 - we update the message in the list view
         *
         */
        mDatabase.getReference("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /**
                 * first, this function is triggered for each child inside the reference
                 */
                Message message = dataSnapshot.getValue(Message.class);
                mMessagesAdapter.addToStart(message,true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /**
                 * Listen for changes to the items in a list. This event fired any time a child node is modified,
                 * including any modifications to descendants of the child node.
                 * The DataSnapshot passed to the event listener contains the updated data for the child.
                 */
                Message message = dataSnapshot.getValue(Message.class);
                mMessagesAdapter.update(message);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /**
                 * Listen for items being removed from a list.
                 * The DataSnapshot passed to the event callback contains the data for the removed child.
                 */
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                /**
                 * Listen for changes to the order of items in an ordered list.
                 * This event is triggered whenever the onChildChanged() callback is triggered
                 */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // creating the option menu of the chat activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.chat_option_menu, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            final int id = item.getItemId();
            switch (id) {
                case R.id.logout:
                    /**
                     * log out the user
                     * and deleting the chat activity from the back stack
                     */
                    mAuth.signOut();
                    finish();
                    break;
                default:
                    return false;
            }
            return true;

    }

    @IgnoreExtraProperties
    public static class Message implements IMessage {
    private String id;
    private String text ;
    private User user ;
    private long timeMillis ;


    public Message(){

    }
    public Message(String Id, String Text, User user, long timeMillis) {
        this.id = Id;
        this.text = Text;
        this.user = user;
        this.timeMillis = timeMillis;
    }
    public Message(String Id, String Text, User user) {
        this.id = Id;
        this.text = Text;
        this.user = user;
        this.timeMillis = System.currentTimeMillis();
    }

    public void setId(String Id) {
        this.id = Id;
    }

    public void setText(String Text) {
        this.text = Text;
    }

    public void setUser(User User) {
        this.user = User;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text ;
    }

    @Override
    public IUser getUser() {
        return user ;
    }

    public long getTimeMillis(){
        return timeMillis;
    }

    @Exclude
    @Override
    public Date getCreatedAt() {
        return new Date(timeMillis) ;
    }



    public static class User implements IUser {
        private String name;
        private String id ;
        private String avatar;

        public User() {

        }
        public  User(String id){
            this.id = id;
        }

        public User(String id,String username) {
            this.name = username;
            this.id = id;
        }
        public User(String id,String username, String avatar) {
            this.name = username;
            this.avatar = avatar;
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;

        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Exclude
        @Override
        public String getAvatar() {
            return "emoji";
        }
    }
}


}
