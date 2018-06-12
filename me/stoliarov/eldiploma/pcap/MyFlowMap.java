package me.stoliarov.eldiploma.pcap;

import me.stoliarov.eldiploma.classification.ClassifierHelper;
import org.jnetpcap.packet.JFlowKey;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VladS on 12/10/2015.
 */
public class MyFlowMap
        extends HashMap<JFlowKey, MyFlow> implements PcapPacketHandler<Object> {
    private int flowsize;

    /**
     *
     */
    private static final long serialVersionUID = -5590314946675005059L;

    /**
     * Total packet count added.
     */
    private int count = 0;

    /**
     *
     */
    public MyFlowMap() {
    }

    /**
     * @param initialCapacity
     */
    public MyFlowMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * @param m
     */
    public MyFlowMap(Map<? extends JFlowKey, ? extends MyFlow> m) {
        super(m);
    }

    /**
     * @param initialCapacity
     * @param loadFactor
     */
    public MyFlowMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public MyFlowMap(ClassifierHelper.Mode mode, int flowsize) {
        this.flowsize = flowsize;
    }


    public void nextPacket(PcapPacket packet, Object user) {
        packet = new PcapPacket(packet); // make a copy
        JFlowKey key = packet.getState().getFlowKey();

        MyFlow flow = super.get(key);
        if (flow == null) {
            flow = new MyFlow(new PcapPacket(packet).getState().getFlowKey());
            super.put(key, flow);
        }
        if (flow.size()<flowsize) {
            flow.add(packet);
            count ++;
        }
    }

    public int getTotalPacketCount() {
        return count;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(1024 * 50);

        b.append("total packet count=").append(count).append("\n");
        b.append("total flow count=").append(size()).append("\n");

        int i = 0;
        for (MyFlow flow: values()) {
            b.append("flow[").append(i++).append(']').append(' ');
            b.append(flow.toString());
            b.append(",\n");
        }

        return b.toString();
    }

}