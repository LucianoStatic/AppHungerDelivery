package br.com.app.hungerdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import dmax.dialog.SpotsDialog;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.Usuarios;

public class CadastroUsuario extends AppCompatActivity {

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
        setContentView(R.layout.activity_cadastro_usuario);

        InicializarComponentes();

        inicializarFirebaseAuth();




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
                            classeUsuarios.setTipo(pegarTipoUsuarioSwitch());

                            //Chamando metodo pra salvar o usuario
                            metodoSalvarUsuarioFirebase(classeUsuarios);


                        } else {

                            Toast.makeText(CadastroUsuario.this, "Por favor insira uma senha válida", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(CadastroUsuario.this, "Por favor preencha o email", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(CadastroUsuario.this, "Por favor preencha o campo nome", Toast.LENGTH_LONG).show();
                }



    }


    /**
     * METODO INICIALIZAR OS COMPONENTES
     */
    public void InicializarComponentes() {
        campoCadastroNome = findViewById(R.id.campoCadastroNome);
        campoCadastroEmail = findViewById(R.id.campoCadastroEmail);
        campoCadastroSenha = findViewById(R.id.campoCadastroSenha);
        switchEscolhaTipo = findViewById(R.id.switchCadastro);
        botaoFinalizarCadastro = findViewById(R.id.botaoFinalizarCriacao);


    }


    /**
     * INICIALIZAR O FIREBASE PEGANDO DA CLASSE CONFIGURACOES FIREBASE
     */
    public void inicializarFirebaseAuth() {
        refAutenticacaofb = ConfiguracaoFirebase.metodoAutenticacaoFirebase();
    }

    /**
     * METODO SALVAR O CADASTRO DO USUARIO
     */
    public void metodoSalvarCadastroUsuario(View view) {

        metodoBotaoFinalizarCadastro();


    }


    /**
     * METODO PRA RETORNAR O TIPO DO USUARIO SE ELE E CLIENTE OU EMPRESA
     */
    public String pegarTipoUsuarioSwitch() {

        if(switchEscolhaTipo.isChecked()){

            return "Empresa";
        }else{

            return "Cliente";
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

            if(task.isSuccessful()){

            idUsuario = task.getResult().getUser().getUid();
            variavelClasseUsuarios.setId(idUsuario);
            variavelClasseUsuarios.salvarUsuario();

                if(pegarTipoUsuarioSwitch() == "Cliente"){

                    startActivity(new Intent(CadastroUsuario.this, Home.class));
                    finish();
                    Toast.makeText(CadastroUsuario.this, "Usuário salvo com sucesso", Toast.LENGTH_LONG).show();
                }else{

                    startActivity(new Intent(CadastroUsuario.this, PainelEmpresa.class));
                    finish();
                    Toast.makeText(CadastroUsuario.this, "Empresa inserida com sucesso!", Toast.LENGTH_LONG).show();
                }


            }



            }
        });

    }

/**
    public void metodoPraExemplo() {
        refAutenticacaofb.
                createUserWithEmailAndPassword(emailCadastro, senhaCadastro)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {


                            Toast.makeText(CadastroUsuario.this, "Cadastro realizado com sucesso!",
                                    Toast.LENGTH_LONG).show();

                            String tipoUsuarioRecebe = pegarTipoUsuarioSwitch();

                            UsuarioFirebase.atualizarTipoUsuario(tipoUsuarioRecebe);

                            metodoRedirecionarTelas(tipoUsuarioRecebe);
                        } else {


                            erroExcecao = "";
                            try {
                                throw task.getException();

                            } catch (FirebaseAuthWeakPasswordException e) {
                                erroExcecao = "Digite uma senha mais forte";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExcecao = "Por favor, digite um e-mail válido";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erroExcecao = "Essa conta já existe!";
                            } catch (Exception e) {
                                erroExcecao = "ao cadastrar: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastroUsuario.this, "Erro " + erroExcecao,
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }
    */

}