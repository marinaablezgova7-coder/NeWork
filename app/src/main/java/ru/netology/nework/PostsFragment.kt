package ru.netology.nework

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.netology.nework.data.AppDatabase

class PostsFragment : Fragment() {
    private lateinit var database: AppDatabase

    private fun getCurrentUserId(): Long {
        return requireContext()
            .getSharedPreferences(
                "nework_auth",
                Context.MODE_PRIVATE
            )
            .getLong(
                "user_id",
                -1L
            )
    }


    private var currentPosts: List<Post> = emptyList()

    private lateinit var postsRecycler: RecyclerView
    private lateinit var adapter: PostAdapter

    override fun onResume() {
        super.onResume()

        if (::database.isInitialized && ::adapter.isInitialized) {
            reloadPosts()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_posts,
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


        postsRecycler = view.findViewById(
            R.id.posts_recycler
        )

        /*
         * РЕЗУЛЬТАТ ПОСЛЕ СОЗДАНИЯ НОВОГО ПОСТА
         */
        parentFragmentManager.setFragmentResultListener(
            NewPostFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            val id = bundle.getLong(
                NewPostFragment.RESULT_ID
            )

            val author = bundle.getString(
                NewPostFragment.RESULT_AUTHOR
            ) ?: return@setFragmentResultListener

            val published = bundle.getString(
                NewPostFragment.RESULT_PUBLISHED
            ) ?: ""

            val content = bundle.getString(
                NewPostFragment.RESULT_CONTENT
            ) ?: ""

            val likes = bundle.getInt(
                NewPostFragment.RESULT_LIKES
            )

            val imageUrl = bundle.getString(
                NewPostFragment.RESULT_IMAGE_URL
            )

            val videoUrl = bundle.getString(
                NewPostFragment.RESULT_VIDEO_URL
            )

            val link = bundle.getString(
                NewPostFragment.RESULT_LINK
            )

            val mentionedUserIds =
                bundle.getLongArray(
                    NewPostFragment.RESULT_MENTIONED_USER_IDS
                )?.toList() ?: emptyList()

            val authorId = bundle.getLong(
                NewPostFragment.RESULT_AUTHOR_ID,
                getCurrentUserId()
            )

            val newPost = Post(
                id = id,
                author = author,
                published = published,
                authorId = authorId,
                content = content,
                likes = likes,
                imageUrl = imageUrl,
                videoUrl = videoUrl,
                link = link,
                mentionedUserIds = mentionedUserIds
            )

            viewLifecycleOwner.lifecycleScope.launch {

                database.postDao().insert(
                    ru.netology.nework.data.PostEntity(
                        id = newPost.id,
                        author = newPost.author,
                        authorId = newPost.authorId,
                        published = newPost.published,
                        content = newPost.content,
                        likes = newPost.likes,
                        likedByMe = newPost.likedByMe,
                        imageUrl = newPost.imageUrl,
                        videoUrl = newPost.videoUrl,
                        link = newPost.link,
                        mentionedUserIds =
                            newPost.mentionedUserIds.joinToString(",")
                    )
                )
            }

            currentPosts =
                listOf(newPost) + currentPosts



            adapter.submitList(currentPosts)

            postsRecycler.scrollToPosition(0)
        }


        parentFragmentManager.setFragmentResultListener(
            EditPostFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            val postId = bundle.getLong(
                EditPostFragment.RESULT_POST_ID
            )

            val newText = bundle.getString(
                EditPostFragment.RESULT_TEXT
            ) ?: return@setFragmentResultListener

            currentPosts = currentPosts.map { post ->

                if (post.id == postId) {

                    val updatedPost = post.copy(
                        content = newText
                    )

                    viewLifecycleOwner.lifecycleScope.launch {

                        database.postDao().insert(
                            ru.netology.nework.data.PostEntity(
                                id = updatedPost.id,
                                author = updatedPost.author,
                                authorId = updatedPost.authorId,
                                published = updatedPost.published,
                                content = updatedPost.content,
                                likes = updatedPost.likes,
                                likedByMe = updatedPost.likedByMe,
                                imageUrl = updatedPost.imageUrl,
                                videoUrl = updatedPost.videoUrl,
                                link = updatedPost.link,
                                mentionedUserIds =
                                    updatedPost.mentionedUserIds.joinToString(",")
                            )
                        )
                    }

                    updatedPost

                } else {
                    post
                }
            }

            adapter.submitList(currentPosts)
        }

        /*
         * АДАПТЕР
         */
        adapter = PostAdapter(

            /*
             * ЛАЙК
             */
            onLikeClicked = { post ->

                val updatedPost = post.copy(

                    likedByMe = !post.likedByMe,

                    likes = if (post.likedByMe) {
                        post.likes - 1
                    } else {
                        post.likes + 1
                    }
                )

                currentPosts = currentPosts.map {

                    if (it.id == updatedPost.id) {
                        updatedPost
                    } else {
                        it
                    }
                }

                adapter.submitList(currentPosts)
                viewLifecycleOwner.lifecycleScope.launch {
                    database.postDao().updateLike(
                        id = updatedPost.id,
                        likes = updatedPost.likes,
                        likedByMe = updatedPost.likedByMe
                    )
                }
            },

            /*
             * ТРИ ТОЧКИ
             */
            onMenuClicked = { post ->

                if (post.authorId != getCurrentUserId()) {
                    return@PostAdapter
                }

                AlertDialog.Builder(requireContext())
                    .setItems(
                        arrayOf(
                            "Редактировать",
                            "Удалить"
                        )
                    ) { _, which ->

                        when (which) {

                            /*
                             * РЕДАКТИРОВАТЬ
                             */
                            0 -> {

                                val editFragment =
                                    EditPostFragment.newInstance(post)

                                parentFragmentManager
                                    .beginTransaction()
                                    .replace(
                                        R.id.fragment_container,
                                        editFragment
                                    )
                                    .addToBackStack(null)
                                    .commit()
                            }

                            /*
                             * УДАЛИТЬ
                             */
                            1 -> {

                                viewLifecycleOwner.lifecycleScope.launch {

                                    database.postDao().deleteById(post.id)
                                }

                                currentPosts =
                                    currentPosts.filter {
                                        it.id != post.id
                                    }

                                adapter.submitList(currentPosts)
                            }
                        }
                    }
                    .show()
            },

            /*
             * ОТКРЫТЬ ДЕТАЛЬНЫЙ ПОСТ
             */
            onPostClicked = { post ->

                parentFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        PostDetailsFragment.newInstance(post)
                    )
                    .addToBackStack(null)
                    .commit()
            },

            /*
             * ПОДЕЛИТЬСЯ
             */
            onShareClicked = { post ->

                val shareText = buildString {

                    append(post.author)
                    append("\n\n")

                    append(post.content)

                    if (!post.link.isNullOrBlank()) {
                        append("\n\n")
                        append(post.link)
                    }
                }

                val shareIntent = Intent(
                    Intent.ACTION_SEND
                ).apply {

                    type = "text/plain"

                    putExtra(
                        Intent.EXTRA_TEXT,
                        shareText
                    )
                }

                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        "Поделиться постом"
                    )
                )
            }
        )

        /*
         * RECYCLER VIEW
         */
        postsRecycler.layoutManager =
            LinearLayoutManager(requireContext())

        postsRecycler.adapter = adapter

        /*
         * КНОПКА +
         */
        val addPostButton =
            view.findViewById<FloatingActionButton>(
                R.id.add_post_button
            )

        addPostButton.setOnClickListener {

            if (isUserLoggedIn()) {

                openNewPost()

            } else {

                showAuthDialog()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            val postsFromDatabase =
                database.postDao().getAll()

            currentPosts = postsFromDatabase.map { entity ->

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
                    mentionedUserIds = entity.mentionedUserIds
                        .split(",")
                        .filter { it.isNotBlank() }
                        .map { it.toLong() }
                )
            }

            adapter.submitList(currentPosts)
        }
    }

    private fun reloadPosts() {

        viewLifecycleOwner.lifecycleScope.launch {

            val postsFromDatabase =
                database.postDao().getAll()

            currentPosts =
                postsFromDatabase.map { entity ->

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
                            entity.mentionedUserIds
                                .split(",")
                                .filter { it.isNotBlank() }
                                .map { it.toLong() }
                    )
                }

            adapter.submitList(currentPosts)
        }
    }

    /*
     * ПРОВЕРКА АВТОРИЗАЦИИ
     */
    private fun isUserLoggedIn(): Boolean {

        val preferences =
            requireContext().getSharedPreferences(
                "nework_auth",
                Context.MODE_PRIVATE
            )

        return preferences.getBoolean(
            "is_logged_in",
            false
        )
    }

    /*
     * ОТКРЫТЬ СОЗДАНИЕ ПОСТА
     */
    private fun openNewPost() {

        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container,
                NewPostFragment()
            )
            .addToBackStack(null)
            .commit()
    }

    /*
     * ДИАЛОГ ВХОДА / РЕГИСТРАЦИИ
     */
    private fun showAuthDialog() {

        AlertDialog.Builder(requireContext())
            .setItems(
                arrayOf(
                    "Войти",
                    "Зарегистрироваться"
                )
            ) { _, which ->

                when (which) {

                    /*
                     * ВОЙТИ
                     */
                    0 -> {

                        parentFragmentManager
                            .beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                LoginFragment()
                            )
                            .addToBackStack(null)
                            .commit()
                    }

                    /*
                     * РЕГИСТРАЦИЯ
                     */
                    1 -> {

                        parentFragmentManager
                            .beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                RegistrationFragment()
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
            .show()
    }

    /*
     * ДОБАВИТЬ НОВЫЙ ПОСТ
     */
    fun addPost(post: Post) {

        currentPosts =
            listOf(post) + currentPosts

        adapter.submitList(currentPosts)

        postsRecycler.scrollToPosition(0)
    }

    companion object {

        const val REQUEST_KEY = "new_post_result"

        const val RESULT_ID = "new_post_id"
        const val RESULT_AUTHOR = "new_post_author"
        const val RESULT_PUBLISHED = "new_post_published"
        const val RESULT_CONTENT = "new_post_content"
        const val RESULT_LIKES = "new_post_likes"
        const val RESULT_IMAGE_URL = "new_post_image_url"
        const val RESULT_VIDEO_URL = "new_post_video_url"
        const val RESULT_LINK = "new_post_link"
        const val RESULT_MENTIONED_USER_IDS =
            "new_post_mentioned_user_ids"
    }
}