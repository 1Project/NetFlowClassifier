package me.stoliarov.finalero.sniffer;

import javafx.beans.property.SimpleStringProperty;
import me.stoliarov.finalero.pcap.FlowHandler;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.*;

/**
 * Created by VladS on 12/10/2015.
 */
public class Sniffer {
    private StringBuilder errbuf = new StringBuilder();
    private final Pcap pcap;
    private int devs = 5;
    private PcapIf device;
    private final SimpleStringProperty stringProperty = new SimpleStringProperty();
    private volatile boolean isReadyToBreak = false;
    private FlowHandler flowHandler;

    public Pcap getPcap() {
        return pcap;
    }

    public void setReadyToBreak(boolean readyToBreak) {
        isReadyToBreak = readyToBreak;
    }

    public String getStringProperty() {
        return stringProperty.get();
    }

    public SimpleStringProperty stringPropertyProperty() {
        return stringProperty;
    }

    public PcapIf getDevice() {
        return device;
    }

    public void setDevs(int devs) {
        this.devs = devs;
    }

    public void setFlowHandler(FlowHandler flowHandler) {
        this.flowHandler = flowHandler;
    }

    public Sniffer() {
        List<PcapIf> alldevs = new ArrayList<>(); // Will be filled with NICs
        int r = Pcap.findAllDevs(alldevs, errbuf);
        device = alldevs.get(devs); // We know we have atleast 1 device
        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;           // 10 seconds in millis
        pcap =
                Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
        if (pcap == null) {
            System.err.println("Ошибка во время открытия устройства: "
                    + errbuf.toString());
            return;
        }
    }

    public StringBuilder getErrbuf() {
        return errbuf;
    }

    public void start() {
        pcap.loop(Pcap.LOOP_INFINITE, flowHandler, null);
        pcap.close();
    }

    public List<PcapIf> getDevsList() {
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.out.println(("Ошибка: список сетевых устройств недоступен. " + errbuf + " \n"));
            return null;
        }
        System.out.println(("Сетевые карты обнаружены!"));
        return alldevs;
    }

    public void stop() {
        flowHandler.setReadyToBreak(true);
    }


    public static void main(String[] args) {
        Sniffer sniffer = new Sniffer();
        sniffer.setDevs(5);
//        sniffer.stop();
        sniffer.start();
    }
}


