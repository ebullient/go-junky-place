package dev.ebullient.gameontext.junkyplace.protocol;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * All magic to convert the on-the-wire protocol for messages
 * is contained in the {@link Message} class
 */
public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public void init(EndpointConfig config) {
        // no set-up
    }

    @Override
    public void destroy() {
        // no tear-down
    }

    @Override
    public Message decode(String s) throws DecodeException {
        return new Message(s);
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

}
