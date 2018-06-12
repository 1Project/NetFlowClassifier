package me.stoliarov.finalero.mainGUI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import me.stoliarov.finalero.algo.AttributeSelection;
import me.stoliarov.finalero.algo.Classifier;
import me.stoliarov.finalero.algo.eval.Evaluator;
import me.stoliarov.finalero.loader.FileLoader;
import me.stoliarov.finalero.pcap.FlowHandler;
import me.stoliarov.finalero.sniffer.Sniffer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jnetpcap.PcapIf;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import javax.xml.transform.Result;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by VladS on 12/12/2015.
 */
public class ControllerFX implements Initializable {
    private final ToggleGroup group = new ToggleGroup();
    private final FileLoader fileLoader = new FileLoader();
    @FXML
    private VBox vBox;
    @FXML
    private Button analyzerTrainFilePathButton;
    @FXML
    private TextField analyzerTrainFilePathTextField;
    @FXML
    private Button analyzerTestFilePathButton;
    @FXML
    private TextField analyzerTestFilePathTextField;
    @FXML
    private TextArea textArea121;
    @FXML
    private ChoiceBox<String> snifferDevsChoiceBox;
    @FXML
    private ToggleButton snifferStartButton;
    @FXML
    private TextArea snifferTextArea;
    @FXML
    private ChoiceBox<String> classifierAlgoListChoiceBox;
    @FXML
    private Button classifierStartButton;
    @FXML
    private TextArea textArea1;
    @FXML
    private TextArea evaluatorTextArea;
    @FXML
    private TableView<ResultTableFX> resultTableView;
    @FXML
    private TextArea textArea;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button analyzerAddPcapButton;
    @FXML
    private TextArea analyzerTextArea;
    @FXML
    private TextArea classifierTextArea;
    @FXML
    private ChoiceBox<String> featureAlgoListChoiceBox;
    @FXML
    private Button featureStartButton;
    @FXML
    private TextArea featureTextArea;
    @FXML
    private Button analyzerSqlToFileButton;
    @FXML
    private TableColumn<ResultTableFX, String> classnameColumn;
    @FXML
    private TableColumn<ResultTableFX, String> packetsColumn;
    @FXML
    private TableColumn<ResultTableFX, String> flowsColumn;
    @FXML
    private TableColumn<ResultTableFX, String> sizeColumn;


    private Stage stage;
    private Sniffer sniffer;
    private Classifier classifier;
    private Evaluator evaluator;
    private Instances trainDataSet;
    private Instances testDataSet;
    private Session session;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Console setup
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        Console logConsole = new Console(textArea);
        System.setOut(new PrintStream(logConsole));
        analyzerTextArea.setStyle("-fx-font-family: monospace");
        textArea.setStyle("-fx-font-family: monospace");
        classifierTextArea.setStyle("-fx-font-family: monospace");
        evaluatorTextArea.setStyle("-fx-font-family: monospace");
        snifferTextArea.setStyle("-fx-font-family: monospace");

        //Hibernate
        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        session = sessionFactory.openSession();

//       Sniffer.class
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                sniffer = new Sniffer();
                List<PcapIf> alldevs = sniffer.getDevsList();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        AtomicInteger index = new AtomicInteger();
                        snifferDevsChoiceBox.setItems(FXCollections.observableArrayList(
                                alldevs.stream().map(devs -> index.getAndIncrement() + " " + devs.getName() + " " + devs.getDescription()).collect(Collectors.toList())
                        ));
                        snifferTextArea.textProperty().bind(sniffer.stringPropertyProperty());
                    }
                });
                return null;
            }
        });
        snifferStartButton.setToggleGroup(group);
        group.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    snifferStart();
                    return null;
                }
            };

            Thread thread = new Thread(task);
            if (toggle != null) {
                snifferStop();
            } else {
                thread.start();
            }
        });
        //FeatureSelector.class
        String[] featureAlgoList = {"InfoGain", "CFS", "Wrapper"};
        featureAlgoListChoiceBox.setItems(FXCollections.observableArrayList(featureAlgoList));
        //Classifier.class
        String[] algoList = {"C4.5 (J48)", "SVM (SMO)", "AdaBoost", "Bagging"};
        classifierAlgoListChoiceBox.setItems(FXCollections.observableArrayList(algoList));
        statusLabel.setVisible(true);
        statusLabel.setText("Программа запущена");
        //Results.class
        classnameColumn.setCellValueFactory(cellData -> cellData.getValue().classnameProperty());
        packetsColumn.setCellValueFactory(cellData -> cellData.getValue().packetsProperty());
        flowsColumn.setCellValueFactory(cellData -> cellData.getValue().flowsProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        resultTableView.setItems(resultTableFXes);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void analyzerTrainFilePathButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл для обучения");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("ARFF", "*.arff"));
        File file = fileChooser.showOpenDialog(stage);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                trainDataSet = fileLoader.read(file.getAbsolutePath());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        fileIsReadLog(trainDataSet, file);
                        analyzerTrainFilePathTextField.setText(file.getAbsolutePath());
                    }
                });
                return null;
            }
        }).start();
    }

    private void fileIsReadLog(Instances dataSet, File file) {
        analyzerTextArea.appendText(fileLoader.getSummaryString(dataSet));
        statusLabel.setText("Файл " + file.getName() + " прочитан");
        System.out.println("Результаты чтения файла " + file.getName() + " доступны во вкладке \"Исходные данные\"");
    }

    @FXML
    void analyzerTestFilePathButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл для тестирования");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("ARFF", "*.arff"));
        File file = fileChooser.showOpenDialog(stage);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                testDataSet = fileLoader.read(file.getAbsolutePath());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        fileIsReadLog(testDataSet, file);
                        analyzerTestFilePathTextField.setText(file.getAbsolutePath());
                    }
                });
                return null;
            }
        }).start();
    }

    @FXML
    void classifierStartButtonClick(ActionEvent event) {
        classifier = new Classifier();
        if ((testDataSet) == null | (trainDataSet) == null) {
            String msg = "Не выбран файл для обучения или тестирования!";
            System.out.println(msg);
            throw new RuntimeException(msg);
        }

        try {
            final Long[] timeToBuild = new Long[1];
            final Long[] timeToEvaluate = new Long[1];
            new Thread(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    classifier.setTrainDataSet(trainDataSet);
                    classifier.setTestDataSet(testDataSet);
                    switch (classifierAlgoListChoiceBox.getSelectionModel().getSelectedIndex()) {
                        case 0:
                            classifier.setClassifier(new J48());
                            break;
                        case 1:
                            classifier.setClassifier(new SMO());
                            break;
                        case 2:
                            classifier.setClassifier(new AdaBoostM1());
                            break;
                        case 3:
                            classifier.setClassifier(new Bagging());
                        default:
                            throw new RuntimeException("Алгоритм классификации не выбран!");
                    }
                    Platform.runLater(() -> {
                        System.out.println("Построение модели классификатора и проведение оценки");
                        statusLabel.setText("Построение модели классификатора");
                        progressBar.setProgress(-1.0);
                    });
                    Remove rm = new Remove();
                    classifier.setFilter(rm);
                    classifier.buildClassifier(trainDataSet);
                    long startTime = System.currentTimeMillis();
                    evaluator = new Evaluator(trainDataSet);
                    long finishTime = System.currentTimeMillis();
                    timeToBuild[0] = (finishTime - startTime);
                    startTime = System.currentTimeMillis();
                    evaluator.evaluateModel(classifier.getClassifier(), testDataSet);
                    finishTime = System.currentTimeMillis();
                    timeToEvaluate[0] = (finishTime - startTime);
                    Platform.runLater(() -> {
                        classifierTextArea.appendText("Время, затраченное на построение модели - " +
                                timeToBuild[0] + "мс. \n");
                        evaluatorTextArea.appendText("Время, затраченное на тестирование - " +
                                timeToEvaluate[0] + "мс. \n\n");
                        evaluatorTextArea.appendText(evaluator.toClassDetailsString() + "\n");
                        classifierTextArea.appendText(evaluator.toSummaryString() + "\n");
                        double[][] cmMatrix = evaluator.confusionMatrix();
                        evaluatorTextArea.appendText("Матрица запутанности:\n");
                        for (int i = 0; i < cmMatrix.length; i++) {
                            for (int j = 0; j < cmMatrix[0].length; j++) {
                                String f = String.format("%6.0f", cmMatrix[i][j],
                                        testDataSet.classAttribute().value((int) testDataSet.instance(i).classValue()));
                                evaluatorTextArea.appendText(f);
                            }
                            evaluatorTextArea.appendText("\n");
                        }
                        System.out.println("Классификация алгоритмом "
                                + classifier.getClassifier().toString() + " проведена, ee результаты доступны во вкладке \"Оценка\"");
                        statusLabel.setText("Классификация окончена");
                        progressBar.progressProperty().setValue(100);
                    });
                    return null;
                }
            }).start();
        } catch (Exception e) {
            System.out.println("Ошибка при оценке результатов классификации!");
            throw new RuntimeException(e);
        }
    }


    private void snifferStart() {
        sniffer = new Sniffer();
        FlowHandler flowHandler = new FlowHandler(sniffer.getPcap(), "?", FlowHandler.Mode.ONLINE);
        flowHandler.setClassifier(classifier);
        sniffer.setFlowHandler(flowHandler);

        int devs = snifferDevsChoiceBox.getSelectionModel().getSelectedIndex();
        if (devs == -1) {
            String msg = "Не выбрана сетевая карта для захвата пакетов!";
            System.out.println(msg);
            throw new RuntimeException(msg);
        }
        sniffer.setDevs(devs);
        String netCard = sniffer.getDevice().getName() + " " + sniffer.getDevice().getDescription();
        Platform.runLater(() -> {
            statusLabel.setText("Пакетный сниффер включен");
            System.out.println("Включен захват пакетов с карточки " +
                    netCard);
        });
        sniffer.start();

    }

    private void snifferStop() {
        sniffer.setReadyToBreak(true);
        Platform.runLater(new Runnable() {
                              @Override
                              public void run() {
                                  statusLabel.setText("Пакетный сниффер выключен");
                                  System.out.println("Захват пакетов с карточки " +
                                          sniffer.getDevice().getName() + " " + sniffer.getDevice().getDescription() +
                                          " выключен");
                              }

                          }
        );
    }


    @FXML
    void analyzerAddPcapButtonClick(ActionEvent event) {

    }

    @FXML
    void analyzerSqlToFileButtonClick(ActionEvent event) {

    }

    @FXML
    void featureStartButtonClick(ActionEvent event) {
//        featureSelector = new FeatureSelector();
        if ((trainDataSet) == null) {
            String msg = "Не выбран файл для обучения или тестирования!";
            System.out.println(msg);
            throw new RuntimeException(msg);
        }

        try {
            final Long[] timeToBuild = new Long[1];
            final Long[] timeToEvaluate = new Long[1];
            new Thread(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ASEvaluation evaluation;
                    switch (featureAlgoListChoiceBox.getSelectionModel().getSelectedIndex()) {
                        case 0:
                            evaluation = new InfoGainAttributeEval();
                            break;
                        case 1:
                            evaluation = new CfsSubsetEval();
                            break;
                        default:
                            throw new RuntimeException("Алгоритм выбора признаков не выбран!");
                    }
                    Platform.runLater(() -> {
                        System.out.println("Выбор признаков и проведение оценки");
                        statusLabel.setText("Выбор признаков");
                        progressBar.setProgress(-1.0);
                    });
                    AttributeSelection attributeSelection = new AttributeSelection();
                    String result = attributeSelection.useFilter(trainDataSet, evaluation).toString();
                    Platform.runLater(() -> {
                        featureTextArea.appendText(result);
                        System.out.println("Выбор признаков алгоритмом"
                                + evaluation.getClass().getSimpleName() + " окончен, результаты доступны во вкладке \"Выбор признаков\"");
                        statusLabel.setText("Выбор признаков окончен");
                        progressBar.progressProperty().setValue(100);
                    });
                    return null;
                }
            }).start();
        } catch (Exception e) {
            System.out.println("Ошибка при выборе признаков!");
            throw new RuntimeException(e);
        }
    }

    public static class Console extends OutputStream {
        private TextArea output;
        private PrintStream out;
        private ArrayList<Byte> bytes = new ArrayList<>();

        public Console(TextArea ta) {
            this.output = ta;
            out = System.out;
        }

        @Override
        public void write(int i) throws IOException {
            Platform.runLater(() -> {
                bytes.add((byte) i);

                byte[] array = new byte[bytes.size()];
                int q = 0;
                for (Byte current : bytes) {
                    array[q] = current;
                    q++;
                }
                try {
                    output.setText(new String(array, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                out.write(i);
            });
        }
    }

    public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Исключение!");
                    alert.setHeaderText(throwable.toString());
                    alert.setContentText(throwable.getMessage());
                    throwable.printStackTrace();
                    // Create expandable Exception.
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwable.printStackTrace(pw);
                    String exceptionText = sw.toString();

                    Label label = new Label("Подробности исключения:");

                    TextArea textArea = new TextArea(exceptionText);
                    textArea.setEditable(false);
                    textArea.setWrapText(true);

                    textArea.setMaxWidth(Double.MAX_VALUE);
                    textArea.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setVgrow(textArea, Priority.ALWAYS);
                    GridPane.setHgrow(textArea, Priority.ALWAYS);

                    GridPane expContent = new GridPane();
                    expContent.setMaxWidth(Double.MAX_VALUE);
                    expContent.add(label, 0, 0);
                    expContent.add(textArea, 0, 1);

                    // Set expandable Exception into the dialog pane.
                    alert.getDialogPane().setExpandableContent(expContent);
                    alert.getDialogPane().setExpanded(true);

                    alert.showAndWait();
                }
            });
        }
    }
}
