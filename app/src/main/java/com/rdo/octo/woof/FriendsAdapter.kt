package com.rdo.octo.woof

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.cell_friend.view.*
import kotlinx.android.synthetic.main.include_poi_detail.view.*

class FriendsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val list = listOf(
        FriendDog("Brian", "Sandro Rochetti | Rome", R.drawable.chien_courir),
        FriendDog("Rex", "Tom Brady | Boston", R.drawable.chien_labrador),
        FriendDog("Laika", "Cristiano Ronaldo | Turin", R.drawable.doggg),
        FriendDog("Medor", "Guillaume Moizan | Paris", R.drawable.l_lof_zoomalia)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_friend, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viemModel = list[position]
        with(holder.itemView) {
            profile_image.setImageResource(viemModel.picture)
            textView5.text = viemModel.name
            textView6.text = viemModel.owner
        }
    }
}

data class FriendDog(
    val name: String,
    val owner: String,
    val picture: Int
)