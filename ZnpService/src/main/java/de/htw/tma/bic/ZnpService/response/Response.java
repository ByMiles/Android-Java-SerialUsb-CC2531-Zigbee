package de.htw.tma.bic.ZnpService.response;


public interface Response {

    ResponseHeader getHeader();
    String getMessageAsString();
    String getMessageAsHex();
}
