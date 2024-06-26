package com.practicum.playlistmaker.player.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.AudioplayerPlaylistItemViewBinding
import com.practicum.playlistmaker.databinding.PlaylistItemViewBinding
import com.practicum.playlistmaker.mediaLibrary.domain.entities.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.playlists.PlaylistViewHolder

class BottomSheetAdapter(
    playlistsList: List<Playlist>,
    private val onPlaylistListener: ((Playlist) -> Unit)? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var playlistsList = playlistsList
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val playlistViewBinding = AudioplayerPlaylistItemViewBinding.inflate(inflater, parent, false)
        return BottomSheetViewHolder(playlistViewBinding)
    }

    override fun getItemCount(): Int = playlistsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BottomSheetViewHolder) {
            holder.bind(playlistsList[position])
            holder.itemView.setOnClickListener {
                onPlaylistListener?.invoke(playlistsList[holder.adapterPosition])
            }
        }
    }

    fun clearList() {
        playlistsList = emptyList()
        notifyDataSetChanged()
    }

    fun updateList(operation: (List<Playlist>) -> List<Playlist>) {
        playlistsList = operation(playlistsList)
        notifyDataSetChanged()
    }
}
