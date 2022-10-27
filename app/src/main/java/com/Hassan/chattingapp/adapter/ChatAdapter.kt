package com.Hassan.chattingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.Hassan.chattingapp.R
import com.Hassan.chattingapp.model.ChatModel
import com.Hassan.chattingapp.utils.ConstantKeys
import com.Hassan.chattingapp.utils.SpeakListener
import com.Hassan.chattingapp.utils.TinyDB

class ChatAdapter(
    //här instansierar vi en arraylist som ska innehålla
    //alla meddelanden samt en context och en tinyDB
    private val context: Context,
    private var mList: ArrayList<ChatModel>,
    private var tinyDB: TinyDB,
    val btnlistener: SpeakListener) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    companion object {
        var mClickListener: SpeakListener? = null
    }

    //här skapar vi en viewholder som ska innehålla alla views
    //som finns i layouten för ett meddelande
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.chat_item_view_recieve, parent, false)
        return ChatViewHolder(view)
    }

    //här sätter vi texten i textviewen till meddelandet
    //och sätter en onclicklistener på knappen som ska spela upp meddelandet
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatModel: ChatModel = mList.get(position)
        mClickListener = btnlistener
        //om meddelandet är skrivet av användaren,
        //sätt texten till meddelandet och visa knappen
        if (chatModel.senderId.equals(tinyDB.getString(ConstantKeys.USER_ID))) {
            holder.sendConstraint.visibility = View.VISIBLE
            holder.sendMic.visibility = View.VISIBLE
            holder.sendMessage.text = chatModel.message

            holder.recieveConstraint.visibility = View.GONE
            holder.recieveMic.visibility = View.GONE
        }
        //annars, visa bara meddelandet
        else {
            holder.sendConstraint.visibility = View.GONE
            holder.sendMic.visibility = View.GONE
            holder.recieveConstraint.visibility = View.VISIBLE
            holder.recieveMic.visibility = View.VISIBLE
            holder.recieveMessage.text = chatModel.message

        }
        holder.recieveMic.setOnClickListener {
            mClickListener!!.click(chatModel.message)
        }
        holder.sendMic.setOnClickListener {
            mClickListener!!.click(chatModel.message)
        }
    }

    //här returnerar vi antalet meddelanden i listan
    override fun getItemCount(): Int {
        return mList.size
    }


    //här skapar vi en viewholder som ska innehålla alla views
    //som finns i layouten för ett meddelande
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var recieveMessage: TextView = itemView.findViewById(R.id.recieveMsg)
        var sendMessage: TextView = itemView.findViewById(R.id.sendMsg)
        var sendConstraint: CardView = itemView.findViewById(R.id.cardSend)
        var recieveConstraint: CardView = itemView.findViewById(R.id.cardRecieve)
        var recieveMic: ImageView = itemView.findViewById(R.id.recieveMic)
        var sendMic: ImageView = itemView.findViewById(R.id.sendMic)



    }

}


