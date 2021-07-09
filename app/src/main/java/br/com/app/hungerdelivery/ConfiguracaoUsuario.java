package br.com.app.hungerdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.Clientes;
import model.Empresa;

public class ConfiguracaoUsuario extends AppCompatActivity {
    private EditText cpNomecompleto, cpEnderecoCompleto;
    /**
     * REFERENCIAS FIREBASE
     */

    private DatabaseReference referenciaFirebase;
    /**
     * PEGAR UM ID DA EMPRESA QUE ESTA LOGADA PRA SALVAR O ID NA FOTO
     */
    private String idClienteLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao_usuario);

        inicializarComponentes();


        //TODO: INICIALIZANDO AS REFERENCIAS DO FIREBASE
        referenciaFirebase = ConfiguracaoFirebase.metodoReferenciaGetFirebase();


        //TODO: PEGANDO ID DO DONO DA EMPRESA QUE ESTA LOGADO
        idClienteLogado = UsuarioFirebase.pegarIdUsuario();

        //TODO: Pegando os dados do cliente para editar ou conferir msm
        metodoRecuperarDadosCliente();

        /**
         * CONFIGURAÇOES DA TOOLBAR
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações de usuário");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_west);

    }


    //TODO: METODO PARA VALIDAR OS DADOS DA EMPRESA

    public void validarDadosCliente(View view) {

        //TODO: Valida se os campos forem preenchidos
        String nome = cpNomecompleto.getText().toString();
        String endereco = cpEnderecoCompleto.getText().toString();


        if (!nome.isEmpty()) {

            if (!endereco.isEmpty()) {


                //TODO: Instanciar um objeto da classe clientes
                Clientes cliente = new Clientes();
                cliente.setIdCliente(idClienteLogado);
                cliente.setNome(nome);
                cliente.setEndereco(endereco);
                cliente.salvarCliente();

                Toast.makeText(ConfiguracaoUsuario.this, "Concluído", Toast.LENGTH_SHORT);
                finish();

            } else {

                Toast.makeText(ConfiguracaoUsuario.this, "Insira um endereço válido", Toast.LENGTH_SHORT);


            }


        } else {

            Toast.makeText(ConfiguracaoUsuario.this, "Insira o nome completo!", Toast.LENGTH_SHORT);


        }


    }


    //TODO: Metodo para recuperar os dados do Cliente
    public void metodoRecuperarDadosCliente(){

        DatabaseReference clienteRef  = referenciaFirebase
                .child("clientes")
                .child(idClienteLogado);
        clienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue()!=null){

                    Clientes cliente = snapshot.getValue(Clientes.class);
                    cpNomecompleto.setText(cliente.getNome());
                    cpEnderecoCompleto.setText(cliente.getEndereco());


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }



    //TODO: Metodo inicializar Componentes
    private void inicializarComponentes() {

        cpNomecompleto = findViewById(R.id.cpNomeCompleto);
        cpEnderecoCompleto = findViewById(R.id.cpEnderecoCompleto);


    }


}