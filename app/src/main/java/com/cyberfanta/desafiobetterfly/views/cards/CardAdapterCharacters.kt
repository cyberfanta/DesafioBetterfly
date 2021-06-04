package com.cyberfanta.desafiobetterfly.views.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyberfanta.desafiobetterfly.R
import com.cyberfanta.desafiobetterfly.views.cards.CardAdapterCharacters.CardViewHolder
import java.util.*

class CardAdapterCharacters (private val cardItemCharacters: ArrayList<CardItemCharacters>) : RecyclerView.Adapter<CardViewHolder>() {
    //Internal Variables
    private var itemClickListener: OnItemClickListener? = null
    private var bottomReachedListener: OnBottomReachedListener? = null

    //Listeners Interfaces
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnBottomReachedListener {
        fun onBottomReached(position: Int)
    }

    //Listener Implementations
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    fun setOnBottomReachedListener(listener: OnBottomReachedListener) {
        this.bottomReachedListener = listener
    }

    //Internal class to describe the object items to be put into a card item
    class CardViewHolder(itemView: View, itemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView? = itemView.findViewById(R.id.characterImage)
        var name: TextView = itemView.findViewById(R.id.characterName)
        var id: TextView = itemView.findViewById(R.id.characterId)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(position)
                }
            }
        }
    }

    //Inflate the card created into the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_character, parent, false)
        return CardViewHolder(view, itemClickListener)
    }

    //Bind an object with a card item
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardItem = cardItemCharacters[position]
        holder.name.text = cardItem.name
        holder.image?.setImageBitmap(cardItem.image)
        holder.id.text = cardItem.id


        if (position > itemCount - 12)
            bottomReachedListener?.onBottomReached(position)
    }

    //Return the amount of items in recycler view
    override fun getItemCount(): Int {
        return cardItemCharacters.size
    }
}