package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Constraints;
import gui.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.service.SellerService;

public class SellerFormController implements Initializable {
	
	private Seller entity;
	
	private SellerService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	@FXML
	private Label lblErrorName;
	
	@FXML
	private Label lblErrorEmail;
	
	@FXML
	private Label lblErrorBirthDate;
	
	@FXML
	private Label lblErrorBaseSalary;
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setDeparmentService(SellerService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity == null) {
			throw new IllegalStateException("Entity was null.");
		}
		if(service== null) {
			throw new IllegalStateException("Service was null.");
		}
		try { // Como é uma operação com acesso a BD, é preciso colocar em um try-catch
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListener();
			Utils.currentStage(event).close(); // Para fechar a janela após salvar no banco.
		} catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}catch(DBException e) {
			Alerts.showAlert("Error saving object.", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListener() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error.");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty.");
		}
		obj.setName(txtName.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
	}
	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entity was null.");
		}
		txtName.setText(entity.getName());
		txtId.setText(String.valueOf(entity.getId()));
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if(entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			lblErrorName.setText(errors.get("name"));
		}
	}
}
