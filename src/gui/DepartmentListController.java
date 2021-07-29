package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
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
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartment;

	/*PRIMEIRO ATRIBUTO ESPECIFICA O TIPO DE ENTIDADE, O SEGUNDO INDICA A COLUNA DA
	 * TABELA QUE NO CASO É O ID*/
	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private Button btNew;

	@FXML//
	private ObservableList<Department> obsList;

	@FXML/*ActionEvent FORNECE UMA REFERÊNCIA PARA O CONTROLE QUE RECEBEU O EVENTO,
	A PARTIR DESSE EVENT TEREI CONDIÇÃO DE ACESSAR O STAGE QUE GEROU O NOVO PALCO
 	UTILIZANDO A FUNÇÃO DA CLASSE Utils*/
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);	
		/*CHAMA O METODO QUE CRIA UM NOVO PALCO(STAGE) PASSANDO O CAMINHO DA VIEW
		 * E O STAGE QUE RECEBEU O EVENTO PARA GERAR UM NOVO STAGE*/
		Department obj = new Department();
		//OBJ É PASSADO COMO PARAMETRO PARA RECEBER OS DADOS DO FORMULARIO, SEJA PARA ATUALIZAR OU
		//ADICIONAR UM NOVO DEPARTAMENTO
		createDialogForm(obj,"/gui/DepartmentForm.fxml",parentStage);
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	//INICIALIZA APROPRIADAMENTE O COMPORTAMENTO DAS COLUNAS DA TABELA
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
	
	
	/*METODO RESPONSAVEL POR ACESSAR O SERVIÇO, CARREGAR OS DEPARTAMENTOS E JOGAR OS DEPARTAMENT0S
	 * NA obsList, APÓS ISSO É SÓ ASSOCIAR A obsList AO tableView E OS DEPARTAMENTOS APARECERÃO NA
	 * TELA */
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}
	
	/*É NECESSARIO PASSAR COMO PARÂMETRO UMA REFERÊNCIA PARA O STAGE DA JANELA QUE CRIOU A JANELA
	 *DE DIALOGO*/
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			 FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			 Pane pane = loader.load();//INSTANCIANDO UMA JANELA TIPO PANE
			 
			 //PEGA O CONTROLE DO CONTROLADOR DE FORMULARIO DE DEPARTAMENTO 
			 DepartmentFormController controller = loader.getController();
			
			 /*ADICIONA NAS CAIXAS DE TEXTO DO FORMULÁRIO OS DADOS DO OBJETO INSTANCIADO.
			 OBS: O OBJ INSTANCIADO SERVE TANTO PARA ADD UM NOVO OBJ QUANTO PARA ATUALIZAR OS DADOS
			 DE UM DEP EXISTENTE*/
			 controller.setEntity(obj);
			 controller.updateFormData();
			 
			 /*PARA CARREGAR UMA JANELA DE DIALOGO MODAL NA FRENTE DA JANELA EXISTENTE É 
			  * NECESSÁRIO INSTANCIAR UM NOVO STAGE. ASSIM O RESULTADO SERÁ UMA JANELA SOBRE A OUTRA.
			  *JANELA MODAL NÃO PERMITE ITERAÇÃO COM OUTRAS JANELAS ENQUANTO A JANELA MODAL NÃO
			  * FOR FECHADA */
			 Stage dialogStage = new Stage();
			 dialogStage.setTitle("Enter department data");
			 dialogStage.setScene(new Scene(pane));
			 dialogStage.setResizable(false);//DIZ SE A JANELA PODE OU NÃO SER REDIMENSIONADA
			 dialogStage.initOwner(parentStage);//DIZ QUAL É A JANELA PAI DA JANELA DIALOGSTAGE 
			 dialogStage.initModality(Modality.WINDOW_MODAL);// DIZ SE A JANELA É MODAL OU NÃO
			 dialogStage.showAndWait();
		}
		catch(IOException e) {
			Alerts.showAlert("IO Exception","Error loading view",e.getMessage(), AlertType.ERROR);
		}
	}
}
