package model;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import br.com.app.hungerdelivery.ProdutoEmpresa;
import helper.ConfiguracaoFirebase;

/**
 * MODEL EMPRESA PRA SALVAR OS DADOS DA EMPRESA
 */
public class Produtos {

    private String idUsuarioLogado;
    private String idProdutos;
    private String nome;
    private String descricao;
    private String urlImagemPerfilProdudo;
    private Double preco;

    public Produtos()
    {

        //jogou aqui no construtor pra gerar um id na tabela pros produtos
        DatabaseReference databaseReference = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference produtosRef = databaseReference.child("produtos");
        setIdProdutos(produtosRef.push().getKey());

    }


    /**
     * METODO SALVAR UM PRODUTO NO BANCO DE DADOS FIREBASE
     */
        public void metodoSalvarProdutos(){

            //instanciar um DatabaseReference
            DatabaseReference databaseReference = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
            DatabaseReference produtosRef = databaseReference.child("produtos")
                    .child(getIdUsuarioLogado())
                    .child(getIdProdutos());

            produtosRef.setValue(this);


        }


    /**
     * METODO EXCLUIR UM PRODUTO DO BANCO DE DADOS DO FIREBASE.
     */
        public void metodoExcluirProduto(){

            //instanciar um DatabaseReference
            DatabaseReference databaseReference = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
            DatabaseReference produtosRef = databaseReference.child("produtos")
                    .child(getIdUsuarioLogado())
                    .child(getIdProdutos());
        produtosRef.removeValue();



        }







    public String getIdUsuarioLogado() {
        return idUsuarioLogado;
    }

    public void setIdUsuarioLogado(String idUsuarioLogado) {
        this.idUsuarioLogado = idUsuarioLogado;
    }

    public String getIdProdutos() {
        return idProdutos;
    }

    public void setIdProdutos(String idProdutos) {
        this.idProdutos = idProdutos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getUrlImagemPerfilProdudo() {
        return urlImagemPerfilProdudo;
    }

    public void setUrlImagemPerfilProdudo(String urlImagemPerfilProdudo) {
        this.urlImagemPerfilProdudo = urlImagemPerfilProdudo;
    }
}
