package de.htw.tma.bic.ZnpService.response;

import static de.htw.tma.bic.ZnpService.service.ZnpCodes.toHex;

/**
 * The response header stores all information from an incoming message except
 * the message body.
 * @version 2.0
 * @author Miles Lorenz
 */
public class ResponseHeader {
    private byte length;      // the length of the messageBody
    private byte cmd0;        // reference to firmware-component that send the message.
    private byte cmd1;        // component specific command id
    private String status;    // equivalent to the znp-definition
    private byte info;        // custom information field
    private byte ip16_1;      // first part of the ip
    private byte ip16_2;      // second part of the ip
    private byte channel;     // used logical channel (11-26)

    // FINALS AT BOTTOM

    public ResponseHeader(byte length, byte cmd0, byte cmd1, byte status, byte info, byte ip16_1, byte ip16_2, byte channel) {
        this.length = length;
        this.cmd0 = cmd0;
        this.cmd1 = cmd1;
        this.status = translateStatusHex(status);
        this.info = info;
        this.ip16_1 = ip16_1;
        this.ip16_2 = ip16_2;
        this.channel = channel;
    }

    public byte getLength() {
        return length;
    }

    public byte getCmd0() {
        return cmd0;
    }

    public byte getCmd1() {
        return cmd1;
    }

    public String getStatus() {
        return status;
    }

    public byte getInfo(){
        return info;
    }

    public String getIp16() {
        return toHex(ip16_1) + toHex(ip16_2);
    }
    public byte getChannel() {
        return channel;
    }

    // FINALS

    private static String translateStatusHex(int hex) {
        switch (hex) {

            case 0x00:
                return "SUCCESS";
            case 0x01:
                return "FAILURE";
            case 0x02:
                return "INVALIDPARAMETER";
            case 0x03:
                return "INVALID_TASK";
            case 0x04:
                return "MSG_BUFFER_NOT_AVAIL";
            case 0x05:
                return "INVALID_MSG_POINTER";
            case 0x06:
                return "INVALID_EVENT_ID";
            case 0x07:
                return "INVALID_INTERRUPT_ID";
            case 0x08:
                return "NO_TIMER_AVAIL";
            case 0x09:
                return "NV_ITEM_UNINIT";
            case 0x0A:
                return "NV_OPER_FAILED";
            case 0x0B:
                return "INVALID_MEM_SIZE";
            case 0x0C:
                return "NV_BAD_ITEM_LEN";
            case 0x0D:
                return "NV_INVALID_DATA";

            default: return toHex(hex) ;
        }
    }

    private static String translateCmd0Hex(int hex){
        switch (hex & 0xff){
            case (byte) 103: return "ZNP";
            case (byte) 0xaa: return "BC_INFO";
            default: return toHex(hex) ;
        }
    }

    private static String translateCmd1Hex(int hex){
        switch (hex & 0xff){
            case (byte) 1: return "HELLO";
            case (byte) 2: return "INFO";
            case (byte) 3: return "RESET";
            default: return toHex(hex) ;
        }
    }

    @Override
    public String toString() {
        return "Info: | " + translateCmd0Hex(cmd0) + " | " + translateCmd1Hex(cmd1) + " | " + status + " | " + (info & 0xff) + " |\n" +
                "Ip16: " + toHex(ip16_2) + toHex(ip16_1) + " | Channel: " + toHex(channel);
    }
}
