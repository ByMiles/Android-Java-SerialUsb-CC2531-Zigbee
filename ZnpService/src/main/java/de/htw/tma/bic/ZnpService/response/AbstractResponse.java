package de.htw.tma.bic.ZnpService.response;

import static de.htw.tma.bic.ZnpService.service.ZnpCodes.toHex;
import static de.htw.tma.bic.ZnpService.service.ZnpCodes.toWords;

/**
 * The AbstractResponse stores the header and the message body (as message).
 * The message can be requested as hex or as byte[].
 *
 * @version 2.0
 * @author Miles Lorenz
 */
public abstract class AbstractResponse implements Response {
    protected ResponseHeader header;
    protected byte[] message;

    public AbstractResponse(ResponseHeader header, byte[] message) {
        this.header = header;
        this.message = message;
    }

    public AbstractResponse(ResponseHeader header, byte[] ip16, byte[] message) {
        this.header = header;
        this.message = createPrivateMessage(ip16, message);
    }

    protected abstract byte[] createPrivateMessage(byte[] ip16, byte[] message);

    @Override
    public ResponseHeader getHeader() {
        return header;
    }

    @Override
    public String getMessageAsString() {
        return toWords(message);
    }

    @Override
    public String getMessageAsHex() {
        return toHex(message);
    }
}
