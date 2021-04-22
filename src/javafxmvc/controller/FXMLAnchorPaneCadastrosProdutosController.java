package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
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
public class FXMLAnchorPaneCadastrosProdutosController implements Initializable {

    @FXML
    private TableView<Produto> tableViewProduto;
    @FXML
    private TableColumn<Produto, Integer> tableColumnProdutoId;
    @FXML
    private TableColumn<Produto, String> tableColumnProdutoNome;
    @FXML
    private ComboBox<Categoria> comboBoxCategoria;
    @FXML
    private Label labelProdutoNome;
    @FXML
    private Label labelProdutoPreco;
    @FXML
    private Label labelProdutoQuantidade;
    @FXML
    private Label labelProdutoCategoria;
    @FXML
    private Button buttonInserir;
    @FXML
    private Button buttonAlterar;
    @FXML
    private Button buttonRemover;
    @FXML
    private TextField buscaPeloCodigo;

    private List<Categoria> listCategoria;
    private ObservableList<Categoria> observableListCategoria;

    private List<Produto> listProduto;
    private ObservableList<Produto> observableListProduto;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoriaDAO.setConnection(connection);
        produtoDAO.setConnection(connection);
        carregarComboBoxCategorias();
        carregarTableViewProduto();
        selecionarItemTableViewProduto(null);

        comboBoxCategoria.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemComboBoxCategoria(newValue));
        tableViewProduto.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewProduto(newValue));
        /* buscaPeloCodigo.textProperty().addListener(
       (observable, oldValue, newValue) -> selecionarItemTableViewProdutoPelaBusca(newValue)); */
    }

    public void carregarComboBoxCategorias() {
        //pega a lista de clientes
        listCategoria = categoriaDAO.listar();
        //converte list para observablearraylist
        observableListCategoria = FXCollections.observableArrayList(listCategoria);
        //coloca dentro do combobox
        comboBoxCategoria.setItems(observableListCategoria);
    }

    public void selecionarItemComboBoxCategoria(Categoria categoria) {
        carregarTableViewProduto();
    }

    public void carregarTableViewProduto() {
        tableColumnProdutoId.setCellValueFactory(new PropertyValueFactory<>("cdProduto"));
        tableColumnProdutoNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        Categoria categoria = (Categoria) comboBoxCategoria.getSelectionModel().getSelectedItem();
        if (categoria != null) {
            listProduto = produtoDAO.listarPorCategoria(categoria);
        } else {
            listProduto = produtoDAO.listar();
        }

        observableListProduto = FXCollections.observableArrayList(listProduto);
        tableViewProduto.setItems(observableListProduto);

    }

    public void selecionarItemTableViewProduto(Produto produto) {
        if (produto != null) {
            labelProdutoNome.setText(produto.getNome());
            labelProdutoPreco.setText(String.valueOf(produto.getPreco()));
            labelProdutoQuantidade.setText(String.valueOf(produto.getQuantidade()));
            labelProdutoCategoria.setText(produto.getCategoria().getDescricao());
        } else {
            labelProdutoNome.setText("");
            labelProdutoPreco.setText("");
            labelProdutoQuantidade.setText("");
            labelProdutoCategoria.setText("");
        }
    }

    @FXML
    public void handleButtonInserir() throws IOException {
        Produto produto = new Produto();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosProdutosDialog(produto);
        if (buttonConfirmarClicked) {
            produtoDAO.inserir(produto);
            carregarTableViewProduto();
        }

    }

    @FXML
    public void handleButtonAlterar() throws IOException {
        Produto produto = tableViewProduto.getSelectionModel().getSelectedItem();
        if (produto != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosProdutosDialog(produto);
            if (buttonConfirmarClicked) {
                produtoDAO.alterar(produto);
                carregarTableViewProduto();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na Tabela!");
            alert.show();
        }
    }

    @FXML
    public void handleButtonRemover() throws IOException {
        Produto produto = tableViewProduto.getSelectionModel().getSelectedItem();
        if (produto != null) {
            produtoDAO.remover(produto);
            carregarTableViewProduto();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na Tabela!");
            alert.show();
        }
    }

    public boolean showFXMLAnchorPaneCadastrosProdutosDialog(Produto produto) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastrosClientesDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneCadastrosProdutosDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Produtos");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        //setando o cliente no Controller
        FXMLAnchorPaneCadastrosProdutosDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setProduto(produto);

        //Mostra o dialog e espera até que o usuario e feche
        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();
    }
}
