package ru.netology.nework

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MentionUsersAdapter(
    private val users: List<User>,
    private val selectedUsers: MutableSet<Long>,
    private val onSelectionChanged: (User, Boolean) -> Unit
) : RecyclerView.Adapter<MentionUsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.mention_user_item,
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
            itemView.findViewById(
                R.id.mention_user_avatar
            )

        private val name: TextView =
            itemView.findViewById(
                R.id.mention_user_name
            )

        private val login: TextView =
            itemView.findViewById(
                R.id.mention_user_login
            )

        private val checkBox: CheckBox =
            itemView.findViewById(
                R.id.mention_user_checkbox
            )

        fun bind(user: User) {

            name.text = user.name

            login.text = user.login

            avatar.text = getInitials(user.name)

            /*
             * Устанавливаем состояние чекбокса.
             *
             * Важно:
             * сначала отключаем listener,
             * чтобы при переиспользовании ViewHolder
             * не срабатывало лишнее изменение.
             */
            checkBox.setOnCheckedChangeListener(null)

            checkBox.isChecked =
                selectedUsers.contains(user.id)

            checkBox.setOnCheckedChangeListener { _, isChecked ->

                onSelectionChanged(
                    user,
                    isChecked
                )
            }

            /*
             * Нажатие на всю строку
             * тоже переключает чекбокс.
             */
            itemView.setOnClickListener {

                checkBox.isChecked =
                    !checkBox.isChecked
            }
        }

        private fun getInitials(
            name: String
        ): String {

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