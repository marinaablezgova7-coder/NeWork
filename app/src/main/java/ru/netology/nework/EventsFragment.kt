package ru.netology.nework

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.netology.nework.data.AppDatabase
import ru.netology.nework.data.EventEntity

class EventsFragment : Fragment() {

    private lateinit var eventsAdapter: EventAdapter
    private lateinit var database: AppDatabase
    private val events = mutableListOf<Event>()

    override fun onResume() {
        super.onResume()

        if (::database.isInitialized && ::eventsAdapter.isInitialized) {
            reloadEvents()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_events,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        database = AppDatabase.getInstance(requireContext())

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.events_recycler)

        eventsAdapter = EventAdapter(
            events,

            onLikeClicked = { event ->

                val updatedEvent = event.copy(
                    likedByMe = !event.likedByMe,
                    likes = if (event.likedByMe) {
                        event.likes - 1
                    } else {
                        event.likes + 1
                    }
                )

                val position =
                    events.indexOfFirst { it.id == updatedEvent.id }

                if (position != -1) {

                    events[position] = updatedEvent

                    eventsAdapter.notifyItemChanged(position)
                }

                viewLifecycleOwner.lifecycleScope.launch {

                    database.eventDao().updateLike(
                        id = updatedEvent.id,
                        likes = updatedEvent.likes,
                        likedByMe = updatedEvent.likedByMe
                    )
                }
            },

            onDeleteClicked = { event ->

                val position =
                    events.indexOfFirst { it.id == event.id }

                if (position != -1) {

                    events.removeAt(position)

                    eventsAdapter.notifyItemRemoved(position)
                }

                viewLifecycleOwner.lifecycleScope.launch {

                    database.eventDao()
                        .deleteById(event.id)
                }
            }
        )

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        recyclerView.adapter = eventsAdapter


        // Загрузка событий из Room
        viewLifecycleOwner.lifecycleScope.launch {

            val eventsFromDatabase =
                database.eventDao().getAll()

            events.clear()

            events.addAll(
                eventsFromDatabase.map { entity ->

                    Event(
                        id = entity.id,
                        author = entity.author,
                        authorId = entity.authorId,
                        published = entity.published,
                        content = entity.content,
                        eventDate = entity.eventDate,
                        eventType = entity.eventType,
                        likes = entity.likes,
                        likedByMe = entity.likedByMe,
                        participants = entity.participants,
                        imageUrl = entity.imageUrl
                    )
                }
            )

            eventsAdapter.notifyDataSetChanged()
        }


        // Результат создания / редактирования события
        parentFragmentManager.setFragmentResultListener(
            NewEventFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            val preferences =
                requireContext()
                    .getSharedPreferences(
                        "nework_auth",
                        Context.MODE_PRIVATE
                    )

            val authorId =
                preferences.getLong(
                    "user_id",
                    1L
                )

            val newEvent = Event(

                id = bundle.getLong(
                    NewEventFragment.RESULT_ID
                ),

                author = bundle.getString(
                    NewEventFragment.RESULT_AUTHOR
                ) ?: "Вы",

                authorId = authorId,

                published = bundle.getString(
                    NewEventFragment.RESULT_PUBLISHED
                ) ?: "",

                content = bundle.getString(
                    NewEventFragment.RESULT_CONTENT
                ) ?: "",

                eventDate = bundle.getString(
                    NewEventFragment.RESULT_EVENT_DATE
                ) ?: "",

                eventType = bundle.getString(
                    NewEventFragment.RESULT_EVENT_TYPE
                ) ?: "Online",

                likes = bundle.getInt(
                    NewEventFragment.RESULT_LIKES
                ),

                participants = bundle.getInt(
                    NewEventFragment.RESULT_PARTICIPANTS
                )
            )


            // Сохраняем событие в Room
            viewLifecycleOwner.lifecycleScope.launch {

                database.eventDao().insert(
                    EventEntity(
                        id = newEvent.id,
                        author = newEvent.author,
                        authorId = newEvent.authorId,
                        published = newEvent.published,
                        content = newEvent.content,
                        eventDate = newEvent.eventDate,
                        eventType = newEvent.eventType,
                        likes = newEvent.likes,
                        likedByMe = newEvent.likedByMe,
                        participants = newEvent.participants,
                        imageUrl = newEvent.imageUrl
                    )
                )
            }


            // Обновляем список
            val existingIndex =
                events.indexOfFirst {
                    it.id == newEvent.id
                }

            if (existingIndex != -1) {

                events[existingIndex] =
                    newEvent

                eventsAdapter.notifyItemChanged(
                    existingIndex
                )

            } else {

                events.add(
                    0,
                    newEvent
                )

                eventsAdapter.notifyItemInserted(0)

                recyclerView.scrollToPosition(0)
            }
        }


        // Кнопка добавления события
        view.findViewById<View>(
            R.id.add_event_button
        ).setOnClickListener {

            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    NewEventFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }


    // Полностью заново загружаем события из Room
    private fun reloadEvents() {

        viewLifecycleOwner.lifecycleScope.launch {

            val eventsFromDatabase =
                database.eventDao().getAll()

            events.clear()

            events.addAll(
                eventsFromDatabase.map { entity ->

                    Event(
                        id = entity.id,
                        author = entity.author,
                        authorId = entity.authorId,
                        published = entity.published,
                        content = entity.content,
                        eventDate = entity.eventDate,
                        eventType = entity.eventType,
                        likes = entity.likes,
                        likedByMe = entity.likedByMe,
                        participants = entity.participants,
                        imageUrl = entity.imageUrl
                    )
                }
            )

            eventsAdapter.notifyDataSetChanged()
        }
    }
}
