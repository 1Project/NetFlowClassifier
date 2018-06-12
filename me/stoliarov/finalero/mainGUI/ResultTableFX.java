package me.stoliarov.finalero.mainGUI;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by VladS on 5/16/2016.
 */
public class ResultTableFX {

    private final StringProperty classname;
    private final StringProperty packets;
    private final StringProperty flows;
    private final StringProperty size;

    public ResultTableFX() {
        this(null,null,null,null);
    }

    public ResultTableFX(String classname, String packets, String flows, String size) {
        this.classname = new SimpleStringProperty(classname);
        this.packets = new SimpleStringProperty(packets);
        this.flows = new SimpleStringProperty(flows);
        this.size = new SimpleStringProperty(size);
    }

    public String getClassname() {
        return classname.get();
    }

    public StringProperty classnameProperty() {
        return classname;
    }

    public String getPackets() {
        return packets.get();
    }

    public StringProperty packetsProperty() {
        return packets;
    }

    public String getFlows() {
        return flows.get();
    }

    public StringProperty flowsProperty() {
        return flows;
    }

    public String getSize() {
        return size.get();
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public void setClassname(String classname) {
        this.classname.set(classname);
    }

    public void setPackets(String packets) {
        this.packets.set(packets);
    }

    public void setFlows(String flows) {
        this.flows.set(flows);
    }

    public void setSize(String size) {
        this.size.set(size);
    }
}
