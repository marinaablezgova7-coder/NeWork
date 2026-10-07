package ru.netology.nework

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private val onLikeClicked: (Post) -> Unit,
    private val onMenuClicked: (Post) -> Unit,
    private val onPostClicked: (Post) -> Unit,
    private val onShareClicked: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var posts: List<Post> = emptyList()

    fun submitList(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item, parent, false)

        return PostViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class PostViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val avatar: TextView =
            itemView.findViewById(R.id.post_avatar)

        private val author: TextView =
            itemView.findViewById(R.id.post_author)

        private val date: TextView =
            itemView.findViewById(R.id.post_date)

        private val text: TextView =
            itemView.findViewById(R.id.post_text)

        private val likesCount: TextView =
            itemView.findViewById(R.id.post_likes_count)

        private val link: TextView =
            itemView.findViewById(R.id.post_link_text)

        private val likeButton: ImageButton =
            itemView.findViewById(R.id.post_like)

        private val menuButton: ImageButton =
            itemView.findViewById(R.id.post_menu)

        private val shareButton: ImageButton =
            itemView.findViewById(R.id.post_share)

        fun bind(post: Post) {

            // АВТОР
            author.text = post.author

            // АВАТАР С ИНИЦИАЛАМИ
            avatar.text = post.author
                .trim()
                .split(" ")
                .filter { it.isNotEmpty() }
                .take(2)
                .joinToString("") {
                    it.first().uppercase()
                }

            // ДАТА
            date.text = post.published

            // ТЕКСТ
            text.text = post.content

            // ЛАЙКИ
            likesCount.text = post.likes.toString()

            likeButton.setImageResource(
                if (post.likedByMe) {
                    R.drawable.ic_like_filled
                } else {
                    R.drawable.ic_like_outline
                }
            )


            // ССЫЛКА
            if (post.link.isNullOrBlank()) {

                link.visibility = View.GONE

                // На всякий случай очищаем старый listener
                link.setOnClickListener(null)

            } else {

                link.visibility = View.VISIBLE

                link.text = post.link

                link.setOnClickListener {

                    val url = post.link

                    if (!url.isNullOrBlank()) {

                        try {

                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(url)
                            )

                            itemView.context.startActivity(intent)

                        } catch (e: Exception) {

                            // Если браузер не найден
                        }
                    }
                }
            }

            // ОТКРЫТИЕ ПОСТА
            itemView.setOnClickListener {

                onPostClicked(post)
            }

            // ЛАЙК
            likeButton.setOnClickListener {

                onLikeClicked(post)
            }

            // ТРИ ТОЧКИ
            menuButton.setOnClickListener {

                onMenuClicked(post)
            }

            // ПОДЕЛИТЬСЯ
            shareButton.setOnClickListener {

                onShareClicked(post)
            }
        }
    }
}