package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewControler implements Initializable {

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction ");
	}

	/*CARREGA A VIEW DA LISTA DE DEPARTAMENTOS E PASSA COMO PARAMETRO UMA EXPRESSÃO LAMBDA QUE DÁ ACESSO AOS METODOS 
	 * DO DepartmentListController*/
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml",(DepartmentListController controller)->{
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}

	@FXML //NO MOMENTO, ABOUT NÃO PRECISA DE NENHUM CONTROLE, POR ISSO ATRIBUI UMA EXPRESSÃO QUE NÃO FAZ NADA
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml",x -> {});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	/*synchronized garante que o processamento não será interrompido durante a operação 
	multithread.
	 */
	public synchronized <T> void loadView(String absoluteName,Consumer<T> initializingAction) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVbox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			
			/*getRoot pega o primeiro elemento(ScroolPane) da view principal(MainView)
			.getContent pega o conteúdo(VBox)do ScroolPane */
			VBox mainVbox =(VBox) ((ScrollPane) mainScene.getRoot()).getContent(); 
			
			/*.getChildren vai pegar o primeiro filho do mainVbox que é o <menu> da MainView
			 * e atribuir ao mainMenu*/
			Node mainMenu = mainVbox.getChildren().get(0);
			
			//apaga todos os filhos do VBox da MainView
			mainVbox.getChildren().clear();
			
			//adiciona barra de menu ao main vbox, pois a barra deve ser preservada
			mainVbox.getChildren().add(mainMenu);
			
			//adiciona ao VBox da view principal(MainView) os filhos do newVbox
			mainVbox.getChildren().addAll(newVbox.getChildren());
			
			
			T controller = loader.getController();
			initializingAction.accept(controller);
		
		} catch (IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
