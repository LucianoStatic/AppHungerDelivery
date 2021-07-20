package br.com.app.hungerdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TelaInicial extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
    }



    public void metodoTelaInscrever(View view){

        startActivity(new Intent(getApplicationContext(), CadastroUsuario.class));

    }


    public void metodoTelaLogin(View view){

        startActivity(new Intent(getApplicationContext(), MainActivity.class));


    }

}