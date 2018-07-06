package de.htw.tma.bic.BilligChat.view.elements.displaylines;

import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

import static de.htw.tma.bic.ZnpService.service.ZnpCodes.util;
import static de.htw.tma.bic.ZnpService.service.ZnpCodes.util_led;


public class LedCommand extends CommandLine {

    private ComboBox<String> ledBox;
    private ComboBox<String> modeBox;

    public LedCommand() {
        super("Led - Control", util, util_led);
    }

    protected HBox initMessageBox() {

        HBox line = new HBox(5);
        ledBox = new ComboBox<>();
        ledBox.getItems().addAll("Led 1", "Led 2");
        ledBox.setValue("Led 1");
        ledBox.getStyleClass().add("button");

        modeBox = new ComboBox<>();
        modeBox.getItems().addAll("Off", "On", "Blink", "Flash", "Toggle");
        modeBox.setValue("Blink");
        modeBox.getStyleClass().add("button");

        line.getChildren().addAll(ledBox, modeBox);
        return line;
    }

    @Override
    public byte[] getMessageBody() {
        return new byte[]{
                (byte) getLed(),
                (byte) getMode()
        };
    }

    private int getLed(){
        return (ledBox.getValue().equals("Led 1"))
                ? 1
                : 2;
    }

    private int getMode() {
        return (modeBox.getValue().equals("Off"))
                ? 0
                : (modeBox.getValue().equals("On"))
                ? 1
                : (modeBox.getValue().equals("Blink"))
                ? 2
                : (modeBox.getValue().equals("Flash"))
                ? 3
                : 4;
    }

}
