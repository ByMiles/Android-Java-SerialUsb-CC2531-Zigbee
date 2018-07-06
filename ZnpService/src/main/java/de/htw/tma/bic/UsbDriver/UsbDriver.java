package de.htw.tma.bic.UsbDriver;

public interface UsbDriver {

    byte[][] readUsbData();
    void sendUsbData(byte[] data);
    int rxDataReady();

    void close();

    String getUsbInfo();
}
