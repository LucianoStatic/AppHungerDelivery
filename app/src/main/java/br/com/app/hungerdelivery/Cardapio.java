package br.com.app.hungerdelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import adapter.AdapterEmpresa;
import adapter.AdapterProduto;
import dmax.dialog.SpotsDialog;
import fragment.PedidosFragment;
import fragment.ProdutosFragment;
import fragment.PromocoesFragment;
import fragment.SobreFragment;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.Clientes;
import model.Empresa;
import model.Produtos;

public class Cardapio extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio, nometeste;
    private Empresa empresaSelecionada;
    private List<Produtos> listaProdutos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterProduto adapterProduto;
    private String idEmpresaLog;
    private Button botaoFProduto, botaoFPromocao;
    private SmartTabLayout smartTabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        //TODO: Inicializando componentes
        inicializarComponentes();

        //TODO: Inicializando instancia do firebase
        firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();


        //TODO: Metodo pra recuperar a empresa selecionada


        Bundle bundle = getIntent().getExtras(); // metodo bundle pega a posicao onde tu clicou e  o nome da tabela no firebase e depois faz a conversão
        if (bundle != null) {

            empresaSelecionada = (Empresa) bundle.getSerializable("empresas"); //pra pegar tbm os dados da empresa que a gente seleciona

            textNomeEmpresaCardapio.setText(empresaSelecionada.getNomeEmpresa());

            idEmpresaLog = empresaSelecionada.getIdEmpresa();


            String urlFotoCardapio = empresaSelecionada.getUrlImagemEmpresa();
            Picasso.get().load(urlFotoCardapio).into(imageEmpresaCardapio);


        }


        //TODO: Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(empresaSelecionada.getNomeEmpresa());
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_west);


        //TODO: Configurar adapter smartTabs - Utilizando o Bundle na transição dos dandos entre as tabs

        FragmentPagerItem itemBranco = FragmentPagerItem.of("Produtos", ProdutosFragment.class,
                new Bundler().putSerializable("empresas", empresaSelecionada).get());

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)

                        .add(itemBranco)
                        .add("Pedidos", PedidosFragment.class)
                        .add("Promoções", PromocoesFragment.class)
                        .create()
        );
        //TODO: Seta o adapter que foi criado acima e apos isso seta na smarttab
        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);


        //TODO: CONFIGURAR O RECICLER-VIEW
        //recuperarDadosUsuario();

    }


    private void recuperarDadosCardapio() {

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idEmpresaLog);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaProdutos.clear();
                for (DataSnapshot ds : snapshot.getChildren()) { //usando um for pra percorrer a lista de produtos no banco de dados

                    listaProdutos.add(ds.getValue(Produtos.class));

                    //apos ter encontrado todos os dados encerra
                }
                adapterProduto.notifyDataSetChanged(); //NOTIFICA O ADAPTER

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    //TODO: Instanciar componentes
    private void inicializarComponentes() {

        //recyclerProdutosCardapio = findViewById(R.id.reciclerFragProduto);
        imageEmpresaCardapio = findViewById(R.id.circleImageView);
        textNomeEmpresaCardapio = findViewById(R.id.nomeEmpresaCardapio);

        //botaoFProduto = findViewById(R.id.botaoFragmentProdutos);
        //botaoFPromocao = findViewById(R.id.botaoFragmentPromocoes);

        smartTabLayout = findViewById(R.id.viewPagerTab);
        viewPager = findViewById(R.id.viewPager);
    }


    //TODO: Metodo para abrir a tela de FRAGMENTO PRODUTOS
    /**
     public void metodoAbrirTelaFragmentoProduto(View view) {

     //TODO: Configurar objeto para o Fragmento
     FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); //TODO: COMEÇA A TRANSACAO E DPS TEM QUE ENCERRAR

     //TODO: Aqui faz a busca do layout do fragmento e depois vc seta a classe que tu quer exibir
     transaction.replace(R.id.layoutFragmentoConteudo, produtosFragment);

     //TODO: Encerra a transação
     transaction.commit();


     }
     */

    /**
     //TODO: Metodo para abrir a tela de FRAGMENTO PROMOÇÕES
     public void metodoAbrirTelaFragmentoPromocoes(View view) {

     //TODO: Configurar objeto para o Fragmento
     FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); //TODO: COMEÇA A TRANSACAO E DPS TEM QUE ENCERRAR

     //TODO: Aqui faz a busca do layout do fragmento e depois vc seta a classe que tu quer exibir
     transaction.replace(R.id.layoutFragmentoConteudo, promocoesFragment);

     //TODO: Encerra a transação
     transaction.commit();


     }
     */

}