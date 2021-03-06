package br.com.app.hungerdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.AdapterPedido;
import adapter.AdapterProduto;
import dmax.dialog.SpotsDialog;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import listener.RecyclerItemClickListener;
import model.Clientes;
import model.ItensPedido;
import model.Pedido;
import model.Produtos;

public class ComandaPedidos extends AppCompatActivity {
    private RecyclerView recyclerComandaPedido;
    private AdapterPedido adapterComandaPedido;
    private List<Pedido> listaPedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private Pedido pedidoRecuperado;
    private String idEmpresaLogada;
    private String idClienteLogado;
    private Clientes cliente;
    private String idEmpresaLog;
    private List<Produtos> listaProdutos = new ArrayList<>();
    private List<ItensPedido> itensCarrinho = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda_pedidos);

        //TODO: Inicializar componentes
        inicializarComponentes();

        //TODO: Inicializando o FirebaseRef
        firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();

        //TODO: Recuperar id da empresa que esteja logada
        idEmpresaLogada = UsuarioFirebase.pegarIdUsuario();

        //TODO: Configura????es da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Comanda - Pedidos");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_west);

        //TODO: Configurar RecicleView
        recyclerComandaPedido.setLayoutManager(new LinearLayoutManager(this));
        recyclerComandaPedido.setHasFixedSize(true);
        adapterComandaPedido = new AdapterPedido(listaPedidos);
        recyclerComandaPedido.setAdapter(adapterComandaPedido);

        recuperarListaPedidos();

        //TODO: Metodo de clique para  o RecicleView
        recyclerComandaPedido.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerComandaPedido, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if(listaPedidos !=null){

                            Pedido pedido = listaPedidos.get(position);
                            String sts  = "Pronto";

                           pedido.metodoStatus();

                            recuperarListaPedidos();

                        }



                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                })
        );




    }


    //TODO:Metodo para recuperar a lista de pedidos
    private void recuperarListaPedidos() {

        //TODO: Carregamento fazer load dos dados
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        //TODO: Metodo de consulta la no banco de dados firebase
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidoPreparo")
                ;
        //TODO: Metodo pra fazer pesquisa personalizada com uma QUERY
        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("Em preparo");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //lista de pedidos
                listaPedidos.clear();

                //TODO: Verifica se a lista esta vazia caso sim cai no if, caso nao vai direto
                if (listaPedidos.isEmpty()) {
                    dialog.dismiss();
                    Toast.makeText(ComandaPedidos.this, "Voc?? n??o possui nenhum pedido dispon??vel", Toast.LENGTH_LONG).show();

                }

                    if (snapshot.getValue() != null) {

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Pedido pedido = ds.getValue(Pedido.class);
                            listaPedidos.add(pedido);
                        }


                        adapterComandaPedido.notifyDataSetChanged();

                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }






    //TODO: Metodo inicializar componentes
    private void inicializarComponentes() {

        recyclerComandaPedido = findViewById(R.id.reciclerComandaPedidos);
    }




}