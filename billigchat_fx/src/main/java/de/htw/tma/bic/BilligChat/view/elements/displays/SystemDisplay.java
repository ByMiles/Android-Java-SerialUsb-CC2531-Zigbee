package de.htw.tma.bic.BilligChat.view.elements.displays;

import de.htw.tma.bic.ZnpService.response.Response;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SystemDisplay extends VBox {

    public SystemDisplay() {
        this.getStyleClass().add("displayTab");
    }
    public void appendInfo(Response info) {
        this.getChildren().add(generateNode(info));
        ((ScrollPane) this.getParent().getParent().getParent()).setVvalue(1.0);
    }

    private Node generateNode(Response info) {

        HBox row = new HBox(5);
        Label headerLabel = new Label (info.getHeader().toString());
        headerLabel.getStyleClass().add("active");
        Label messageLabel = new Label (info.getMessageAsHex());
        messageLabel.getStyleClass().add("chatText");
        row.getChildren().addAll(headerLabel, messageLabel);

        return row;
    }
}
