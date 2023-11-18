import java.util.Objects

data class Post(
    val id: Int,
    val fromId: Int,
    val ownerId: Int,
    val text: String,
    val date: Int,
    val canPin: Boolean,
    val friendsOnly: Boolean,
    val markedAsAds: Boolean,
    val isPinned: Boolean,
    val created_by: Int,
    val likes: Likes = Likes(0)

)

data class Likes(
    val count: Int
)

object WallService {
    private var posts = emptyArray<Post>()

    fun clear() {
        posts = emptyArray()
    }


    fun add(post: Post): Post {
        val postId = if (posts.isNotEmpty()) posts.maxOf { it.id } + 1 else 1
        val newPost = post.copy(id = postId)
        posts += newPost
        return posts.last()
    }

    fun update(post: Post): Boolean {
        for ((index, existingPost) in posts.withIndex()) {
            if (post.id == existingPost.id) {
                posts[index] = post
                return true
            }
        }
        return false
    }
}