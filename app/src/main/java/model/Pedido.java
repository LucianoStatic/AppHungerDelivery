package model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import helper.ConfiguracaoFirebase;

public class Pedido {

    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String endereco;
    private List<ItensPedido> itens;
    private Double total;
    private String status = "pendente";
    private int metodoPagamento;
    private String observacao;

    public Pedido() {
    }


    //TODO: Um construtor que ira receber alguns parametros
    public Pedido(String idEmpre, String idCliente) {

        setIdUsuario(idEmpre);
        setIdEmpresa(idCliente);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidocliente")
                .child(idEmpre)
                .child(idCliente);
        setIdPedido(pedidoRef.push().getKey()); //TODO: pedidoRef.Push().getKey é pra criar um identificador pra tabela pedido


    }

    //TODO: Metodo salvar um pedido
    public void metodoSalvarPedido() {


        DatabaseReference firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidocliente")
                .child(getIdEmpresa())
                .child(getIdUsuario());
        pedidoRef.setValue(this);

    }


    //TODO: Metodo finalizar pedido
    public void metodoFinalizarPedido() {


        DatabaseReference firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidoConfirmados")
                .child(getIdEmpresa())
                .child(getIdPedido());
        pedidoRef.setValue(this);

    }

    //TODO: Metodo finalizar pedido
    public void metodoTestePedido() {


        DatabaseReference firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidoTeste");
        String idRequisicaoPedidos = pedidoRef.push().getKey();
        setIdPedido(idRequisicaoPedidos);
        pedidoRef.child(getIdPedido()).setValue(this);

    }


    //TODO: Metodo deletar tabela de pedidos do cliente
    public void metodoRemovePedidosCliente() {


        DatabaseReference firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedidocliente")
                .child(getIdEmpresa())
                .child(getIdUsuario());
        pedidoRef.removeValue();

    }


    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<ItensPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItensPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
