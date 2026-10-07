package ru.netology.nework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class MentionUsersFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var usersRecycler: RecyclerView

    private val selectedUsers = mutableSetOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_mention_users,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(
            R.id.mention_users_toolbar
        )

        usersRecycler = view.findViewById(
            R.id.mention_users_recycler
        )

        /*
         * НАЗАД
         */
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /*
         * КНОПКА "ГОТОВО"
         */
        toolbar.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.action_mention_done -> {

                    parentFragmentManager
                        .setFragmentResult(
                            REQUEST_KEY,
                            Bundle().apply {
                                putLongArray(
                                    RESULT_USER_IDS,
                                    selectedUsers.toLongArray()
                                )
                            }
                        )

                    parentFragmentManager.popBackStack()

                    true
                }

                else -> false
            }
        }

        /*
         * СПИСОК ПОЛЬЗОВАТЕЛЕЙ
         */
        val users = listOf(

            User(
                id = 1L,
                name = "Adison Levin",
                login = "jgummera8"
            ),

            User(
                id = 2L,
                name = "Livia Donin",
                login = "spinuricy"
            ),

            User(
                id = 3L,
                name = "Lydia Westervelt",
                login = "lbragancapx"
            ),

            User(
                id = 4L,
                name = "Kaylynn Mango",
                login = "smorrill8"
            ),

            User(
                id = 5L,
                name = "Leo Lipshutz",
                login = "tokenny8q"
            ),

            User(
                id = 6L,
                name = "Vito Dobbs",
                login = "afruen83"
            ),

            User(
                id = 7L,
                name = "Dulce Kenter",
                login = "thenaughant"
            ),

            User(
                id = 8L,
                name = "Lydia Levin",
                login = "gprends"
            ),

            User(
                id = 9L,
                name = "Ryan Dias",
                login = "fsukbhansf2"
            ),

            User(
                id = 10L,
                name = "Charlie Lipshutz",
                login = "missacson9p"
            )
        )

        usersRecycler.layoutManager =
            LinearLayoutManager(requireContext())

        usersRecycler.adapter =
            MentionUsersAdapter(
                users = users,
                selectedUsers = selectedUsers
            ) { user, selected ->

                if (selected) {
                    selectedUsers.add(user.id)
                } else {
                    selectedUsers.remove(user.id)
                }
            }
    }

    companion object {

        const val REQUEST_KEY =
            "mention_users_result"

        const val RESULT_USER_IDS =
            "mention_user_ids"
    }
}