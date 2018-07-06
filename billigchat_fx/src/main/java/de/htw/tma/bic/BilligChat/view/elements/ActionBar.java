package de.htw.tma.bic.BilligChat.view.elements;

import de.htw.tma.bic.BilligChat.controller.FxEvents;
import de.htw.tma.bic.ZnpService.eventHandler.InfoEventHandler;
import de.htw.tma.bic.ZnpService.service.ZnpCodes;
import de.htw.tma.bic.ZnpService.service.ZnpService;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ActionBar extends VBox implements InfoEventHandler {

    private Button networkButton, resetButton, startCoordinatorButton, startRouterButton, infoButton;
    private ComboBox<ZnpCodes.Channels> channelBox;

    public ActionBar() {
        initActionBar();
    }

    private void initActionBar() {

        this.getStyleClass().add("actionBar");

        networkButton = new Button("noNetwork");
        networkButton.getStyleClass().add("networkOffline");
        networkButton.setOnAction(FxEvents.networkEvent());
        resetButton = new Button("reset");
        resetButton.setOnAction(FxEvents.resetEvent());

        startCoordinatorButton = new Button("Start\nCoordinator");
        startCoordinatorButton.setOnAction(FxEvents.startCoordinatorEvent());

        startRouterButton = new Button("Start\nRouter");
        startRouterButton.setOnAction(FxEvents.startRouterEvent());

        channelBox = new ComboBox<>();
        channelBox.getItems().addAll(ZnpCodes.Channels.values());
        channelBox.valueProperty().addListener(FxEvents.changeChannelListener());
        channelBox.getStyleClass().add("button");


        infoButton = new Button();
        VBox.setVgrow(infoButton, Priority.ALWAYS);
        infoButton.setOnAction(FxEvents.infoEvent(this));
        this.getChildren().addAll(
                networkButton,
                resetButton,
                startCoordinatorButton,
                startRouterButton,
                channelBox,
                infoButton
        );
    }

    public void setInfoText(String usbInfo) {
        infoButton.setText(usbInfo);
    }

    public void setIp16(String ip16) {
        networkButton.setText(ip16);
    }

    public void setChannel(byte channel) {
        ZnpService.service().setUpdatedChannel(true);
        channelBox.setValue(ZnpCodes.Channels.resolve(channel));
    }

    public void setStatus(boolean isNodeOnANetwork, ZnpCodes.DevStates devState, ZnpCodes.CommissioningModes mode) {
        updateNetworkButtonColor(isNodeOnANetwork);
        updateDevState(devState);
    }

    private void updateDevState(ZnpCodes.DevStates devState) {
        startCoordinatorButton.getStyleClass().remove("active");
        startRouterButton.getStyleClass().remove("active");

        if (devState == ZnpCodes.DevStates.DEV_ZB_COORD)
            startCoordinatorButton.getStyleClass().add("active");
        else if (devState == ZnpCodes.DevStates.DEV_ROUTER)
            startRouterButton.getStyleClass().add("active");
    }

    private void updateNetworkButtonColor(boolean isNodeOnANetwork) {

        networkButton.getStyleClass().remove(networkButton.getStyleClass().size() - 1);
        String style = (isNodeOnANetwork) ? "networkOnline" : "networkOffline";
        networkButton.getStyleClass().add(style);
    }
}
