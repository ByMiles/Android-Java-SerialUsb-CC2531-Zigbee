package de.htw.tma.bic.ZnpService.eventHandler;

public interface SendMessageEventHandler {

    byte[] getDestination();

    String sendMessage();
}
