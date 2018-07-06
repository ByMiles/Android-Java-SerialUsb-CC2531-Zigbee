package de.htw.tma.bic.BilligChat.view.elements;

import de.htw.tma.bic.BilligChat.controller.FxEvents;
import de.htw.tma.bic.ZnpService.eventHandler.SendMessageEventHandler;
import de.htw.tma.bic.ip16NameResolver.Ip16NameResolver;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import static de.htw.tma.bic.ZnpService.service.ZnpCodes.MAX_MESSAGE_SIZE;


public class SendBar extends HBox implements SendMessageEventHandler {

    private TextField textField;
    private ComboBox<String> destinationBox;
    private Button sendButton;
    private Ip16NameResolver nameResolver;

    public SendBar() {
        super(10);
        initSendBar();
    }

    private void initSendBar() {

        this.getStyleClass().add("sendBar");
        nameResolver = Ip16NameResolver.service();

        this.textField = new TextField();
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= MAX_MESSAGE_SIZE ? change : null));
        textField.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                sendButton.fire();
            }
        });

        destinationBox = new ComboBox<>();
        destinationBox.getStyleClass().add("button");
        destinationBox.getItems().addAll(nameResolver.getNames());
        destinationBox.setValue(destinationBox.getItems().get(0));

        sendButton = new Button("Send");
        sendButton.setOnAction(FxEvents.sendMessageEvent(this));
        this.getChildren().addAll(
                textField,
                destinationBox,
                sendButton
        );
    }

    public byte[] getDestination() {
        return nameResolver.resolveName(destinationBox.getValue());
    }


    public String sendMessage() {
        String text = textField.getText();
        textField.clear();
        return text;
    }

    public void updateDestinationBox() {
        String value = destinationBox.getValue();


        destinationBox.getItems().clear();
        destinationBox.getItems().addAll(nameResolver.getNames());
        if (destinationBox.getItems().contains(value))
            destinationBox.setValue(value);
        else
            destinationBox.setValue(destinationBox.getItems().get(0));

    }
}
