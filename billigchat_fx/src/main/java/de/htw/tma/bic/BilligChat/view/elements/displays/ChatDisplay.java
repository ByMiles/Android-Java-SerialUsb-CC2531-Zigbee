package de.htw.tma.bic.BilligChat.view.elements.displays;
import de.htw.tma.bic.ZnpService.response.Response;
import de.htw.tma.bic.ip16NameResolver.Ip16NameResolver;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class ChatDisplay extends VBox {

    private Ip16NameResolver nameResolver;

    public ChatDisplay() {
        this.getStyleClass().add("displayTab");
        nameResolver = Ip16NameResolver.service();
    }

    public void appendInfo(Response info) {
        this.getChildren().add(generateNode(info));
        ((ScrollPane) this.getParent().getParent().getParent()).setVvalue(1.0);
    }

    private Node generateNode(Response info) {

        HBox row = new HBox(5);
        Label headerLabel = new Label ("<" + nameResolver.resolveIp(info.getHeader().getIp16()) + ">: ");
        headerLabel.getStyleClass().add("active");
        Label messageLabel = new Label (info.getMessageAsString());
        messageLabel.getStyleClass().add("chatText");
        row.getChildren().addAll(headerLabel, messageLabel);

        return row;
    }
}
