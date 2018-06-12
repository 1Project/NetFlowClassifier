package me.stoliarov.eldiploma.classification;

import javafx.beans.property.SimpleStringProperty;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.tools.data.FileHandler;

import java.io.File;
import java.io.IOException;

/**
 * Created by Владислав on 10.10.2015.
 */
public class ClassifierHelper {

    public static SimpleStringProperty DATASET_PATH = new SimpleStringProperty("C:\\traffic\\DATABASE.dat");
    public static SimpleStringProperty PCAP_PATH = new SimpleStringProperty("");
    public static SimpleStringProperty DATABASE_PATH = new SimpleStringProperty("jdbc:postgresql://localhost:5432/postgres");

    public static enum Mode {TRAIN, CAPTURETODB, CAPTURETOFILE, CAPTURETOBOTH, CLASSIFY}

    public static Dataset dataSetLoader(String datasetPath) {
        Dataset dataset;
        try {
            dataset = FileHandler.loadDataset(new File(DATASET_PATH.get()),
                    0, "\t");
        } catch (IOException e) {
            dataset = new DefaultDataset();
        }
        return dataset;
    }
}
