import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class WallServiceTest {

    @Before
    fun clearBeforeTest() {
        WallService.clear()
    }


    @Test
    fun ifIdIsNotNull() {
        val newPost = WallService.add(Post(1, 1, 1, "Hello", 10, 1, 1, 1, null, null, "post", 1))
        val result = newPost.id

        assertEquals(1, result)
    }

    @Test
    fun updateWithExistingId() {
        WallService.add(Post(1, 1, 1, "Hello", 10, 1, 1, 1, null, null, "post", 1))
        val result = WallService.update(Post(1, 1, 1, "Bye", 10, 1, 1, 1, null, null, "post", 1))

        assertEquals(false, result)
    }

    @Test
    fun updateWithNonExistingId() {
        WallService.add(Post(1, 1, 1, "Hello", 10, 1, 1, 1, null, null, "post", 1))
        val result = WallService.update(Post(13, 1, 1, "Bye", 10, 1, 1, 1, null, null, "post", 1))

        assertEquals(true, result)
    }

    @Test
    fun createCommentIfPostExist() {
        val comment = Comment(1, 1, 1, "Hi")
        WallService.add(Post(1, 1, 1, "Hello", 10, 1, 1, 1, null, null, "post", 1))
        val result = WallService.createComment(1, comment).text

        assertEquals("Hi", result)
    }

    @Test(expected = PostNotFoundException::class)
    fun shouldThrow() {
        val comment = Comment(1, 1, 1, "Hi")
        WallService.add(Post(1, 1, 1, "Hello", 10, 1, 1, 1, null, null, "post", 1))
        WallService.createComment(2, comment)
    }
}