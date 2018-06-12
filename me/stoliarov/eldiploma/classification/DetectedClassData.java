package me.stoliarov.eldiploma.classification;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Владислав on 12.07.2015.
 */
public class DetectedClassData {
    private final SimpleStringProperty name;
    private final SimpleStringProperty bytes;
    private final SimpleStringProperty flows;
    private final SimpleStringProperty packets;

    public DetectedClassData(String name, String bytes, String flows, String packets) {
        this.name = new SimpleStringProperty(name);
        this.bytes = new SimpleStringProperty(bytes);
        this.flows = new SimpleStringProperty(flows);
        this.packets = new SimpleStringProperty(packets);
    }

    public String getName() {

        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getBytes() {
        return bytes.get();
    }

    public void setBytes(String bytes) {
        this.bytes.set(bytes);
    }

    public SimpleStringProperty bytesProperty() {
        return bytes;
    }

    public String getFlows() {
        return flows.get();
    }

    public void setFlows(String flows) {
        this.flows.set(flows);
    }

    public SimpleStringProperty flowsProperty() {
        return flows;
    }

    public String getPackets() {
        return packets.get();
    }

    public void setPackets(String packets) {
        this.packets.set(packets);
    }

    public SimpleStringProperty packetsProperty() {
        return packets;
    }
}
