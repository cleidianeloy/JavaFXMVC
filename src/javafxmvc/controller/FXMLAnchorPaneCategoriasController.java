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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxmvc.model.dao.CategoriaDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Categoria;

/**
 * FXML Controller class
 *
 * @author Cleid
 */
public class FXMLAnchorPaneCategoriasController implements Initializable {

    @FXML
    private TableView<Categoria> tableViewCategoria;
    @FXML
    private TableColumn<Categoria, Integer> tableColumnId;
    @FXML
    private TableColumn<Categoria, String> tableColumnNomeCategoria;
    @FXML
    private Button buttonInserir;
    @FXML
    private Button buttonAtualizar;
    @FXML
    private Button buttonDeletar;

    private List<Categoria> listCategoria;
    private ObservableList<Categoria> observableListCategoria;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoriaDAO.setConnection(connection);
        carregarTableViewCategoria();

    }

    public void carregarTableViewCategoria() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("cdCategoria"));
        tableColumnNomeCategoria.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        listCategoria = categoriaDAO.listar();

        observableListCategoria = FXCollections.observableArrayList(listCategoria);
        tableViewCategoria.setItems(observableListCategoria);

    }

    @FXML
    public void handleButtonInserir() throws IOException {
        Categoria categoria = new Categoria();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCategoriasDialog(categoria);
        if (buttonConfirmarClicked) {
            categoriaDAO.inserir(categoria);
            carregarTableViewCategoria();
        }

    }

    @FXML
    public void handleButtonAlterar() throws IOException {
        Categoria categoria = tableViewCategoria.getSelectionModel().getSelectedItem();
        if (categoria != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneCategoriasDialog(categoria);
            if (buttonConfirmarClicked) {
                categoriaDAO.alterar(categoria);
                carregarTableViewCategoria();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma categoria na Tabela!");
            alert.show();
        }
    }

    @FXML
    public void handleButtonRemover() throws IOException {
        Categoria categoria = tableViewCategoria.getSelectionModel().getSelectedItem();
        if (categoria != null) {
            categoriaDAO.remover(categoria);
            carregarTableViewCategoria();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma categoria na Tabela!");
            alert.show();
        }
    }

    public boolean showFXMLAnchorPaneCategoriasDialog(Categoria categoria) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCategoriasDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneCategoriasDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Categorias");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        //setando a categoria no Controller
        FXMLAnchorPaneCategoriasDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setCategoria(categoria);

        //Mostra o dialog e espera até que o usuario e feche
        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();
    }
}
