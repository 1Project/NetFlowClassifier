package me.stoliarov.finalero.domain;

import javax.persistence.*;

/**
 * Created by VladS on 4/21/2016.
 */
@Entity
@Table(name = "flowclass", schema = "public", catalog = "postgres")
public class FlowClass {
    public String className;
    public double totPktsQty;
    public double totPktsBytes;
    public double revPktsQty;
    public double revPktsBytes;
    public double fwPktsQty;
    public double fwPktsBytes;
    public long class_id;
    public int isReversable;
    public double transportProtocol;
    public double tcpSrcPort;
    public double tcpDstPort;
    public double tcpFlowDir;
    private double payloadLength;
    private double payloadOffset;
    private double tcpFlags;
    private double tcpAck;
    private double tcpSyn;
    private double headerCount;

    @Override
    public String toString() {
        return "FlowClass{" +
                "className='" + className + '\'' +
                ", totPktsQty=" + totPktsQty +
                ", totPktsBytes=" + totPktsBytes +
                ", revPktsQty=" + revPktsQty +
                ", revPktsBytes=" + revPktsBytes +
                ", fwPktsQty=" + fwPktsQty +
                ", fwPktsBytes=" + fwPktsBytes +
                ", class_id=" + class_id +
                ", isReversable=" + isReversable +
                ", transportProtocol=" + transportProtocol +
                ", tcpSrcPort=" + tcpSrcPort +
                ", tcpDstPort=" + tcpDstPort +
                ", tcpFlowDir=" + tcpFlowDir +
                ", payloadLength=" + payloadLength +
                ", payloadOffset=" + payloadOffset +
                ", tcpFlags=" + tcpFlags +
                ", tcpAck=" + tcpAck +
                ", tcpSyn=" + tcpSyn +
                ", headerCount=" + headerCount +
                ", wirelen=" + wirelen +
                ", ipType=" + ipType +
                '}';
    }

    private double wirelen;
    private double ipType;

    @Basic
    @Column(name = "is_reversable")
    public int getIsReversable() {
        return isReversable;
    }

    public void setIsReversable(int isReversable) {
        this.isReversable = isReversable;
    }

    @Column(name = "transport_protocol")
    public double getTransportProtocol() {
        return transportProtocol;
    }

    public void setTransportProtocol(double transportProtocol) {
        this.transportProtocol = transportProtocol;
    }

    @Column(name = "tcp_src_port")
    public double getTcpSrcPort() {
        return tcpSrcPort;
    }

    public void setTcpSrcPort(double tcpSrcPort) {
        this.tcpSrcPort = tcpSrcPort;
    }

    @Column(name = "tcp_dst_port")
    public double getTcpDstPort() {
        return tcpDstPort;
    }

    public void setTcpDstPort(double tcpDstPort) {
        this.tcpDstPort = tcpDstPort;
    }

    @Column(name = "tcp_flow_dir")
    public double getTcpFlowDir() {
        return tcpFlowDir;
    }

    public void setTcpFlowDir(double tcpFlowDir) {
        this.tcpFlowDir = tcpFlowDir;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowClass flowClass = (FlowClass) o;

        if (Double.compare(flowClass.totPktsQty, totPktsQty) != 0) return false;
        if (Double.compare(flowClass.totPktsBytes, totPktsBytes) != 0) return false;
        if (Double.compare(flowClass.revPktsQty, revPktsQty) != 0) return false;
        if (Double.compare(flowClass.revPktsBytes, revPktsBytes) != 0) return false;
        if (Double.compare(flowClass.fwPktsQty, fwPktsQty) != 0) return false;
        if (Double.compare(flowClass.fwPktsBytes, fwPktsBytes) != 0) return false;
        if (class_id != flowClass.class_id) return false;
        if (isReversable != flowClass.isReversable) return false;
        if (Double.compare(flowClass.transportProtocol, transportProtocol) != 0) return false;
        if (Double.compare(flowClass.tcpSrcPort, tcpSrcPort) != 0) return false;
        if (Double.compare(flowClass.tcpDstPort, tcpDstPort) != 0) return false;
        if (Double.compare(flowClass.tcpFlowDir, tcpFlowDir) != 0) return false;
        if (Double.compare(flowClass.payloadLength, payloadLength) != 0) return false;
        if (Double.compare(flowClass.payloadOffset, payloadOffset) != 0) return false;
        if (Double.compare(flowClass.tcpFlags, tcpFlags) != 0) return false;
        if (Double.compare(flowClass.tcpAck, tcpAck) != 0) return false;
        if (Double.compare(flowClass.tcpSyn, tcpSyn) != 0) return false;
        if (Double.compare(flowClass.headerCount, headerCount) != 0) return false;
        if (Double.compare(flowClass.wirelen, wirelen) != 0) return false;
        if (Double.compare(flowClass.ipType, ipType) != 0) return false;
        return className != null ? className.equals(flowClass.className) : flowClass.className == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = className != null ? className.hashCode() : 0;
        temp = Double.doubleToLongBits(totPktsQty);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totPktsBytes);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(revPktsQty);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(revPktsBytes);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fwPktsQty);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fwPktsBytes);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (class_id ^ (class_id >>> 32));
        result = 31 * result + isReversable;
        temp = Double.doubleToLongBits(transportProtocol);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tcpSrcPort);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tcpDstPort);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tcpFlowDir);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(payloadLength);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(payloadOffset);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tcpFlags);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tcpAck);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(tcpSyn);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(headerCount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(wirelen);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ipType);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


    @Column(name = "payload_length")
    public double getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(double payloadLength) {
        this.payloadLength = payloadLength;
    }

    @Column(name = "payload_offset")
    public double getPayloadOffset() {
        return payloadOffset;
    }

    public void setPayloadOffset(double payloadOffset) {
        this.payloadOffset = payloadOffset;
    }

    @Column(name = "tcp_flags")
    public double getTcpFlags() {
        return tcpFlags;
    }

    public void setTcpFlags(double tcpFlags) {
        this.tcpFlags = tcpFlags;
    }

    @Column(name = "tcp_ack")
    public double getTcpAck() {
        return tcpAck;
    }

    public void setTcpAck(double tcpAck) {
        this.tcpAck = tcpAck;
    }

    @Column(name = "tcp_syn")
    public double getTcpSyn() {
        return tcpSyn;
    }

    public void setTcpSyn(double tcpSyn) {
        this.tcpSyn = tcpSyn;
    }

    @Column(name = "header_count")
    public double getHeaderCount() {
        return headerCount;
    }

    public void setHeaderCount(double headerCount) {
        this.headerCount = headerCount;
    }

    @Column(name = "wirelen")
    public double getWirelen() {
        return wirelen;
    }

    public void setWirelen(double wirelen) {
        this.wirelen = wirelen;
    }

    @Basic
    @Column(name = "classname", nullable = true)
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Basic
    @Column(name = "tot_pkts_qty", nullable = true)
    public double getTotPktsQty() {
        return totPktsQty;
    }

    public void setTotPktsQty(double totPktsQty) {
        this.totPktsQty = totPktsQty;
    }

    @Basic
    @Column(name = "tot_pkts_bytes", nullable = true)
    public double getTotPktsBytes() {
        return totPktsBytes;
    }

    public void setTotPktsBytes(double totPktsBytes) {
        this.totPktsBytes = totPktsBytes;
    }

    @Basic
    @Column(name = "rev_pkts_qty", nullable = true)
    public double getRevPktsQty() {
        return revPktsQty;
    }

    public void setRevPktsQty(double revPktsQty) {
        this.revPktsQty = revPktsQty;
    }

    @Basic
    @Column(name = "rev_pkts_bytes", nullable = true)
    public double getRevPktsBytes() {
        return revPktsBytes;
    }

    public void setRevPktsBytes(double revPktsBytes) {
        this.revPktsBytes = revPktsBytes;
    }

    @Basic
    @Column(name = "fw_pkts_qty", nullable = true)
    public double getFwPktsQty() {
        return fwPktsQty;
    }

    public void setFwPktsQty(double fwPktsQty) {
        this.fwPktsQty = fwPktsQty;
    }

    @Basic
    @Column(name = "fw_pkts_bytes", nullable = true)
    public double getFwPktsBytes() {
        return fwPktsBytes;
    }

    public void setFwPktsBytes(double fwPktsBytes) {
        this.fwPktsBytes = fwPktsBytes;
    }

    @Id
    @SequenceGenerator(name = "flowclass_flowclass_id_seq",
            sequenceName = "flowclass_flowclass_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "flowclass_flowclass_id_seq")
    @Column(name = "flowclass_id", updatable = false)
    public long getId() {
        return class_id;
    }

    public void setId(long id) {
        this.class_id = id;
    }

    public void setIpType(double ipType) {
        this.ipType = ipType;
    }
}
