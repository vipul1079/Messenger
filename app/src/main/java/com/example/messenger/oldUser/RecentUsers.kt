package com.example.messenger.oldUser

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.os.AsyncTask
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.recent_chat_row.view.*

class RecentUsers(val chatmessage: ChatMessage) : Item<ViewHolder>() {
    var chatPartner: User? = null


    override fun getLayout(): Int {
        return R.layout.recent_chat_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {


        val partnerId: String
        if (chatmessage.fromId == FirebaseAuth.getInstance().uid) {
            partnerId = chatmessage.toId
        } else {
            partnerId = chatmessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$partnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartner = p0.getValue(User::class.java)
                viewHolder.itemView.recent_message_recentchat.text = chatmessage.text
                viewHolder.itemView.name_user_recentchat.text = chatPartner?.username
                if (chatPartner?.profileimageurl != " ") {
                    Picasso.get().load(chatPartner?.profileimageurl)
                        .config(Bitmap.Config.RGB_565)
                        .placeholder(R.drawable.loading)
                        .priority(Picasso.Priority.HIGH)
                        .into(viewHolder.itemView.small_profile_recentchat)
                } else {
                    viewHolder.itemView.small_profile_recentchat.setImageResource(R.drawable.avatar1)
                }


            }
        })
    }
}

















