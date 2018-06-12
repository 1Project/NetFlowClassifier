package me.stoliarov.finalero.algo.eval;

import weka.Run;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.AbstractEvaluationMetric;
import weka.classifiers.evaluation.InformationRetrievalEvaluationMetric;
import weka.classifiers.evaluation.InformationTheoreticEvaluationMetric;
import weka.classifiers.evaluation.StandardEvaluationMetric;
import weka.core.Instances;
import weka.core.Utils;

import java.util.List;

/**
 * Created by VladS on 5/10/2016.
 */
public class Evaluator extends weka.classifiers.evaluation.Evaluation {
    public Evaluator(Instances data) throws Exception {
        super(data);
    }

    public Evaluator(Instances data, CostMatrix costMatrix) throws Exception {
        super(data, costMatrix);
    }

    @Override
    public String toClassDetailsString()  {
        try {
            return toClassDetailsString("=== Показатели классификации по классам ===");
        } catch (Exception e) {
            String msg = "Ошибка про построении показателей классификации";
            System.out.println(msg);
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public String toClassDetailsString(String title) throws Exception {

        if (!m_ClassIsNominal) {
            throw new Exception("Evaluation: No per class statistics possible!");
        }

        boolean displayTP = m_metricsToDisplay.contains("tp rate");
        boolean displayFP = m_metricsToDisplay.contains("fp rate");
        boolean displayP = m_metricsToDisplay.contains("precision");
        boolean displayR = m_metricsToDisplay.contains("recall");
        boolean displayFM = m_metricsToDisplay.contains("f-measure");
        boolean displayMCC = m_metricsToDisplay.contains("mcc");
        boolean displayROC = m_metricsToDisplay.contains("roc area");
        boolean displayPRC = m_metricsToDisplay.contains("prc area");

        StringBuffer text = new StringBuffer(title + "\n                 "
                + (displayTP ? "TP показатель  " : "") + (displayFP ? "FP показатель  " : "")
                + (displayP ? "Достоверность  " : "") + (displayR ? "Полнота   " : "")
                + (displayFM ? " F-оценка " : "") + (displayMCC ? "MCC      " : "")
                + (displayROC ? "ROC площадь  " : "") + (displayPRC ? "PRC площадь  " : ""));

        if (m_pluginMetrics != null && m_pluginMetrics.size() > 0) {
            for (AbstractEvaluationMetric m : m_pluginMetrics) {
                if (m instanceof InformationRetrievalEvaluationMetric
                        && m.appliesToNominalClass()) {
                    String metricName = m.getMetricName().toLowerCase();
                    if (m_metricsToDisplay.contains(metricName)) {
                        List<String> statNames = m.getStatisticNames();
                        for (String name : statNames) {
                            if (m_metricsToDisplay.contains(name.toLowerCase())) {
                                if (name.length() < 7) {
                                    name = Utils.padRight(name, 7);
                                }
                                text.append(name).append("  ");
                            }
                        }
                    }
                }
            }
        }

        text.append("Класс\n");
        for (int i = 0; i < m_NumClasses; i++) {
            text.append("                 ");
            if (displayTP) {
                text.append(String.format("%-15.3f", truePositiveRate(i)));
            }
            if (displayFP) {
                text.append(String.format("%-15.3f", falsePositiveRate(i)));
            }
            if (displayP) {
                text.append(String.format("%-15.3f", precision(i)));
            }
            if (displayR) {
                text.append(String.format("%-11.3f", recall(i)));
            }
            if (displayFM) {
                text.append(String.format("%-9.3f", fMeasure(i)));
            }
            if (displayMCC) {
                double mat = matthewsCorrelationCoefficient(i);
                if (Utils.isMissingValue(mat)) {
                    text.append("?       ");
                } else {
                    text.append(String
                            .format("%-9.3f", matthewsCorrelationCoefficient(i)));
                }
            }

            if (displayROC) {
                double rocVal = areaUnderROC(i);
                if (Utils.isMissingValue(rocVal)) {
                    text.append("?         ");
                } else {
                    text.append(String.format("%-13.3f", rocVal));
                }
            }
            if (displayPRC) {
                double prcVal = areaUnderPRC(i);
                if (Utils.isMissingValue(prcVal)) {
                    text.append("?         ");
                } else {
                    text.append(String.format("%-13.3f", prcVal));
                }
            }

            if (m_pluginMetrics != null && m_pluginMetrics.size() > 0) {
                for (AbstractEvaluationMetric m : m_pluginMetrics) {
                    if (m instanceof InformationRetrievalEvaluationMetric
                            && m.appliesToNominalClass()) {
                        String metricName = m.getMetricName().toLowerCase();
                        if (m_metricsToDisplay.contains(metricName)) {
                            List<String> statNames = m.getStatisticNames();
                            for (String name : statNames) {
                                if (m_metricsToDisplay.contains(name.toLowerCase())) {
                                    double stat = ((InformationRetrievalEvaluationMetric) m)
                                            .getStatistic(name, i);
                                    if (name.length() < 7) {
                                        name = Utils.padRight(name, 7);
                                    }
                                    if (Utils.isMissingValue(stat)) {
                                        Utils.padRight("?", name.length());
                                    } else {
                                        text.append(
                                                String.format("%-" + name.length() + ".3f", stat))
                                                .append("  ");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            text.append(m_ClassNames[i]).append('\n');
        }

        text.append("Взвеш. среднее   ");
        if (displayTP) {
            text.append(String.format("%-15.3f", weightedTruePositiveRate()));
        }
        if (displayFP) {
            text.append(String.format("%-15.3f", weightedFalsePositiveRate()));
        }
        if (displayP) {
            text.append(String.format("%-15.3f", weightedPrecision()));
        }
        if (displayR) {
            text.append(String.format("%-11.3f", weightedRecall()));
        }
        if (displayFM) {
            text.append(String.format("%-9.3f", weightedFMeasure()));
        }
        if (displayMCC) {
            text.append(String.format("%-9.3f", weightedMatthewsCorrelation()));
        }
        if (displayROC) {
            text.append(String.format("%-13.3f", weightedAreaUnderROC()));
        }
        if (displayPRC) {
            text.append(String.format("%-13.3f", weightedAreaUnderPRC()));
        }

        if (m_pluginMetrics != null && m_pluginMetrics.size() > 0) {
            for (AbstractEvaluationMetric m : m_pluginMetrics) {
                if (m instanceof InformationRetrievalEvaluationMetric
                        && m.appliesToNominalClass()) {
                    String metricName = m.getMetricName().toLowerCase();
                    if (m_metricsToDisplay.contains(metricName)) {
                        List<String> statNames = m.getStatisticNames();
                        for (String name : statNames) {
                            if (m_metricsToDisplay.contains(name.toLowerCase())) {
                                double stat = ((InformationRetrievalEvaluationMetric) m)
                                        .getClassWeightedAverageStatistic(name);
                                if (name.length() < 7) {
                                    name = Utils.padRight(name, 7);
                                }
                                if (Utils.isMissingValue(stat)) {
                                    Utils.padRight("?", name.length());
                                } else {
                                    text
                                            .append(String.format("%-" + name.length() + ".3f", stat))
                                            .append("  ");
                                }
                            }
                        }
                    }
                }
            }
        }

        text.append("\n");

        return text.toString();
    }

    @Override
    public String toSummaryString() {
        return super.toSummaryString();
    }

    @Override
    public String toSummaryString(String title, boolean printComplexityStatistics) {

        StringBuffer text = new StringBuffer();

        if (printComplexityStatistics && m_NoPriors) {
            printComplexityStatistics = false;
            System.err
                    .println("Priors disabled, cannot print complexity statistics!");
        }

        text.append(title + "\n");
        try {
            if (m_WithClass > 0) {
                if (m_ClassIsNominal) {
                    boolean displayCorrect = m_metricsToDisplay.contains("correct");
                    boolean displayIncorrect = m_metricsToDisplay.contains("incorrect");

                    if (displayCorrect) {
                        text.append(String.format("%-55s %-10.0f %.4f %%\n", "Правильно класифицированных потоков", correct(), pctCorrect()));
                    }
                    if (displayIncorrect) {
                        text.append(String.format("%-55s %-10.0f %.4f %%\n", "Неравильно класифицированных потоков", incorrect(), pctIncorrect()));
                    }

                    if (m_pluginMetrics != null) {
                        for (AbstractEvaluationMetric m : m_pluginMetrics) {
                            if (m instanceof StandardEvaluationMetric
                                    && m.appliesToNominalClass() && !m.appliesToNumericClass()) {
                                String metricName = m.getMetricName().toLowerCase();
                                boolean display = m_metricsToDisplay.contains(metricName);
                                // For the GUI and the command line StandardEvaluationMetrics
                                // are an "all or nothing" jobby (because we need the user to
                                // supply how they should be displayed and formated via the
                                // toSummaryString() method
                                if (display) {
                                    String formattedS = ((StandardEvaluationMetric) m)
                                            .toSummaryString();
                                    text.append(formattedS);
                                }
                            }
                        }
                    }
                } else {
                    boolean displayCorrelation = m_metricsToDisplay
                            .contains("correlation");
                    if (displayCorrelation) {
                        text.append("Correlation coefficient            ");
                        text.append(Utils.doubleToString(correlationCoefficient(), 12, 4)
                                + "\n");
                    }

                    if (m_pluginMetrics != null) {
                        for (AbstractEvaluationMetric m : m_pluginMetrics) {
                            if (m instanceof StandardEvaluationMetric
                                    && !m.appliesToNominalClass() && m.appliesToNumericClass()) {
                                String metricName = m.getMetricName().toLowerCase();
                                boolean display = m_metricsToDisplay.contains(metricName);
                                List<String> statNames = m.getStatisticNames();
                                for (String s : statNames) {
                                    display = (display && m_metricsToDisplay.contains(s
                                            .toLowerCase()));
                                }
                                if (display) {
                                    String formattedS = ((StandardEvaluationMetric) m)
                                            .toSummaryString();
                                    text.append(formattedS);
                                }
                            }
                        }
                    }
                }


                if (printComplexityStatistics && m_pluginMetrics != null) {
                    for (AbstractEvaluationMetric m : m_pluginMetrics) {
                        if (m instanceof InformationTheoreticEvaluationMetric) {
                            if ((m_ClassIsNominal && m.appliesToNominalClass())
                                    || (!m_ClassIsNominal && m.appliesToNumericClass())) {
                                String metricName = m.getMetricName().toLowerCase();
                                boolean display = m_metricsToDisplay.contains(metricName);
                                List<String> statNames = m.getStatisticNames();
                                for (String s : statNames) {
                                    display = (display && m_metricsToDisplay.contains(s
                                            .toLowerCase()));
                                }
                                if (display) {
                                    String formattedS = ((InformationTheoreticEvaluationMetric) m)
                                            .toSummaryString();
                                    text.append(formattedS);
                                }
                            }
                        }
                    }
                }

                boolean displayMAE = m_metricsToDisplay.contains("mae");
                boolean displayRMSE = m_metricsToDisplay.contains("rmse");
                boolean displayRAE = m_metricsToDisplay.contains("rae");
                boolean displayRRSE = m_metricsToDisplay.contains("rrse");

                if (displayMAE) {
                    text.append(String.format("%-55s %.4f %%\n", "Средняя абсолютная ошибка", meanAbsoluteError()));
                }
                if (displayRMSE) {
                    text.append(String.format("%-55s %.4f %%\n", "Корень из среднеквадратичной ошибки", rootMeanSquaredError()));
                }
                if (!m_NoPriors) {
                    if (displayRAE) {
                        text.append(String.format("%-55s %.4f %%\n","Относительная абсолютная ошибка", relativeAbsoluteError()));
                    }
                    if (displayRRSE) {
                        text.append(String.format("%-55s %.4f %%\n","Корень относительной среднеквадратичной ошибки", rootRelativeSquaredError()));
                    }
                }
                if (m_pluginMetrics != null) {
                    for (AbstractEvaluationMetric m : m_pluginMetrics) {
                        if (m instanceof StandardEvaluationMetric
                                && m.appliesToNominalClass() && m.appliesToNumericClass()) {
                            String metricName = m.getMetricName().toLowerCase();
                            boolean display = m_metricsToDisplay.contains(metricName);
                            List<String> statNames = m.getStatisticNames();
                            for (String s : statNames) {
                                display = (display && m_metricsToDisplay.contains(s
                                        .toLowerCase()));
                            }
                            if (display) {
                                String formattedS = ((StandardEvaluationMetric) m)
                                        .toSummaryString();
                                text.append(formattedS);
                            }
                        }
                    }
                }

            }
            text.append(String.format("%-55s %.4f", "Общее количество потоков", m_WithClass));
        } catch (Exception e) {
            System.out.println("Ошибка при оценке показателей классификации");
            throw new RuntimeException(e);
        }

        return text.toString();
    }

    @Override
    public double[][] confusionMatrix() {
        return super.confusionMatrix();
    }

    @Override
    public double areaUnderROC(int classIndex) {
        return super.areaUnderROC(classIndex);
    }

    @Override
    public double[] evaluateModel(Classifier classifier, Instances data, Object... forPredictionsPrinting) throws Exception {
        return super.evaluateModel(classifier, data, forPredictionsPrinting);
    }


    @Override
    public String toMatrixString() throws Exception {
        return super.toMatrixString();
    }
}
