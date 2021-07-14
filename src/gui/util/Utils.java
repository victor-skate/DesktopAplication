package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

//METODO QUE RETORNA O PALCO QUE RECEBEU UM EVENTO E GEROU UM NOVO PALCO
public class Utils {
	public static Stage currentStage(ActionEvent event) {
		return (Stage)((Node)event.getSource()).getScene().getWindow();		
	}
 }
