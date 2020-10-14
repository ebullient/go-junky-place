package dev.ebullient.gameontext.junkyplace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class RoomDescription {

    // Room -> Mediator -> Client: Location message
    //  player,<userId>,{
    //      "type": "location",
    //      "name": "Room name",
    //      "fullName": "Room's descriptive full name",
    //      "description", "Lots of text about what the room looks like",
    //      "commands": {
    //          "/custom" : "Description of what command does"
    //      },
    //      "roomInventory": ["itemA","itemB"]
    //  }

    final String type = "location";

    String name = "junkyPlace";
    String fullName = "The Junky Place";
    String description = "This room is very old. There are socks on the ceiling and dust all over the floor, or is it dust on the ceiling and socks all over the floor?";

    private Map<String, String> commands = new HashMap<>();

    private Set<String> roomInventory = new HashSet<>();

    public RoomDescription() {
        commands.put("/use",
                "Take, hold, or deploy (something) as a means of accomplishing or achieving something");

        roomInventory.add("red teddy bear");
        roomInventory.add("big pile of mud");
        roomInventory.add("moon diagram");
        roomInventory.add("bookshelf");
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("name=").append(name);
        s.append(", fullName=").append(fullName);
        s.append(", description=").append(description);
        s.append(", commands=").append(commands);
        s.append(", items=").append(roomInventory);

        return s.toString();
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, String> commands) {
        this.commands = commands;
    }

    public Set<String> getRoomInventory() {
        return roomInventory;
    }

    public void setRoomInventory(Set<String> roomInventory) {
        this.roomInventory = roomInventory;
    }
}
