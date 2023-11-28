package mx.itlalaguna.c20130022.u3diccionarioapp;

import static mx.itlalaguna.c20130022.u3diccionarioapp.MainActivity.MESSAGE;
import static mx.itlalaguna.c20130022.u3diccionarioapp.MainActivity.MESSAGE_CONTENT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import util.permisos.ChecadorDePermisos;
//import util.permisos.PermisoApp;
import teclag.c19130952.androlib.util.permisos.PermisoApp;
import teclag.c19130952.androlib.util.permisos.ChecadorDePermisos;


public class EditarNotaActivity extends AppCompatActivity {

    SharedPreferences pref1;
    EditText edtxEditarNota;
    EditText edtxTituloNota;
    //Button btnAceptar;
    //int IndicePath = 0;
    ImageButton rec;
    private PermisoApp [] permisosReq = {
            new PermisoApp(android.Manifest.permission.RECORD_AUDIO, "Audio", true),
            new PermisoApp(Manifest.permission.WRITE_EXTERNAL_STORAGE, "Almacenamiento", true)
    };
    private EditText edtNombreAudio;
    private Button  btnGrabar;

    private Button  btnDetener;
    private MediaRecorder mediaRecorder;
    private String fichero;
    private String ruta = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + "DCIM" + File.separator;

    ArrayList <String> Path = new ArrayList<>();

    boolean band = false;
    ArrayList valor = new ArrayList<String>();

    //DECLARO EL OBJETO DE LA BASE DE DATOS


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyDatabaseHelper myDb = new MyDatabaseHelper(this);



        setContentView(R.layout.activity_editar_nota);
        //------------PERMISOS----------------------------------------------
        ChecadorDePermisos.checarPermisos(this, permisosReq);
        //------------------------------------------------------------------

        //edtNombreAudio = (EditText)findViewById(R.id.edtNombreAudio);
        rec = (ImageButton)findViewById(R.id.imageButton2);

        edtxEditarNota = findViewById(R.id.edtxEditarNota);
        edtxTituloNota = findViewById(R.id.edtxTituloNota);

        valor = getIntent().getStringArrayListExtra("valor");
        if(valor!= null) {

            edtxTituloNota.setText(valor.get(1).toString());
            edtxEditarNota.setText(valor.get(2).toString());

            fichero = valor.get(3).toString();

           // edtxEditarNota.setText(valor);
            band = true;
        }

    }



    public void btnAceptarClick(View v)
    {
        if ( band == true ) {
            //--------------------------------------------------------------------------------------
            //EN ESTE PUNTO ANADO EL SQLITE
            MyDatabaseHelper db = new MyDatabaseHelper(this);


            //--------------------------------------------------------------------------------------

            Intent intent = new Intent();

            String tituloNota = edtxTituloNota.getText().toString();
            String contenidoNota = edtxEditarNota.getText().toString();
            String file = fichero;
          //  boolean estado = true;

            if(fichero == null){
                fichero = ruta + edtxTituloNota.getText().toString() + ".3gp";
                file = fichero;
               // estado = false;
            }
            File archivo = new File(ruta, edtxTituloNota.getText().toString() + ".3gp");

            if(archivo.exists() && !tituloNota.contains(" 🎤"))
            {
                tituloNota = edtxTituloNota.getText().toString() + " 🎤";
            }
            db.updateData(valor.get(0).toString(),tituloNota,contenidoNota,file);

            setResult(1, intent);
            this.finish();

        } else {

            //--------------------------------------------------------------------------------------
            //EN ESTE PUNTO ANADO EL SQLITE
            MyDatabaseHelper db = new MyDatabaseHelper(this);




            Intent intent = new Intent();
            String tituloNota = edtxTituloNota.getText().toString();
            String contenidoNota =  tituloNota + ": "+edtxEditarNota.getText().toString();
            String file = fichero;

            if(fichero == null){
                fichero = ruta + edtxTituloNota.getText().toString() + ".3gp";
                file = fichero;

            }


            File archivo = new File(ruta, edtxTituloNota.getText().toString() + ".3gp");

            if(archivo.exists())
            {
                tituloNota = edtxTituloNota.getText().toString() + " 🎤";
            }

            db.addConcept(tituloNota,contenidoNota,file);
            setResult(RESULT_OK, intent);
            this.finish();
        }


    }

    public void Grabar(View view) {

            if (mediaRecorder == null) {

                fichero = ruta + edtxTituloNota.getText().toString() + ".3gp";

                mediaRecorder = new MediaRecorder();
                //Establecer el microfono como fuente de audio
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //Establecer formato de archivo en 3gp
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                //Establecer el codificador de audio
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                //Establecer el archivo de salida de la grabacion
                mediaRecorder.setOutputFile(fichero);

                try {

                    mediaRecorder.prepare();
                    mediaRecorder.start();

                } catch (IOException ex) {

                    //txtvMensajes.setText("");
                    //btnGrabar.setEnabled(true);
                    //btnDetener.setEnabled(false);
                    //btnReproducir.setEnabled(false);

                    Toast.makeText(this, "Fallo en la grabacion", Toast.LENGTH_SHORT).show();
                }

                rec.setBackgroundResource(R.drawable.btnstop);
                Toast.makeText(getApplicationContext(), "Grabando...", Toast.LENGTH_SHORT).show();

            } else if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                rec.setBackgroundResource(R.drawable.botongrabar3);
                Toast.makeText(getApplicationContext(), "Grabacion finalizada!!", Toast.LENGTH_SHORT).show();
            }

            /*
        } else {
            Toast.makeText(this, "Ya hay un archivo de audio para esta nota", Toast.LENGTH_SHORT).show();
        }

             */






    }



}