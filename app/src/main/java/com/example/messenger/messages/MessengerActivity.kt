package com.example.messenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.messenger.LoginScreen.MainActivity
import com.example.messenger.R
import com.example.messenger.oldUser.ChatMessage
import com.example.messenger.oldUser.RecentUsers
import com.example.messenger.oldUser.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messenger_mainscreen.*
import kotlinx.android.synthetic.main.activity_random_user.*
import kotlinx.android.synthetic.main.recent_chat_row.view.*
import kotlinx.android.synthetic.main.user_row_message.view.*

class MessengerActivity : AppCompatActivity() {
    companion object{
        var currentUser: User?=null
    }
    val adapter=GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger_mainscreen)
        recyclerview_recent_chats.setItemViewCacheSize(20)
        val simpleItemAnimator: SimpleItemAnimator = recyclerview_recent_chats.itemAnimator as SimpleItemAnimator
        simpleItemAnimator.supportsChangeAnimations=false
        verifiedUserLoggedIn()
        fetchCurrentUser()
        listenforlatestmessages()


        //clicking on a user in the recent chat log
        adapter.setOnItemClickListener{item,view ->
            val intent = Intent(this,RandomUser::class.java)
            // we are missing chat partner user

            val row = item as RecentUsers

            intent.putExtra(NewMessageActivity.USER_KEY,row.chatPartner)
            startActivity(intent)
        }
        recyclerview_recent_chats.setHasFixedSize(true)



        recyclerview_recent_chats.adapter=adapter

        recyclerview_recent_chats.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))






        }
    val latestmessagesmap=HashMap<String,ChatMessage>()

    private fun refreshlatestmessage(){
        adapter.clear()
        latestmessagesmap.values.forEach{
            adapter.add(RecentUsers(it))
        }
    }




    private fun listenforlatestmessages(){
        val fromId=FirebaseAuth.getInstance().uid
        val reference=FirebaseDatabase.getInstance().getReference("/latest-message/$fromId")
        reference.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage=p0.getValue(ChatMessage::class.java) ?: return
                latestmessagesmap[p0.key!!]=chatMessage
                refreshlatestmessage()

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage=p0.getValue(ChatMessage::class.java) ?: return
                latestmessagesmap[p0.key!!]=chatMessage
                refreshlatestmessage()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })

    }




    private fun fetchCurrentUser(){
        val uid=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser=p0.getValue(User::class.java)
            }
        })

    }
    private fun verifiedUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid== null){
            val intent=Intent(this, MainActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.Sign_Out_option -> {
                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this, MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            R.id.newMessage_option ->{
                val intent= Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }
}


