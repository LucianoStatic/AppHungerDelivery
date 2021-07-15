package fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.squareup.picasso.Picasso;
import com.tuann.floatingactionbuttonexpandable.FloatingActionButtonExpandable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import adapter.AdapterProduto;
import br.com.app.hungerdelivery.R;
import dmax.dialog.SpotsDialog;
import helper.ConfiguracaoFirebase;
import helper.UsuarioFirebase;
import listener.RecyclerItemClickListener;
import model.Clientes;
import model.Empresa;
import model.ItensPedido;
import model.Pedido;
import model.Produtos;


public class ProdutosFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TextView textoFrame;

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView txtQuantidadeProduto, txtValorTotalProduto;

    private List<Produtos> listaProdutos = new ArrayList<>();
    private List<ItensPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterProduto adapterProduto;
    private String idEmpresaLog;
    private Empresa empresaSelecionada;
    private ValueEventListener valueEventListenerProdutos;
    private String user = UsuarioFirebase.pegarIdUsuario();

    private AlertDialog dialog;
    private String idClienteLogado;
    private Clientes cliente;
    private Pedido pedidoRecuperado;
    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private FloatingActionButtonExpandable fb;
    private int metodoCartaoDinheiro;
    private Dialog alertaDialogo;

    public ProdutosFragment() {
        // Required empty public constructor
    }


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
        fb = view.findViewById(R.id.fab);
        txtQuantidadeProduto = view.findViewById(R.id.txtCarrinhoQuantidade);
        txtValorTotalProduto = view.findViewById(R.id.txPedidoValorTotal);

        alertaDialogo = new Dialog(getActivity());
        //TODO: Inicializando instancia do firebase
        firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();

        idClienteLogado = UsuarioFirebase.pegarIdUsuario();

        //TODO: CONFIGURAR ADAPTER
        adapterProduto = new AdapterProduto(listaProdutos, view.getContext());

        //TODO: CONFIGURAR O RECICLER-VIEW - NECESSARIO O USO DO VIEW.GETCONTEXT EM BASE DE FRAGMENTS
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerProdutosCardapio.setLayoutManager(layoutManager);
        recyclerProdutosCardapio.setHasFixedSize(true);
        recyclerProdutosCardapio.setAdapter(adapterProduto);


        //TODO: CONFIGURAR O EVENTO DE CLIQUE DO RECICLER VIEW
        recyclerProdutosCardapio.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),
                        recyclerProdutosCardapio,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                confirmarQuantidade(position);


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        })
        );


        //Pegar o tipoProduto do EstoqueActivity
        Bundle bundle = getArguments();

        empresaSelecionada = (Empresa) bundle.getSerializable("empresas");
        idEmpresaLog = empresaSelecionada.getIdEmpresa();
        if (empresaSelecionada == null) {
            Toast.makeText(getActivity(), "O tipo produto é nulo", Toast.LENGTH_LONG).show();
        } else {

            Log.i("SERAAAA", "TESTANDO PRA VE SE TA CHEGANDO OS DADOS " + empresaSelecionada.getIdEmpresa());

            metodoListarCardapioProdutos();

        }


        recuperarDadosUsuario();

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                metodoConfirmarPedido();

            }
        });

        return view;
    }


    //TODO: Metodo Listar Cardapio dos Produtos
    public void metodoListarCardapioProdutos() {


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


    //TODO: Metodo Confirmar quantidade de items
    private void confirmarQuantidade(int posicao) {

        //TODO: COnfigurando a tela de dialogo para quantidade
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade do produto selecionado");

        //TODO: Criar um EditText
        EditText editQuantidade = new EditText(getActivity());
        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Clique de botao ok positivo
                String quantidade = editQuantidade.getText().toString();

                //TODO: Recuperar os dados de um produto quando vc seleciona ele, via position
                Produtos produtoSelecionado = listaProdutos.get(posicao);
                ItensPedido pedidosItem = new ItensPedido();
                pedidosItem.setIdProduto(produtoSelecionado.getIdProdutos());
                pedidosItem.setNomeProduto(produtoSelecionado.getNome());
                pedidosItem.setPrecoProduto(produtoSelecionado.getPreco());
                pedidosItem.setQuantiade(Integer.parseInt(quantidade));
                itensCarrinho.add(pedidosItem);

                //TODO: SABER SE JA EXISTE UM PEDIDO PENDENTE
                if (pedidoRecuperado == null) {  //TODO: SE NAO EXISTE NENHUM PEDIDO RECUPERADO A GENTE CRIA UM NOVO PEDIDO

                    pedidoRecuperado = new Pedido(idClienteLogado, idEmpresaLog);


                }

                pedidoRecuperado.setNome(cliente.getNome());
                pedidoRecuperado.setEndereco(cliente.getEndereco());
                pedidoRecuperado.setItens(itensCarrinho);
                pedidoRecuperado.metodoSalvarPedido();


            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Clique de botao ok positivo


            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


    //TODO: -----Metodo recuperar dados do usuario-----
    private void recuperarDadosUsuario() {

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();
        //TODO: Recuperar os dados do usuario apos ter criado o dialog de carregamento
        DatabaseReference clienteRef = firebaseRef
                .child("clientes")
                .child(idClienteLogado);

        clienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //TODO: Testar se realmente temos essas informacoes
                if (snapshot.getValue() != null) { //TODO: SE FOR DIFERENTE DE NULO, FAÇA ALGO

                    cliente = snapshot.getValue(Clientes.class); //TODO: FEITO ISSO RECUPERAMOS OS DADOS DO CLIENTE LOGADO


                }

                //TODO: Metodo recuperar pedido
                recuperarPedido();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    //TODO: Recuperar pedido/s salvo no banco de dados
    private void recuperarPedido() {

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidocliente")
                .child(idEmpresaLog)
                .child(idClienteLogado);
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();


                if (snapshot.getValue() != null) {

                    pedidoRecuperado = snapshot.getValue(Pedido.class);

                    itensCarrinho = pedidoRecuperado.getItens();

                    for (ItensPedido itensPedido : itensCarrinho) {

                        int qtde = itensPedido.getQuantiade();
                        Double preco = itensPedido.getPrecoProduto();

                        totalCarrinho += (qtde * preco); //o mais e pra acomular valores

                        qtdItensCarrinho += qtde;
                    }


                }


                DecimalFormat df = new DecimalFormat("0.00");

                txtQuantidadeProduto.setText("Quantidade: " + String.valueOf(qtdItensCarrinho));
                txtValorTotalProduto.setText("Valor Total: " + df.format(totalCarrinho));

                dialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    //TODO: Metodo confirmar o pedido do cliente
    private void metodoConfirmarPedido() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecione um método de pagamento");

        CharSequence[] itens = new CharSequence[]{
                "Dinheiro", "Máquina cartão"
        };

        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                metodoCartaoDinheiro = which;
            }
        });

        EditText editObservacao = new EditText(getActivity());
        editObservacao.setHint("Digite uma observação");
        builder.setView(editObservacao);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String observacaoPedido = editObservacao.getText().toString();
                pedidoRecuperado.setMetodoPagamento(metodoCartaoDinheiro);
                pedidoRecuperado.setObservacao(observacaoPedido);

                pedidoRecuperado.setStatus("Confirmado");

                //TODO:Chamando metodo finalizar pedido
                pedidoRecuperado.metodoTestePedido();

                //TODO:Chamando metodo remover pedido
                pedidoRecuperado.metodoRemovePedidosCliente();

                pedidoRecuperado = null;

               alertaCliente();

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();


    }


    //TODO:Metodo pra chamar alerta de dialogo
    private void alertaCliente() {


        alertaDialogo.setContentView(R.layout.win_layout_dialog);
        alertaDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageView = alertaDialogo.findViewById(R.id.imagemClose);
        Button btnOk = alertaDialogo.findViewById(R.id.botaookay);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaDialogo.dismiss();
            }
        });


        alertaDialogo.show();


    }



    //TODO:Metodo alertaDialog Animado
    private void alertaAnimado(){

        new FancyGifDialog.Builder(getActivity())
                .setTitle("Granny eating chocolate dialog box") // You can also send title like R.string.from_resources
                .setMessage("This is a granny eating chocolate dialog box. This library is used to help you easily create fancy gify dialog.") // or pass like R.string.description_from_resources
                .setTitleTextColor(R.color.purple_200)
                .setDescriptionTextColor(R.color.light_blue_600)
                .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                .setPositiveBtnBackground(R.color.purple_200)
                .setPositiveBtnText("Ok") // or pass it like android.R.string.ok
                .setNegativeBtnBackground(R.color.light_blue_600)
                .setGifResource(R.drawable.gif1)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(getActivity(),"Ok",Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(getActivity(),"Cancel",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();


    }
}