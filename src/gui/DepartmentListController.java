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

	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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

}
