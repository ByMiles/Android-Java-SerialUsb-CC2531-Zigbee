package de.htw.tma.bic.BilligChat;

import de.htw.tma.bic.BilligChat.controller.BilligChat;
import de.htw.tma.bic.BilligChat.usb.JSerialUsbDriver;
import de.htw.tma.bic.ZnpService.service.ZnpService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxApp extends Application {

    private Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        initStage();
        initChat(BilligChat.startBilligChatFxApp());
    }

    private void initStage() {
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public void initChat(Scene chatScene){
        chatScene.getStylesheets().add(getClass().getResource("/Style.css").toExternalForm());
        stage.setTitle("BILLIG-CHAT - DER CHILLIG-CHAT");
        stage.setScene(chatScene);
        stage.show();
    }

    public static boolean initZnp(){

        if (ZnpService.isConnected())
            return true;

        try {
            JSerialUsbDriver driver = new JSerialUsbDriver();
            ZnpService.service(driver);
            return ZnpService.isConnected();

        } catch (Exception e) {
            return false;
        }
    }
}
