package fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import adapter.AdapterProduto;
import br.com.app.hungerdelivery.R;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import model.Empresa;
import model.Produtos;


public class ProdutosFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TextView textoFrame;

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio;

    private List<Produtos> listaProdutos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterProduto adapterProduto;
    private String idEmpresaLog;
    private  Empresa empresaSelecionada;
    private ValueEventListener valueEventListenerProdutos;
    private String user = UsuarioFirebase.pegarIdUsuario();
    private String myStr;
    private TextView tevMyText;


    public ProdutosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PromocoesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProdutosFragment newInstance(String param1, String param2) {
        ProdutosFragment fragment = new ProdutosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produtos, container, false);




        //TODO: Inicializando componentes
        recyclerProdutosCardapio = view.findViewById(R.id.reciclerCardapioProdutos);
        imageEmpresaCardapio = view.findViewById(R.id.circleImageView);
        textNomeEmpresaCardapio = view.findViewById(R.id.nomeEmpresaCardapio);

        //TODO: Inicializando instancia do firebase
        firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();


        //TODO: CONFIGURAR ADAPTER
        adapterProduto = new AdapterProduto(listaProdutos, view.getContext());

        //TODO: CONFIGURAR O RECICLER-VIEW - NECESSARIO O USO DO VIEW.GETCONTEXT EM BASE DE FRAGMENTS
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerProdutosCardapio.setLayoutManager(layoutManager);
        recyclerProdutosCardapio.setHasFixedSize(true);
        recyclerProdutosCardapio.setAdapter(adapterProduto);



        //Pegar o tipoProduto do EstoqueActivity
        Bundle bundle = getArguments();

        empresaSelecionada = (Empresa) bundle.getSerializable("empresas");
        idEmpresaLog = empresaSelecionada.getIdEmpresa();
        if (empresaSelecionada == null){
            Toast.makeText(getActivity(), "O tipo produto é nulo", Toast.LENGTH_LONG).show();
        }else {

            Log.i("SERAAAA","TESTANDO PRA VE SE TA CHEGANDO OS DADOS "+empresaSelecionada.getIdEmpresa());

            metodoListarCardapioProdutos();

        }

        return view;
    }


    public void metodoListarCardapioProdutos(){


        //TODO AQUI E ONDE ESTA O  METODO PRA BUSCAR
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



}