package com.example.calculadoraipv;

public class SubnetInfo {
    private String networkAddress;
    private String usableHostRange;
    private String broadcastAddress;

    public SubnetInfo(String networkAddress, String usableHostRange, String broadcastAddress) {
        this.networkAddress = networkAddress;
        this.usableHostRange = usableHostRange;
        this.broadcastAddress = broadcastAddress;
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

    public String getUsableHostRange() {
        return usableHostRange;
    }

    public String getBroadcastAddress() {
        return broadcastAddress;
    }
}
