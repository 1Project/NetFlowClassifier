package me.stoliarov.eldiploma.classification;

import me.stoliarov.eldiploma.pcap.MyFlow;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;
import org.jnetpcap.packet.JFlowMap;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static me.stoliarov.eldiploma.classification.ClassifierHelper.dataSetLoader;

/**
 * Created by Владислав on 11.10.2015.
 */
public class AbstractClassifier {
    private final Map<String, List<MyFlow>> detectedApps;
    private final Dataset dataset;
    private final Classifier classifier;


    public AbstractClassifier(Dataset dataset, Classifier classifier) {
        this.detectedApps = new HashMap<>();
        this.dataset = dataset;
        this.classifier = classifier;
    }

    enum Mode {
        TRAINING,
        WORKING
    }

    public static void train(Collection<MyFlow> flowList, String className, String datasetPath) {
        Dataset dataset = dataSetLoader(datasetPath);
        File file = new File(ClassifierHelper.DATASET_PATH.get());
        flowList.parallelStream().forEach(flow -> {
            DenseInstance di = (DenseInstance) flow.getInstance();
            di.setClassValue(className);
            dataset.add(di);
        });
        try {
            FileHandler.exportDataset(dataset, new File(ClassifierHelper.DATASET_PATH.get()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Map<String, List<MyFlow>> classify(Collection<MyFlow> flowList) {
       classifier.buildClassifier(dataset);

        for (MyFlow flow : flowList) {
            Instance instance = flow.getInstance();

            Object predicted = classifier.classify(instance);
            if (predicted == null) continue;
            if (detectedApps.get(predicted) == null) detectedApps.put((String) predicted, new LinkedList<>());
            List<MyFlow> tmpList = detectedApps.get(predicted);
            tmpList.add(flow);
            detectedApps.put(predicted.toString(), tmpList);
            System.out.println(predicted+flow.toString());
        }
        return detectedApps;
    }
}
