package de.htw.tma.bic.BilligChat.controller;

import de.htw.tma.bic.BilligChat.usb.JSerialUsbDriver;
import de.htw.tma.bic.BilligChat.view.scenes.ChatScene;
import de.htw.tma.bic.UsbDriver.UsbDriver;
import de.htw.tma.bic.ZnpService.callBack.RxCallBack;
import de.htw.tma.bic.ZnpService.service.ZnpService;
import javafx.scene.Scene;


/**
 * This class is the entry point for an javafx based billig-chat-application.
 * It initialises the view, and the znp-service.
 * Then the view is updated the first time.
 *
 * @version 2.0
 * @author Miles Lorenz
 */
public class BilligChat {

    private static RxCallBack callBack;

    public static Scene startBilligChatFxApp() {

        ChatScene scene = new ChatScene();
        callBack = new FxRxCallBack(scene);

        initZnp();

        scene.setInfoText(ZnpService.getUsbInfo());
        return scene.initChat(900, 600);
    }

    public static void initZnp() {

        if (!ZnpService.isConnected()) {
            try {
                UsbDriver usbDriver = new JSerialUsbDriver();

                ZnpService.service(usbDriver).start(callBack);
                if (ZnpService.isConnected()) ZnpService.service().tx().requestNetworkStatus();


            } catch (Exception ignored) { }
        }
    }
}
