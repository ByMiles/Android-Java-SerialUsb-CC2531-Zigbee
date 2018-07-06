package de.htw.tma.bic.ZnpService.callBack;


import de.htw.tma.bic.ZnpService.response.ResponseHeader;

public interface RxCallBack {

    void processRxCallBack(byte[] message, ResponseHeader header);
}
