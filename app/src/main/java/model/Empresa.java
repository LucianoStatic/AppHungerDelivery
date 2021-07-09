package model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.com.app.hungerdelivery.ConfiguracaoEmpresa;
import helper.ConfiguracaoFirebase;

/**
 * MODEL EMPRESA PRA SALVAR OS DADOS DA EMPRESA
 */
public class Empresa implements Serializable {

    private String idEmpresa;
    private String urlImagemEmpresa;
    private String nomeEmpresa;
    private String tempoEntrega;
    private String categoria;
    private Double precoEntrega;
    private String horarioFuncionamento;
    private String status;

    public Empresa() {
    }


    /**
     * METODO SALVAR EMPRESA
     */
    public void metodoSalvarDadosEmpresa() {

        //Instanciar um Database reference
        DatabaseReference referenciaDataBase = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference empresaRef = referenciaDataBase
                .child("empresas")
                .child(getIdEmpresa());

        empresaRef.setValue(this);


    }


    /**
     *
     * @ATUALIZAR
     */
    public void metodoAtualizar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference requisicoes = firebaseRef.child("empresas");
        DatabaseReference requisicao = requisicoes.child(getIdEmpresa());

        Map objeto = new HashMap();
        objeto.put("status", getStatus());
        //quero atualizar o status que ja tem o valor na classe Painel viagens do motorista
        requisicao.updateChildren(objeto);


    }


    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getUrlImagemEmpresa() {
        return urlImagemEmpresa;
    }

    public void setUrlImagemEmpresa(String urlImagemEmpresa) {
        this.urlImagemEmpresa = urlImagemEmpresa;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getTempoEntrega() {
        return tempoEntrega;
    }

    public void setTempoEntrega(String tempoEntrega) {
        this.tempoEntrega = tempoEntrega;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecoEntrega() {
        return precoEntrega;
    }

    public void setPrecoEntrega(Double precoEntrega) {
        this.precoEntrega = precoEntrega;
    }

    public String getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(String horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
