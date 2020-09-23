package dev.ebullient.gameontext.junkyplace.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.websocket.DecodeException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.logmanager.Level;

import dev.ebullient.gameontext.junkyplace.Log;
import dev.ebullient.gameontext.junkyplace.RoomDescription;

public class Message {

    static final ObjectMapper mapper = new ObjectMapper();

    static String valueToJsonString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            Log.log(Level.ERROR, value, "Unable to convert object to JSON", e);
        }
        return "";
    }

    /**
     * prefix for bookmark: customize it! Just doing something here to make it less
     * likely to collide with other rooms.
     */
    private static String bookmark() {
        return "JunkyPlace-" + bookmark.incrementAndGet();
    }

    /** Incrementing message id for bookmark */
    private static AtomicLong bookmark = new AtomicLong(0);

    /**
     * Create an event targeted at a specific player (still use broadcast to send to
     * all connections)
     *
     * @return constructed message
     */
    public static Message createSpecificEvent(String userid, String messageForUser) {
        // player,<userId>,{
        // "type": "event",
        // "content": {
        // "<userId>": "specific to player"
        // },
        // "bookmark": "String representing last message seen"
        // }
        EventMessage msg = new EventMessage(userid, messageForUser);
        return new Message(Target.player, userid, valueToJsonString(msg));
    }

    /**
     * Construct an event that broadcasts to all players. The first string will be
     * the message sent to all players. Additional messages should be specified in
     * pairs afterwards, "userId1", "player message 1", "userId2", "player message
     * 2". If the optional specified messages are uneven, only the general message
     * will be sent.
     *
     * @return constructed message
     */
    public static Message createBroadcastEvent(String allContent, String... pairs) {
        EventMessage msg = new EventMessage();
        msg.content.put("*", allContent);
        if (pairs != null) {
            if (pairs.length % 2 == 0) {
                for (int i = 0; i < pairs.length; i += 2) {
                    msg.content.put(pairs[i], pairs[i + 1]);
                }
            } else {
                Log.log(Level.WARNING, Message.class,
                        "Programmer error: use one element as user id, and the next as the message: {0}",
                        (Object[]) pairs);
            }
        }
        return new Message(Target.player, ALL, valueToJsonString(msg));
    }

    /**
     * Content is a simple string containing the chat message.
     *
     * @return constructed message
     */
    public static Message createChatMessage(String username, String content) {
        RoomChat msg = new RoomChat(username, content);
        return new Message(Target.player, ALL, valueToJsonString(msg));
    }

    /**
     * Send information about the room to the client. This message is sent after
     * receiving a `roomHello`.
     *
     * @param userId
     * @param roomDescription Room attributes
     * @return constructed message
     * @throws JsonProcessingException
     */
    public static Message createLocationMessage(String userId, RoomDescription roomDescription) {
        return new Message(Target.player, userId, valueToJsonString(roomDescription));
    }

    /**
     * Indicates that a player can leave by the requested exit (`exitId`).
     *
     * @param userId Targeted user
     * @param exitId Direction the user will be exiting (used as a lookup key)
     * @return constructed message
     */
    public static Message createExitMessage(String userId, String exitId) {
        return createExitMessage(userId, exitId, null);
    }

    /**
     * Indicates that a player can leave by the requested exit (`exitId`).
     *
     * @param userId  Targeted user
     * @param exitId  Direction the user will be exiting (used as a lookup key)
     * @param message Message to be displayed when the player leaves the room
     * @return constructed message
     */
    public static Message createExitMessage(String userId, String exitId, String content) {
        if (exitId == null) {
            throw new IllegalArgumentException("exitId is required");
        }
        RoomPlayerLocation msg = new RoomPlayerLocation(exitId, content == null ? "Fare thee well" : content);
        return new Message(Target.playerLocation, userId, valueToJsonString(msg));
    }

    /**
     * Used for test purposes, create a message targeted for the room
     *
     * @param roomId   Id of target room
     * @param userId   Id of user that sent the message
     * @param username username for user that sent the message
     * @param content  content of message
     * @return constructed message
     */
    public static Message createRoomMessage(String roomId, String userId, String username, String content) {
        ClientMessage msg = new ClientMessage(userId, username, content);
        return new Message(Target.room, roomId, valueToJsonString(msg));
    }

    /**
     * Used for test purposes, create a room hello message
     *
     * @param roomId   Id of target room
     * @param userId   Id of user entering the room
     * @param username username for user entering the room
     * @param version  version negotiated with mediator
     * @return constructed message
     */
    public static Message createRoomHello(String roomId, String userId, String username, long version) {
        MediatorRoomHello roomHello = new MediatorRoomHello(true, userId, username, version);
        return new Message(Target.roomHello, roomId, valueToJsonString(roomHello));
    }

    /**
     * Used for test purposes, create a room hello message
     *
     * @param roomId   Id of target room
     * @param userId   Id of user entering the room
     * @param username username for user entering the room
     * @param version  version negotiated with mediator
     * @return constructed message
     */
    public static Message createRoomGoodbye(String roomId, String userId, String username) {
        MediatorRoomGoodbye roomGoodbye = new MediatorRoomGoodbye(true, userId, username);
        return new Message(Target.roomGoodbye, roomId, valueToJsonString(roomGoodbye));
    }

    /**
     * Used for test purposes, create a room hello message
     *
     * @param roomId   Id of target room
     * @param userId   Id of user entering the room
     * @param username username for user entering the room
     * @param version  version negotiated with mediator
     * @return constructed message
     */
    public static Message createRoomJoin(String roomId, String userId, String username, long version) {
        MediatorRoomHello roomJoin = new MediatorRoomHello(false, username, userId, version);
        return new Message(Target.roomJoin, roomId, valueToJsonString(roomJoin));
    }

    /**
     * Used for test purposes, create a room hello message
     *
     * @param roomId   Id of target room
     * @param userId   Id of user entering the room
     * @param username username for user entering the room
     * @param version  version negotiated with mediator
     * @return constructed message
     */
    public static Message createRoomPart(String roomId, String userId, String username) {
        MediatorRoomGoodbye roomPart = new MediatorRoomGoodbye(false, userId, username);
        return new Message(Target.roomPart, roomId, valueToJsonString(roomPart));
    }

    /**
     * Target for the message
     *
     * @see Target
     */
    private final Target target;

    /**
     * Target for the message (This room, specific player, or '*')
     */
    private final String targetId;

    /**
     * Stringified JSON payload
     */
    private final String payload;

    /**
     * Parse a string read from the WebSocket, and convert it into a message
     *
     * @param s String read from WebSocket
     * @throws DecodeException
     * @see MessageDecoder#decode(String)
     */
    public Message(String s) throws DecodeException {
        // this is getting parsed in a low-level/raw way.
        // We don't split on commas arbitrarily: there are commas in the
        // json payload, which means unnecessary splitting and joining.
        List<String> list = new ArrayList<>(3);

        int brace = s.indexOf('{'); // first brace
        int i = 0;
        int j = s.indexOf(',');
        while (j > 0 && j < brace) {
            list.add(s.substring(i, j).trim());
            i = j + 1;
            j = s.indexOf(',', i);
        }

        if (list.isEmpty()) {
            throw new DecodeException(s, "Badly formatted payload, unable to target and targetId: \"" + s + "\"");
        }

        // stash all of the rest in the data field.
        this.payload = s.substring(i).trim();

        // The flowTarget is always present.
        // The destination may or may not be present, but shouldn't return null.
        this.target = Target.valueOf(list.get(0));
        this.targetId = list.size() > 1 ? list.get(1) : "";
    }

    /**
     * Construct a new outbound message
     *
     * @param target   General target for the message
     * @param targetId Specific player id, '*', or null (for
     * @param payload
     */
    private Message(Target target, String targetId, String payload) {
        this.target = target;
        this.targetId = targetId == null ? "" : targetId;
        this.payload = payload;
    }

    /**
     * @return message's target,specifically either:
     *         <ul>
     *         <li>{@link Target#room}</li>
     *         <li>{@link Target#roomHello}</li>
     *         <li>{@link Target#roomGoodbye}</li>
     *         <li>{@link Target#roomJoin}</li>
     *         <li>{@link Target#roomPart}</li>
     *         </ul>
     */
    public Target getTarget() {
        return target;
    }

    /**
     * @return message target id, should always be the room id.
     */
    public String getTargetId() {
        return targetId;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParsedBody() {
        try {
            switch (target) {
                case room:
                    return (T) mapper.readValue(payload, ClientMessage.class);
                case roomHello:
                    return (T) mapper.readValue(payload, MediatorRoomHello.class);
                case roomJoin:
                    return (T) mapper.readValue(payload, MediatorRoomHello.class);
                case roomGoodbye:
                    return (T) mapper.readValue(payload, MediatorRoomGoodbye.class);
                case roomPart:
                    return (T) mapper.readValue(payload, MediatorRoomGoodbye.class);
                default:
                    return null;
            }
        } catch (ClassCastException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert message to a string for use as an outbound message over the WebSocket
     *
     * @see MessageEncoder#encode(Message)
     */
    public String encode() {
        StringBuilder result = new StringBuilder();
        result.append(target).append(',');

        if (!targetId.isEmpty()) {
            result.append(targetId).append(',');
        }

        result.append(payload);

        return result.toString();
    }

    @Override
    public String toString() {
        return encode();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + payload.hashCode();
        result = prime * result + target.hashCode();
        result = prime * result + targetId.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        // Private constructor, none of these are ever null.
        Message other = (Message) obj;

        // Private constructor, none of these are ever null.
        return payload.equals(other.payload)
                && target.equals(other.target)
                && targetId.equals(other.targetId);
    }

    /**
     * The first segment in the WebSocket protocol for Game On! This is used as a
     * primitive routing filter as messages flow through the system
     * (String.startsWith... )
     */
    public enum Target {
        /** Protocol acknowledgement, sent onOpen */
        ack,
        /** Message sent to player(s) */
        player,
        /**
         * Message sent to a specific player to trigger a location change (they are
         * allowed to exit the room)
         */
        playerLocation,
        /** Message sent to the room */
        room,
        /** A player enters the room */
        roomHello,
        /** A player reconnects to the room (e.g. reconnected session) */
        roomJoin,
        /** A player's has disconnected from the room without leaving it */
        roomPart,
        /** A player leaves the room */
        roomGoodbye
    };

    /**
     * Ack message: this supports both version 1 & 2 {@code ack,{\"version\":[1,2]}}
     */
    public static final Message ACK_MSG = new Message(Target.ack, "", "{\"version\":[1,2]}");


    /** Messages sent to everyone */
    private static final String ALL = "*";

    // Room -> Client: Event message
    // player,*,{
    //   "type": "event",
    //   "content": {
    //     "*": "general text for everyone",
    //     "<userId>": "specific to player"
    //   },
    //   "bookmark": "String representing last message seen"
    // }
    public static class EventMessage {
        public String type = "event";
        public Map<String, String> content = new HashMap<>();
        public String bookmark = bookmark();

        EventMessage() {}

        EventMessage(String userid, String message) {
            content.put(userid, message);
        }
    }

    // Room -> Mediator -> Client: Chat message
    // player,*,{
    //   "type": "chat",
    //   "username": "username",
    //   "content": "<message>",
    //   "bookmark": "String representing last message seen"
    // }
    public static class RoomChat {
        public String type = "chat";
        public String username;
        public String content;
        public String bookmark = bookmark();

        RoomChat() {}

        RoomChat(String username, String message) {
            this.username = username;
            this.content = message;
        }
    }

    // Room -> Mediator -> Client: Player location message
    //  playerLocation,<userId>,{
    //      "type": "exit",
    //      "content": "You exit through door xyz... ",
    //      "exitId": "N"
    //  }
    public static class RoomPlayerLocation {
        public String type = "exit";
        public String content;
        public String exitId;

        RoomPlayerLocation() {}

        RoomPlayerLocation(String exitId, String message) {
            this.exitId = exitId;
            this.content = message;
        }
    }

    // Client -> Mediator -> Room: chat/command message
    //  room,<roomId>,{
    //      "username": "username",
    //      "userId": "<userId>"
    //      "content": "<message>"
    //  }
    public static class ClientMessage {
        public String type = "room";
        public String username;
        public String userId;
        public String content;

        ClientMessage() {}

        ClientMessage(String userId, String username, String content) {
            this.userId = userId;
            this.username = username;
            this.content = content;
        }
    }

    // Mediator -> Room: Room Hello or Room Join
    //  roomHello,<roomId>,{
    //      "username": "username",
    //      "userId": "<userId>",
    //      "version": 1|2
    //  }
    //  roomJoin,<roomId>,{
    //      "username": "username",
    //      "userId": "<userId>",
    //      "version": 2
    //  }
    public static class MediatorRoomHello {
        public String type = "roomHello";
        public String username;
        public String userId;
        public long version = 2;

        MediatorRoomHello() {}

        MediatorRoomHello(boolean isHello, String userId, String username, long version) {
            this.type = isHello ? "roomHello" : "roomJoin";
            this.userId = userId;
            this.username = username;
            this.version = isHello ? version : 2;
        }
    }

    // Mediator -> Room: Room Goodbye or Room Part
    //  roomGoodbye,<roomId>,{
    //      "username": "username",
    //      "userId": "<userId>"
    //  }
    //  roomPart,<roomId>,{
    //      "username": "username",
    //      "userId": "<userId>"
    //  }
    public static class MediatorRoomGoodbye {
        public String type = "roomGoodbye";
        public String username;
        public String userId;

        MediatorRoomGoodbye() {}

        MediatorRoomGoodbye(boolean isGoodbye, String username, String userId) {
            this.type = isGoodbye ? "roomGoodbye" : "roomPart";
            this.userId = userId;
            this.username = username;
        }
    }
}
