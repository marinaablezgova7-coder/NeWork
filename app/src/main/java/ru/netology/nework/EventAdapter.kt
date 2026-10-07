package ru.netology.nework

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
class EventAdapter(
    private val events: MutableList<Event>,
    private val onLikeClicked: (Event) -> Unit,
    private val onDeleteClicked: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val avatar: TextView =
            itemView.findViewById(R.id.event_avatar)

        val author: TextView =
            itemView.findViewById(R.id.event_author)

        val published: TextView =
            itemView.findViewById(R.id.event_published)

        val menu: ImageButton =
            itemView.findViewById(R.id.event_menu)

        val type: TextView =
            itemView.findViewById(R.id.event_type)

        val date: TextView =
            itemView.findViewById(R.id.event_date)

        val content: TextView =
            itemView.findViewById(R.id.event_content)

        val image: ImageView =
            itemView.findViewById(R.id.event_image)

        val like: ImageButton =
            itemView.findViewById(R.id.event_like)

        val likeCount: TextView =
            itemView.findViewById(R.id.event_like_count)

        val share: ImageButton =
            itemView.findViewById(R.id.event_share)

        val participants: ImageButton =
            itemView.findViewById(R.id.event_participants)

        val participantsCount: TextView =
            itemView.findViewById(
                R.id.event_participants_count
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {

        val view = LayoutInflater.from(
            parent.context
        ).inflate(
            R.layout.event_item,
            parent,
            false
        )

        return EventViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: EventViewHolder,
        position: Int
    ) {

        val event = events[position]

        // Аватар
        holder.avatar.text =
            event.author
                .firstOrNull()
                ?.uppercase()
                ?: "?"

        // Автор
        holder.author.text =
            event.author

        // Дата публикации
        holder.published.text =
            event.published

        // Тип
        holder.type.text =
            event.eventType

        // Дата события
        holder.date.text =
            event.eventDate

        // Текст
        holder.content.text =
            event.content

        // Лайки
        holder.likeCount.text =
            event.likes.toString()

        // Цвет сердечка
        holder.like.setImageResource(
            if (event.likedByMe) {
                R.drawable.ic_like_filled
            } else {
                R.drawable.ic_like_outline
            }
        )

        // Участники
        holder.participantsCount.text =
            event.participants.toString()

        // Изображение
        holder.image.visibility =
            if (event.imageUrl != null) {
                View.VISIBLE
            } else {
                View.GONE
            }

        // Лайк
        holder.like.setOnClickListener {

            val position = holder.bindingAdapterPosition

            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            val event = events[position]

            onLikeClicked(event)
        }

        // Поделиться
        holder.share.setOnClickListener {
            // Логику поделиться подключим позже
        }

        // Участники
        holder.participants.setOnClickListener {
            // Список участников подключим позже
        }

        // Меню события
        holder.menu.setOnClickListener {

            showEventMenu(
                holder.menu,
                event,
                holder.bindingAdapterPosition
            )
        }
    }

    /**
     * Меню редактирования / удаления
     */
    private fun showEventMenu(
        anchor: View,
        event: Event,
        position: Int
    ) {
        val preferences =
            anchor.context.getSharedPreferences(
                "nework_auth",
                android.content.Context.MODE_PRIVATE
            )

        val currentUserId =
            preferences.getLong(
                "user_id",
                1L
            )

        if (event.authorId != currentUserId) {
            return
        }

        val popupMenu =
            PopupMenu(
                anchor.context,
                anchor
            )

        popupMenu.menu.add(
            "Редактировать"
        )

        popupMenu.menu.add(
            "Удалить"
        )

        popupMenu.setOnMenuItemClickListener { item ->

            when (item.title.toString()) {

                "Редактировать" -> {

                    val fragment =
                        NewEventFragment().apply {

                            arguments = Bundle().apply {

                                putLong(
                                    NewEventFragment.ARG_EVENT_ID,
                                    event.id
                                )

                                putString(
                                    NewEventFragment.ARG_EVENT_AUTHOR,
                                    event.author
                                )

                                putLong(
                                    NewEventFragment.ARG_EVENT_AUTHOR_ID,
                                    event.authorId
                                )

                                putString(
                                    NewEventFragment.ARG_EVENT_PUBLISHED,
                                    event.published
                                )

                                putString(
                                    NewEventFragment.ARG_EVENT_CONTENT,
                                    event.content
                                )

                                putString(
                                    NewEventFragment.ARG_EVENT_DATE,
                                    event.eventDate
                                )

                                putString(
                                    NewEventFragment.ARG_EVENT_TYPE,
                                    event.eventType
                                )

                                putInt(
                                    NewEventFragment.ARG_EVENT_LIKES,
                                    event.likes
                                )

                                putInt(
                                    NewEventFragment.ARG_EVENT_PARTICIPANTS,
                                    event.participants
                                )
                            }
                        }

                    anchor.context
                        .let { context ->

                            if (context is androidx.fragment.app.FragmentActivity) {

                                context.supportFragmentManager
                                    .beginTransaction()
                                    .replace(
                                        R.id.fragment_container,
                                        fragment
                                    )
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }

                    true
                }

                "Удалить" -> {

                    if (position != RecyclerView.NO_POSITION) {

                        onDeleteClicked(event)
                    }

                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    override fun getItemCount(): Int =
        events.size
}