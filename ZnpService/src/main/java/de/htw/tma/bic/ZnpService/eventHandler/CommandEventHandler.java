package de.htw.tma.bic.ZnpService.eventHandler;

public interface CommandEventHandler {

    byte[] getIp16();
    byte getCmd0();
    byte getCmd1();
    byte[] getMessageBody();

}
