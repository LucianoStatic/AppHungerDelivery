package adapter;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.app.hungerdelivery.CategoriaPizzaria;
import br.com.app.hungerdelivery.R;

import helper.ConfiguracaoFirebase;
import model.Empresa;

/**
 *
 */

public class AdapterEmpresa extends RecyclerView.Adapter<AdapterEmpresa.MyViewHolder> {

    private List<Empresa> empresas;
    private DatabaseReference firebaseRef;
    private Empresa empresa;

    public AdapterEmpresa(List<Empresa> empresas) {
        this.empresas = empresas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_empresa, parent, false);

        firebaseRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase();

        return new MyViewHolder(itemLista);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
         empresa = empresas.get(i);



            DatabaseReference pizzariaRef = firebaseRef
                    .child("empresas"); //Efetua busca na tabela do DB
            Query pesquisarPizzaria = pizzariaRef.orderByChild("status")
                    .equalTo(empresa.getStatus()); //Utiliza do status pra fazer o filtro

            pesquisarPizzaria.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {



                    for(DataSnapshot ds: snapshot.getChildren()){
                        empresa = ds.getValue(Empresa.class);
                        if(empresa.getStatus().equals("Aberto")){
                            empresa = empresas.get(i);
                            holder.nomeEmpresa.setText(empresa.getNomeEmpresa());
                            holder.categoria.setText(empresa.getCategoria() + " - ");
                            holder.tempo.setText(empresa.getTempoEntrega() + " Min");
                            holder.entrega.setText("R$ " + empresa.getPrecoEntrega().toString());
                            holder.status.setText(empresa.getStatus());
                            holder.status.setTextColor(Color.GREEN);
                            holder.iconeStatus.setBackgroundResource(R.drawable.ic_aberto24);
                            //Carregar imagem
                            String urlImagem = empresa.getUrlImagemEmpresa();
                            Picasso.get().load( urlImagem ).into( holder.imagemEmpresa );
                        }else{

                            empresa = empresas.get(i);
                            holder.nomeEmpresa.setText(empresa.getNomeEmpresa());
                            holder.categoria.setText(empresa.getCategoria() + " - ");
                            holder.tempo.setText(empresa.getTempoEntrega() + " Min");
                            holder.entrega.setText("R$ " + empresa.getPrecoEntrega().toString());
                            holder.status.setText(empresa.getStatus());
                            holder.status.setTextColor(Color.RED);
                            holder.iconeStatus.setBackgroundResource(R.drawable.ic_fechado24);


                            //Carregar imagem
                            String urlImagem = empresa.getUrlImagemEmpresa();
                            Picasso.get().load( urlImagem ).into( holder.imagemEmpresa );


                        }
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });





    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView categoria;
        TextView tempo;
        TextView entrega;
        TextView iconeStatus;


              public  TextView status;

        public MyViewHolder(View itemView) {
            super(itemView);

            nomeEmpresa = itemView.findViewById(R.id.txtNomePizzaria);
            categoria = itemView.findViewById(R.id.txtCategoria);
            tempo = itemView.findViewById(R.id.txtTempoEntrega);
            entrega = itemView.findViewById(R.id.txtValorcorrida);
            imagemEmpresa = itemView.findViewById(R.id.imagemPerfilPizzaria);
            status = itemView.findViewById(R.id.txtStatusab);
            iconeStatus = itemView.findViewById(R.id.txtIconeab);
        }
    }
}
