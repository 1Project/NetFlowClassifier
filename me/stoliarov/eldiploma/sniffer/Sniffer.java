package me.stoliarov.eldiploma.sniffer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import me.stoliarov.eldiploma.classification.ClassifierHelper;
import me.stoliarov.eldiploma.domain.FlowInstance;
import me.stoliarov.eldiploma.pcap.MyFlow;
import me.stoliarov.eldiploma.pcap.MyFlowMap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JFlowKey;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by VladS on 12/10/2015.
 */
// local in memory h2 database??
    // postgresql
public class Sniffer {
    private StringBuilder errbuf = new StringBuilder();
    private MyFlowMap flowMap;
    private SessionFactory sessionFactory;
    private Pcap pcap;
    private Session session;
    private HashMap<JFlowKey, Integer> flowPacketCountMap;
    private int devs;
    private ClassifierHelper.Mode mode;

    public void setMode(ClassifierHelper.Mode mode) {
        this.mode = mode;
    }

    public StringBuilder getErrbuf() {
        return errbuf;
    }

    public void start() {
        List<PcapIf> alldevs = new ArrayList<>(); // Will be filled with NICs
        int r = Pcap.findAllDevs(alldevs, errbuf);
        PcapIf device = alldevs.get(devs); // We know we have atleast 1 device
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
        flowMap = new MyFlowMap(ClassifierHelper.Mode.CAPTURETODB, 1000);
        pcap.loop(Pcap.LOOP_INFINITE, (PcapPacketHandler<Object>) (packet, user) -> {
            flowMap.nextPacket(packet, user);
        }, null);
    }

    public List<PcapIf> getDevsList() {
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.out.println(("Ошибка: список сетевых устройств недоступен. " + errbuf + " \n"));
            return null;
        }
        System.out.println(("Сетевые карты обнаружены"));

        int i = 0;
        for (PcapIf device : alldevs) {
            String description =
                    (device.getDescription() != null) ? device.getDescription()
                            : "";
            System.out.println((i++ + " " + device.getName() + " " + description));
        }
        return alldevs;
    }

    public void setDevs(int devs) {
        this.devs = devs;
    }

    public Sniffer() {
        try {
            sessionFactory = new Configuration().configure()
                    .buildSessionFactory();
            session = sessionFactory.openSession();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка БД" + e.toString());
        }

    }

    public String save() {
        session.beginTransaction();
        flowMap.forEach((key, flow) -> session.save(flow.getFlowInstance()));
        session.getTransaction().commit();
        session.close();
        return "";
    }

    public void stop() {
        System.out.println(flowMap.toString());
        pcap.breakloop();
        pcap.close();
    }

    public static void main(String[] args) {
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs

        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errbuf
                    .toString());
            return;
        }

        System.out.println("Network devices found:");

        int i = 0;
        for (PcapIf device : alldevs) {
            String description =
                    (device.getDescription() != null) ? device.getDescription()
                            : "No description available";
            System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
        }

        PcapIf device = alldevs.get(3); // We know we have atleast 1 device
        System.out
                .printf("\nChoosing '%s' on your behalf:\n",
                        (device.getDescription() != null) ? device.getDescription()
                                : device.getName());


        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;           // 10 seconds in millis
        Pcap pcap =
                Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: "
                    + errbuf.toString());
            return;
        }

        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        Session session = sessionFactory.openSession();
        MyFlowMap flowMap = new MyFlowMap(ClassifierHelper.Mode.TRAIN, 10);
        pcap.loop(1000, flowMap, null);
        session.beginTransaction();
        flowMap.forEach((key, flow) -> session.save(flow.getFlowInstance()));
        session.getTransaction().commit();
        session.close();
        pcap.close();
        System.out.println(flowMap.toString());
    }
}


