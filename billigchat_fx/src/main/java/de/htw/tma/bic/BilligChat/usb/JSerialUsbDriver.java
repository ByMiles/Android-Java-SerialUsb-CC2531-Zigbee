package de.htw.tma.bic.BilligChat.usb;

import com.fazecast.jSerialComm.SerialPort;
import de.htw.tma.bic.UsbDriver.UsbDriver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class JSerialUsbDriver implements UsbDriver {
    private SerialPort port;

    public JSerialUsbDriver() throws Exception {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        for (SerialPort possiblePort : serialPorts) {
            possiblePort.openPort();
            if (possiblePort.isOpen()) {
                this.port = possiblePort;
                port.setBaudRate(38400);
                port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
                return;
            }
        }
        throw new Exception("Usb not openable");
    }

    @Override
    public byte[][] readUsbData() {

        byte[][] messages = new byte[0][];
        int messageLength = port.bytesAvailable();
        if (messageLength > 0) {
            try (InputStream in = port.getInputStream()) {
                messages = new byte[messageLength][];
                for (int i = 0; i < messageLength; i++) {
                    byte[] buffer = new byte[300];
                    int dataLength = in.read(buffer);
                    messages[i] = Arrays.copyOfRange(buffer, 0, dataLength);
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
            return messages;
        }


        return new byte[0][];
    }

    @Override
    public void sendUsbData(byte[] data) {

        try (OutputStream out = port.getOutputStream()) {
            out.write(data);
        } catch (Exception e) {
            System.out.println("Write-Exception " + e.getMessage());
        }
    }

    @Override
    public int rxDataReady() {
        return port.bytesAvailable();
    }

    @Override
    public void close() {
        port.closePort();
    }

    @Override
    public String getUsbInfo() {
        return "Port: COM" + cutCOM(port.getDescriptivePortName()) +
                "\n B.R.: " + port.getBaudRate() +
                "\n Databits: " + port.getNumDataBits() +
                "\n Parities: " + port.getParity() +
                "\n Stopbits: " + port.getNumStopBits();
    }

    private String cutCOM(String descriptivePortName) {

        StringBuilder com = new StringBuilder();
        while (true) {
            if (descriptivePortName.length() == 0) break;

            String s = descriptivePortName.substring(0, 1);
            try {
                com.append(String.valueOf(Integer.parseInt(s)));
            } catch (Exception ignored) {
            }
            descriptivePortName = descriptivePortName.substring(1);
        }
        return com.toString();
    }
}
