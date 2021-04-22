package javafxmvc.controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.dao.CategoriaDAO;
import javafxmvc.model.dao.ProdutoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Categoria;
import javafxmvc.model.domain.Produto;

/**
 * FXML Controller class
 *
 * @author Cleid
 */
public class FXMLAnchorPaneCadastrosProdutosDialogController implements Initializable {

    @FXML
    private TextField textFieldProdutoNome;
    @FXML
    private TextField textFieldProdutoPreco;
    @FXML
    private TextField textFieldProdutoQuantidade;
    @FXML
    private ComboBox comboBoxCategoria;
    @FXML
    private Button buttonConfirmar;
    @FXML
    private Button buttonCancelar;

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private Produto produto;
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private List<Categoria> listCategoria = new ArrayList<>();
    private ObservableList<Categoria> observableListCategoria;

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isButtonConfirmarClicked() {
        return buttonConfirmarClicked;
    }

    public void setButtonConfirmarClicked(boolean buttonConfirmarClicked) {
        this.buttonConfirmarClicked = buttonConfirmarClicked;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        textFieldProdutoNome.setText(produto.getNome());
        textFieldProdutoPreco.setText(String.valueOf(produto.getPreco()));
        textFieldProdutoQuantidade.setText(String.valueOf(produto.getQuantidade()));
        comboBoxCategoria.getSelectionModel().select(produto.getCategoria());
    }

    public void carregarComboBoxCategorias() {
        //pega a lista de clientes
        listCategoria = categoriaDAO.listar();
        //converte list para observablearraylist
        observableListCategoria = FXCollections.observableArrayList(listCategoria);
        //coloca dentro do combobox
        comboBoxCategoria.setItems(observableListCategoria);
    }

    @FXML
    public void handleButtonConfirmar() {
        if (validarEntradaDeDados()) {
            produto.setNome(textFieldProdutoNome.getText());
            produto.setPreco(Double.parseDouble(textFieldProdutoPreco.getText()));
            produto.setQuantidade(Integer.parseInt(textFieldProdutoQuantidade.getText()));
            produto.setCategoria((Categoria) comboBoxCategoria.getSelectionModel().getSelectedItem());
            buttonConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    public void handleButtonCancelar() {
        dialogStage.close();
    }

    private boolean validarEntradaDeDados() {
        String errorMessage = "";
        if (textFieldProdutoNome.getText() == null || textFieldProdutoNome.getText().length() == 0) {
            errorMessage += "Nome Inválido!\n";
        }
        if (textFieldProdutoPreco.getText() == null || textFieldProdutoPreco.getText().length() == 0) {
            errorMessage += "Preço Inválido!\n";
        }
        if (textFieldProdutoQuantidade.getText() == null || textFieldProdutoQuantidade.getText().length() == 0) {
            errorMessage += "Quantidade invalida!\n";
        }

        if (comboBoxCategoria.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Categoria invalida!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campos inválidos, por favor, corrija...");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoriaDAO.setConnection(connection);
        produtoDAO.setConnection(connection);
        carregarComboBoxCategorias();
    }

}
