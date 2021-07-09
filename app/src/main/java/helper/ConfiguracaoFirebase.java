package helper;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutenticacao;
    private static StorageReference referenceStorage;

    /**
     * RETORNA  A REFERENCIA  DO DATABASE REFERENCE
     */
    public static DatabaseReference metodoReferenciaGetFirebase(){

        if(referenciaFirebase == null){

            referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        }else{

            Log.i("ERROR","OCORREU UM ERRO NO METODO REFERENCIA GET FIREBASE"+referenciaFirebase);
        }

        return referenciaFirebase;
    }


    /**
     * RETORNA A REFENCIA DO FIREBASE AUTH PRA FAZER AUTENTICACAO
     */
    public static FirebaseAuth metodoAutenticacaoFirebase(){

        if(referenciaAutenticacao == null){

            referenciaAutenticacao = FirebaseAuth.getInstance();
        }else{

            Log.i("ERROR","OCORREU UM ERRO NO METODO REFERENCIA AUTENTICAO"+referenciaAutenticacao);
        }

        return referenciaAutenticacao;

    }


    /**
     * RETORNA A REFERENCIA DO STORAGE QUE E PRA TU SALVAR FOTO NO FIREBASE
     */
    public static StorageReference metodoReferenciaFoto(){

        if(referenceStorage == null){
            referenceStorage = FirebaseStorage.getInstance().getReference();
        }else{

            Log.i("ERROR","OCORREU UM ERRO NO METODO REFERENCIA FOTO "+referenceStorage);
        }

        return referenceStorage;

    }





}
