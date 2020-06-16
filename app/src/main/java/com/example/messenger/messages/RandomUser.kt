package com.example.messenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.messenger.R
import com.example.messenger.oldUser.ChatMessage
import com.example.messenger.oldUser.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_random_user.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class RandomUser : AppCompatActivity() {
    val adapter= GroupAdapter<ViewHolder>()

    var toUser:User?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_user)

        recyclerview_chatlog_random.adapter=adapter


        toUser=intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title= toUser?.username

        listenForMessages()

        send_button_user_random.setOnClickListener {
            if (chat_log_typing.text.isEmpty() ) return@setOnClickListener
            performSendMessage()

        }

    }




    private fun listenForMessages(){
        val fromId=FirebaseAuth.getInstance().uid
        val toId=toUser?.uid
        val ref=FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatmessage=p0.getValue(ChatMessage::class.java)
                if (toUser==null)return

                if(chatmessage !=null){

                    if(chatmessage.fromId== FirebaseAuth.getInstance().uid) {
                        val currentUser=MessengerActivity.currentUser
                        adapter.add(ChatFromItem(chatmessage.text,currentUser!!))
                    }else{
                        adapter.add(ChatToItem(chatmessage.text,toUser!!))
                    }
                }
                recyclerview_chatlog_random.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    private fun performSendMessage(){

        val text=chat_log_typing.text.toString()
        // we will be sending message here


        ////////////////////////////////////change later and see the desired result./././.././././...//.//

        val fromId=FirebaseAuth.getInstance().uid
        val toId=toUser!!.uid
        val reference= FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toreference= FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        if(fromId==null)return

        val chatmessage=ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatmessage).addOnSuccessListener {
            chat_log_typing.text.clear()
            recyclerview_chatlog_random.scrollToPosition(adapter.itemCount-1)

        }
        toreference.setValue(chatmessage)

        val latestreference=FirebaseDatabase.getInstance().getReference("/latest-message/$fromId/$toId")
        latestreference.setValue(chatmessage)

        val latestreference2=FirebaseDatabase.getInstance().getReference("/latest-message/$toId/$fromId")
        latestreference2.setValue(chatmessage)
    }
}
/// now i dont want to make more changes to this code so leaving this as it is as long as it works but here i accidentally made the
/// chat_to_row as chat_from_row
class ChatFromItem(val text:String,val user: User): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.chat_user_to.text=text

        Picasso.get().load(user.profileimageurl).into(viewHolder.itemView.image_chat_to_row)


    }
}
class ChatToItem(val text:String,val user: User): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_from_row

        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.chat_user_from.text=text

            //load user image to the chat log
            Picasso.get().load(user.profileimageurl).into(viewHolder.itemView.image_chat_from_row)

        }

}
