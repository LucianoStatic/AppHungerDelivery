package model;

import com.google.firebase.database.DatabaseReference;

import helper.ConfiguracaoFirebase;

public class Usuarios {

private String id;
private String nome;
private String email;
private String senha;
private String tipo;


    public Usuarios() {
    }


    /**
     *
     * Metodo para Salvar dados no Banco Firebase Real
     */
        public void salvarUsuario(){

            DatabaseReference referenciaFirebase = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
            DatabaseReference usuarios = referenciaFirebase.child("usuarios").child(getId());
            usuarios.setValue(this);


        }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
