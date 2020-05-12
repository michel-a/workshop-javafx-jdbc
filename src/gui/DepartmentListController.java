package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.utils.Alerts;
import gui.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.service.DepartmentService;

public class DepartmentListController implements Initializable {
	
	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		createDialogForm("/gui/DepartmentFormView.fxml", parentStage);
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		initializaNodes();
	}

	private void initializaNodes() {
		// Padrão do javafx para iniciar o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// getWindow() = pega a referência da janela, window é uma superclasse de stage e por isso é necessário fazer o downcasting 
		Stage stage = (Stage) Main.getMainScene().getWindow();
		// através desse comando, o taleView vai acompanhar a altura da janela
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	
	// Esse método será responsável por acessar o serviço, carregar os
	// departamentos e jogar os departamentos na ObservableList
	// O ObservableList será associado ao TableView e os departamentos
	// vão aparecer na tela
	public void updateTableView() {
		// a injeção de dependencia setDepartmentService está totalmente manual
		// então se o programardor esquecer de injetar a dependência, não vai
		// ter como usar o serviço aqui no método, e or isso o if é para proteger
		// algum erro que o programador fizer, para de propósito, estourar uma
		// execeção e o mesmo ver que ele esqueceu.
		if(service == null) {
			throw new IllegalStateException("Service was null.");
		}
		
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	
	private void createDialogForm(String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL); // esse método que diz que a janela será modal, deixando-a travada
			dialogStage.showAndWait();
		} catch(IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
