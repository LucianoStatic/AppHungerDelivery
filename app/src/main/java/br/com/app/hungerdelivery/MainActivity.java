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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.Usuarios;

public class MainActivity extends AppCompatActivity {

    private Button botaoFazerLogin;
    private EditText campoLoginEmail, campoLoginSenha;
    public String emailLogin, senhaLogin;
    private AlertDialog caixaDialogo;
    private Button abrirJanelaCadastro;
    /**
     * CRIAR INSTANCIA DO FIREBASE DE AUTENTICACAO
     */
    private FirebaseAuth refAutenticacaoFIREBASE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        InicializarComponentes();
        inicializarFirebaseAuth();

        //UsuarioFirebase.redirecionarUsuarioLogado(MainActivity.this);

    }

    /**
     * METODO ON START PRA SABER SE EXISTE UM LOGIN PRA IR DIRETO OU MANDAR PRA TELA DE CRIAR CONTA
     */
    @Override
    protected void onStart() {
        super.onStart();

        UsuarioFirebase.redirecionarUsuarioLogado(MainActivity.this);
    }



    /**
     *METODO PRA FAZER AUTENTICACAO DO USUARIO NO APP
     */

        private void metodoFazerAutenticacaoUsuario(final Usuarios recebeUsuario){

            refAutenticacaoFIREBASE = ConfiguracaoFirebase.metodoAutenticacaoFirebase();
            refAutenticacaoFIREBASE.signInWithEmailAndPassword(recebeUsuario.getEmail(), recebeUsuario.getSenha())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                            UsuarioFirebase.redirecionarUsuarioLogado(MainActivity.this);


                            }else{
                                Toast.makeText(MainActivity.this, "Ocorreu um erro ao tentar autenticar", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        }


    /**
     * METODO DE CLIQUE DO BOTAO PRA FAZER O LOGIN COM AS INFORMACOES DE AUTENTICACAO
     */
        public void metodoConfirmarLogin(View view){

            String emailUsuario = campoLoginEmail.getText().toString();
            String senhaUsuario = campoLoginSenha.getText().toString();
            if(!emailUsuario.isEmpty()){

                if(!senhaUsuario.isEmpty()){
                    Usuarios usuario = new Usuarios();
                    usuario.setEmail(emailUsuario);
                    usuario.setSenha(senhaUsuario);
                    metodoFazerAutenticacaoUsuario(usuario);

                }else{
                    Toast.makeText(MainActivity.this, "Preencha o campo senha", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(MainActivity.this, "Preencha o campo de email", Toast.LENGTH_LONG).show();
            }

        }




    /**
     * METODO QUE INICIALIZA OS COMPONENTES
     */
    public void InicializarComponentes() {

        campoLoginEmail = findViewById(R.id.campoLoginEmail);
        campoLoginSenha = findViewById(R.id.campoLoginSenha);
        botaoFazerLogin = findViewById(R.id.botaoLogin);


    }


    /**
     * INICIALIZAR O FIREBASE PEGANDO DA CLASSE CONFIGURACOES FIREBASE
     */
    public void inicializarFirebaseAuth() {
        refAutenticacaoFIREBASE = ConfiguracaoFirebase.metodoAutenticacaoFirebase();
    }



    /**
     * METODO ABRIR JANELA DE CADASTRO
     */
    public void metodoAbrirTelaCadastro(View view) {

        startActivity(new Intent(getApplicationContext(), CadastroUsuario.class));

    }


    public void metodoAbrirTelaCadastroEntregador(View view){


        startActivity(new Intent(getApplicationContext(), CadastroEntregador.class));

    }



}