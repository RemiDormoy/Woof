package com.rdo.octo.woof

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.include_poi_detail.view.*

class PoiAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val list = listOf(
        PoiViewModel("Dog run contest", "4pm to 7pm", 12, R.drawable.tuileries, 4),
        PoiViewModel("Meeting of the dog's lovers", "10am to 1pm", 85, R.drawable.orangerie, 8),
        PoiViewModel("Clean walk with tacos", "7pm to 8pm", 120, R.drawable.paume, 11)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_poi_detail, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viemModel = list[position]
        with(holder.itemView) {
            textView.text = viemModel.title
            textView2.text = viemModel.time
            textView3.text = "${viemModel.peoples} peoples"
            textView4.text = "${viemModel.travel} min away"
            imageView4.setImageResource(viemModel.picture)
        }
    }
}

data class PoiViewModel(
    val title: String,
    val time: String,
    val peoples: Int,
    val picture: Int,
    val travel: Int
)