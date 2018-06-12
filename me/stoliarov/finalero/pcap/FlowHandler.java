package me.stoliarov.finalero.pcap;

import me.stoliarov.eldiploma.pcap.MyFlowClass;
import org.hibernate.Session;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.packet.*;
import weka.Run;
import weka.classifiers.Classifier;

import java.util.HashMap;

/**
 * Created by VladS on 5/15/2016.
 */
public class FlowHandler extends HashMap<JFlowKey, MyFlowList> implements PcapPacketHandler<Object> {
    private final Pcap pcap;
    private String className;
    private Mode mode;
    private Session session;
    private static final String filePath = "E:\\tmp.pcap";
    private PcapDumper pcapDumper;
    private volatile boolean isReadyToBreak;
    private Classifier classifier;

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public boolean isReadyToBreak() {
        return isReadyToBreak;
    }

    public void setReadyToBreak(boolean readyToBreak) {
        isReadyToBreak = readyToBreak;
    }

    public FlowHandler(Pcap pcap, String className, Mode mode) {
        this.pcap = pcap;
        this.className = className;
        this.mode = mode;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setFilePath(String filePath) {
//        this.filePath = filePath;
    }



    @Override
    public void nextPacket(PcapPacket packet, Object user) {
        JFlowKey key = packet.getFlowKey();
        long packetTime = packet.getCaptureHeader().timestampInMillis();
        long flowTime;
        MyFlowList myFlow = this.get(key);

//        pcap.dumpOpen(filePath);
//        pcapDumper.dump(packet);

        if (myFlow == null) {
            myFlow = new MyFlowList(key, className, packetTime);
            this.put(key, myFlow);
            flowTime = packetTime;
        } else {
            flowTime = myFlow.getStartTime();
        }
        long diff = Math.abs(flowTime - packetTime);
        if (diff < 1000) {
            myFlow.add(packet);
        } else {
            myFlow.prepare();
            if (mode == mode.ONLINE) {
//                session.save(myFlow.getFlowClass());
                try {
                    System.out.println(classifier.classifyInstance(myFlow.getInstance().instance(0)));
                } catch (Exception e) {
                    String msg = "Невозможно классифицировать пакет";
                    System.out.println(msg);
                    throw new RuntimeException(msg, e);
                }
            }
            myFlow = new MyFlowList(key, className, packetTime);
            myFlow.add(packet);
            this.put(key, myFlow);
        }
        if(isReadyToBreak==true) { // User implemented
            // Make sure pcap is marked 'final' so we can use it inside anonymous class
            pcap.breakloop();
        }
    }


    public enum Mode {
        DB, FILE, ONLINE
    }
}

