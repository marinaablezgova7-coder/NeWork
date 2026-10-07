package ru.netology.nework

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import ru.netology.nework.data.AppDatabase

class UserDetailsFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var avatar: TextView
    private lateinit var name: TextView
    private lateinit var login: TextView
    private lateinit var logoutButton: MaterialButton

    private lateinit var wallRecycler: RecyclerView
    private lateinit var jobsRecycler: RecyclerView

    private lateinit var wallAdapter: PostAdapter
    private lateinit var workAdapter: WorkAdapter

    private lateinit var emptyText: TextView

    private var userId: Long = -1L

    override fun onResume() {
        super.onResume()

        if (::wallAdapter.isInitialized && userId != -1L) {
            loadUserPosts()
        }

        if (::workAdapter.isInitialized && userId != -1L) {
            loadUserWorks()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_user_details,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        toolbar = view.findViewById(
            R.id.user_details_toolbar
        )

        avatar = view.findViewById(
            R.id.user_details_avatar
        )

        name = view.findViewById(
            R.id.user_details_name
        )

        login = view.findViewById(
            R.id.user_details_login
        )

        logoutButton = view.findViewById(
            R.id.logout_button
        )

        val addWorkButton =
            view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
                R.id.add_work_button
            )

        wallRecycler = view.findViewById(
            R.id.profile_wall_recycler
        )

        jobsRecycler = view.findViewById(
            R.id.profile_jobs_recycler
        )

        emptyText = view.findViewById(
            R.id.profile_empty_text
        )

        val authPreferences =
            requireContext().getSharedPreferences(
                "nework_auth",
                Context.MODE_PRIVATE
            )

        /*
         * ID текущего пользователя.
         *
         * Если ID передан через arguments,
         * используем его.
         *
         * Иначе берём из авторизации.
         */
        userId =
            if (arguments?.containsKey(ARG_USER_ID) == true) {
                arguments?.getLong(
                    ARG_USER_ID,
                    -1L
                ) ?: -1L
            } else {
                authPreferences.getLong(
                    "user_id",
                    -1L
                )
            }

        val currentUserId =
            authPreferences.getLong(
                "user_id",
                -1L
            )

        addWorkButton.visibility =
            if (userId == currentUserId) {
                View.VISIBLE
            } else {
                View.GONE
            }

        val userName =
            arguments?.getString(ARG_NAME)
                ?: authPreferences.getString(
                    "name",
                    ""
                )
                ?: ""

        val userLogin =
            arguments?.getString(ARG_LOGIN)
                ?: authPreferences.getString(
                    "login",
                    ""
                )
                ?: ""

        name.text = userName
        login.text = userLogin

        avatar.text = getInitials(
            userName
        )

        toolbar.title = userName

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        addWorkButton.setOnClickListener {

            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    NewWorkFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        setupWall()
        setupWorks()

        val wallTab =
            view.findViewById<TextView>(
                R.id.profile_tab_wall
            )

        val jobsTab =
            view.findViewById<TextView>(
                R.id.profile_tab_jobs
            )

        wallTab.setOnClickListener {

            wallRecycler.visibility =
                View.VISIBLE

            jobsRecycler.visibility =
                View.GONE

            emptyText.visibility =
                if (wallAdapter.itemCount == 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            wallTab.setTextColor(
                resources.getColor(
                    android.R.color.holo_purple,
                    null
                )
            )

            wallTab.textStyleBold()

            jobsTab.setTextColor(
                resources.getColor(
                    android.R.color.darker_gray,
                    null
                )
            )

            jobsTab.textStyleNormal()
        }

        jobsTab.setOnClickListener {

            wallRecycler.visibility =
                View.GONE

            jobsRecycler.visibility =
                View.VISIBLE

            emptyText.visibility =
                View.GONE

            wallTab.setTextColor(
                resources.getColor(
                    android.R.color.darker_gray,
                    null
                )
            )

            wallTab.textStyleNormal()

            jobsTab.setTextColor(
                resources.getColor(
                    android.R.color.holo_purple,
                    null
                )
            )

            jobsTab.textStyleBold()
        }

        // Начальная вкладка — Стена
        wallTab.performClick()

        logoutButton.setOnClickListener {

            requireContext()
                .getSharedPreferences(
                    "nework_auth",
                    Context.MODE_PRIVATE
                )
                .edit()
                .clear()
                .apply()

            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    PostsFragment()
                )
                .commit()
        }
    }

    private fun setupWall() {

        wallAdapter = PostAdapter(

            onLikeClicked = { post ->

                viewLifecycleOwner.lifecycleScope.launch {

                    val database =
                        AppDatabase.getInstance(
                            requireContext()
                        )

                    val newLiked =
                        !post.likedByMe

                    val newLikes =
                        if (newLiked) {
                            post.likes + 1
                        } else {
                            (post.likes - 1)
                                .coerceAtLeast(0)
                        }

                    database.postDao().updateLike(
                        id = post.id,
                        likes = newLikes,
                        likedByMe = newLiked
                    )

                    loadUserPosts()
                }
            },

            onMenuClicked = { },

            onPostClicked = { post ->

                parentFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        PostDetailsFragment.newInstance(
                            post
                        )
                    )
                    .addToBackStack(null)
                    .commit()
            },

            onShareClicked = { }
        )

        wallRecycler.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        wallRecycler.adapter =
            wallAdapter

        loadUserPosts()
    }

    private fun loadUserPosts() {

        if (userId == -1L) {
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {

            val database =
                AppDatabase.getInstance(
                    requireContext()
                )

            val entities =
                database.postDao().getAll()

            val posts =
                entities
                    .filter { entity ->
                        entity.authorId == userId
                    }
                    .map { entity ->

                        Post(
                            id = entity.id,
                            author = entity.author,
                            authorId = entity.authorId,
                            published = entity.published,
                            content = entity.content,
                            likes = entity.likes,
                            likedByMe = entity.likedByMe,
                            imageUrl = entity.imageUrl,
                            videoUrl = entity.videoUrl,
                            link = entity.link,
                            mentionedUserIds =
                                if (
                                    entity.mentionedUserIds.isBlank()
                                ) {
                                    emptyList()
                                } else {
                                    entity.mentionedUserIds
                                        .split(",")
                                        .mapNotNull {
                                            it.toLongOrNull()
                                        }
                                }
                        )
                    }

            wallAdapter.submitList(posts)

            emptyText.visibility =
                if (posts.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    private fun setupWorks() {

        workAdapter = WorkAdapter { work ->

            viewLifecycleOwner.lifecycleScope.launch {

                val database =
                    AppDatabase.getInstance(
                        requireContext()
                    )

                database.workDao()
                    .deleteById(work.id)

                loadUserWorks()
            }
        }
        jobsRecycler.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        jobsRecycler.adapter =
            workAdapter

        loadUserWorks()
    }

    private fun loadUserWorks() {

        if (userId == -1L) {
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {

            val database =
                AppDatabase.getInstance(
                    requireContext()
                )

            val entities =
                database.workDao()
                    .getByUserId(userId)

            val works =
                entities.map { entity ->

                    Work(
                        id = entity.id,
                        userId = entity.userId,
                        company = entity.company,
                        start = entity.start,
                        finish = entity.finish,
                        position = entity.position
                    )
                }

            workAdapter.submitList(
                works
            )
        }
    }

    private fun getInitials(
        name: String
    ): String {

        val result =
            name
                .trim()
                .split(" ")
                .filter {
                    it.isNotEmpty()
                }
                .take(2)
                .joinToString("") {
                    it.first()
                        .uppercase()
                }

        return result.ifEmpty {
            "?"
        }
    }

    companion object {

        private const val ARG_USER_ID =
            "userId"

        private const val ARG_NAME =
            "name"

        private const val ARG_LOGIN =
            "login"

        fun newInstance(
            user: User
        ): UserDetailsFragment {

            return UserDetailsFragment().apply {

                arguments =
                    Bundle().apply {

                        putLong(
                            ARG_USER_ID,
                            user.id
                        )

                        putString(
                            ARG_NAME,
                            user.name
                        )

                        putString(
                            ARG_LOGIN,
                            user.login
                        )
                    }
            }
        }

        fun newInstance(
            name: String,
            login: String
        ): UserDetailsFragment {

            return UserDetailsFragment().apply {

                arguments =
                    Bundle().apply {

                        putString(
                            ARG_NAME,
                            name
                        )

                        putString(
                            ARG_LOGIN,
                            login
                        )
                    }
            }
        }
    }
}

/*
 * Небольшие расширения для вкладок
 */
private fun TextView.textStyleBold() {
    setTypeface(
        typeface,
        android.graphics.Typeface.BOLD
    )
}

private fun TextView.textStyleNormal() {
    setTypeface(
        typeface,
        android.graphics.Typeface.NORMAL
    )
}