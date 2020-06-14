package com.example.messenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.messenger.R
import com.example.messenger.oldUser.User
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title= "Select User"

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
                val adapter=GroupAdapter<ViewHolder>()

                p0.children.forEach{
                    val user= it.getValue(User::class.java)
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

        Picasso.get().load(user.profileimageurl).into(viewHolder.itemView.user_profile_image_row_chat)


    }

}
