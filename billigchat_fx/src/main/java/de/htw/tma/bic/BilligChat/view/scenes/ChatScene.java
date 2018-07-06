package de.htw.tma.bic.BilligChat.view.scenes;

import de.htw.tma.bic.BilligChat.view.elements.ActionBar;
import de.htw.tma.bic.BilligChat.view.elements.Display;
import de.htw.tma.bic.BilligChat.view.elements.SendBar;
import de.htw.tma.bic.ZnpService.response.Response;
import de.htw.tma.bic.ZnpService.response.ResponseHeader;
import de.htw.tma.bic.ZnpService.service.ZnpCodes;
import de.htw.tma.bic.ZnpService.view.View;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class ChatScene implements View {

    private ActionBar actionBar;
    private BorderPane root;
    private Display display;
    private SendBar sendBar;

    public ChatScene(){
        this.actionBar = new ActionBar();
        this.display = new Display();
        this.sendBar = new SendBar();

        root = new BorderPane();
        root.setCenter(display);
        root.setRight(actionBar);
        root.setBottom(sendBar);
    }

    public Scene initChat(double width, double height) {

        return new Scene(root, width, height);
    }

    @Override
    public void appendChatResponse(Response response) {
        display.getChatDisplay().appendInfo(response);
    }

    public void appendSystemResponse(Response response) {
        display.getSystemDisplay().appendInfo(response);
    }

    @Override
    public void updateActionBar(byte[] message, ResponseHeader header) {

        boolean isNodeOnANetwork = (message[0] == 1);
        ZnpCodes.DevStates devState = ZnpCodes.DevStates.values()[message[1]];
        ZnpCodes.CommissioningModes mode = ZnpCodes.CommissioningModes.resolve(message[2]);

        actionBar.setIp16(header.getIp16());
        actionBar.setChannel(header.getChannel());
        actionBar.setStatus(isNodeOnANetwork, devState, mode);
    }

    @Override
    public void setInfoText(String usbInfo) {
        actionBar.setInfoText(usbInfo);
    }

    @Override
    public void updateNames() {
        updateDestinationBox();
        updateNameBox();
    }



    private void updateNameBox() {
        display.getCommandDisplay().updateNameBox();
    }

    private void updateDestinationBox() {
        sendBar.updateDestinationBox();
    }
}
