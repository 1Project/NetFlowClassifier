package me.stoliarov.finalero.pcap;

import me.stoliarov.eldiploma.domain.FlowClass;
import org.jnetpcap.packet.JFlowKey;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import weka.core.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by VladS on 4/22/2016.
 */
public class MyFlowList {
    private final JFlowKey key;
    private final boolean reversable;
    private final List<JPacket> all;
    private final List<JPacket> forward;
    private final List<JPacket> reverse;

    private final FlowClass flowClass; //my
    private final String className;
    private final long startTime;

    private final Tcp tcp = new Tcp();
    private final Udp udp = new Udp();
    private final Ip4 ip = new Ip4();
    private final Ethernet eth = new Ethernet();

    private double totPktsQty;
    private double totPktsBytes;
    private double revPktsQty;
    private double revPktsBytes;
    private double fwPktsQty;
    private double fwPktsBytes;
    private double headerCount;
    private double wirelen;
    private double tcpAck;
    private double tcpSyn;
    private double tcpFlags;
    private double payloadLength;
    private double payloadOffset;
    private double tcpDstPort;
    private double tcpSrcPort;
    private double ipType;
    private double transportProtocol;
    private double tcpFlowDir;

    public MyFlowList(JFlowKey key, String className, long startTime) {
        this.flowClass = new FlowClass();
        flowClass.setClassName(className);
        this.className = className;
        this.startTime = startTime;
        this.key = key;
        this.reversable = (key.getFlags() & JFlowKey.FLAG_REVERSABLE) > 0;
        flowClass.setIsReversable(reversable == true ? 1 : 0);

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

    public boolean prepare() {
        flowClass.setTotPktsQty(totPktsQty);
        flowClass.setTotPktsBytes(totPktsBytes / totPktsQty);
        flowClass.setRevPktsQty(revPktsQty / totPktsQty);
        flowClass.setRevPktsBytes(revPktsBytes / totPktsBytes);
        flowClass.setFwPktsQty(fwPktsQty / totPktsQty);
        flowClass.setFwPktsBytes(fwPktsBytes / totPktsBytes);
        flowClass.setHeaderCount(headerCount);
        flowClass.setWirelen(wirelen / totPktsQty);
        flowClass.setTcpAck(tcpAck);
        flowClass.setTcpSyn(tcpSyn);
        flowClass.setTcpFlags(tcpFlags / totPktsQty);
        flowClass.setIpType(ipType / totPktsQty);
        flowClass.setPayloadLength(payloadLength / totPktsQty);
        flowClass.setPayloadOffset(payloadOffset / totPktsQty);
        flowClass.setTcpDstPort(tcpDstPort);
        flowClass.setTcpSrcPort(tcpSrcPort);
        flowClass.setTransportProtocol(transportProtocol);
        flowClass.setTcpFlowDir(tcpFlowDir);

        return true;
    }

    public String getClassName() {
        return className;
    }

    public long getStartTime() {
        return startTime;
    }

    public final JFlowKey getKey() {
        return this.key;
    }

    public Instances getInstance() {
        Instances dataset = new Instances("Online", attributes(), 1);
        dataset.setClassIndex(0);
        Instance instance = new DenseInstance(4);
        instance.setValue(0, "?");
        instance.setValue(1, totPktsQty);
        instance.setValue(2, totPktsBytes);
        instance.setValue(3, revPktsQty);
        instance.setValue(4, revPktsBytes);
        instance.setValue(5, fwPktsQty);
        instance.setValue(6, fwPktsBytes);
        instance.setValue(7, reversable ? 1 : 0);
        instance.setValue(8, transportProtocol);
        instance.setValue(9, tcpSrcPort);
        instance.setValue(10, tcpDstPort);
        instance.setValue(11, wirelen);
        instance.setValue(12, headerCount);
        instance.setValue(13, tcpSyn);
        instance.setValue(14, tcpAck);
        instance.setValue(15, tcpFlags);
        instance.setValue(16, payloadOffset);
        instance.setValue(17, payloadLength);
        instance.setValue(18, 0.0);
        instance.setValue(19, 5.0);
        instance.setValue(20, tcpFlowDir);
        dataset.add(instance);
        return dataset;
    }

    private ArrayList<Attribute> attributes() {
        ArrayList<Attribute> attributeList = new ArrayList<>();
        List<String> classValues = new ArrayList<>();
        classValues.add("FTP");
        classValues.add("HTTP");
        classValues.add("MAIL");
        classValues.add("P2P");
        classValues.add("SKYPE");
        classValues.add("SSH");
        attributeList.add(new Attribute("classname", classValues));
        attributeList.add(new Attribute("tot_pkts_qty"));
        attributeList.add(new Attribute("tot_pkts_bytes"));
        attributeList.add(new Attribute("rev_pkts_qty"));
        attributeList.add(new Attribute("rev_pkts_bytes"));
        attributeList.add(new Attribute("fw_pkts_qty"));
        attributeList.add(new Attribute("fw_pkts_bytes"));
        attributeList.add(new Attribute("is_reversable"));
        attributeList.add(new Attribute("transport_protocol"));
        attributeList.add(new Attribute("tcp_src_port"));
        attributeList.add(new Attribute("tcp_dst_port"));
        attributeList.add(new Attribute("wirelen"));
        attributeList.add(new Attribute("header_count"));
        attributeList.add(new Attribute("tcp_syn"));
        attributeList.add(new Attribute("tcp_ack"));
        attributeList.add(new Attribute("tcp_flags"));
        attributeList.add(new Attribute("payload_offset"));
        attributeList.add(new Attribute("payload_length"));
        attributeList.add(new Attribute("is_fragment"));
        attributeList.add(new Attribute("hlen"));
        attributeList.add(new Attribute("tcp_flow_dir"));

        return attributeList;
    }

    public FlowClass getFlowClass() {
        return flowClass;
    }

    public boolean add(JPacket packetS) {
        JPacket packet = packetS;
        int dir = key.match(packet.getState().getFlowKey());
        tcpFlowDir = dir;
        if (dir == 0) {
            return false;
        }
        if (this.isReversable() == false) {
            totPktsQty++;
            totPktsBytes += packet.getTotalSize();
            all.add(packet);
        } else if ((dir == 1)) {
            fwPktsQty++;
            fwPktsBytes += packet.getTotalSize();
            forward.add(packet);
        } else if (!(dir == 1)) {
            revPktsQty++;
            revPktsBytes += packet.getTotalSize();
            reverse.add(packet);
        }
        totPktsQty++;
        totPktsBytes += packet.getTotalSize();
        totPktsBytes += packet.getTotalSize();
        headerCount += packet.getHeaderCount();
        wirelen += packet.getPacketWirelen();

        if (packet.hasHeader(tcp)) {
            tcpAck += packet.getHeader(tcp).flags_ACK() ? 1 : 0;
            tcpSyn += packet.getHeader(tcp).flags_SYN() ? 1 : 0;
            tcpFlags += packet.getHeader(tcp).flags();

            tcpDstPort = packet.getHeader(tcp).destination();
            tcpSrcPort = packet.getHeader(tcp).source();

            transportProtocol = 1;
        } else if (packet.hasHeader(udp)) {
            tcpAck += packet.getHeader(udp).getHeaderLength();
            tcpSyn += packet.getHeader(udp).getGapLength();
            tcpFlags += packet.getHeader(udp).getGapOffset();

            tcpDstPort = packet.getHeader(udp).destination();
            tcpSrcPort = packet.getHeader(udp).source();

            transportProtocol = 0;
        }
        if (packet.hasHeader(ip)) {
            ipType += packet.getHeader(ip).type();
            payloadLength += packet.getHeader(ip).getPayloadLength();
            payloadOffset += packet.getHeader(ip).getPayloadOffset();
        }

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
            ;

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
            return key.toDebugString();
        }
    }
}