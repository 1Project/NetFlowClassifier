package me.stoliarov.eldiploma.snifferGUI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.stoliarov.eldiploma.classification.ClassifierHelper;
import me.stoliarov.eldiploma.sniffer.Sniffer;
import org.hibernate.annotations.SourceType;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by VladS on 12/12/2015.
 */
public class SnifferControllerFX implements Initializable {
    private Stage stage;
    private Sniffer sniffer;


    @FXML
    private VBox vBox;
    @FXML
    private TextArea textArea;
    @FXML
    private ToggleButton toggleButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ChoiceBox devsChoiceBox;
    @FXML
    private TextField dbTextField;
    @FXML
    private Button showDbBtn;
    @FXML
    private Button changeDbBtn;
    @FXML
    private Accordion accordion;
    @FXML
    private TitledPane pktsTextArea;
    @FXML
    private TextArea flowsTextArea;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private CheckBox fileCheckBox;
    @FXML
    private CheckBox dbCheckBox;

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
        sniffer = new Sniffer();
        List<PcapIf> alldevs = sniffer.getDevsList();
        AtomicInteger index = new AtomicInteger();
        devsChoiceBox.setItems(FXCollections.observableArrayList(
                alldevs.stream().map(devs -> new String(index.getAndIncrement() + " " + devs.getDescription())).collect(Collectors.toList())));
        dbTextField.textProperty().bind(ClassifierHelper.DATABASE_PATH);
        System.out.println(("Для начала работы выберите сетевое устройство"));
        System.out.println(("Соединение с БД успешно установлено"));
        statusLabel.setVisible(true);
        statusLabel.setText("Программа запущена");
    }



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private Button start;

    @FXML
    private Button stop;

    @FXML
    private Button save;


    @FXML
    void toggle(ActionEvent event) {
        if (toggleButton.isSelected()) {
            start(event);
        } else {
            stop(event);
        }
    }

    @FXML
    void save(ActionEvent event) {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                sniffer.save();
                return null;
            }
        };
        progressBar.setVisible(true);
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
        statusLabel.setText("Сохранено в БД");
    }

    @FXML
    void start(ActionEvent event) {
        sniffer = new Sniffer();
        sniffer.setDevs(devsChoiceBox.getSelectionModel().getSelectedIndex());
        sniffer.setMode(dbCheckBox.isSelected() ?
                (fileCheckBox.isSelected() ? ClassifierHelper.Mode.CAPTURETOBOTH : ClassifierHelper.Mode.CAPTURETODB) :
                (fileCheckBox.isSelected() ? ClassifierHelper.Mode.CAPTURETOFILE : null));
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                sniffer.start();
                return null;
            }
        };
        progressBar.setVisible(true);
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
        statusLabel.setText("Включен захват пакетов");
    }

    @FXML
    void stop(ActionEvent event) {
        progressBar.setVisible(false);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                sniffer.stop();
                return null;
            }
        }).start();
        statusLabel.setText("Захват пакетов окончен");
    }
}
