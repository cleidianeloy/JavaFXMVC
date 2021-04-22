/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxmvc.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.domain.Cliente;

/**
 * FXML Controller class
 *
 * @author Cleid
 */
public class FXMLAnchorPaneCadastrosClientesDialogController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label labelClienteNome;
    @FXML
    private Label labelClienteCPF;
    @FXML
    private Label labelClienteTelefone;
    @FXML
    private TextField textFieldClienteNome;
    @FXML
    private TextField textFieldClienteCPF;
    @FXML
    private TextField textFieldClienteTelefone;
    @FXML
    private Button buttonConfirmar;
    @FXML 
    private Button buttonCancelar;
    
    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private Cliente cliente;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    /**
     * @return the dialogStage
     */
    public Stage getDialogStage() {
        return dialogStage;
    }

    /**
     * @param dialogStage the dialogStage to set
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        
    }

    /**
     * @return the buttonConfirmarClicked
     */
    public boolean isButtonConfirmarClicked() {
        return buttonConfirmarClicked;
    }

    /**
     * @param buttonConfirmarClicked the buttonConfirmarClicked to set
     */
    public void setButtonConfirmarClicked(boolean buttonConfirmarClicked) {
        this.buttonConfirmarClicked = buttonConfirmarClicked;
    }

    /**
     * @return the cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        this.textFieldClienteNome.setText(cliente.getNome());
        this.textFieldClienteCPF.setText(cliente.getCpf());
        this.textFieldClienteTelefone.setText(cliente.getTelefone());
        
    }
    @FXML
   public void handleButtonConfirmar(){
       if(validarEntradaDeDados()){
            cliente.setNome(textFieldClienteNome.getText());
            cliente.setCpf(textFieldClienteCPF.getText());
            cliente.setTelefone(textFieldClienteTelefone.getText());
       
            buttonConfirmarClicked = true;
            dialogStage.close();
       }
   }
   @FXML
   public void handleButtonCancelar(){
       dialogStage.close();
   }
   private boolean validarEntradaDeDados(){
       String errorMessage = "";
       if(textFieldClienteNome.getText() == null || textFieldClienteNome.getText().length() == 0){
           errorMessage += "Nome Inválido!\n";
       }
       if(textFieldClienteCPF.getText() == null || textFieldClienteCPF.getText().length() == 0) {
           errorMessage += "CPF Inválido!\n";
       }
       if(textFieldClienteTelefone.getText() == null || textFieldClienteTelefone.getText().length() == 0){
           errorMessage += "Telefone invalido!\n";    
       }
       
       if(errorMessage.length() == 0){
           return true;
       }else {
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Erro no cadastro");
           alert.setHeaderText("Campos inválidos, por favor, corrija...");
           alert.setContentText(errorMessage);
           alert.show();
           return false;
       }
   }
}
