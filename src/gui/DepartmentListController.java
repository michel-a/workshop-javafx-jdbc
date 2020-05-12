package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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
		// Padr�o do javafx para iniciar o comportamento das colunas
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// getWindow() = pega a refer�ncia da janela, window � uma superclasse de stage e por isso � necess�rio fazer o downcasting 
		Stage stage = (Stage) Main.getMainScene().getWindow();
		// atrav�s desse comando, o taleView vai acompanhar a altura da janela
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	
	// Esse m�todo ser� respons�vel por acessar o servi�o, carregar os
	// departamentos e jogar os departamentos na ObservableList
	// O ObservableList ser� associado ao TableView e os departamentos
	// v�o aparecer na tela
	public void updateTableView() {
		// a inje��o de dependencia setDepartmentService est� totalmente manual
		// ent�o se o programardor esquecer de injetar a depend�ncia, n�o vai
		// ter como usar o servi�o aqui no m�todo, e or isso o if � para proteger
		// algum erro que o programador fizer, para de prop�sito, estourar uma
		// exece��o e o mesmo ver que ele esqueceu.
		if(service == null) {
			throw new IllegalStateException("Service was null.");
		}
		
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
}
