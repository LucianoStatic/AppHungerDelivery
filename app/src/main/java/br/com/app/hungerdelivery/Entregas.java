package br.com.app.hungerdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.AdapterPedido;
import dmax.dialog.SpotsDialog;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.Pedido;

public class Entregas extends AppCompatActivity {
    private RecyclerView recyclerEntrega;
    private AdapterPedido adapterComandaPedido;
    private List<Pedido> listaPedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresaLogada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entregas);

        inicializarComponentes();

        //TODO: Inicializando o FirebaseRef
        firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();

        //TODO: Recuperar id da empresa que esteja logada
        idEmpresaLogada = UsuarioFirebase.pegarIdUsuario();

        //TODO: Configurar RecicleView
        recyclerEntrega.setLayoutManager(new LinearLayoutManager(this));
        recyclerEntrega.setHasFixedSize(true);
        adapterComandaPedido = new AdapterPedido(listaPedidos);
        recyclerEntrega.setAdapter(adapterComandaPedido);

        recuperarListaPedidos();


    }



    //TODO: Metodo inicializar componentes
    private void inicializarComponentes() {

        recyclerEntrega = findViewById(R.id.reciclerEntregas);
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
                .child("pedidoTeste");

        //TODO: Metodo pra fazer pesquisa personalizada com uma QUERY
        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("Confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //lista de pedidos
                listaPedidos.clear();

                //TODO: Verifica se a lista esta vazia caso sim cai no if, caso nao vai direto
                if (listaPedidos.isEmpty()) {
                    dialog.dismiss();
                    Toast.makeText(Entregas.this, "Você não possui nenhum pedido disponível", Toast.LENGTH_LONG).show();

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


}




