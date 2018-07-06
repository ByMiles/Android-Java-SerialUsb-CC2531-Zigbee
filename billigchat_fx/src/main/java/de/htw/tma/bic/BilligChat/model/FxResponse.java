package de.htw.tma.bic.BilligChat.model;

import de.htw.tma.bic.ZnpService.response.AbstractResponse;
import de.htw.tma.bic.ZnpService.response.ResponseHeader;
import de.htw.tma.bic.ip16NameResolver.Ip16NameResolver;

import static de.htw.tma.bic.ZnpService.service.ZnpCodes.toHex;

/**
 * The Response stores the header and the message body (as message).
 * The message can be requested as hex or as byte[].
 *
 * @version 2.0
 * @author Miles Lorenz
 */
public class FxResponse extends AbstractResponse {

    public FxResponse(ResponseHeader header, byte[] message) {
        super(header, message);
    }

    public FxResponse(ResponseHeader header, byte[] ip16, byte[] message) {
        super(header, ip16, message);
    }

    protected byte[] createPrivateMessage(byte[] ip16, byte[] message) {
        ip16 = ("to "+ Ip16NameResolver.service().resolveIp(toHex(ip16)) + ": ").getBytes();
        byte[] privateMessage = new byte[ip16.length + message.length];

        int index = 0;
        for (byte b:ip16)
            privateMessage[index++] = b;
        for (byte b:message)
            privateMessage[index++] = b;
        return privateMessage;
    }
}
