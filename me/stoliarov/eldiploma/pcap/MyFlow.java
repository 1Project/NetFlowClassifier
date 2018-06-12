package me.stoliarov.eldiploma.pcap;

import me.stoliarov.eldiploma.domain.FlowInstance;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import org.jnetpcap.packet.JFlowKey;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by VladS on 12/10/2015.
 */
public class MyFlow{
    private final JFlowKey key;
    private final boolean reversable;
    private final List<JPacket> all;
    private final List<JPacket> forward;
    private final List<JPacket> reverse;

    private final FlowInstance flowInstance; //my

    public MyFlow(JFlowKey key) {
        this.flowInstance = new FlowInstance();
        this.key = key;
        this.reversable = (key.getFlags() & JFlowKey.FLAG_REVERSABLE) > 0;

        if (this.reversable) {
            this.all = new LinkedList<JPacket>();
            this.forward = new LinkedList<JPacket>();
            this.reverse = new LinkedList<JPacket>();
        } else {
            this.all = new LinkedList<JPacket>();
            this.forward = null;
            this.reverse = null;
        }
    }

    public final JFlowKey getKey() {
        return this.key;
    }

    public Instance getInstance() {
        double[] values = new double[6];
        values[0] = flowInstance.totPktsBytes;
        values[1] = flowInstance.totPktsQty;
        values[2] = flowInstance.fwPktsBytes;
        values[3] = flowInstance.fwPktsQty;
        values[4] = flowInstance.revPktsBytes;
        values[5] = flowInstance.revPktsQty;

        return new DenseInstance(values);
    }

    public FlowInstance getFlowInstance() {
        return flowInstance;
    }

    public boolean add(JPacket packet) {
        int dir = key.match(packet.getState().getFlowKey());
        if (dir == 0) {
            return false;
        }

        if (this.isReversable() == false) {
            flowInstance.totPktsQty++;
            flowInstance.totPktsBytes+=packet.getTotalSize();
            return this.all.add(packet);
        }

        if ((dir == 1)) {

            flowInstance.fwPktsQty++;
            flowInstance.fwPktsBytes+=packet.getTotalSize();
            forward.add(packet);
        } else {
            flowInstance.revPktsQty++;
            flowInstance.revPktsBytes+=packet.getTotalSize();
            reverse.add(packet);
        }
        flowInstance.totPktsQty++;
        flowInstance.totPktsBytes+=packet.getTotalSize();
        return all.add(packet);
    }

    public final boolean isReversable() {
        return this.reversable;
    }

    public final List<JPacket> getAll() {
        return this.all;
    }

    public int size() {
        return all.size();
    }

    public final List<JPacket> getForward() {
        return (this.reversable) ? this.forward : this.all;
    }

    public final List<JPacket> getReverse() {
        return (this.reversable) ? this.reverse : null;
    }

    private Tcp tcp = new Tcp();

    private Ip4 ip = new Ip4();

    private Ethernet eth = new Ethernet();

    public String toString() {
        if (all.isEmpty()) {
            return key.toDebugString() + " size=" + all.size();
        }

        JPacket packet = all.get(0);
        if (packet.hasHeader(tcp) && packet.hasHeader(ip)) {
            String dst = FormatUtils.ip(ip.destination());
            String src = FormatUtils.ip(ip.source());
            String sport = "" + tcp.source();
            String dport = "" + tcp.destination();
            // String hash = Integer.toHexString(key.hashCode());

            return src + ":" + sport + " -> " + dst + ":" + dport
                    + " Tcp fw/rev/tot pkts=[" + forward.size() + "/" + reverse.size()
                    + "/" + all.size() + "]";

        } else if (packet.hasHeader(ip)) {
            String dst = FormatUtils.ip(ip.destination());
            String src = FormatUtils.ip(ip.source());
            String type = "" + ip.type();

            return src + " -> " + dst + ":" + type + " Ip4 tot pkts=[" + all.size()
                    + "]";

        } else if (packet.hasHeader(eth)) {
            String dst = FormatUtils.mac(eth.destination());
            String src = FormatUtils.mac(eth.source());
            String type = Integer.toHexString(eth.type());

            return src + " -> " + dst + ":" + type + " Eth tot pkts=[" + all.size()
                    + "]";

        } else {
            return key.toDebugString() + " packets=" + all.size();
        }
    }
}
