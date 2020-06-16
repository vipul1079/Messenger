package com.example.messenger.oldUser

import com.example.messenger.R
import com.example.messenger.messages.RandomUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.recent_chat_row.view.*

class RecentUsers(val chatmessage: ChatMessage) : Item<ViewHolder>(){

    var chatPartner: User?=null

    override fun getLayout(): Int {
        return R.layout.recent_chat_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.recent_message_recentchat.text=chatmessage.text

        val partnerId :String
        if(chatmessage.fromId == FirebaseAuth.getInstance().uid){
            partnerId=chatmessage.toId
        }else{
            partnerId=chatmessage.fromId
        }

        val ref= FirebaseDatabase.getInstance().getReference("/users/$partnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartner=p0.getValue(User::class.java)
                viewHolder.itemView.name_user_recentchat.text=chatPartner?.username

                Picasso.get().load(chatPartner?.profileimageurl).into(viewHolder.itemView.small_profile_recentchat)


            }

        })




    }

}