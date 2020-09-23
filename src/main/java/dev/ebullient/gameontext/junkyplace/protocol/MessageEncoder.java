package dev.ebullient.gameontext.junkyplace.protocol;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * All magic to convert the on-the-wire protocol for messages
 * is contained in the {@link Message} class
 */
public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public void init(EndpointConfig config) {
        // no set-up
    }

    @Override
    public void destroy() {
        // no tear-down
    }

    @Override
    public String encode(Message msg) throws EncodeException {
        return msg.encode();
    }
}
