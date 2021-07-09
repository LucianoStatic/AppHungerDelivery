package br.com.app.hungerdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class CategoriaComercios extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_comercios);

    }


    public void abrirTelaPizzaria(View view){

        startActivity(new Intent(CategoriaComercios.this, CategoriaPizzaria.class));
        finish();

    }


}