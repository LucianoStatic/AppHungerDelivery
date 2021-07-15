package br.com.app.hungerdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import helper.ConfiguracaoFirebase;
import model.Usuarios;

public class CadastroEntregador extends AppCompatActivity {

    private EditText campoCadastroEmail, campoCadastroSenha, campoCadastroNome;
    private Switch switchEscolhaTipo;
    private Button botaoFinalizarCadastro;
    public String emailCadastro, senhaCadastro, tipoUsuario, erroExcecao, nomeCadastro;
    private AlertDialog caixaDialogo;
    private String idUsuario;

    /**
     * CRIAR INSTANCIA DO FIREBASE DE AUTENTICACAO
     */
    private FirebaseAuth refAutenticacaofb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_entregador);


        InicializarComponentes();

        inicializarFirebaseAuth();

    }


    /**
     * METODO INICIALIZAR OS COMPONENTES
     */
    public void InicializarComponentes() {
        campoCadastroNome = findViewById(R.id.campoNomeEntregador);
        campoCadastroEmail = findViewById(R.id.campoEmailEntregador);
        campoCadastroSenha = findViewById(R.id.campoSenhaEntregador);

        botaoFinalizarCadastro = findViewById(R.id.botaoCadastrarEntregador);


    }


    /**
     * INICIALIZAR O FIREBASE PEGANDO DA CLASSE CONFIGURACOES FIREBASE
     */
    public void inicializarFirebaseAuth() {
        refAutenticacaofb = ConfiguracaoFirebase.metodoAutenticacaoFirebase();
    }


    /**
     * METODO DE CLIQUE BOTAO FINALIZAR CADASTRO
     */
    public void metodoBotaoFinalizarCadastro() {


        nomeCadastro = campoCadastroNome.getText().toString();
        emailCadastro = campoCadastroEmail.getText().toString();
        senhaCadastro = campoCadastroSenha.getText().toString();


        if (!nomeCadastro.isEmpty()) {

            if (!emailCadastro.isEmpty()) {

                if (!senhaCadastro.isEmpty()) {

                    Usuarios classeUsuarios = new Usuarios();

                    classeUsuarios.setNome(nomeCadastro);
                    classeUsuarios.setEmail(emailCadastro);
                    classeUsuarios.setSenha(senhaCadastro);
                    classeUsuarios.setTipo("Entregador");

                    //Chamando metodo pra salvar o usuario
                    metodoSalvarUsuarioFirebase(classeUsuarios);


                } else {

                    Toast.makeText(CadastroEntregador.this, "Por favor insira uma senha válida", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(CadastroEntregador.this, "Por favor preencha o email", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(CadastroEntregador.this, "Por favor preencha o campo nome", Toast.LENGTH_LONG).show();
        }


    }


    /**
     * METODO PRA SALVAR O USUARIO EMAIL SENHA ID
     */
    private void metodoSalvarUsuarioFirebase(final Usuarios variavelClasseUsuarios) {

        refAutenticacaofb = ConfiguracaoFirebase.metodoAutenticacaoFirebase();
        refAutenticacaofb.createUserWithEmailAndPassword(variavelClasseUsuarios.getEmail(),
                variavelClasseUsuarios.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    idUsuario = task.getResult().getUser().getUid();
                    variavelClasseUsuarios.setId(idUsuario);
                    variavelClasseUsuarios.salvarUsuario();


                    startActivity(new Intent(CadastroEntregador.this, Home.class));
                    finish();
                    Toast.makeText(CadastroEntregador.this, "Usuário salvo com sucesso", Toast.LENGTH_LONG).show();


                }


            }
        });

    }

    /**
     * METODO SALVAR O CADASTRO DO USUARIO
     */
    public void metodoSalvarCadastroEntregador(View view) {

        metodoBotaoFinalizarCadastro();


    }

}