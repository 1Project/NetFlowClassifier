package me.stoliarov.eldiploma.domain;

import javax.persistence.*;

/**
 * Created by VladS on 12/10/2015.
 */
@Entity
@Table(name = "flowinstance", schema = "public", catalog = "postgres")
public class FlowInstance {
    public int totPktsQty;
    public int totPktsBytes;
    public int revPktsQty;
    public int revPktsBytes;
    public int fwPktsQty;
    public int fwPktsBytes;
    public long id;

    @Override
    public String toString() {
        return "FlowInstance{" +
                "totPktsQty=" + totPktsQty +
                ", totPktsBytes=" + totPktsBytes +
                ", revPktsQty=" + revPktsQty +
                ", revPktsBytes=" + revPktsBytes +
                ", fwPktsQty=" + fwPktsQty +
                ", fwPktsBytes=" + fwPktsBytes +
                ", id=" + id +
                '}';
    }

    @Basic
    @Column(name = "tot_pkts_qty", nullable = true)
    public int getTotPktsQty() {
        return totPktsQty;
    }

    public void setTotPktsQty(int totPktsQty) {
        this.totPktsQty = totPktsQty;
    }

    @Basic
    @Column(name = "tot_pkts_bytes", nullable = true)
    public int getTotPktsBytes() {
        return totPktsBytes;
    }

    public void setTotPktsBytes(int totPktsBytes) {
        this.totPktsBytes = totPktsBytes;
    }

    @Basic
    @Column(name = "rev_pkts_qty", nullable = true)
    public int getRevPktsQty() {
        return revPktsQty;
    }

    public void setRevPktsQty(int revPktsQty) {
        this.revPktsQty = revPktsQty;
    }

    @Basic
    @Column(name = "rev_pkts_bytes", nullable = true)
    public int getRevPktsBytes() {
        return revPktsBytes;
    }

    public void setRevPktsBytes(int revPktsBytes) {
        this.revPktsBytes = revPktsBytes;
    }

    @Basic
    @Column(name = "fw_pkts_qty", nullable = true)
    public int getFwPktsQty() {
        return fwPktsQty;
    }

    public void setFwPktsQty(int fwPktsQty) {
        this.fwPktsQty = fwPktsQty;
    }

    @Basic
    @Column(name = "fw_pkts_bytes", nullable = true)
    public int getFwPktsBytes() {
        return fwPktsBytes;
    }

    public void setFwPktsBytes(int fwPktsBytes) {
        this.fwPktsBytes = fwPktsBytes;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = totPktsQty;
        result = 31 * result + totPktsBytes;
        result = 31 * result + revPktsQty;
        result = 31 * result + revPktsBytes;
        result = 31 * result + fwPktsQty;
        result = 31 * result + fwPktsBytes;
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
