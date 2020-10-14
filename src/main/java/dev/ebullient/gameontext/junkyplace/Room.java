package dev.ebullient.gameontext.junkyplace;

import java.util.Locale;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

import dev.ebullient.gameontext.junkyplace.protocol.Message;
import dev.ebullient.gameontext.junkyplace.protocol.RoomEndpoint;

/**
 * Here is where your room implementation lives.
 *
 * <p>
 * A JAX-RS application is defined in {@link RestApplication}.
 * <p>
 * The WebSocket endpoint is defined in {@link RoomEndpoint}, with
 * {@link Message} as the text-based payload being sent on the wire.
 * <p>
 * This is an ApplicationScoped CDI bean, which means it will be started when
 * the server/application starts, and stopped when it stops.
 */
@ApplicationScoped
public class Room {
    /** The id of the room: you can retrieve this from the room editing view in the UI */
    public static final String LOOK_UNKNOWN = "It doesn't look interesting";
    public static final String UNKNOWN_COMMAND = "This room is a basic model. It doesn't understand `%s`";
    public static final String UNSPECIFIED_DIRECTION = "You didn't say which way you wanted to go.";
    public static final String UNKNOWN_DIRECTION = "There isn't a door in that direction (%s)";
    public static final String GO_FORTH = "You head %s";
    public static final String HELLO_ALL = "%s is here";
    public static final String HELLO_USER = "Welcome!";
    public static final String GOODBYE_ALL = "%s has gone";
    public static final String GOODBYE_USER = "Bye!";

    protected RoomDescription roomDescription = new RoomDescription();

    @PostConstruct
    protected void postConstruct() {
        Log.log(Level.INFO, this, "Room initialized: {0}", roomDescription);
    }

    @PreDestroy
    protected void preDestroy() {
        Log.log(Level.FINE, this, "Room to be destroyed");
    }

    public void handleMessage(Session session, Message message, RoomEndpoint endpoint) {

        // Who doesn't love switch on strings in Java 8?
        switch (message.getTarget()) {
            case roomHello:
                Message.MediatorRoomHello helloMsg = message.getParsedBody();

                // Send location message
                endpoint.sendMessage(session, Message.createLocationMessage(helloMsg.userId, roomDescription));

                // Say hello to a new person in the room
                endpoint.sendMessage(session,
                        Message.createBroadcastEvent(String.format(HELLO_ALL, helloMsg.username), helloMsg.userId, HELLO_USER));
                break;

            case roomJoin:
                Message.MediatorRoomHello joinMsg = message.getParsedBody();

                // Send location message
                endpoint.sendMessage(session, Message.createLocationMessage(joinMsg.userId, roomDescription));
                break;

            case roomGoodbye:
                Message.MediatorRoomGoodbye goodbyeMsg = message.getParsedBody();

                // Say goodbye to person leaving the room
                endpoint.sendMessage(session,
                        Message.createBroadcastEvent(String.format(GOODBYE_ALL, goodbyeMsg.username), goodbyeMsg.userId,
                                GOODBYE_USER));

                break;

            case room:
                Message.ClientMessage clientMsg = message.getParsedBody();
                if (clientMsg.content.charAt(0) == '/') {
                    processCommand(clientMsg.userId, clientMsg.username, clientMsg.content, endpoint, session);
                } else {
                    endpoint.sendMessage(session,
                            Message.createChatMessage(clientMsg.username, clientMsg.content));
                }
                break;

            default:
                // discard
                break;
        }
    }

    private void processCommand(String userId, String username, String content, RoomEndpoint endpoint, Session session) {
        // Work mostly off of lower case.
        String contentToLower = content.toLowerCase(Locale.ENGLISH).trim();

        String firstWord;
        String remainder;

        int firstSpace = contentToLower.indexOf(' '); // find the first space
        if (firstSpace < 0 || contentToLower.length() <= firstSpace) {
            firstWord = contentToLower;
            remainder = null;
        } else {
            firstWord = contentToLower.substring(0, firstSpace);
            remainder = contentToLower.substring(firstSpace + 1);
        }

        switch (firstWord) {
            case "/go":
                // See RoomCommandsTest#testHandle*Go*
                // Always process the /go command.
                String exitId = getExitId(remainder);

                if (exitId == null) {
                    // Send error only to source session
                    if (remainder == null) {
                        endpoint.sendMessage(session,
                                Message.createSpecificEvent(userId, UNSPECIFIED_DIRECTION));
                    } else {
                        endpoint.sendMessage(session,
                                Message.createSpecificEvent(userId, String.format(UNKNOWN_DIRECTION, remainder)));
                    }
                } else {
                    // Allow the exit
                    endpoint.sendMessage(session,
                            Message.createExitMessage(userId, exitId, String.format(GO_FORTH, prettyDirection(exitId))));
                }
                break;

            case "/look":
            case "/examine":
                // See RoomCommandsTest#testHandle*Look*
                if (remainder == null || remainder.contains("room")) {
                    // This is looking at or examining the entire room. Send the player location message,
                    // which includes the room description and inventory
                    endpoint.sendMessage(session, Message.createLocationMessage(userId, roomDescription));

                } else if (remainder.contains("moon diagram")) {
                    endpoint.sendMessage(session,
                            Message.createBroadcastEvent(
                                    username + " picks up the moon diagram and looks at it fondly before dropping it again",
                                    userId, "You pick it up, read it, and love it for no reason. You put it down."));

                } else if (remainder.contains("mud")) {
                    endpoint.sendMessage(session,
                            Message.createBroadcastEvent(username + " is disgusted by mud on the floor",
                                    userId, "It looks awful. You look away."));

                } else if (remainder.contains("teddy")) {
                    endpoint.sendMessage(session,
                            Message.createBroadcastEvent("The teddy bear burps, 'Hello'"));

                } else if (remainder.contains("books")) {
                    endpoint.sendMessage(session,
                            Message.createBroadcastEvent(username + " is confused by the bookshelf",
                                    userId, "It's a bit odd"));

                } else {
                    endpoint.sendMessage(session, Message.createSpecificEvent(userId, LOOK_UNKNOWN));
                }
                break;

            case "/use":
                // Custom command!

                if (remainder != null) {
                    if (remainder.contains("teddy")) {
                        endpoint.sendMessage(session,
                                Message.createBroadcastEvent(
                                        "The teddy bear squeaks! " + username
                                                + " looks around sheepishly, and sets the teddy back down.",
                                        userId,
                                        "You pick up the teddy and put it in your mouth. It squeaks!! You quickly put it back down."));
                        break;
                    } else if (remainder.contains("mud")) {
                        endpoint.sendMessage(session,
                                Message.createBroadcastEvent(username + " has very dirty hands.",
                                        userId,
                                        "You pat the big pile of mud. It's very sticky, and now it's all over your hands!"));
                        break;
                    } else if (remainder.contains("moon diagram")) {
                        endpoint.sendMessage(session,
                                Message.createBroadcastEvent(username
                                        + " picks up the moon diagram, and scrunches it into a ball! After a brief moment, "
                                        + username
                                        + " smiles, smoothes it out again, and lets the diagram float back to the floor",
                                        userId,
                                        "You grab the moon diagram and crumple it into a ball. Hey! That looks like a moon! How satisfying! You unfold it, and let it go."));
                        break;
                    } else if (remainder.contains("book")) {
                        endpoint.sendMessage(session,
                                Message.createExitMessage(userId, "w",
                                        "You take a book down from the shelf, but it vanishes in your hand. Hey.. what? .. You're going west!"));
                        break;
                    }
                }
                endpoint.sendMessage(session, Message.createSpecificEvent(userId, "You have no idea how to use that"));

                break;

            case "/about":

                break;

            default:
                endpoint.sendMessage(session,
                        Message.createSpecificEvent(userId, String.format(UNKNOWN_COMMAND, content)));
                break;
        }
    }

    /**
     * Given a lower case string describing the direction someone wants
     * to go (/go N, or /go North), filter or transform that into a recognizable
     * id that can be used as an index into a known list of exits. Always valid
     * are n, s, e, w. If the string doesn't match a known exit direction,
     * return null.
     *
     * @param lowerDirection String read from the provided message
     * @return exit id or null
     */
    protected String getExitId(String lowerDirection) {
        if (lowerDirection == null) {
            return null;
        }

        switch (lowerDirection) {
            case "north":
            case "south":
            case "east":
            case "west":
                return lowerDirection.substring(0, 1);

            case "n":
            case "s":
            case "e":
            case "w":
                // Assume N/S/E/W are managed by the map service.
                return lowerDirection;

            default:
                // Otherwise unknown direction
                return null;
        }
    }

    /**
     * From the direction we used as a key
     *
     * @param exitId The exitId in lower case
     * @return A pretty version of the direction for use in the exit message.
     */
    protected String prettyDirection(String exitId) {
        switch (exitId) {
            case "n":
                return "north";
            case "s":
                return "south";
            case "e":
                return "east";
            case "w":
                return "west";

            default:
                return exitId;
        }
    }
}
