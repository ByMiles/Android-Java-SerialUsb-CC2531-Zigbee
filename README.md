# Android-Java-SerialUsb-CC2531-Zigbee
Connecting java-based devices using dongles.

This repository is the result of an university-project. Following this guide shell enable you to understand the mechanism of the used firmware and how to get started with your own project. 

## Used hardware, software and dependencies

1. CC2531 
You will need at least two of this: http://www.ti.com/product/CC2531

2. Debugger
You will need a debugger-tool.

2. Flash Programmer
For flashing the dongles use this: http://www.ti.com/tool/FLASH-PROGRAMMER?keyMatch=flash%20programmer&tisearch=Search-EN-Products

3. IAR-Embeddded Workbench
Use the time-limited version (8051): https://www.iar.com/iar-embedded-workbench/#!?device=CC2531F256&architecture=8051

4. Z-Stack
You can download the full iar-project here: http://www.ti.com/tool/Z-STACK

5. Usb-Driver

=> For non-Android this driver is used: https://github.com/Fazecast/jSerialComm
=> For Android this driver is used: https://github.com/mik3y/usb-serial-for-android/blob/master/usbSerialForAndroid/src/main/java/com/hoho/android/usbserial/driver/CdcAcmSerialDriver.java

6. Java
To avoid complications with Android use Java 1.8 .

7. Android Device
Please be aware that older devices may not support the driver-library. To find out if or not is left to you ;-).


## Talking with ZNP

Assuming you made the connection via usb and are able to call driver.write() (or to use the outputstream) the hole magic is in the construction of the message. For reference have a look in ZnpCodes.java (generateMessage()).
In short a message (a byte[]) looks like this (each 1 byte):
[SOP|DATALENGHT|COMMAND0|COMMAND1|DATA|FCS] 

To call a command you need to know its command 0 and command 1. The values are defined in the znp-project. If you are developing an own part of firmware you will also need to define some commands.

To test build in commands you can use the Z-tool, witch is included in the Z-Stack-download. For example util led_control or getDeviceInfo are good first commands to test. Search the z-tool command-names in the project and extract the command-values.


## BC - Billig-Chat

This repo contains a folder BC. This folder contains three files. bc.c and bc.h are build the example-firmware-extension witch was developed in this university-project. The firmwareChanges.txt contains a description of the changes made to the original firmware.
Note that as this is only a prototype for a real development there must be one more step, that extracts parts of mt and znp_app as an own implementation customised to the requirements. 


## Znp-Service

The Folder ZnpService contains a java-project, that constructs a pattern to interact with the dongle in an appropriate way.
Unfortunally the way of multithreading is not supported in android and time run out. 
But, and it is much easier too, you can just extract the ZnpCodes-methods you need (i.e. generateMessage and calcFCS).

## Billig-Chat Fx

The Folder billigchat_fx contains a java-project that implements the Znp-Service in an fx-gui. 

## Android App

The Android implementation can be found in this repo: https://github.com/FrancoisBertrand/Awesome_Chat
As its a clone of https://github.com/mik3y/usb-serial-for-android the focus is only on:
https://github.com/FrancoisBertrand/Awesome_Chat/tree/master/usbSerialExamples/src/main/java/src/com/hoho/android/usbserial/examples
where in SerialConsoleActivity the view is customised and the methods generateMessage and calcFCS are implemented directly.
Noobs we are we wheren't able to accomplish asynchronus writing. Thatswhy the 
https://github.com/FrancoisBertrand/Awesome_Chat/blob/master/usbSerialForAndroid/src/main/java/com/hoho/android/usbserial/util/SerialInputOutputManager.java

is extended with 

   public UsbSerialPort getmDriver() {
        return mDriver;
}

. 




