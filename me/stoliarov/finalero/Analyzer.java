package me.stoliarov.finalero;

import me.stoliarov.eldiploma.classification.AbstractClassifier;
import me.stoliarov.eldiploma.domain.FlowClass;
import me.stoliarov.finalero.algo.eval.Evaluator;
import me.stoliarov.finalero.loader.DatabaseLoader;
import me.stoliarov.finalero.loader.FileLoader;
import me.stoliarov.finalero.pcap.FlowHandler;
import me.stoliarov.finalero.sniffer.Sniffer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Standardize;

import java.io.BufferedReader;
import java.io.File;

import static weka.core.converters.ConverterUtils.*;

/**
 * Created by VladS on 5/4/2016.
 */
public class Analyzer {
    public static void main(String[] args) throws Exception{
        Analyzer analyzer = new Analyzer();
        FileLoader fl = new FileLoader();
        Instances train = fl.read("E://api//flowclass_train.arff");
        train.setClassIndex(0);
        Instances test = fl.read("E://api//flowclass_test.arff");
        test.setClassIndex(0);

//        Standardize filter = new Standardize();
//        filter.setInputFormat(train);
        //filtering not needed for now
        Remove rm = new Remove();
//        rm.setAttributeIndices("1"); //TODO for SQL extraction

        //classifier
        Bagging j48 = new Bagging();
        //meta-classifier for filtering
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(rm);
        fc.setClassifier(j48);
        fc.buildClassifier(train);

        for (int i = 0; i < test.numInstances(); i++) {
            double prediction = fc.classifyInstance(test.instance(i));
            System.out.print("ID: " + test.instance(i).value(0));
            System.out.print(", actual: " + test.classAttribute().value((int) test.instance(i).classValue()));
            System.out.println(", predicted: " + test.classAttribute().value((int) prediction));
        }
        Sniffer sniffer = new Sniffer();
        sniffer.setDevs(5);
        FlowHandler flowHandler = new FlowHandler(sniffer.getPcap(), "?", FlowHandler.Mode.ONLINE);
        flowHandler.setClassifier(fc);
        sniffer.setFlowHandler(flowHandler);
        sniffer.start();
    }




    public Instances filter(Instances instances) {
        try {
            Remove rm = new Remove();
            rm.setAttributeIndices("7");
            rm.setInputFormat(instances);

            return Filter.useFilter(instances, rm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
