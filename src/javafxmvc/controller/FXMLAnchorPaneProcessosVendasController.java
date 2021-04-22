
package javafxmvc.controller;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafxmvc.model.domain.Venda;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxmvc.model.dao.ItemDeVendaDAO;
import javafxmvc.model.dao.ProdutoDAO;
import javafxmvc.model.dao.VendaDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.ItemDeVenda;
import javafxmvc.model.domain.Produto;
/**
 * FXML Controller class
 *
 * @author Cleid
 */
public class FXMLAnchorPaneProcessosVendasController implements Initializable {

    @FXML
    private TableView<Venda> tableViewVendas;
    @FXML
    private TableColumn<Venda, Integer> tableColumnVendaCodigo;
    @FXML
    private TableColumn<Venda, LocalDate> tableColumnVendaData;
    @FXML
    private TableColumn<Venda,Venda>  tableColumnVendaCliente;
    @FXML
    private Button buttonInserir;
    @FXML
    private Button buttonAlterar;
    @FXML
    private Button buttonRemover;
    @FXML
    private Label labelVendaCodigo;
    @FXML
    private Label labelVendaData;
    @FXML
    private Label labelVendaValor;
    @FXML
    private Label labelVendaPago;
    @FXML
    private Label labelVendaCliente;
    
    private List<Venda> listVendas;
    private ObservableList<Venda> observableListVendas;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final VendaDAO vendaDAO = new VendaDAO();
    private final ItemDeVendaDAO itemDeVendaDAO = new ItemDeVendaDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vendaDAO.setConnection(connection);
        produtoDAO.setConnection(connection);
        carregarTableViewVendas();
        selecionarItemTableViewVendas(null);
        tableViewVendas.getSelectionModel().selectedItemProperty().addListener(
        (observable,oldValue,newValue) -> selecionarItemTableViewVendas(newValue));
        
    }
    public void selecionarItemTableViewVendas(Venda venda){
        if(venda != null){
            labelVendaCodigo.setText(String.valueOf(venda.getCdVenda()));
            labelVendaData.setText(String.valueOf(venda.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            labelVendaValor.setText(String.format("%.2f", venda.getValor()));
            labelVendaPago.setText(String.valueOf(venda.getPago()));
            labelVendaCliente.setText(String.valueOf(venda.getCliente().toString()));
            
            
        }else {
            labelVendaCodigo.setText("");
            labelVendaData.setText("");
            labelVendaValor.setText("");
            labelVendaPago.setText("");
            labelVendaCliente.setText("");
        }
    }
    public void carregarTableViewVendas(){
        tableColumnVendaCodigo.setCellValueFactory(new PropertyValueFactory<>("cdVenda"));
        tableColumnVendaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        tableColumnVendaCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        listVendas = vendaDAO.listar();
        
        observableListVendas = FXCollections.observableArrayList(listVendas);
        tableViewVendas.setItems(observableListVendas);
    }
    @FXML
    public void handleButtonInserir() throws IOException {
        Venda venda = new Venda();
        List<ItemDeVenda> listItensDeVenda = new ArrayList<>();
        venda.setItensDeVenda(listItensDeVenda);
        boolean buttonConfirmarClicked = showFXMLAnchorPaneProcessosVendasDialog(venda);
        if(buttonConfirmarClicked){
            try {
                connection.setAutoCommit(false);
                vendaDAO.setConnection(connection);
                vendaDAO.inserir(venda);
                itemDeVendaDAO.setConnection(connection);
                produtoDAO.setConnection(connection);
                for(ItemDeVenda listItemDeVenda : venda.getItensDeVenda()){
                    Produto produto = listItemDeVenda.getProduto();
                    listItemDeVenda.setVenda(vendaDAO.buscarUltimaVenda());
                    itemDeVendaDAO.inserir(listItemDeVenda);
                    produto.setQuantidade(produto.getQuantidade() - listItemDeVenda.getQuantidade());
                    produtoDAO.alterar(produto);
                }
                connection.commit();
                carregarTableViewVendas();
            } catch (SQLException ex){
                try {
                    connection.rollback();
                } catch (SQLException ex1){
                    Logger.getLogger(FXMLAnchorPaneProcessosVendasController.class.getName()).log(Level.SEVERE, null, ex1);
                }
                Logger.getLogger(FXMLAnchorPaneProcessosVendasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean showFXMLAnchorPaneProcessosVendasDialog(Venda venda) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneProcessosVendassDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneProcessosVendassDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        //criando um estagio de dialogo (Stage dialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Registro de Vendas");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        //Setando a venda no controller
        FXMLAnchorPaneProcessosVendassDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setVenda(venda);
        //Mostra o dialog e espera até que o usuario feche
        dialogStage.showAndWait();
        return controller.isButtonConfirmarClicked();

    } 
    public void handleButtonRemover() throws IOException, SQLException {
        Venda venda = tableViewVendas.getSelectionModel().getSelectedItem();
        if(venda != null){
            connection.setAutoCommit(false);
            vendaDAO.setConnection(connection);
            itemDeVendaDAO.setConnection(connection);
            for(ItemDeVenda listItemDeVenda : venda.getItensDeVenda()){
                Produto produto = listItemDeVenda.getProduto();
                produto.setQuantidade(produto.getQuantidade() + listItemDeVenda.getQuantidade());
                produtoDAO.alterar(produto);
                itemDeVendaDAO.remover(listItemDeVenda);
            }
            vendaDAO.remover(venda);
            connection.commit();
            carregarTableViewVendas();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma venda na Tabela");
            alert.show();
        }
    }
    
}
