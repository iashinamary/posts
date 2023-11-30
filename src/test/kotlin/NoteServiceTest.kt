import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class NoteServiceTest{
    @Before
    fun clearBeforeTest() {
        NoteService.clear()
    }

    @Test
    fun idShouldNotBeNull(){
        val newNote = NoteService.add(Note(1, "Task", "Do homework"))
        val result = newNote.noteId

        assertEquals(1, result)
    }

    @Test(expected = NoteNotFoundException::class)
    fun shouldThrow() {
        NoteService.delete(1)
    }

    @Test
    fun shouldDeleteExistingNote(){
        val newNote = NoteService.add(Note(1, "Note", "Do that"))
        val result = NoteService.delete(newNote.noteId)

        assertEquals(true, result)
    }
    @Test
    fun editNonExistingNote(){
        val result = NoteService.edit(Note(1, "Note", "Edited"))

        assertEquals(false, result)
    }

    @Test
    fun editExistingNote(){
        NoteService.add(Note(1, "Note", "Do that"))
        val result = NoteService.edit(Note(1, "Note", "Done"))

        assertEquals(true, result)

    }

    @Test
    fun getOnlyExistingNotes(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val note2 = NoteService.add(Note(2, "Note", "Do this"))
        val note3 = NoteService.add(Note(3, "Note", "This should be deleted"))
        NoteService.delete(note3.noteId)

        val result = NoteService.get().size
        assertEquals(2, result)
    }

    @Test
    fun getExistingNote(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val result = NoteService.getById(note1.noteId)
        assertEquals(note1, result)
    }

    @Test(expected = NoteNotFoundException::class)
    fun getNonExistingNote(){
        NoteService.getById(1)
    }

    @Test
    fun getFriendsNotes(){
        val note1 = NoteService.add(Note(1, "Note", "Only for friends", privacy = 1))
        val note2 = NoteService.add(Note(2, "Note", "Only for me", privacy = 3))
        val note3 = NoteService.add(Note(3, "Note", "I don't know who read this"))

        val result = NoteService.getFriendsNotes().size
        assertEquals(1, result)
    }

    @Test
    fun commentIdIsNotNull(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)
        val result = NoteService.createComment(note1.noteId, commentForNote1).commentId

        assertEquals(1, result)

    }

    @Test(expected = NoteNotFoundException::class)
    fun commentForNonExistingNote(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        NoteService.delete(note1.noteId)
        val commentForNote = CommentForNote(1, "No", note1.noteId)
        val result = NoteService.createComment(note1.noteId, commentForNote)
    }

    @Test
    fun deleteComment(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)
        NoteService.createComment(note1.noteId, commentForNote1)

        val result = NoteService.deleteComment(note1.noteId, commentForNote1.commentId)

        assertEquals(true, result)
    }

    @Test(expected = CommentNotFoundException::class)
    fun deleteNonExistingComment(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)
        val result = NoteService.deleteComment(note1.noteId, commentForNote1.commentId)

    }

    @Test(expected = NoteNotFoundException::class)
    fun deleteCommentWhenNoteDeleted(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)
        NoteService.delete(note1.noteId)
        NoteService.createComment(note1.noteId, commentForNote1)
    }

    @Test
    fun editComment(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)
        NoteService.createComment(note1.noteId, commentForNote1)

        val result = NoteService.editComment(CommentForNote(1, "Yes", note1.noteId))

        assertEquals(true, result)
    }

    @Test
    fun editNonExistingComment(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)

        val result = NoteService.editComment(CommentForNote(1, "Yes", note1.noteId))

        assertEquals(false, result)
    }

    @Test
    fun editCommentWhenNoteDeleted(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)
        with(NoteService) {
            createComment(note1.noteId, commentForNote1)
            delete(note1.noteId)
        }
        val result = NoteService.editComment(CommentForNote(1, "Yes", note1.noteId))
        assertEquals(false, result)

    }

    @Test
    fun restoreComment(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)
        with(NoteService) {
            createComment(note1.noteId, commentForNote1)
            deleteComment(note1.noteId, commentForNote1.commentId)
        }

        val result = NoteService.restoreComment(note1.noteId, commentForNote1.commentId)
        assertEquals(true, result)
    }

    @Test(expected = CommentNotFoundException::class)
    fun restoreNonExistingComment(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)

        NoteService.restoreComment(note1.noteId, commentForNote1.commentId)
    }

    @Test
    fun restoreCommentWhenNoteDeleted(){
        val note1 = NoteService.add(Note(1, "Note", "Do that"))
        val note2 = NoteService.add(Note(2, "Note", "Do this"))
        val commentForNote1 = CommentForNote(1, "No", note1.noteId)
        with(NoteService) {
            createComment(note1.noteId, commentForNote1)
            deleteComment(note1.noteId, commentForNote1.commentId)
            delete(note1.noteId)
        }
        val result = NoteService.restoreComment(note1.noteId, commentForNote1.commentId)
        assertEquals(false, result)
    }

    @Test
    fun getExistingComment(){
        val note1 = NoteService.add(Note(1, "Note", "Let's run"))
        val note2 = NoteService.add(Note(2, "Note", "What's your favourite food?"))
        val comment1ForNote1 = CommentForNote(1, "No", note1.noteId)
        val comment2ForNote1 = CommentForNote(2, "Yes", note1.noteId)
        val comment3ForNote1 = CommentForNote(3, "Let's go", note1.noteId)
        val comment1ForNote2 = CommentForNote(1, "Chicken", note2.noteId)
        with(NoteService) {
            createComment(note1.noteId, comment1ForNote1)
            createComment(note1.noteId, comment2ForNote1)
            createComment(note1.noteId, comment3ForNote1)
            createComment(note2.noteId, comment1ForNote2)
            deleteComment(note1.noteId, comment1ForNote1.commentId)
        }
        val result = NoteService.getComments(note1.noteId).size
        assertEquals(2, result)
    }




}