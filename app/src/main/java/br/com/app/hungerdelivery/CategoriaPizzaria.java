package br.com.app.hungerdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import adapter.AdapterEmpresa;
import helper.ConfiguracaoFirebase;
import listener.RecyclerItemClickListener;
import model.Empresa;

public class CategoriaPizzaria extends AppCompatActivity {




    /**
     * INSTANCIAR O FIREBASE DE AUTENTICACAO
     */
    private FirebaseAuth firebaseAutenticacao;

    /**
     * INSTANCIAR UM DATABASE DO FIREBASE
     */
    private DatabaseReference firebaseRef;

    /**
     * METERIAL SEARCH VIEW
     */
    private MaterialSearchView searchView;

    /**
     * INSTANCIAR O RECICLER VIEW
     */
    private RecyclerView recyclerEmpresa;

    /**
     * INSTANCIAR UM LIST PRA GERAR UMA LISTA DE EMPRESAS PARA USUARIO HOME
     */
    private List<Empresa> empresas = new ArrayList<>();

    /**
     * INSTANCIAR UM ADAPTER PRA PUXAR OS COMPONENTES DA EMPRESA
     */
    private AdapterEmpresa adapterEmpresa;

    public LinearLayout layoutpizza;
    public TextView porfolioEmpresa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_pizzaria);


        inicializarComponentes();

        firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        firebaseAutenticacao = ConfiguracaoFirebase.metodoAutenticacaoFirebase();

        /**
         * CONFIGURAÇOES DA TOOLBAR
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pizzarias");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_west);


        /**
         * CONFIGURAR O RECICLER-VIEW
         */
        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresa.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresa.setAdapter(adapterEmpresa);




        metodoBuscarPizzarias();

//TODO: Evento de clique para redirencionar para o cardápio

        recyclerEmpresa.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerEmpresa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Empresa empresaSelecionada = empresas.get(position);
                        Intent i = new Intent(CategoriaPizzaria.this, Cardapio.class);
                        i.putExtra("empresas",empresaSelecionada); //O put leva os dados da empresa que eu cliquei para o cardapio
                        Log.i("PEGANDO O PUT EXTRA!!!!","VALOR DO QUE ESTA NO PUT EXTRA: "+i);
                        startActivity(i);




                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));


    }



    /**
     * METODO QUE RECUPERA AS PIZZARIAS CADASTRADAS
     */
    private void metodoBuscarPizzarias(){


      DatabaseReference pizzariaRef = firebaseRef
                .child("empresas"); // vai no laco da tabela que no caso e empresa
        Query pesquisarPizzaria = pizzariaRef.orderByChild("categoria")
        .equalTo("Pizzaria"); // aq a gente vai via o tipo de categoria



        pesquisarPizzaria.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {




            if(snapshot.getChildrenCount()>0){

            }else{

                Toast.makeText(CategoriaPizzaria.this, "Nenhum valor encontrado", Toast.LENGTH_LONG).show();

            }
                    empresas.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) { //usando um for pra percorrer a lista de produtos no banco de dados
                        empresas.add(ds.getValue(Empresa.class));
                        //apos ter encontrado todos os dados encerra

                        adapterEmpresa.notifyDataSetChanged();
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    /**
     * METODO VERIFICAR STATUS
     */
    private void verificar(){


        DatabaseReference pizzariaRef = firebaseRef
                .child("empresas"); // vai no laco da tabela que no caso e empresa
        Query pesquisarPizzaria = pizzariaRef.orderByChild("status")
                .equalTo("Fechado"); // aq a gente vai via o tipo de categoria

        pesquisarPizzaria.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) { //usando um for pra percorrer a lista de produtos no banco de dados
                    empresas.add(ds.getValue(Empresa.class));
                    //apos ter encontrado todos os dados encerra
                    adapterEmpresa.notifyDataSetChanged();

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }






    /**
     * METODO INICIALIZA OS COMPONENTES
     */
    private void inicializarComponentes() {

        searchView = findViewById(R.id.searchmaterial);

        recyclerEmpresa = findViewById(R.id.reciclerPizzas);
        //setar imagens

    }

}