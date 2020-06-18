package com.example.messenger.messages

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.messenger.R
import com.example.messenger.oldUser.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_message.view.*

class NewMessageActivity : AppCompatActivity() {
    val adapter=GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title= "Select User"
        val simpleItemAnimator:SimpleItemAnimator = recyclerview_message.itemAnimator as SimpleItemAnimator
        simpleItemAnimator.supportsChangeAnimations=false
        recyclerview_message.setItemViewCacheSize(20)

        recyclerview_message.setHasFixedSize(true)

        recyclerview_message.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        fetchUsers()


    }
    companion object{
        const val USER_KEY="USER_KEY"
    }
    private fun fetchUsers(){
        val ref= FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {


                p0.children.forEach{
                    val user= it.getValue(User::class.java)
                    if (FirebaseAuth.getInstance().uid == it.key){
                        return@forEach
                    }
                    if(user !=null) adapter.add(
                        Useritem(
                            user
                        )
                    )
                }
                adapter.setOnItemClickListener{item,view ->
                    val userItem=item as Useritem
                    val intent= Intent(view.context,RandomUser::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)


                    finish()
                }
                recyclerview_message.adapter=adapter

            }

        })
    }


}
class Useritem(val user: User) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_row_message
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //represents individual user which we want using a layout which is being passed to it by the getlayout fun
        viewHolder.itemView.user_name_row_chat.text=user.username

        if ( user.profileimageurl != " "){
            Picasso.get().load(user.profileimageurl).config(Bitmap.Config.RGB_565).placeholder(R.drawable.loading).priority(Picasso.Priority.HIGH)
                .into(viewHolder.itemView.user_profile_image_row_chat)
        }else{
            viewHolder.itemView.user_profile_image_row_chat.setImageResource(R.drawable.avatar1)
        }




    }

}
