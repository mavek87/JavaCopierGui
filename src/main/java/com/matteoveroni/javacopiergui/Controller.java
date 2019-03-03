package com.matteoveroni.javacopiergui;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
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
    @FXML private Button btn_chooseSrcFile;
    @FXML private Button btn_chooseDestFile;
    @FXML private Button btn_chooseSrcDir;
    @FXML private Button btn_chooseDestDir;
    @FXML private TextField txt_source;
    @FXML private TextField txt_dest;
    @FXML private Button btn_startCopy;
    @FXML private BorderPane pane_copyStatusArea;
    @FXML private Label lbl_progressPercentage;
    @FXML private ProgressBar progressBar;
    @FXML private Label lbl_progressText;
    @FXML private TextArea txtArea_console;

    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

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

    @FXML
    void onChooseSourceFile(ActionEvent event) {
        attachFileChooserToTextField(txt_source);
    }

    @FXML
    void onChooseDestFile(ActionEvent event) {
        attachFileChooserToTextField(txt_dest);
    }

    @FXML
    void onChooseSourceDir(ActionEvent event) {
        attachDirectoryChooserToTextField(txt_source);
    }

    @FXML
    void onChooseDestDir(ActionEvent event) {
        attachDirectoryChooserToTextField(txt_dest);
    }

    @FXML
    void onStartCopy(ActionEvent event) {
        txtArea_console.clear();
        setUiForCopyRunningLayout(true);

        Path src = Paths.get(txt_source.getText());
        Path dest = Paths.get(txt_dest.getText());

        CopyTask copyTask = new CopyTask(src, dest, StandardCopyOption.REPLACE_EXISTING);
        lbl_progressPercentage.textProperty().bind(Bindings.format("%.1f%%", copyTask.progressProperty().multiply(100)));
        progressBar.progressProperty().bind(copyTask.progressProperty());
        lbl_progressText.textProperty().bind(copyTask.messageProperty());
        ChangeListener copyMessageChangeListener = (observable, oldValue, newValue) -> txtArea_console.appendText(copyTask.getMessage() + "\n");
        copyTask.messageProperty().addListener(copyMessageChangeListener);
        copyTask.setOnSucceeded(s -> {
            finalizeTask(copyTask, copyMessageChangeListener);
        });
        copyTask.setOnFailed(f -> {
            finalizeTask(copyTask, copyMessageChangeListener);
        });
        copyTask.setOnCancelled(c -> {
            finalizeTask(copyTask, copyMessageChangeListener);
        });

        Thread copyThread = new Thread(copyTask);
        // if copyThread is a daemon the copy doesnt stop when the ui is closed
        // thread.setDaemon(true);
        copyThread.start();
    }

    private void finalizeTask(CopyTask copyTask, ChangeListener copyMessageChangeListener) {
        clearBindingsAndListeners(copyTask, copyMessageChangeListener);
        setUiForCopyRunningLayout(false);
    }

    private void clearBindingsAndListeners(CopyTask copyTask, ChangeListener copyMessageChangeListener) {
        lbl_progressPercentage.textProperty().unbind();
        progressBar.progressProperty().unbind();
        lbl_progressText.textProperty().unbind();
        copyTask.messageProperty().removeListener(copyMessageChangeListener);
    }

    private void setUiForCopyRunningLayout(boolean isCopyRunning) {
        pane_copyStatusArea.setVisible(isCopyRunning);
        btn_startCopy.setDisable(isCopyRunning);
        btn_chooseSrcFile.setDisable(isCopyRunning);
        btn_chooseDestFile.setDisable(isCopyRunning);
        btn_chooseSrcDir.setDisable(isCopyRunning);
        btn_chooseDestDir.setDisable(isCopyRunning);
    }

    private void attachFileChooserToTextField(TextField txt_source) {
        File file = fileChooser.showOpenDialog(pane.getScene().getWindow());
        if (file != null) {
            txt_source.setText(file.getAbsolutePath());
        }
    }

    private void attachDirectoryChooserToTextField(TextField txt_source) {
        File file = directoryChooser.showDialog(pane.getScene().getWindow());
        if (file != null) {
            txt_source.setText(file.getAbsolutePath());
        }
    }
}
