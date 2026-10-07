package ru.netology.nework

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Liker(
    val name: String,
    val username: String,
    val avatar: String
)

class LikerAdapter :
    RecyclerView.Adapter<LikerAdapter.LikerViewHolder>() {

    private var users: List<Liker> = emptyList()

    fun submitList(newUsers: List<Liker>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LikerViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.liker_item,
                parent,
                false
            )

        return LikerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: LikerViewHolder,
        position: Int
    ) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class LikerViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val avatar =
            itemView.findViewById<TextView>(
                R.id.liker_avatar
            )

        private val name =
            itemView.findViewById<TextView>(
                R.id.liker_name
            )

        private val username =
            itemView.findViewById<TextView>(
                R.id.liker_username
            )

        fun bind(user: Liker) {

            avatar.text = user.avatar
            name.text = user.name
            username.text = user.username

            val background =
                GradientDrawable().apply {

                    shape = GradientDrawable.OVAL

                    setColor(
                        Color.rgb(
                            111,
                            74,
                            168
                        )
                    )
                }

            avatar.background = background
        }
    }
}