package de.htw.tma.bic.ZnpService.service;



import de.htw.tma.bic.ZnpService.callBack.RxCallBack;
import de.htw.tma.bic.ZnpService.response.ResponseHeader;
import de.htw.tma.bic.UsbDriver.UsbDriver;

import java.util.Arrays;

import static de.htw.tma.bic.ZnpService.service.ZnpCodes.*;


/**
 * Inside the thread incoming messages are transformed to a response,
 * including its header, followed by a call of the registered RxCallback.
 *
 *  @version 1.0
 * @author Miles Lorenz
 */
class ZnpRxService {
    private UsbDriver driver;
    private boolean stop;
    private RxCallBack subscriber;

    ZnpRxService(UsbDriver driver, RxCallBack subscriber) {

        this.driver = driver;
        this.stop = false;

        registerRxCallBack(subscriber);
    }


    private void registerRxCallBack(RxCallBack subscriber) {

        this.subscriber = subscriber;
        new Thread(() -> {
            while (!stop) {
                if (driver.rxDataReady() > 0) {
                    byte[][] messages = driver.readUsbData();
                    for (byte[] message : messages) {
                        processRx(message);
                    }
                }
            }
            driver.close();
        }).start();
    }

    private void processRx(byte[] buffer) {

        int pointer = fetchSOP(buffer);
        if (pointer == 0) return;

        ResponseHeader header;
        byte[] message;
        if ((buffer[pointer + 1] & 0xff) == bcResp) {
            header = new ResponseHeader(buffer[pointer++], buffer[pointer++], buffer[pointer++], buffer[pointer++], buffer[pointer++], buffer[pointer++], buffer[pointer++], buffer[pointer++]);

            if (header.getLength() > 5) {
                message = Arrays.copyOfRange(buffer, pointer, pointer + header.getLength() - 6);
            } else {
                message = new byte[0];
            }
        } else {
            header = new ResponseHeader(buffer[pointer++], buffer[pointer++], buffer[pointer++], buffer[pointer], (byte) 0, (byte) 0, (byte) 0, (byte) 0);
            if (header.getLength() > 1) {
                message = Arrays.copyOfRange(buffer, pointer, pointer + header.getLength());
            } else {
                message = new byte[0];
            }
        }
        subscriber.processRxCallBack(message, header);
    }

    private int fetchSOP(byte[] buffer) {

        if (buffer == null) return 0;
        int pointer = 0;

        do {
            if (pointer >= buffer.length - 1) return 0;
        } while (buffer[pointer++] == 255); // this was sop 254
        return pointer;
    }
}
