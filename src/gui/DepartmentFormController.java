package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listner.DataChangeListner;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

//CLASSE CONTROLADORA DO FORMULARIO DE INSCRIÇÃO DE UM NOVO DEPARTAMENTO
public class DepartmentFormController implements Initializable {

	private Department entity;
	private DepartmentService service;

	// LISTA DE OUVINTES DE ALTERAÇÃO DE DADOS
	private List<DataChangeListner> dataChangeListners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			// NOTIFICAR LISTA DE ALTERAÇÃO DE DADOS
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		//SE O CAMPO NAME ESTIVER VAZIO, É LANÇADA EXCESSÃO
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}

		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}

	}

	// METODO RESPONSAVEL POR CHAMAR O METODO updateTableView
	private void notifyDataChangeListeners() {
		for (DataChangeListner listener : dataChangeListners) {
			listener.onDataChange();
		}
	}

	// METODO QUE PEGA OS DADOS DO FORMULARIO E RETORNA UM NOVO OBJETO Department
	private Department getFormData() {
		Department obj = new Department();
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		ValidationException exception = new ValidationException("Validation error");

		// SE O CAMPO NAME ESTIVER VAZIO, É LANÇADA UMA EXCESSÃO
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	// ADICIONA NAS CAIXAS DE TEXTO DO FORMULÁRIO OS DADOS DO OBJETO INSTANCIADO.
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

	/*
	 * O OBJ INSTANCIADO SERVE TANTO PARA ADD UM NOVO OBJ QUANTO PARA ATUALIZAR OS
	 * DADOS DE UM DEP EXISTENTE
	 */
	public void setEntity(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void subscribeDataChangeListner(DataChangeListner listener) {
		dataChangeListners.add(listener);
	}

	//RECEBE UMA COLEÇÃO DE CHAVE E VALOR, ADICIONA AO fields APENAS A CHAVE DA COLEÇÃO errors	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}
