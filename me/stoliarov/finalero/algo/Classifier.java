package me.stoliarov.finalero.algo;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 * Created by VladS on 5/13/2016.
 */
public class Classifier extends FilteredClassifier implements IClassifier {
    private static final J48 j48 = new J48();
    private static final Bagging bagging = new Bagging();
    private static final SMO smo = new SMO();
    private static final AdaBoostM1 adaBoost = new AdaBoostM1();
    private Instances trainDataSet;
    private Instances testDataSet;
    private Mode mode;
    private TestMode testMode;
    private DataSource dataSource;
    private weka.classifiers.Classifier classifier;

    public Classifier() {
    }


    public Classifier(Instances trainDataSet, Instances testDataSet, Mode mode, TestMode testMode, DataSource dataSource) {
        this.trainDataSet = trainDataSet;
        this.testDataSet = testDataSet;
        this.mode = mode;
        this.testMode = testMode;
        this.dataSource = dataSource;
    }

    public static J48 getJ48() {
        return j48;
    }

    public static Bagging getBagging() {
        return bagging;
    }

    public static SMO getSmo() {
        return smo;
    }

    public static AdaBoostM1 getAdaBoost() {
        return adaBoost;
    }


    public Instances getTrainDataSet() {
        return trainDataSet;
    }

    public void setTrainDataSet(Instances trainDataSet) {
        this.trainDataSet = trainDataSet;
    }

    public Instances getTestDataSet() {
        return testDataSet;
    }

    public void setTestDataSet(Instances testDataSet) {
        this.testDataSet = testDataSet;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public TestMode getTestMode() {
        return testMode;
    }

    public void setTestMode(TestMode testMode) {
        this.testMode = testMode;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
