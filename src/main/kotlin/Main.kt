import java.lang.Exception
import java.lang.RuntimeException
import java.sql.Time
import java.time.format.DateTimeFormatter
import java.util.Date
import java.lang.IndexOutOfBoundsException as IndexOutOfBoundsException

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

data class Note(
    val noteId: Int,
    val title: String,
    val text: String,
    val privacy: Int = 0,
    val commentPrivacy: Int = 0,
)

data class CommentForNote(
    val commentId: Int,
    val message: String,
    val noteId: Int,
    var doesExist: Boolean = true
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
    val albumId: Int,
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
    val stickerId: Int,
    val isAllowed: Boolean

)

class PostNotFoundException(message: String) : RuntimeException(message)
class NoteNotFoundException(message: String) : RuntimeException(message)
class CommentNotFoundException(message: String) : RuntimeException(message)

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
        } else throw PostNotFoundException("No post with $postId id")
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

object NoteService {
    private var notes = mutableListOf<Note>()
    private var commentsForNote = mutableListOf<CommentForNote>()

    fun clear() {
        notes.clear()
    }

    fun add(note: Note): Note {
        val confirmedNoteId = if (notes.isNotEmpty()) notes.maxOf { it.noteId } + 1 else 1
        val newNote = note.copy(noteId = confirmedNoteId)
        notes.add(newNote)
        return notes.last()
    }

    fun delete(noteId: Int): Boolean {
        val note = notes.find { it.noteId == noteId }
        if (note != null) {
            println("Заметка $noteId удалена")
            notes.remove(note)
            commentsForNote.removeAt(note.noteId)
            return true
        } else throw NoteNotFoundException("No note with $noteId id")
    }

    fun edit(note: Note): Boolean {
        for ((index, existingNote) in notes.withIndex()) {
            if (note.noteId == existingNote.noteId) {
                notes[index] = note
                return true
            }
        }
        return false
    }

    fun get(): List<Note> {
        return notes
    }

    fun getById(noteId: Int): Note {
        val noteWeNeed = notes.find { it.noteId == noteId }
        if (noteWeNeed != null) {
            return noteWeNeed
        } else throw NoteNotFoundException("No note with $noteId id")
    }

    fun getFriendsNotes(): List<Note> {
        return notes.filter { it.privacy == 1 }
    }

    fun createComment(noteId: Int, commentForNote: CommentForNote): CommentForNote {
        val note = notes.find { it.noteId == noteId }
        if (note != null) {
            val commentId = if (commentsForNote.isNotEmpty()) commentsForNote.maxOf { it.commentId } + 1 else 1
            val noteId = note.noteId
            val newComment = commentForNote.copy(commentId = commentId, noteId = noteId)
            commentsForNote.add(newComment)
            return commentsForNote.last()
        } else throw NoteNotFoundException("No note with $noteId id")
    }

    fun deleteComment(noteId: Int, commentId: Int): Boolean {
        val note = notes.find { it.noteId == noteId }
        if (note != null) {
            val commentForNote = commentsForNote.find { it.commentId == commentId }
            if (commentForNote != null && commentForNote.doesExist) {
                commentForNote.doesExist = false
                println("Комментарий $commentId удален")
                return true
            } else throw CommentNotFoundException("Comment $commentId doesn't exist")
        } else throw NoteNotFoundException("Note $noteId doesn't exist")
    }

    fun editComment(commentForNote: CommentForNote): Boolean {
        for ((index, existingComment) in commentsForNote.withIndex()) {
            try {
               if (commentForNote.commentId == existingComment.noteId && commentForNote.doesExist) {
                    commentsForNote[index] = commentForNote
                    return true
                }
            } catch (e: IndexOutOfBoundsException){
                println("Что-то пошло не так")
                continue
            }
        }
        return false
    }

    fun restoreComment(noteId: Int, commentId: Int): Boolean {
        try {

            val note = notes.find { it.noteId == noteId }
            if (note != null) {
                val commentForNote = commentsForNote.find { it.commentId == commentId }
                if (commentForNote != null && !commentForNote.doesExist) {
                    commentForNote.doesExist = true
                    println("Комментарий $commentId восстановлен")
                    return true
                } else throw CommentNotFoundException("Comment $commentId doesn't exist")
            }
        } catch (e: IndexOutOfBoundsException){
            println("Что-то пошло не так")
        }
        return false
    }

    fun getComments(noteId: Int): List<CommentForNote>{
        return commentsForNote.filter { it.noteId == noteId && it.doesExist}
    }
}
