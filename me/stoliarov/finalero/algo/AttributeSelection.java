package me.stoliarov.finalero.algo;

/**
 * Created by VladS on 5/16/2016.
 */

import me.stoliarov.finalero.loader.FileLoader;
import weka.attributeSelection.*;
import weka.core.*;
import weka.classifiers.*;
import weka.classifiers.meta.*;
import weka.classifiers.trees.*;
import weka.filters.*;

import java.util.*;


public class AttributeSelection {
    /**
     * uses the filter
     */
    public Instances useFilter(Instances data, ASEvaluation evaluator) throws Exception {
        weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();
        GreedyStepwise search = new GreedyStepwise();
        search.setSearchBackwards(true);
        filter.setEvaluator(evaluator);
        filter.setSearch(search);
        filter.setInputFormat(data);
        Instances newData = Filter.useFilter(data, filter);
        return newData;
    }


    /**
     * takes a dataset as first argument
     *
     * @param args the commandline arguments
     * @throws Exception if something goes wrong
     */
    public static void main(String[] args) throws Exception {
        // load data
        System.out.println("\n0. Loading data");
        FileLoader fl = new FileLoader();
        Instances data = fl.read("E://flowclass_test.csv.arff");


        if (data.classIndex() == -1)
            data.setClassIndex(data.numAttributes() - 1);

        AttributeSelection attributeSelection = new AttributeSelection();
        attributeSelection.useFilter(data, new CfsSubsetEval());
    }
}