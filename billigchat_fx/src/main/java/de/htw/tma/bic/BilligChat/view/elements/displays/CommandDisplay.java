package de.htw.tma.bic.BilligChat.view.elements.displays;

import de.htw.tma.bic.BilligChat.view.elements.displaylines.CommandLine;
import de.htw.tma.bic.BilligChat.view.elements.displaylines.LedCommand;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class CommandDisplay extends VBox {

    public CommandDisplay(){
        super(20);

        initDisplay();
    }

    private void initDisplay() {

        this.getChildren().add(new LedCommand());
    }


    public void updateNameBox() {
        for (Node line:this.getChildren()) {
            ((CommandLine) line).updateNameBox();
        }
    }
}
