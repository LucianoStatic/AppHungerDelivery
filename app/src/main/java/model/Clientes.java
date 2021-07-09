package model;

import com.google.firebase.database.DatabaseReference;

import helper.ConfiguracaoFirebase;

public class Clientes {

    private String idCliente;
    private String nome;
    private String endereco;

    public Clientes() {
    }


    //TODO: Metodo pra ser chamado na outra classe, ela e responsável por salvar os dados do cliente
    public void salvarCliente(){

        //TODO:Instanciar um Database reference
        DatabaseReference referenciaDataBase = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference clientesRef = referenciaDataBase
                .child("clientes")
                .child(getIdCliente());
        clientesRef.setValue(this);



    }




    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
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
}
