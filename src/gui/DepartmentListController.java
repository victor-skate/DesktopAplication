package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listner.DataChangeListner;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListner {

	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;

	/*
	 * PRIMEIRO ATRIBUTO ESPECIFICA O TIPO DE ENTIDADE, O SEGUNDO INDICA A COLUNA DA
	 * TABELA QUE NO CASO ? O ID
	 */
	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew;

	@FXML //
	private ObservableList<Department> obsList;

	@FXML /*
			 * ActionEvent FORNECE UMA REFER?NCIA PARA O CONTROLE QUE RECEBEU O EVENTO, A
			 * PARTIR DESSE EVENT TEREI CONDI??O DE ACESSAR O STAGE QUE GEROU O NOVO PALCO
			 * UTILIZANDO A FUN??O DA CLASSE Utils
			 */
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		/*
		 * CHAMA O METODO QUE CRIA UM NOVO PALCO(STAGE) PASSANDO O CAMINHO DA VIEW E O
		 * STAGE QUE RECEBEU O EVENTO PARA GERAR UM NOVO STAGE
		 */
		Department obj = new Department();
		// OBJ ? PASSADO COMO PARAMETRO PARA RECEBER OS DADOS DO FORMULARIO, SEJA PARA
		// ATUALIZAR OU
		// ADICIONAR UM NOVO DEPARTAMENTO
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	// INICIALIZA APROPRIADAMENTE O COMPORTAMENTO DAS COLUNAS DA TABELA
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// FAZ COM QUE A ALTURA DA TABELA SEJA DA MESMA ALTURA DA JANELA PRINCIPAL
		// ATRIBUI AO PALCO(STAGE) AS PROPRIEDADES DA JANELA PRINCIPAL
		Stage stage = (Stage) Main.getMainScene().getWindow();
		// ATRIBUI AO TABLEVIEW A PROPRIEDADE DE ALTURA IGUAL AO DA JANELA PRINCIPAL COM
		// stage.heightProperty
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	/*
	 * METODO RESPONSAVEL POR ACESSAR O SERVI?O, CARREGAR OS DEPARTAMENTOS E JOGAR
	 * OS DEPARTAMENT0S NA obsList, AP?S ISSO ? S? ASSOCIAR A obsList AO tableView E
	 * OS DEPARTAMENTOS APARECER?O NA TELA
	 */
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
		initEditButtons();/* METODO ACRESCENTA UM NOVO BOT?O EDIT EM CADA LINHA DA TABELA,
		 QUANDO CLICADO NO BOT?O, ? GERADO UM FORMULARIO DE EDI??O*/
		
		initRemoveButtons();/*METODO ACRESCENTA BOT?O DE REMO??O DE DEPARTAMENTO*/
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}

	/*
	 * ? NECESSARIO PASSAR COMO PAR?METRO UMA REFER?NCIA PARA O STAGE DA JANELA QUE
	 * CRIOU A JANELA DE DIALOGO
	 */
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();// INSTANCIANDO UMA JANELA TIPO PANE

			// PEGA O CONTROLE DO CONTROLADOR DE FORMULARIO DE DEPARTAMENTO
			DepartmentFormController controller = loader.getController();

			/*
			 * ADICIONA NAS CAIXAS DE TEXTO DO FORMUL?RIO OS DADOS DO OBJETO INSTANCIADO.
			 * OBS: O OBJ INSTANCIADO SERVE TANTO PARA ADD UM NOVO OBJ QUANTO PARA ATUALIZAR
			 * OS DADOS DE UM DEP EXISTENTE
			 */
			controller.setEntity(obj);
			controller.setDepartmentService(new DepartmentService());
			// ADICIONA ESSA CLASSE A LISTA DE OUVINTES, OU SEJA, QUANDO HOUVER MUDAN?AS
			// ESSA CLASSE CHAMA O METODO onDataChange QUE ATUALIZA A LISTA DE DEPARTAMENTOS
			// ASSIM QUE ADICIONAR UM NOVO DEPARTAMENTO
			controller.subscribeDataChangeListner(this);
			controller.updateFormData();

			/*
			 * PARA CARREGAR UMA JANELA DE DIALOGO MODAL NA FRENTE DA JANELA EXISTENTE ?
			 * NECESS?RIO INSTANCIAR UM NOVO STAGE. ASSIM O RESULTADO SER? UMA JANELA SOBRE
			 * A OUTRA. JANELA MODAL N?O PERMITE ITERA??O COM OUTRAS JANELAS ENQUANTO A
			 * JANELA MODAL N?O FOR FECHADA
			 */
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);// DIZ SE A JANELA PODE OU N?O SER REDIMENSIONADA
			dialogStage.initOwner(parentStage);// DIZ QUAL ? A JANELA PAI DA JANELA DIALOGSTAGE
			dialogStage.initModality(Modality.WINDOW_MODAL);// DIZ SE A JANELA ? MODAL OU N?O
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override // IMPLEMENTA??O DO METODO ABSTRATO DA CLASSE ABSTRATA DataChangeListner.
	public void onDataChange() {
		updateTableView();
	}

	// CRIA BOT?O EDIT PARA POSSIBILITAR A EDI??O DOS DEPARTAMENTOS
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	//ADICIONA BOT?O REMOVE NOS DEPARTAMENTOS EXISTENTES
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department obj, boolean empty) {
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
	
	//? CHAMADO PELO METODO ACIMA
	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "are you sure to delete?");
		
		if (result.get() == ButtonType.OK) {//SE O USUARIO CLICAR EM CONFIRMAR AI SIM ? DELETADO
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(),AlertType.ERROR);
			}
		}
	}
}