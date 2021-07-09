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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.Empresa;

public class ConfiguracaoEmpresa extends AppCompatActivity {

    private EditText campoCEmpresa, campoCTempoEntrega, campoCCategoria, campoTaxaEntrega, campoHorario;
    private CircleImageView imagemPerfilEmpresa;
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
        setContentView(R.layout.activity_configuracao_empresa);


        //TODO: CONFIGURAÇÕES INICIAIS

        inicializarComponentes();


        //TODO: INICIALIZANDO AS REFERENCIAS DO FIREBASE

        referenciaStorageImages = ConfiguracaoFirebase.metodoReferenciaFoto();
        referenciaFirebase = ConfiguracaoFirebase.metodoReferenciaGetFirebase();


        //TODO: PEGANDO ID DO DONO DA EMPRESA QUE ESTA LOGADO

        idDonoEmpresaLogado = UsuarioFirebase.pegarIdUsuario();


        //TODO: CONFIGURAÇOES DA TOOLBAR

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //TODO: EVENTO DE CLICK PRA BUSCAR UMA IMAGEM DE PERFIL PARA A EMPRESA

        imagemPerfilEmpresa.setOnClickListener(new View.OnClickListener() {
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


        /**
         * METODO RECUPERAR OS DADOS PRA EDICAO DAS CONFIGURACOES DA EMPRESA
         */
        //metodoRecuperarDadosEmpresa();


    }


    //TODO: METODO PARA VALIDAR OS DADOS DA EMPRESA

    public void validarDadosEmpresa(View view) {

        //Valida se os campos forem preenchidos
        String nome = campoCEmpresa.getText().toString();
        String categoria = campoCCategoria.getText().toString();
        String taxa = campoTaxaEntrega.getText().toString();
        String tempo = campoCTempoEntrega.getText().toString();
        String horario = campoHorario.getText().toString();

        if (!nome.isEmpty()) {

            if (!categoria.isEmpty()) {

                if (!taxa.isEmpty()) {

                    if (!tempo.isEmpty()) {

                        /**
                         * INSTANCIAR O OBJETO DA CLASSE EMPRESA
                         */
                        Empresa empresa = new Empresa();
                        empresa.setIdEmpresa(idDonoEmpresaLogado);
                        empresa.setNomeEmpresa(nome);
                        empresa.setCategoria(categoria);
                        empresa.setTempoEntrega(tempo);
                        empresa.setPrecoEntrega(Double.parseDouble(taxa));
                        empresa.setHorarioFuncionamento(horario);
                        empresa.setStatus("Aberto");
                        empresa.setUrlImagemEmpresa(urlImagemSelecionada);
                        empresa.metodoSalvarDadosEmpresa();
                        finish();

                    } else {

                        Toast.makeText(ConfiguracaoEmpresa.this, "Insira o tempo de entrega", Toast.LENGTH_SHORT);

                    }


                } else {

                    Toast.makeText(ConfiguracaoEmpresa.this, "Insira um valor para taxa de entrega", Toast.LENGTH_SHORT);

                }


            } else {

                Toast.makeText(ConfiguracaoEmpresa.this, "Insira uma categoria para a sua empresa", Toast.LENGTH_SHORT);


            }


        } else {

            Toast.makeText(ConfiguracaoEmpresa.this, "Insira um nome para a empresa!", Toast.LENGTH_SHORT);


        }


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

                    imagemPerfilEmpresa.setImageBitmap(imagem);
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
                            .child("empresas")
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
                            Toast.makeText(ConfiguracaoEmpresa.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT);
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

                                            Toast.makeText(ConfiguracaoEmpresa.this, "Sucesso ao fazer o upload", Toast.LENGTH_SHORT);

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
     * METODO RESPONSAVEL POR RECUPERAR OS DADOS DAS CONFIGURACOES DA EMPRESA
     */
    private void metodoRecuperarDadosEmpresa() {

        DatabaseReference empresaRef = referenciaFirebase
                .child("empresas")
                .child(idDonoEmpresaLogado); //pega os dados atraves do id da empresa que esta logada no app

        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null) {

                    Empresa modelEmpresa = snapshot.getValue(Empresa.class); // com isso se recupera os dados do objeto empresa
                    //chama as caixas de texto pra setar os valores recuperados do firebase
                    campoCEmpresa.setText(modelEmpresa.getNomeEmpresa());
                    campoCCategoria.setText(modelEmpresa.getCategoria());
                    campoCTempoEntrega.setText(modelEmpresa.getTempoEntrega());
                    campoTaxaEntrega.setText(modelEmpresa.getPrecoEntrega().toString());
                    campoHorario.setText(modelEmpresa.getHorarioFuncionamento());

                    /**
                     * Função para recuperar a imagem de perfil da empresa
                     */
                    urlImagemSelecionada = modelEmpresa.getUrlImagemEmpresa();
                    if (urlImagemSelecionada != "") {
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(imagemPerfilEmpresa);

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    /**
     * METODO PRA INICIALIZAR COMPONENTES
     */
    private void inicializarComponentes() {

        campoCEmpresa = findViewById(R.id.campoCadNomeEmpresa);
        campoCCategoria = findViewById(R.id.campoCategoriaEmpresa);
        campoCTempoEntrega = findViewById(R.id.campoCadEntTemp);
        campoTaxaEntrega = findViewById(R.id.campoCadTaxa);
        campoHorario = findViewById(R.id.campoHrFuncionamentoEmpresa);
        imagemPerfilEmpresa = findViewById(R.id.imagemConfEmpresa);


    }


}