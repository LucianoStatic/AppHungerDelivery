package br.com.app.hungerdelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.Empresa;
import model.Produtos;

public class ProdutoEmpresa extends AppCompatActivity {

    private EditText campoProduto, campoDescricaoProduto, campoPrecoProduto;
    private CircleImageView imagemPerfilProduto;
    private static final int SELECAO_GALERIA = 200;

    /**
     * REFERENCIAS FIREBASE
     */
    private StorageReference referenciaStorageImages;
    private DatabaseReference referenciaFirebase;

    /**
     * PEGAR UM ID DA EMPRESA QUE ESTA LOGADA PRA SALVAR O ID NA FOTO
     */
    private String idDonoEmpresaLogado;

    /**
     * URL DA IMAGEM SELECIONADA
     */
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_empresa);


        /**
         * CONFIGURAÇÕES INICIAIS
         */
        inicializarComponentes();


        /**
         * INICIALIZANDO AS REFERENCIAS DO FIREBASE
         */
        referenciaStorageImages = ConfiguracaoFirebase.metodoReferenciaFoto();
        referenciaFirebase = ConfiguracaoFirebase.metodoReferenciaGetFirebase();


        /**
         * PEGANDO ID DO DONO DA EMPRESA QUE ESTA LOGADO
         */
        idDonoEmpresaLogado = UsuarioFirebase.pegarIdUsuario();


        /**
         * CONFIGURAÇOES DA TOOLBAR
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_west);

        /**
         * EVENTO DE CLICK PRA BUSCAR UMA IMAGEM DE PERFIL PARA A EMPRESA
         */
        imagemPerfilProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //criar uma intent
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if (i.resolveActivity(getPackageManager()) != null) {

                    startActivityForResult(i, SELECAO_GALERIA);
                }

            }
        });

    }


    /**
     * METODO PRA INICIALIZAR COMPONENTES
     */
    private void inicializarComponentes() {

        campoProduto = findViewById(R.id.campoCadNomeProduto);
        campoDescricaoProduto = findViewById(R.id.campoDescProduto);
        campoPrecoProduto = findViewById(R.id.campoValorProduto);
        imagemPerfilProduto = findViewById(R.id.imagemConfProduto);


    }


    /**
     * SOBRE-ESCREVER O METODO ON RESULT - FAZER UPLOAD DA IMAGEM
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Bitmap imagem = null;
            try {

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localDaImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localDaImagem
                                );

                        break;
                }

                if (imagem != null) {

                    imagemPerfilProduto.setImageBitmap(imagem);
                    /**
                     * FAZER UPLOAD DA FOTO
                     */
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();
                    /**
                     * STORAGE REFERENCE
                     */
                    StorageReference imagemRef = referenciaStorageImages
                            .child("imagens")
                            .child("produtos")
                            .child(idDonoEmpresaLogado + "jpeg");

                    /**
                     * INICIALIZAR O PROCESSO DE UPLOAD DA  IMAGEM
                     */
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    /**
                     * METODO PARA O TRATAMENTO DE UM POSSIVEL ERRO DE UPLOAD DA IMAGEM
                     */
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProdutoEmpresa.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT)
                            .show();
                        }
                    })
                            /**
                             * METODO PARA O TRATAMENTO DE SUCESSO AO FAZER UPLOAD DA IMAGEM
                             */
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    /**
                                     * PEGAR A URL DA IMAGEM PRA SALVAR NO CADASTRO COMPLETO
                                     */
                                    imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            Uri url = task.getResult();

                                            urlImagemSelecionada = String.valueOf(url);

                                            Toast.makeText(ProdutoEmpresa.this, "Sucesso ao fazer o upload", Toast.LENGTH_SHORT)
                                            .show();

                                        }
                                    });

                                }
                            });
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * METODO PARA VALIDAR OS DADOS DA EMPRESA
     */
    public void validarDadosProdutos(View view) {

        //Valida se os campos forem preenchidos
        String nomeProduto = campoProduto.getText().toString();
        String descricaoProduto = campoDescricaoProduto.getText().toString();
        String valorProduto = campoPrecoProduto.getText().toString();


        if (!nomeProduto.isEmpty()) {

            if (!descricaoProduto.isEmpty()) {

                if (!valorProduto.isEmpty()) {



                        /**
                         * INSTANCIAR O OBJETO DA CLASSE EMPRESA
                         */
                        Produtos produtos = new Produtos();
                        produtos.setIdUsuarioLogado(idDonoEmpresaLogado);

                        produtos.setNome(nomeProduto);
                        produtos.setDescricao(descricaoProduto);
                        produtos.setPreco(Double.parseDouble(valorProduto));
                        produtos.setUrlImagemPerfilProdudo(urlImagemSelecionada);
                        produtos.metodoSalvarProdutos();
                        finish();
                    Toast.makeText(ProdutoEmpresa.this, "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(ProdutoEmpresa.this, "Insira um valor para taxa para o produto", Toast.LENGTH_SHORT).show();

                }


            } else {

                Toast.makeText(ProdutoEmpresa.this, "Insira uma descrição para o produto", Toast.LENGTH_SHORT).show();


            }


        } else {

            Toast.makeText(ProdutoEmpresa.this, "Insira um nome para o produto!", Toast.LENGTH_SHORT).show();


        }


    }


}