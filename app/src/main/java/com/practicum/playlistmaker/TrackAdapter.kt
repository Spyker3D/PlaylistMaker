package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ClearHistoryButtonBinding
import com.practicum.playlistmaker.databinding.TrackViewBinding

private const val TYPE_TRACK = 1
private const val TYPE_BUTTON = 2

class TrackAdapter(
    trackList: List<Track>,
    private val onTrackClickListener: OnTrackClickListener? = null,
    private val onActionButtonClickListener: ((TrackAdapter) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var trackList = trackList
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TRACK -> {
                val trackViewBinding = TrackViewBinding.inflate(inflater, parent, false)
                TrackViewHolder(trackViewBinding)
            }

            TYPE_BUTTON -> {
                val buttonBinding = ClearHistoryButtonBinding.inflate(inflater, parent, false)
                buttonBinding.buttonClearHistory.setOnClickListener {
                    onActionButtonClickListener?.invoke(this)
                }
                ButtonViewHolder(buttonBinding.root)
            }

            else -> error("Unknown type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TrackViewHolder) {
            holder.bind(trackList[position])
            holder.itemView.setOnClickListener {
                onTrackClickListener?.onTrackClick(trackList[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount() = trackList.size + if (onActionButtonClickListener == null) 0 else 1
    override fun getItemViewType(position: Int): Int {
        return if (onActionButtonClickListener != null && position == itemCount - 1) TYPE_BUTTON else TYPE_TRACK
    }

    fun clearList() {
        trackList = emptyList()
        notifyDataSetChanged()
    }

    fun updateList(operation: (List<Track>) -> List<Track>) {
        trackList = operation(trackList)
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return trackList.isEmpty()
    }
}