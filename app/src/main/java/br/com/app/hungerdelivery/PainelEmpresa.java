package br.com.app.hungerdelivery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.AdapterProduto;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import listener.RecyclerItemClickListener;
import model.Empresa;
import model.Produtos;

public class PainelEmpresa extends AppCompatActivity {

    private TextView textoStatusHr;

    /**
     * INSTANCIAR O FIREBASE DE AUTENTICACAO
     */
    private FirebaseAuth firebaseAutenticacao;

    /**
     * REFERENCIA DO BANCO DE DADOS FIREBASE
     */
    private DatabaseReference firebaseRef;

    /**
     * CRIAR ATRIBUTO  RECICLER
     */
    private RecyclerView recyclerProdutos;

    /**
     * CRIAR REFERENCIA PARA O ADAPTER
     */
    private AdapterProduto adapterProduto;

    /**
     * CRIAR UM ATRIBUTO DO TIPO LISTA
     */
    private List<Produtos> produtos = new ArrayList<>();

    /**
     *
     * CRIAR VARIAVEL PRA PEGAR O ID PRODUTO
     */
    private String idEmpresaLogada;

    public static final String status = "Aberto";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel_empresa);

        /**
         * METODO PRA INICIALIZAR OS COMPONENTES
         */
        metodoInicializarComponentes();

        /**
         * CHAMANDO REFERENCIA DE AUTENTICACAO DO FIREBASE
         */
        firebaseAutenticacao = ConfiguracaoFirebase.metodoAutenticacaoFirebase();

        /**
         * CHAMANDO REFERECIA DE FIREBASE DATABASE
         */
        firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();

        /**
         * INICIALIZANDO UMA FUNCAO PRA BUSCAR O ID DO USUARIO QUE ESTA LOGADO
         */
        idEmpresaLogada = UsuarioFirebase.pegarIdUsuario();


        /**
         * CONFIGURAÇOES DA TOOLBAR
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Painel - Empresa");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_west);

        /**
         * CONFIGURAR O RECICLER-VIEW
         */
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);

        /**
         * RODAR METODO PRA RECUPERAR OS PRODUTOS DO BANCO DE DADOS
         */
        metodoRecuperarProdutosFirebase();

        /**
         * METODO VERIFICAR SE A EMPRESA ESTA ABERTA OU NAO
         */
        metodoVerificarFuncionamento();

        /**
         * RODAR O METODO DE CLIQUE DO RECICLER VIEW PRA EXCLUIR UM PRODUTO
         */
            recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(
                    this, recyclerProdutos,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                            Produtos produtoSelecionado = produtos.get(position);
                            produtoSelecionado.metodoExcluirProduto();
                            Toast.makeText(PainelEmpresa.this, "Produto Removido com sucesso!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    }
            ));

    }


    /**
     * METODO INICIALIZAR COMPONENTES
     */
    private void metodoInicializarComponentes() {

        recyclerProdutos = findViewById(R.id.reciclerProdutosEmpresa);
        textoStatusHr = findViewById(R.id.txtStatusab);


    }


    /**
     * METODO RECUPERAR PRODUTOS SALVOS NO FIREBASE
     */
    private void metodoRecuperarProdutosFirebase() {

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idEmpresaLogada);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                produtos.clear();
                for(DataSnapshot ds: snapshot.getChildren()){ //usando um for pra percorrer a lista de produtos no banco de dados

                    produtos.add(ds.getValue(Produtos.class));

                    //apos ter encontrado todos os dados encerra
                }
                adapterProduto.notifyDataSetChanged(); //NOTIFICA O ADAPTER

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    /**
     * METODO RESPONSAVEL POR VERIFICAR SE O COMERCIO VAI ESTAR ABERTO OU NAO PARA O USUARIO
     */
    private void metodoVerificarFuncionamento(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();
        DatabaseReference requisicoes = firebaseRef.child("empresas");
        DatabaseReference requisicao = requisicoes.child(idEmpresaLogada);

        requisicao.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                Map objeto = new HashMap();
                objeto.put("status",status);
                //quero atualizar o status que ja tem o valor na classe Painel viagens do motorista
                requisicao.updateChildren(objeto);


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
        inflater.inflate(R.menu.menu_empresa, menu);


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
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto:
                abrirNovoProduto();
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
     * METODO ABRIR CONFIGURACOES DA EMPRESA
     */
    private void abrirConfiguracoes() {

        startActivity(new Intent(PainelEmpresa.this, ConfiguracaoEmpresa.class));
    }


    /**
     * METODO ABRIR NOVO PRODUTO PRA CADASTRAR NA EMPRESA
     */
    private void abrirNovoProduto() {

        startActivity(new Intent(PainelEmpresa.this, ProdutoEmpresa.class));

    }

}