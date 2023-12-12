import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.UUID
import kotlin.math.exp

class ChatServiceTest {

    @Before
    fun clear(){
        ChatService.clear()
    }
    @Test
    fun firstMessageShouldCreateNewChat() {
        val myId = 100
        val SashaId = 2
        val firstMsg = Message(SashaId, 1, myId, "Hi!", true)
        ChatService.createMessage(firstMsg)
        assertEquals(1, ChatService.chats.size)
    }

    @Test
    fun newCreatedChatShouldContainFirstMessage() {
        val myId = 100
        val SashaId = 2
        val firstMsg = Message(SashaId, 1, myId, "Hi!", true)
        ChatService.createMessage(firstMsg)
        assertEquals(firstMsg, ChatService.chats[0].messages[0])
    }

    @Test
    fun secondMessageShouldBeAddedToExistingChat(){
        val myId = 100
        val SashaId = 2
        val firstMsg = Message(SashaId, 1, myId, "Hi!", true)
        val secondMsg = Message(SashaId, 2, myId, "HRU?", true)
        ChatService.createMessage(firstMsg)
        ChatService.createMessage(secondMsg)
        assertEquals(secondMsg, ChatService.chats[0].messages[1])
    }

    @Test
    fun chatShouldBeDeleted(){
        val myId = 100
        val SashaId = 2
        val firstMsg = Message(SashaId, 1, myId, "Hi!", true)
        val secondMsg = Message(SashaId, 2, myId, "HRU?", true)
        ChatService.createMessage(firstMsg)
        ChatService.createMessage(secondMsg)
        val result = ChatService.deleteChat(SashaId)
        assertEquals(result, true)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteDeletedChat(){
        val myId = 100
        val SashaId = 2
        val firstMsg = Message(SashaId, 1, myId, "Hi!", true)
        val secondMsg = Message(SashaId, 2, myId, "HRU?", true)
        ChatService.createMessage(firstMsg)
        ChatService.createMessage(secondMsg)
        ChatService.deleteChat(SashaId)
        ChatService.deleteChat(SashaId)
    }

    @Test(expected = MessageNotFoundException::class)
    fun messagesShouldBeDeletedIfChatDeleted(){
        val myId = 100
        val SashaId = 2
        val firstMsg = Message(SashaId, 1, myId, "Hi!", true)
        val secondMsg = Message(SashaId, 2, myId, "HRU?", true)
        ChatService.createMessage(firstMsg)
        ChatService.createMessage(secondMsg)
        ChatService.deleteChat(SashaId)
        ChatService.deleteMessage(SashaId,1)
    }

    @Test
    fun messageShouldBeDeleted(){
        val myId = 100
        val SashaId = 2
        val firstMsg = Message(SashaId, 1, myId, "Hi!", true)
        val secondMsg = Message(SashaId, 2, myId, "HRU?", true)
        ChatService.createMessage(firstMsg)
        ChatService.createMessage(secondMsg)
        ChatService.deleteMessage(SashaId, 2)
        assertEquals(1, ChatService.chats[0].messages.size)
    }

    @Test(expected = MessageNotFoundException::class)
    fun deleteDeletedMessageShouldThrow(){
        val myId = 100
        val SashaId = 2
        val firstMsg = Message(SashaId, 1,   myId,"Hi!", true)
        val secondMsg = Message(SashaId, 2,  myId,"HRU?", true)
        ChatService.createMessage(firstMsg)
        ChatService.createMessage(secondMsg)
        ChatService.deleteMessage(SashaId, 2)
        ChatService.deleteMessage(SashaId, 2)
    }

    @Test
    fun messageShouldBeReplacedWhenEdited(){
        val SashaId = 2
        val myId = 100
        val firstMsg = Message(SashaId, 1, myId, "Hi!", true)
        val editedMessage = Message(SashaId, 1,  myId, "Hello", true)
        ChatService.createMessage(firstMsg)
        ChatService.editMessage(editedMessage)
        assertEquals(editedMessage, ChatService.chats[0].messages[0])
    }

    @Test
    fun deleteNonExistingMessage(){
        val SashaId = 2
        val myId = 100
        val firstMsg = Message(SashaId, 1, myId, "Hi!", true)
        val editedMessage = Message(SashaId, 2, myId,"Hello", true)
        ChatService.createMessage(firstMsg)
        val result = ChatService.editMessage(editedMessage)
        assertEquals(false, result)
    }

    @Test
    fun getOnlyUnreadChats(){
        val myId = 100
        val SashaId = 2
        val PashaId = 3
        val msgToSasha = Message(SashaId, 1,   myId,"Hi!", true)
        val msgToPasha = Message(PashaId, 2,  myId,"Hello", true)
        val msgFromPasha = Message(PashaId, 3, PashaId, "Hi, Masha", false)
        with(ChatService){
            createMessage(msgToSasha)
            createMessage(msgToPasha)
            createMessage(msgFromPasha)
        }
        val result = ChatService.getUnreadChatsCount()
        assertEquals(1, result.size)

    }

    @Test
    fun getAllChats(){
        val myId = 100
        val SashaId = 2
        val PashaId = 3
        val msgToSasha = Message(SashaId, 1,   myId,"Hi!", true)
        val msgToPasha = Message(PashaId, 2,  myId,"Hello", true)
        val msgFromPasha = Message(PashaId, 3, PashaId, "Hi, Masha", false)
        with(ChatService){
            createMessage(msgToSasha)
            createMessage(msgToPasha)
            createMessage(msgFromPasha)
        }
        val result = ChatService.getAllChats().size
        assertEquals(2, result)
    }
    @Test
    fun getOnlyLastMessages(){
        val myId = 100
        val SashaId = 2
        val PashaId = 3
        val msgToSasha = Message(SashaId, 1,   myId,"Hi!", true)
        val msgToPasha = Message(PashaId, 2,  myId,"Hello", true)
        val msgFromPasha = Message(PashaId, 3, PashaId, "Hi, Masha", false)
        with(ChatService){
            createMessage(msgToSasha)
            createMessage(msgToPasha)
            createMessage(msgFromPasha)
        }
        assertEquals(2, ChatService.getLastMessages().size)
    }

    @Test
    fun getLastMessagesIfTheyAreNone(){
        val myId = 100
        val SashaId = 2
        val PashaId = 3
        val msgToSasha = Message(SashaId, 1,   myId,"Hi!", true)
         with(ChatService){
            createMessage(msgToSasha)
            deleteMessage(SashaId, msgToSasha.messageId)
        }
        val result = ChatService.getLastMessages()
        assertEquals(true, result.contains(Message(0,0,0,"нет сообщений")))
    }

    @Test
    fun getMessageFrom(){
        val myId = 100
        val SashaId = 2
        val PashaId = 3
        val msgToSasha = Message(SashaId, 1,   myId,"Hi!", true)
        val msgToPasha = Message(PashaId, 2,  myId,"Hello", true)
        val msgFromPasha = Message(PashaId, 3, PashaId, "Hi, Masha", false)
        with(ChatService){
            createMessage(msgToSasha)
            createMessage(msgToPasha)
            createMessage(msgFromPasha)
        }
        val result = ChatService.getMessagesFromId(PashaId).size
        assertEquals(1, result)
    }

    @Test
    fun getThatCountOfMessage(){
        val myId = 100
        val SashaId = 2
        val PashaId = 3
        val msgToSasha = Message(SashaId, 1,   myId,"Hi!", true)
        val msgToPasha = Message(PashaId, 2,  myId,"Hello", true)
        val msgFromPasha = Message(PashaId, 3, PashaId, "Hi, Masha", false)
        with(ChatService){
            createMessage(msgToSasha)
            createMessage(msgToPasha)
            createMessage(msgFromPasha)
        }
        val result = ChatService.getSeveralMessages(PashaId, 1)
        assertEquals(1, result.size)
    }

    @Test
    fun ifGetLastMessagesChatBecomeRead(){
        val myId = 100
        val SashaId = 2
        val PashaId = 3
        val msgToSasha = Message(SashaId, 1,   myId,"Hi!", true)
        val msgToPasha = Message(PashaId, 2,  myId,"Hello", true)
        val msgFromPasha = Message(PashaId, 3, PashaId, "Hi, Masha", false)
        with(ChatService){
            createMessage(msgToSasha)
            createMessage(msgToPasha)
            createMessage(msgFromPasha)
            getSeveralMessages(PashaId, 2)
        }
        val result = ChatService.getUnreadChatsCount().size
        assertEquals(0, result)
    }

}