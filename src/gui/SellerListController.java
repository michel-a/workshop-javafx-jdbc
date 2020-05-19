package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DBIntegrityException;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.service.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	
	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEdit;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnRemove;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerFormView.fxml", parentStage);
	}
	
	public void setSellerService(SellerService service) {
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
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}
	
	// Esse m�todo ser� respons�vel por acessar o servi�o, carregar os
	// departamentos e jogar os departamentos na ObservableList
	// O ObservableList ser� associado ao TableView e os departamentos
	// v�o aparecer na tela
	public void updateTableView() {
		// a inje��o de dependencia setSellerService est� totalmente manual
		// ent�o se o programardor esquecer de injetar a depend�ncia, n�o vai
		// ter como usar o servi�o aqui no m�todo, e or isso o if � para proteger
		// algum erro que o programador fizer, para de prop�sito, estourar uma
		// exece��o e o mesmo ver que ele esqueceu.
		if(service == null) {
			throw new IllegalStateException("Service was null.");
		}
		
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//			
//			SellerFormController controller = loader.getController();
//			controller.setSeller(obj);
//			controller.setDeparmentService(new SellerService()); // Aula 277, como n�o est� sendo usado nenhum framework, � preciso ijetar manualmente
//			controller.subscribeDataChangeListener(this); // Aula 278
//			controller.updateFormData();
//			
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Enter Seller data");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			dialogStage.initModality(Modality.WINDOW_MODAL); // esse m�todo que diz que a janela ser� modal, deixando-a travada
//			dialogStage.showAndWait();
//		} catch(IOException e) {
//			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() { // Aula 278
		updateTableView();
	}
	
	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
			
			private final Button button = new Button("edit");
			
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
					event -> createDialogForm(obj, "/gui/SellerFormView.fxml", Utils.currentStage(event))
				);
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Seller, Seller>() {
			
			private final Button button = new Button("remove");
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you show to delete?");
		
		if(result.get() == ButtonType.OK) {
			
			if(service == null) {
				throw new IllegalStateException("Service was null.");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch(DBIntegrityException e) {
				Alerts.showAlert("Error removing object.", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
