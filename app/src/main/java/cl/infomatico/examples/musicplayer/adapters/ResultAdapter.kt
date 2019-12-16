package cl.infomatico.examples.musicplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import cl.infomatico.examples.musicplayer.R
import  cl.infomatico.examples.musicplayer.models.Result

import kotlinx.android.synthetic.main.item_result.view.*
import kotlin.collections.ArrayList

class ResultAdapter : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    private var results: ArrayList<Result> = ArrayList()

    interface OnListener {
        fun onClick(result: Result, viewHolder: ViewHolder)
    }

    lateinit var onListener: OnListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val result = results[position]

        val album = holder.view.context.getString(R.string.album) + result.collectionName
        val artist = holder.view.context.getString(R.string.artist) + result.collectionName

        holder.title.text = result.trackName
        holder.category.text = album
        holder.artist.text = artist

        holder.view.setOnClickListener { onListener.onClick(result, holder) }
    }

    override fun getItemCount(): Int = results.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val title: AppCompatTextView = view.tvTitle
        val category: AppCompatTextView = view.tvCategory
        val artist: AppCompatTextView = view.tvArtist
    }

    fun setResults(results: ArrayList<Result>) {
        this.results = results
        notifyDataSetChanged()
    }

    fun addResults(results: ArrayList<Result>) {
        this.results.addAll(results)
        notifyItemRangeInserted(itemCount, results.size)
    }
}