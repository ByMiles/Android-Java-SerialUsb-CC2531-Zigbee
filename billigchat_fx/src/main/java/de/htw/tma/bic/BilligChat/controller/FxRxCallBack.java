package de.htw.tma.bic.BilligChat.controller;

import de.htw.tma.bic.BilligChat.model.FxResponse;
import de.htw.tma.bic.ZnpService.callBack.RxCallBack;
import de.htw.tma.bic.ZnpService.response.ResponseHeader;
import de.htw.tma.bic.ZnpService.view.View;
import de.htw.tma.bic.ip16NameResolver.Ip16NameResolver;
import javafx.animation.AnimationTimer;

import java.util.Arrays;

import static de.htw.tma.bic.ZnpService.service.ZnpCodes.*;

/**
 * This class is an javafx-compatible implementation of the RxCallBack-interface.
 * Cause javafx is single threaded instead of a thread an animation timer is used
 * to inject the received data into the view.
 * For Android this class needs to be replaced with an equivalent implementation
 * but with an Android compatible thread.
 *
 * @version 2.0
 * @author Miles Lorenz
 */
public class FxRxCallBack implements RxCallBack {

    private View view;

    FxRxCallBack(View view) {
        this.view = view;
    }

    @Override
    public void processRxCallBack(byte[] messageBody, ResponseHeader header) {

        new AnimationTimer() {
            byte[] message = messageBody;

            @Override
            public void handle(long now) {

                if (header.getCmd0() ==(byte) bcResp)
                    handleBcResponse(message, header);
                else
                    view.appendSystemResponse(new FxResponse(header, message));

                this.stop();
            }}.start();
    }

    private void handleBcResponse(byte[] message, ResponseHeader header) {
        if (header.getCmd1() == bc_receiveMSG || header.getCmd1() == bc_sendToAll)
            handleGlobalMessage(message, header);
        else if (header.getCmd1() == bc_info)
            view.updateActionBar(message, header);
        else if (header.getCmd1() == bc_sendToOne)
            handlePersonalMessage(message, header);
    }

    private void handleGlobalMessage(byte[] message, ResponseHeader header) {
        if (toWords(message).startsWith("<") && toWords(message).endsWith(">")) {
            message = Ip16NameResolver.service().addNameToMap(header.getIp16(), toWords(Arrays.copyOfRange(message, 1, message.length - 1)));
            view.updateNames();
        }
        view.appendChatResponse(new FxResponse(header, message));
    }

    private void handlePersonalMessage(byte[] message, ResponseHeader header) {
        byte[] ip16 = Arrays.copyOfRange(message, 0, 2);
        message = Arrays.copyOfRange(message, 2, message.length);
        view.appendChatResponse(new FxResponse(header, ip16, message));
    }
}
