package ru.netology.nework

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(
    private val onUserClicked: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    private var users: List<User> = emptyList()

    fun submitList(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.user_item,
                parent,
                false
            )

        return UserViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val avatar: TextView =
            itemView.findViewById(R.id.user_avatar)

        private val name: TextView =
            itemView.findViewById(R.id.user_name)

        private val login: TextView =
            itemView.findViewById(R.id.user_login)

        fun bind(user: User) {

            // ИМЯ
            name.text = user.name

            // ЛОГИН
            login.text = user.login

            // ИНИЦИАЛЫ
            avatar.text = getInitials(user.name)

            // ФИОЛЕТОВЫЙ КРУГ
            val background = GradientDrawable().apply {
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

            // НАЖАТИЕ НА КАРТОЧКУ
            itemView.setOnClickListener {
                onUserClicked(user)
            }
        }

        private fun getInitials(name: String): String {

            return name
                .trim()
                .split(" ")
                .filter {
                    it.isNotEmpty()
                }
                .take(2)
                .joinToString("") {
                    it.first().uppercase()
                }
        }
    }
}