package com.matteoveroni.javacopiergui;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
public class Controller implements Initializable {

    @FXML private BorderPane pane;
    @FXML private Button btn_chooseSrc;
    @FXML private Button btn_chooseDest;
    @FXML private TextField txt_source;
    @FXML private TextField txt_dest;
    @FXML private Button btn_startCopy;
    @FXML private BorderPane pane_copyStatusArea;
    @FXML private Label lbl_progressPercentage;
    @FXML private ProgressBar progressBar;
    @FXML private Label lbl_progressText;
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
        txtArea_console.clear();
        pane_copyStatusArea.setVisible(true);

        Path src = Paths.get(txt_source.getText());
        Path dest = Paths.get(txt_dest.getText());

        CopyTask copyTask = new CopyTask(src, dest, StandardCopyOption.REPLACE_EXISTING);
        lbl_progressPercentage.textProperty().bind(Bindings.format("%.1f%%", copyTask.progressProperty().multiply(100)));
        progressBar.progressProperty().bind(copyTask.progressProperty());
        lbl_progressText.textProperty().bind(copyTask.messageProperty());
        ChangeListener copyMessageChangeListener = (observable, oldValue, newValue) -> txtArea_console.appendText(copyTask.getMessage() + "\n");
        copyTask.messageProperty().addListener(copyMessageChangeListener);
        copyTask.setOnSucceeded(s -> {
            clearBindingsAndListeners(copyTask, copyMessageChangeListener);
            pane_copyStatusArea.setVisible(false);
        });
        copyTask.setOnFailed(f -> {
            clearBindingsAndListeners(copyTask, copyMessageChangeListener);
            pane_copyStatusArea.setVisible(false);
        });
        copyTask.setOnCancelled(c -> {
            clearBindingsAndListeners(copyTask, copyMessageChangeListener);
            pane_copyStatusArea.setVisible(false);
        });

        Thread copyThread = new Thread(copyTask);
        // if copyThread is a daemon the copy doesnt stop when the ui is closed
        // thread.setDaemon(true);
        copyThread.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btn_startCopy.setVisible(true);
        pane_copyStatusArea.setVisible(false);

        txt_source.setText("C:\\Users\\veroni\\Downloads");
        txt_dest.setText("C:\\Users\\veroni\\inesistente");

        txt_source.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                btn_startCopy.setVisible(false);
            } else if (!txt_dest.getText().trim().isEmpty()) {
                btn_startCopy.setVisible(true);
            }
        });

        txt_dest.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                btn_startCopy.setVisible(false);
            } else if (!txt_source.getText().trim().isEmpty()) {
                btn_startCopy.setVisible(true);
            }
        });
    }

    private void clearBindingsAndListeners(CopyTask copyTask, ChangeListener copyMessageChangeListener) {
        lbl_progressPercentage.textProperty().unbind();
        progressBar.progressProperty().unbind();
        lbl_progressText.textProperty().unbind();
        copyTask.messageProperty().removeListener(copyMessageChangeListener);
    }
}
