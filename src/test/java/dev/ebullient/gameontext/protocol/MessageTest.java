package dev.ebullient.gameontext.protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.ebullient.gameontext.junkyplace.RoomDescription;
import dev.ebullient.gameontext.junkyplace.protocol.Message;

public class MessageTest {

    @Test
    public void testCreateSpecificEventMessage() throws Exception {
        Message m1 = Message.createSpecificEvent("user1", "Message for user1");
        String s = m1.encode();

        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,user1,{\"type\":\"event\",\"content\":{\"user1\""), s);
        Assertions.assertFalse(s.contains("*"), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreateBroadcastEventMessage() throws Exception {
        Message m1 = Message.createBroadcastEvent("EVERYTHING", "user1", "Message for user1");
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,*,{\"type\":\"event\",\"content\":{"), s);
        Assertions.assertTrue(s.contains("\"*\":\"EVERYTHING\""), s);
        Assertions.assertTrue(s.contains("\"user1\":\"Message for user1\""), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreateBroadcastEventMessageGeneralOnly() throws Exception {
        Message m1 = Message.createBroadcastEvent("EVERYTHING");
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,*,{\"type\":\"event\",\"content\":{"), s);
        Assertions.assertTrue(s.contains("\"*\":\"EVERYTHING\""), s);
        Assertions.assertFalse(s.contains("\"user1\":\"Message for user1\""), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreateBroadcastEventMessageMismatch() throws Exception {
        Message m1 = Message.createBroadcastEvent("EVERYTHING", "user1");
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,*,{\"type\":\"event\",\"content\":{"), s);
        Assertions.assertTrue(s.contains("\"*\":\"EVERYTHING\""), s);
        Assertions.assertFalse(s.contains("\"user1\""), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreateChatMessage() throws Exception {
        Message m1 = Message.createChatMessage("userName", "Message from userName");
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,*,{\"type\":\"chat\""), s);
        Assertions.assertTrue(s.contains("\"content\":\"Message from userName\""), s);
        Assertions.assertTrue(s.contains("\"username\":\"userName\""), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreateLocationMessageMinimal() throws Exception {
        RoomDescription roomDescription = new RoomDescription();

        Message m1 = Message.createLocationMessage("user1", roomDescription);
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,user1,{\"type\":\"location\""), s);
        Assertions.assertTrue(s.contains("\"name\":\"" + roomDescription.getName()+ "\""), s);
        Assertions.assertTrue(s.contains("\"fullName\":\"" + roomDescription.getFullName() + "\""), s);
        Assertions.assertTrue(s.contains("\"description\":\"" + roomDescription.getDescription() + "\""), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreateLocationMessageFailNullName() throws Exception {
        RoomDescription data = new RoomDescription();
        data.setName(null);
        data.setFullName("b");
        data.setDescription("c");

        Message m1 = Message.createLocationMessage("user1", data);
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,user1,{\"type\":\"location\""), s);
        Assertions.assertNotNull(s, "name should not be null");
        Assertions.assertTrue(s.contains("\"fullName\":\"b\""), s);
        Assertions.assertTrue(s.contains("\"description\":\"c\""), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreateLocationMessageFailNullFullName() throws Exception {
        RoomDescription data = new RoomDescription();
        data.setName("a");
        data.setFullName(null);
        data.setDescription("c");

        Message m1 = Message.createLocationMessage("user1", data);
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,user1,{\"type\":\"location\""), s);
        Assertions.assertTrue(s.contains("\"name\":\"a\""), s);
        Assertions.assertNotNull(s, "Full name should not be null");
        Assertions.assertTrue(s.contains("\"description\":\"c\""), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreateLocationMessageFailNullDescription() throws Exception {
        RoomDescription data = new RoomDescription();
        data.setName("a");
        data.setFullName("b");
        data.setDescription(null);

        Message m1 = Message.createLocationMessage("user1", data);
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,user1,{\"type\":\"location\""), s);
        Assertions.assertTrue(s.contains("\"name\":\"a\""), s);
        Assertions.assertTrue(s.contains("\"fullName\":\"b\""), s);
        Assertions.assertNotNull(s, "Description should not be null");

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreateLocationMessageCommands() throws Exception {
        RoomDescription roomDescription = new RoomDescription();

        Message m1 = Message.createLocationMessage("user1", roomDescription);
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("player,user1,{\"type\":\"location\""));
        Assertions.assertTrue(s.contains("\"name\":\"" + roomDescription.getName() + "\""));
        Assertions.assertTrue(s.contains("\"fullName\":\"" + roomDescription.getFullName() + "\""));
        Assertions.assertTrue(s.contains("\"description\":\"" + roomDescription.getDescription() + "\""));
        Assertions.assertTrue(s.contains("\"commands\":"));
        Assertions.assertTrue(s.contains("\"roomInventory\":"), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreatePlayerLocationMessage() throws Exception {
        Message m1 = Message.createExitMessage("user1", "N", "So long, and thanks for all the fish");
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("playerLocation,user1,{\"type\":\"exit\""), s);
        Assertions.assertTrue(s.contains("\"content\":\"So long, and thanks for all the fish\""), s);
        Assertions.assertTrue(s.contains("\"exitId\":\"N\""), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }

    @Test
    public void testCreatePlayerLocationMessageNullMessage() throws Exception {
        Message m1 = Message.createExitMessage("user1", "N", null);
        String s = m1.encode();
        System.out.println(s);

        Assertions.assertTrue(s.startsWith("playerLocation,user1,{\"type\":\"exit\""), s);
        Assertions.assertTrue(s.contains("\"content\":\"Fare thee well\""), s);
        Assertions.assertTrue(s.contains("\"exitId\":\"N\""), s);

        Message m2 = new Message(s);
        Assertions.assertEquals(m1, m2);
    }
}
