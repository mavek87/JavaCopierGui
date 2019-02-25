package com.matteoveroni.javacopiergui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {

	@FXML
	private BorderPane pane;
	@FXML
	private Button btn_chooseSrc;
	@FXML
	private Button btn_chooseDest;
	@FXML
	private TextField txt_source;
	@FXML
	private TextField txt_dest;

	private final FileChooser fileChooser = new FileChooser();

	@FXML
	void onChooseSource(ActionEvent event) {
//		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
//		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog((Stage) pane.getScene().getWindow());

//		txt_source.clear();
		txt_source.setText(file.getAbsolutePath());
	}

	@FXML
	void onChooseDest(ActionEvent event) {
		File file = fileChooser.showOpenDialog((Stage) pane.getScene().getWindow());

//		txt_source.clear();
		txt_dest.setText(file.getAbsolutePath());
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}
}
