package de.htw.tma.bic.BilligChat.view.elements;

import de.htw.tma.bic.BilligChat.view.elements.displays.ChatDisplay;
import de.htw.tma.bic.BilligChat.view.elements.displays.CommandDisplay;
import de.htw.tma.bic.BilligChat.view.elements.displays.SystemDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;



public class Display extends TabPane {
    private ChatDisplay chatDisplay;
    private SystemDisplay systemDisplay;
    private CommandDisplay commandDisplay;

    public Display() {
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        this.chatDisplay = new ChatDisplay();
        this.systemDisplay = new SystemDisplay();
        this.commandDisplay = new CommandDisplay();
        this.getStyleClass().add("display");

        this.getTabs().addAll(
                new Tab("Chat", new ScrollPane(chatDisplay)),
                new Tab("System", new ScrollPane(systemDisplay)),
                new Tab("Command", new ScrollPane(commandDisplay))
        );
    }

    public ChatDisplay getChatDisplay() {
        return chatDisplay;
    }

    public SystemDisplay getSystemDisplay() {
        return systemDisplay;
    }

    public CommandDisplay getCommandDisplay() {
        return commandDisplay;
    }
}
