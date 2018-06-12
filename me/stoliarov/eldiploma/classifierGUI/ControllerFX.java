package me.stoliarov.eldiploma.classifierGUI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import me.stoliarov.eldiploma.classification.AbstractClassifier;
import me.stoliarov.eldiploma.classification.ClassifierHelper;
import me.stoliarov.eldiploma.classification.DetectedClassData;
import me.stoliarov.eldiploma.domain.FlowInstance;
import me.stoliarov.eldiploma.pcap.MyFlow;
import me.stoliarov.eldiploma.pcap.MyFlowMap;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.weka.WekaClassifier;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JFlow;
import org.jnetpcap.packet.JFlowMap;
import org.jnetpcap.packet.PcapPacketHandler;
import weka.classifiers.functions.SMO;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by Владислав on 11.10.2015.
 */

public class ControllerFX implements Initializable {
    private Stage stage;


    @FXML
    private Button sqlToFileButton;
    @FXML
    private TextField pcapPathTextField;
    @FXML
    private TableView<DetectedClassData> resultTable;
    @FXML
    private VBox vBox;
    @FXML
    private TextField dbPathTextArea;
    @FXML
    private Button changeDbPathBtn;
    @FXML
    private Button openDbBtn;
    @FXML
    private ChoiceBox modeChoiceBox;
    @FXML
    private Button runBtn;
    @FXML
    private Button changePcapPathBtn;
    @FXML
    private Button openPcapBtn;
    @FXML
    private TextArea textArea;
    @FXML
    private ChoiceBox<?> featureAlgoListChoiceBox;
    @FXML
    private Button featureStartButton;
    @FXML
    private TextArea featureTextArea;

    public void appendOut(String str) {
        Platform.runLater(() -> textArea.appendText(str));
    }

    public void appendErr(String str) {
        Platform.runLater(() -> textArea.appendText(str));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int i) throws IOException {
                appendErr(String.valueOf((char) i));
            }
        }, true));
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int i) throws IOException {
                appendOut(String.valueOf((char) i));
            }
        }, true));

        modeChoiceBox.setItems(FXCollections.observableArrayList("Обучения", "Классификации"));
        dbPathTextArea.textProperty().bind(ClassifierHelper.DATASET_PATH);
        pcapPathTextField.textProperty().bind(ClassifierHelper.PCAP_PATH);
        System.out.println(("Программа запущена"));
    }

    private TableColumn tableColumnHelper(String text, String cellValue) {
        TableColumn tableColumn = new TableColumn();
        tableColumn.setText(text);
        tableColumn.setCellValueFactory(new PropertyValueFactory<DetectedClassData, String>(cellValue));
        return tableColumn;
    }

    @FXML
    void changeDbPath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с набором данных");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("DAT", "*.dat"));
        File file = fileChooser.showOpenDialog(stage);
        ClassifierHelper.DATASET_PATH.set(file.getAbsolutePath());
    }

    @FXML
    void changePcapPath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с пакетами");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PCAP", "*.pcap"));
        File file = fileChooser.showOpenDialog(stage);
        ClassifierHelper.PCAP_PATH.set(file.getAbsolutePath());
    }

    @FXML
    void openDb(ActionEvent event) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ClassifierHelper.DATASET_PATH.get()))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            WebView webView = new WebView();
            webView.getEngine().loadContent(sb.toString(), "text/plain");
            Scene scene = new Scene(webView);
            Stage st = new Stage();
            st.setScene(scene);
            st.show();
        }
    }

    @FXML
    void openPcap(ActionEvent event) {
        String location = "";
        try {
            location = new File(pcapPathTextField.getText()).toURI().toURL().toExternalForm();
        } catch (IOException ioe) {
            System.err.println("err");
        }
        WebView webView = new WebView();
        webView.getEngine().load(location);
    }

    @FXML
    void run(ActionEvent event) {
        int selectedIdx = modeChoiceBox.getSelectionModel().getSelectedIndex();
        System.out.println(selectedIdx);

        if (selectedIdx == 0) {
            train();
        } else {
            classify();
        }
    }

    @FXML
    void train() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Диалог");
        dialog.setHeaderText(null);
        dialog.setContentText("Введите название класса:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> System.out.println("Будет произведено обучение класса " + name));

        String pcapPath = pcapPathTextField.getText();
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (pcapPath != null) {
                    final StringBuilder errbuff = new StringBuilder();
                    final Pcap pcap = Pcap.openOffline(pcapPath, errbuff);
                    if (pcap == null) System.out.println("Ошибка чтения файла " + pcapPath);

                    MyFlowMap flowMap = new MyFlowMap(ClassifierHelper.Mode.TRAIN, 100);
                    assert pcap != null;
                    pcap.loop(Pcap.LOOP_INFINITE, (PcapPacketHandler<Object>) (packet, user) -> {
                                flowMap.nextPacket(packet, user);
                            }, null
                    );
                    AbstractClassifier.train(flowMap.values(), result.get(), ClassifierHelper.DATASET_PATH.get());
                    System.out.println(flowMap.toString());
                    pcap.close();
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    void classify() {
        System.out.println("Начало");
        String pcapPath = pcapPathTextField.getText();
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (pcapPath != null) {
                    System.out.println("Чтение файла с пакетами");
                    final StringBuilder errbuff = new StringBuilder();
                    final Pcap pcap = Pcap.openOffline(pcapPath, errbuff);

                    if (pcap == null) System.out.println("Ошибка чтения файла " + pcapPath);
                    MyFlowMap flowMap = new MyFlowMap(ClassifierHelper.Mode.CLASSIFY, 100);
                    pcap.loop(Pcap.LOOP_INFINITE, flowMap, null);
                    System.out.println("Найдено " + flowMap.getTotalPacketCount() + " пакетов");
                    System.out.println("Распределение пакетов по потокам");
                    System.out.println("Найдено " + flowMap.size() + " потоков");
                    Dataset dataset = ClassifierHelper.dataSetLoader(dbPathTextArea.getText());
                    System.out.println(("Классификация потоков"));
                    Classifier cls = new WekaClassifier(new SMO()); // HERE
                    AbstractClassifier classifier = new AbstractClassifier(dataset, cls);
                    Map<String, List<MyFlow>> labeledFlowList = classifier.classify(flowMap.values());
                    System.out.println(("Создание таблицы с результатами классификации"));
                    labeledFlowList.forEach((s, myFlows) -> System.out.println(s +
                            myFlows.stream().map(MyFlow::getFlowInstance).map(FlowInstance::getTotPktsBytes).reduce((a,b) -> a+b).toString()));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
