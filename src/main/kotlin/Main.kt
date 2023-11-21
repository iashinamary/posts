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

interface Attachment{
    val type: String
}

data class PhotoAttachment(
    override val type: String = "photo",
    val photo: Photo
) : Attachment

data class Photo (
    val id: Int,
    val album_id: Int,
    val width: Int,
    val height: Int
)

data class VideoAttachment(
    override val type: String = "video",
    val video: Video
):Attachment

data class Video(
    val id: Int,
    val title: String,
    val canComment: Boolean
)

data class AudioAttachment(
    override val type: String = "audio",
    val audio: Audio
):Attachment

data class Audio(
    val id: Int,
    val artist: String,
    val title: String
)

data class FileAttachment(
    override val type: String = "file",
    val file: File
):Attachment

data class File(
    val id: Int,
    val ownerId: Int,
    val title: String
)

data class StickerAttachment(
    override val type: String = "sticker",
    val sticker: Sticker
): Attachment

data class Sticker(
    val productId: Int,
    val stickeId: Int,
    val isAllowed: Boolean

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