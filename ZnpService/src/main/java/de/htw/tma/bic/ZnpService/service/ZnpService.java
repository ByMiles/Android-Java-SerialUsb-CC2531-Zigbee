package de.htw.tma.bic.ZnpService.service;


import de.htw.tma.bic.ZnpService.callBack.RxCallBack;
import de.htw.tma.bic.UsbDriver.UsbDriver;

public class ZnpService {

    private static ZnpService instance;
    private static UsbDriver driver;
    private ZnpTxService znpTxService;
    private RxCallBack subscriber;
    private static boolean connected = false;
    private static boolean updatedChannel = false;

    public ZnpService(UsbDriver usbDriver) {
        driver = usbDriver;
    }

    public static ZnpService service(UsbDriver driver) {
        if (instance == null) instance = new ZnpService(driver);
        return instance;
    }

    public static ZnpService service() {
        return instance;
    }

    public void start(RxCallBack subscriber) {

        if (driver != null) connected = true;
        if (subscriber == null) return;

        try {
            updatedChannel = false;
            this.subscriber = subscriber;
            this.znpTxService = new ZnpTxService(driver);
            new ZnpRxService(driver, subscriber);
            connected = true;
        } catch (Exception e){
            driver = null;
            connected = false; }
    }

    public void start(){
        if (driver != null) connected = true;
        start(subscriber);
    }

    public ZnpTxService tx(){
        return znpTxService;
    }

    public static String getUsbInfo(){

        return (driver != null) ? driver.getUsbInfo() : "NO DEVICE\nFOUND!";
    }

    public static boolean isConnected(){
        return connected;
    }

    public static boolean isChangeChannel() {

        if (updatedChannel) {
            updatedChannel = false;
            return false;
        }
        return connected;
    }

    public void setUpdatedChannel(boolean updatedChannel) {
        this.updatedChannel = updatedChannel;
    }
}
