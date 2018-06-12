package me.stoliarov.finalero.loader;

import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by VladS on 5/10/2016.
 */
public class FileLoader implements Loader {
    public class FileInstances extends Instances {
        public FileInstances(String name, ArrayList<Attribute> attInfo, int capacity) {
            super(name, attInfo, capacity);
        }

        public FileInstances(Instances dataset) {
            super(dataset);
        }

        @Override
        public String toSummaryString() {
            StringBuffer result = new StringBuffer();
            result.append("Количество объектов:  ").append(numInstances()).append('\n');
            result.append("Количество атрибутов: ").append(numAttributes()).append('\n');
            result.append('\n');

            result.append(Utils.padLeft("", 5)).append(Utils.padRight("Название атрибута", 20));
            result.append(Utils.padLeft("Тип", 6)).append(Utils.padLeft("Строка", 10));
            result.append(Utils.padLeft("Целое", 8)).append(Utils.padLeft("Веществ.", 9));
            result.append(Utils.padLeft("Уникальное", 11));
            result.append(Utils.padLeft("Значения", 9)).append('\n');

            // Figure out how many digits we need for the index
            int numDigits = (int)Math.log10((int)numAttributes()) + 1;

            for (int i = 0; i < numAttributes(); i++) {
                Attribute a = attribute(i);
                AttributeStats as = attributeStats(i);
                result.append(Utils.padLeft("" + (i + 1), numDigits)).append(' ');
                result.append(Utils.padRight(a.name(), 22)).append(' ');
                long percent;
                switch (a.type()) {
                    case Attribute.NOMINAL:
                        result.append(Utils.padLeft("Строка  ", 8)).append(' ');
                        percent = Math.round(100.0 * as.intCount / as.totalCount);
                        result.append(Utils.padLeft("" + percent, 5)).append("% ");
                        result.append(Utils.padLeft("" + 0, 6)).append("% ");
                        percent = Math.round(100.0 * as.realCount / as.totalCount);
                        result.append(Utils.padLeft("" + percent, 5)).append("% ");
                        break;
                    case Attribute.NUMERIC:
                        result.append(Utils.padLeft("Веществ.", 8)).append(' ');
                        result.append(Utils.padLeft("" + 0, 5)).append("% ");
                        percent = Math.round(100.0 * as.intCount / as.totalCount);
                        result.append(Utils.padLeft("" + percent, 6)).append("% ");
                        percent = Math.round(100.0 * as.realCount / as.totalCount);
                        result.append(Utils.padLeft("" + percent, 5)).append("% ");
                        break;
                    default:
                        result.append(Utils.padLeft("???", 4)).append(' ');
                        result.append(Utils.padLeft("" + 0, 3)).append("% ");
                        percent = Math.round(100.0 * as.intCount / as.totalCount);
                        result.append(Utils.padLeft("" + percent, 3)).append("% ");
                        percent = Math.round(100.0 * as.realCount / as.totalCount);
                        result.append(Utils.padLeft("" + percent, 3)).append("% ");
                        break;
                }
                result.append(Utils.padLeft("" + as.uniqueCount, 5)).append(" /");
                percent = Math.round(100.0 * as.uniqueCount / as.totalCount);
                result.append(Utils.padLeft("" + percent, 3)).append("% ");
                result.append(Utils.padLeft("" + as.distinctCount, 5)).append(' ');
                result.append("\n");
            }
            return result.toString();
        }

    }

    @Override
    public Instances read(String filename) {
        ConverterUtils.DataSource source;
        Instances data;
        try {
            source = new ConverterUtils.DataSource(filename);
            data = source.getDataSet();
            data.setClassIndex(data.numAttributes()-1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (Instances) data;
    }

    public String getSummaryString(Instances instances) {
        return new FileInstances(instances).toSummaryString();
    }
}
