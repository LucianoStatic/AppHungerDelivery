package helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.app.hungerdelivery.CategoriaComercios;
import br.com.app.hungerdelivery.Home;
import br.com.app.hungerdelivery.PainelEmpresa;
import model.Usuarios;

public class UsuarioFirebase {

    /**
     * METODO PRA PEGAR O ID DO USUARIO DO FIREBASE
     */
    public static String pegarIdUsuario() {

        FirebaseAuth autenticacao = ConfiguracaoFirebase.metodoAutenticacaoFirebase();

        return autenticacao.getCurrentUser().getUid();

    }

    /**
     * METODO PRA RECUPERAR UM USUARIO ATUAL
     */
    public static FirebaseUser pegarUsuarioAtual() {

        FirebaseAuth usuario = ConfiguracaoFirebase.metodoAutenticacaoFirebase();

        return usuario.getCurrentUser();
    }


    /**
     * METODO PRA RECUPERAR UM USUARIO ATUAL
     */
    public static boolean atualizarTipoUsuario(String tipo) { // AQUI RECEBE LA DO SWTICH SE E CLIENTE OU EMPRESA

        try {

            FirebaseUser user = pegarUsuarioAtual();
            UserProfileChangeRequest perfilRecebe = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tipo)
                    .build();
            user.updateProfile(perfilRecebe);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * METODO PRA REDIRECIONAR O USUARIO A UMA TELA ESPECIFICA
     */
    public static void redirecionarUsuarioLogado(final Activity activity) {
        /**
         * LOGICA PEGA O USUARIO ATUAL VERIFICA SE ELE EXISTE, CASO SIM ELE EXECULTA O CODIGO E MANDA PRA TELA RESPONSAVEL
         * CASO CONTRARIO ELE NAO FARA NADA!
         */
        FirebaseUser user = pegarUsuarioAtual();

        if (user != null) {

            DatabaseReference usuarioRef = ConfiguracaoFirebase.metodoReferenciaGetFirebase()
                    .child("usuarios")
                    .child(pegarIdUsuario());
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    Usuarios usuarios = snapshot.getValue(Usuarios.class);
                    String recuperarTipoUsuario = usuarios.getTipo(); //RECEBE O TIPO DO USUARIO
                    String recuperarNomeUsuario = usuarios.getNome(); //RECEBE O NOME DO USUARIO EX: O BRABO DELAS

                    if (recuperarTipoUsuario.equals("Empresa")) { //COMPARA QUE O BRABO E DO TIPO: EMP

                        activity.startActivity(new Intent(activity, PainelEmpresa.class)); //ABRINDO TELA PAINEL

                    } else if (recuperarTipoUsuario.equals("Cliente")) {

                        activity.startActivity(new Intent(activity, Home.class));

                    } else if (recuperarTipoUsuario.equals("Entregador")) {

                        activity.startActivity(new Intent(activity, CategoriaComercios.class));
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {


                }
            });


        }


    }


}
