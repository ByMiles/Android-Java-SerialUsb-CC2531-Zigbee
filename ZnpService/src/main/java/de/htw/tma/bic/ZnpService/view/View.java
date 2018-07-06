package de.htw.tma.bic.ZnpService.view;


import de.htw.tma.bic.ZnpService.response.Response;
import de.htw.tma.bic.ZnpService.response.ResponseHeader;

public interface View {
    void appendChatResponse(Response response);

    void appendSystemResponse(Response response);

    void updateActionBar(byte[] message, ResponseHeader header);

    void setInfoText(String usbInfo);

    void updateNames();
}
