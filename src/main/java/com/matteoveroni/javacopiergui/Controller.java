package com.matteoveroni.javacopiergui;

import com.matteoveroni.javacopier.CopyListener;
import com.matteoveroni.javacopier.CopyStatusReport;
import com.matteoveroni.javacopier.JavaCopier;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

/**
 * @author Matteo Veroni
 */

// TODO: the controller class should not be the copyListener but a Task running on a separate Thread bound with the ui should be
public class Controller implements Initializable, CopyListener {

    @FXML private BorderPane pane;
    @FXML private Button btn_chooseSrc;
    @FXML private Button btn_chooseDest;
    @FXML private TextField txt_source;
    @FXML private TextField txt_dest;
    @FXML private Button btn_startCopy;
    @FXML private ProgressBar progressBar;
    @FXML private TextArea txtArea_console;

    private final FileChooser fileChooser = new FileChooser();

    @FXML
    void onChooseSource(ActionEvent event) {
//		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
//		fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(pane.getScene().getWindow());
        if (file != null) {
            txt_source.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void onChooseDest(ActionEvent event) {
        File file = fileChooser.showOpenDialog(pane.getScene().getWindow());
        if (file != null) {
            txt_dest.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void onStartCopy(ActionEvent event) {
        progressBar.setVisible(true);
        txtArea_console.clear();

        Path src = Paths.get(txt_source.getText());
        Path dest = Paths.get(txt_dest.getText());

        final CopyListener that = this;
        Thread thread = new Thread(() -> {
            JavaCopier.copy(src, dest, that, StandardCopyOption.REPLACE_EXISTING);
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btn_startCopy.setVisible(false);
        progressBar.setVisible(false);

        txt_source.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.trim().isEmpty()) {
                    btn_startCopy.setVisible(false);
                } else if (!txt_dest.getText().trim().isEmpty()) {
                    btn_startCopy.setVisible(true);
                }
            }
        });

        txt_dest.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.trim().isEmpty()) {
                    btn_startCopy.setVisible(false);
                } else if (!txt_source.getText().trim().isEmpty()) {
                    btn_startCopy.setVisible(true);
                }
            }
        });
    }

    @Override
    public void onCopyProgress(CopyStatusReport copyStatusReport) {
        progressBar.setProgress(copyStatusReport.getCopyPercentage() / 100);
//        Task <Void> task = new Task<Void>() {
//            @Override public Void call() throws InterruptedException {
//                // "message2" time consuming method (this message will be seen).
//                updateMessage("message2");
//
//                // some actions
//                Thread.sleep(3000);
//
//                // "message3" time consuming method (this message will be seen).
//                updateMessage("message3");
//
//                //more  time consuming actions
//                Thread.sleep(7000);
//
//                // this will never be actually be seen because we also set a message
//                // in the task::setOnSucceeded handler.
//                updateMessage("time consuming method is done with success");
//
//                return null;
//            }
//        };
//
//
//        Thread thread = new Thread(() -> {
//            List<CopyHistoryEvent> history = copyStatusReport.getCopyHistory().getHistory();
//            int historySize = history.size();
//
//            CopyHistoryEvent copyHistoryEvent = history.get(historySize - 1);
//            String src = copyHistoryEvent.getSrc().toString();
//            String dest = copyHistoryEvent.getDest().toString();
//            boolean isCopySuccessfull = copyHistoryEvent.isCopySuccessful();
//            String exceptionMessage = copyHistoryEvent.getExceptionMessage();
//            txtArea_console.appendText("Copy of src: " + src + " to dest: " + dest + ((isCopySuccessfull) ? " is successful" : " failed") + "\n");
//        });
//        thread.setDaemon(true);
//        thread.start();
    }

    @Override
    public void onCopyComplete(CopyStatusReport copyStatusReport) {
        progressBar.setVisible(false);
    }
}
