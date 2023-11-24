import java.lang.Exception
import java.lang.RuntimeException
import java.sql.Time
import java.time.format.DateTimeFormatter
import java.util.Date

data class Post(
    val id: Int,
    val fromId: Int,
    val ownerId: Int,
    val text: String,
    val date: Int,
    val createdBy: Int,
    val replyOwnerId: Int,
    val replyPostId: Int,
    val attachments: Attachments?,
    val postponedId: Int?,
    val postType: String,
    val signerId: Int,
    val comments: Comments = Comments(0),
    val copyright: Copyright = Copyright(1),
    val geo: Geo = Geo("Moscow"),
    val likes: Likes = Likes(0),
    val canPin: Boolean = true,
    val friendsOnly: Boolean = false,
    val markedAsAds: Boolean = false,
    val isPinned: Boolean = false,
    val isFavourite: Boolean = false,
    val canEdit: Boolean = true,
    val canDelete: Boolean = true
)

data class Likes(
    val count: Int
)

data class Copyright(
    val id: Int
)

data class Geo(
    val type: String
)

data class Comments( //класс, содержащий комментарии под постом
    val count: Int
)

data class Comment( //класс, описывающий сам комментарий
    val id: Int,
    val fromId: Int,
    val date: Int,
    val text: String,
    val replyToUser: Int? = null,
    val replyToComment: Int? = null,
    val attachments: Attachments? = null,
    val thread: Thread? = null
)

data class Thread(
    val count: Int,
    val canPost: Boolean,
    val showReplyButton: Boolean = true,
    val groupsCanPost: Boolean = true
)

interface Attachments{
    val type: String
}

data class PhotoAttachment(
    override val type: String = "photo",
    val photo: Photo
) : Attachments

data class Photo (
    val id: Int,
    val album_id: Int,
    val width: Int,
    val height: Int
)

data class VideoAttachment(
    override val type: String = "video",
    val video: Video
):Attachments

data class Video(
    val id: Int,
    val title: String,
    val canComment: Boolean
)

data class AudioAttachment(
    override val type: String = "audio",
    val audio: Audio
):Attachments

data class Audio(
    val id: Int,
    val artist: String,
    val title: String
)

data class FileAttachment(
    override val type: String = "file",
    val file: File
):Attachments

data class File(
    val id: Int,
    val ownerId: Int,
    val title: String
)

data class StickerAttachment(
    override val type: String = "sticker",
    val sticker: Sticker
): Attachments

data class Sticker(
    val productId: Int,
    val stickeId: Int,
    val isAllowed: Boolean

)

class PostNotFoundException(message: String) : RuntimeException(message)

object WallService {
    private var posts = emptyArray<Post>()
    private var comments = emptyArray<Comment>()


    fun createComment(postId: Int, comment: Comment): Comment {
        val post = posts.find { it.id == postId }
        if (post != null) {
            val commentId = if (comments.isNotEmpty()) comments.maxOf { it.id } + 1 else 1
            val newComment = comment.copy(id = commentId)
            comments += newComment
            return comments.last()
        } else {
            throw PostNotFoundException("No post with $postId id")
        }
    }

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