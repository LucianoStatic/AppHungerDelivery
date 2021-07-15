package model;

public class ItensPedido {

    private String idProduto;
    private String nomeProduto;
    private int quantiade;
    private Double precoProduto;


    public ItensPedido() {
    }


    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantiade() {
        return quantiade;
    }

    public void setQuantiade(int quantiade) {
        this.quantiade = quantiade;
    }

    public Double getPrecoProduto() {
        return precoProduto;
    }

    public void setPrecoProduto(Double precoProduto) {
        this.precoProduto = precoProduto;
    }
}
