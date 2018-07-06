package de.htw.tma.bic.ip16NameResolver;


import java.util.HashMap;
import java.util.Map;

import static de.htw.tma.bic.ZnpService.service.ZnpCodes.toBytes;

/**
 * This singleton stores names mapped to their network-address.
 * Names can be resolved to addresses and other way around.
 * All names can be requested as array.
 *
 * @version 1.0
 * @author Miles Lorenz
 */
public class Ip16NameResolver {

    private static Ip16NameResolver instance;
    private HashMap<String, String> ipNameMap;
    public static final String BROADCAST = "- ALL -";

    private Ip16NameResolver() {
        this.ipNameMap = new HashMap<>();
        ipNameMap.put("ff ff ", BROADCAST);
    }

    public static Ip16NameResolver service(){

        return (instance != null)
                ? instance
                : (instance = new Ip16NameResolver());
    }

    public byte[] addNameToMap(String ip, String name){
        String oldName = (ipNameMap.get(ip) != null) ? ipNameMap.get(ip) : ip;
        ipNameMap.remove(ip, oldName);
        ipNameMap.put(ip, name);
        return (oldName + " is now " + name).getBytes();
    }

    public String[] getNames() {
        return ipNameMap.values().toArray(new String[ipNameMap.values().size()]);
    }

    public String resolveIp(String ip){
        String name = ipNameMap.get(ip);

        return (name != null) ? name : ip;
    }

    public byte[] resolveName(String destination) {

        String ipString = "ff ff ";

        for (Map.Entry<String, String > entry : ipNameMap.entrySet()) {
            if (entry.getValue().equals(destination)) {
                ipString = entry.getKey();
                break;
            }
        }

        if (ipString.equals("ff ff ")){
            return new byte[0];
        }

        return toBytes(ipString);
    }
}
