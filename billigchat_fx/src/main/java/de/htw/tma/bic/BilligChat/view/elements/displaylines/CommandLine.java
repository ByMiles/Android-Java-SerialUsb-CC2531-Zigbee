package de.htw.tma.bic.BilligChat.view.elements.displaylines;

import de.htw.tma.bic.ZnpService.eventHandler.CommandEventHandler;
import de.htw.tma.bic.ip16NameResolver.Ip16NameResolver;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static de.htw.tma.bic.BilligChat.controller.FxEvents.generateCommandEvent;
import static de.htw.tma.bic.ip16NameResolver.Ip16NameResolver.BROADCAST;


public abstract class CommandLine extends VBox implements CommandEventHandler{

    private ComboBox<String> nameBox;
    private int cmd0;
    private int cmd1;

    CommandLine(String command, int cmd0, int cmd1) {
        this.cmd0 = cmd0;
        this.cmd1 = cmd1;

        this.getStyleClass().add("commandLine");
        Label titleLabel = new Label(command);
        titleLabel.getStyleClass().add("title");
        HBox messageBox = initMessageBox();
        this.nameBox = new ComboBox<>();
        nameBox.getStyleClass().add("button");
        Button performButton = new Button(" SUBMIT ");
        performButton.setOnAction(generateCommandEvent(this));

        updateNameBox();

        HBox commandBox = new HBox(5);
        commandBox.getChildren().addAll(nameBox, messageBox, performButton);
        this.getChildren().addAll(titleLabel, commandBox);
    }

    protected abstract HBox initMessageBox();

    public abstract byte[] getMessageBody();

    public byte[] getIp16() {
        return Ip16NameResolver.service().resolveName(nameBox.getValue());
    }

    public void updateNameBox() {

        nameBox.getItems().clear();
        nameBox.getItems().setAll(Ip16NameResolver.service().getNames());
        nameBox.getItems().remove(BROADCAST);
        if (nameBox.getItems().size() > 0)
            nameBox.setValue(nameBox.getItems().get(0));
    }

    @Override
    public byte getCmd0() {
        return (byte) cmd0;
    }

    @Override
    public byte getCmd1() {
        return (byte)  cmd1;
    }
}
