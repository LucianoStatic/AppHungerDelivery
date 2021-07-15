package br.com.app.hungerdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

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
import adapter.AdapterProduto;
import fragment.ProdutosFragment;
import helper.ConfiguracaoFirebase;
import listener.RecyclerItemClickListener;
import model.Empresa;

public class Home extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializarComponentes();

        firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        firebaseAutenticacao = ConfiguracaoFirebase.metodoAutenticacaoFirebase();


        /**
         * CONFIGURAÇOES DA TOOLBAR
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Hunger Delivery");
        toolbar.setLogo(R.drawable.delivery2);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        /**
         * CONFIGURAR O RECICLER-VIEW
         */
        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresa.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresa.setAdapter(adapterEmpresa);

        /**
         * RODAR METODO PRA RECUPERAR OS PRODUTOS DO BANCO DE DADOS
         */
        metodoRecuperarEmpresaFirebase();


        /**
         * CONFIGURACAODO SEARCH VIEW
         */
        searchView.setHint("Pesquisar Restaurantes");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);


                return true;
            }
        });


        //TODO: Evento de clique para redirencionar para o cardápio

        recyclerEmpresa.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerEmpresa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Empresa empresaSelecionada = empresas.get(position);
                        Intent i = new Intent(Home.this, Cardapio.class);
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
     * METODO QUE RECUPERA OS DADOS DAS EMPRESAS QUE ESTÃO NO FIREBASE
     */
    private void metodoRecuperarEmpresaFirebase() {

        DatabaseReference empresaRef = firebaseRef.
                child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                empresas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) { //usando um for pra percorrer a lista de produtos no banco de dados
                    empresas.add(ds.getValue(Empresa.class));
                    //apos ter encontrado todos os dados encerra
                }

                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    /**
     * METODO PRA PESQUISAR EMPRESA NO CAMPO DE PESQUISA DO SEARCH VIEW
     */
    private void pesquisarEmpresas(String valorPesquisa) {

        DatabaseReference empresasRef = firebaseRef.
                child("empresas");

        Query query = empresasRef.orderByChild("nome").startAt(valorPesquisa)
                .endAt(valorPesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                empresas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) { //usando um for pra percorrer a lista de produtos no banco de dados
                    empresas.add(ds.getValue(Empresa.class));
                    //apos ter encontrado todos os dados encerra
                }

                adapterEmpresa.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    /**
     * MENUS
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        /**
         * CONFIGURAR BOTAO DE PESQUISA
         */
        MenuItem item = menu.findItem(R.id.menuPesquisa);

        searchView.setMenuItem(item);


        return super.onCreateOptionsMenu(menu);


    }

    /**
     * CHAMADA DE TELA PRA CADA MENU SELECIONADO
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuEmpresaSair:
                deslogarEmpresa();

                break;

            case R.id.menuConfiguracoesUsuario:
                abrirConfiguracoesUsuario();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    /**
     * METODO SAIR PARTE DO EMPRESARIO
     */
    private void deslogarEmpresa() {

        try {

            firebaseAutenticacao.signOut();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * METODO INICIALIZA OS COMPONENTES
     */
    private void inicializarComponentes() {

        searchView = findViewById(R.id.searchmaterial);

        recyclerEmpresa = findViewById(R.id.reciclerListaEmpresas);
    }


    /**
     * METODO ABRIR CONFIGURACOES DA EMPRESA
     */
    private void abrirConfiguracoesUsuario() {

        startActivity(new Intent(Home.this, Entregas.class));
    }


}