package mx.itlalaguna.c20130022.u3diccionarioapp;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

//import util.permisos.ChecadorDePermisos;
//import util.permisos.PermisoApp;
import teclag.c19130952.androlib.util.permisos.PermisoApp;
import teclag.c19130952.androlib.util.permisos.ChecadorDePermisos;


public class MainActivity extends AppCompatActivity {
    //------------------------------------------------------------------------------------------
    TextView txtvTituloMain;
    RelativeLayout relativeLayout;
    ArrayList<String> seleccionados = new ArrayList<String>();

    private static final int   CODIGO_ARCHIVO_SELECCIONADO = 1;
    Button btnEliminarNotas;
    private ListView lstvNotas;

    private List<String> lstTitulos = new ArrayList<>();
    private List<String> lstContenidos = new ArrayList<>();
    private List<String> lstFiltrado = new ArrayList<>();
    private List<String> lstPaths = new ArrayList<>();
    private List<String> lstId = new ArrayList<>();
    MediaPlayer mediaPlayer;

    MiAdaptador adaptador;
    ArrayAdapter<String> arrayAdapter;
    private final int[] imagen = {R.drawable.nota,
           // R.drawable.nota
        // R.drawable.vozz
           };
    //private int imagen2 = R.drawable.vozz;

    // private List<Integer> lstImagen = new ArrayList();
    //esto es lo que cree para la base de datos
    MyDatabaseHelper mydb;
   // ArrayList<String> concepto_id, concepto_titulo, concepto_path;


    private ActivityResultLauncher<Intent> activityResultLauncher;

    private PermisoApp[] permisosReq = {
            new PermisoApp(android.Manifest.permission.RECORD_AUDIO, "Audio", true),
            new PermisoApp(WRITE_EXTERNAL_STORAGE, "Almacenamiento", true),
            new PermisoApp(READ_EXTERNAL_STORAGE, "Acceder a tus archivos", true)
    };
    //--------------------------------------------------------------------------------------------

    final static int REQUEST_CODE = 1024;

    final static String MESSAGE = "Mensaje";
    final static String MESSAGE_CONTENT = "Mensaje";

     static boolean estado = false;

    private Button btnAgregar;

    int indiceLst;
    FloatingActionButton fab_Principal, fab_Agregar, fab_AcercaDe;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    private SharedPreferences pref1;
    private final  static  int LOCATION_REQUEST_CODE = 23;

    //----------------------------------------------------------------------------------------------

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        if (checkPermission()) {
            Toast.makeText(MainActivity.this,"WE Have Permission",Toast.LENGTH_SHORT).show();   // WE have a permission just start your work.
        } else {
            requestPermission(); // Request Permission
        }
        //Add these line of code in onCreate Method
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult( ActivityResult result ) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager())
                        Toast.makeText(MainActivity.this,"We Have Permission",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(MainActivity.this, "You Denied the permission", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "You Denied the permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ChecadorDePermisos.checarPermisos(this, permisosReq);

        btnEliminarNotas = (Button) findViewById(R.id.eliminarNotaSeleccionada);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, lstTitulos);

        lstvNotas = findViewById(R.id.lstvNotas);

        //OrdenarArraylist(lstTitulos);
        //lstvNotas2 = findViewById(R.id.lstvNotas2);
        txtvTituloMain = findViewById(R.id.txtvTituloMain);
        relativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout);
        //btnAgregar = findViewById( R.id.btnAgregar );

        //leerSharedPreferences();

        //ficheros de para los arraylist


        //Conceptos de ejemplo
        /*

        lstTitulos.add("Flutter");
        lstContenidos.add("Flutter: es un SDK desarrollado por Google para crear aplicaciones móviles " +
                "tanto para Android como para iOS (Apple). Fue desarrollado como un software para uso " +
                "interno dentro de la compañía pero vieron el potencial que tenia y decidieron lanzarlo " +
                "como proyecto de código libre. Actualmente es uno de los proyectos de desarrollo de " +
                "aplicaciones móviles que más está creciendo.");
        lstPaths.add("/storage/emulated/0/DCIM/Flutter.3gp");
        //lstImagen.add(imagen2);

        lstTitulos.add("Katalon");
        lstContenidos.add("Katalon: es una herramienta de software de prueba de automatización " +
                "desarrollada por Katalon, Inc. El software se basa en los marcos de automatización de " +
                "código abierto Selenium, Appium con una interfaz IDE especializada para pruebas de " +
                "aplicaciones web, API, móviles y de escritorio.");
        lstPaths.add("/storage/emulated/0/DCIM/Katalon.3gp");
        //lstImagen.add(imagen2);


        lstTitulos.add("NFC");
        lstContenidos.add("NFC: sigue siendo un gran desconocido para una gran parte de la población. " +
                "Los usuarios menos avanzados desconocen todas las posibilidades que ofrece. " +
                "Es más, muchas personas desconocen si su móvil cuenta con esta tecnología de " +
                "comunicación inalámbrica. Si desconoces qué es el NFC, o quieres saber cómo sacarle " +
                "todo el partido, a continuación vamos a explicarte todo sobre esta tecnología que lleva " +
                "ya más de 10 años conquistando nuestros móviles.");
        lstPaths.add("/storage/emulated/0/DCIM/NFC.3gp");
       // lstImagen.add(imagen2);

        lstTitulos.add("GUID");
        lstContenidos.add("GUID: Un identificador único global (GUID) es un número de 128 bits creado por " +
                "el sistema operativo Windows u otra aplicación de Windows para identificar de forma " +
                "exclusiva componentes, hardware, software, archivos, cuentas de usuario, entradas de" +
                " bases de datos y otros elementos específicos. Los GUID son parte del estándar de ID " +
                "único universal (UUID) que se usa en aplicaciones Windows");
        lstPaths.add("/storage/emulated/0/DCIM/GUID.3gp");
       // lstImagen.add(imagen2);
        */

        lstFiltrado.addAll(lstTitulos);
        //adaptador = new MiAdaptador(this,lstTitulos,lstContenidos,imagen);
        //lstvNotas.setAdapter(adaptador);

        //Encontramos el id de los botones flotantes
        fab_Principal = (FloatingActionButton) findViewById(R.id.fab_Principal);
        fab_Agregar = (FloatingActionButton) findViewById(R.id.fab_agregar);
        fab_AcercaDe = (FloatingActionButton) findViewById(R.id.fab_AcercaDe);

        //Animaciones
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fab_Principal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatefab();
            }
        });
        //Boton flotante para ir al Activity editarNota y agregar un nuevo concepto
        fab_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatefab();
                //Toast.makeText(MainActivity.this,"Agregar click",Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(getApplicationContext(), EditarNotaActivity.class), REQUEST_CODE);
            }
        });

        //Boton flotante para desplegar el alertdialog con nuestro acerca de
        fab_AcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatefab();
                //Toast.makeText(MainActivity.this,"Acerca de click",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("DiccionarioApp" + " V1.0\n"
                                + "Creado por: \n"
                                + "Omar Adrian Tapia Guzman\n"
                                + "Cesar Alexis Ochoa Tapia\n"
                                + "Jorge Antonio Reyes Muñoz\n"
                                + "Ene-Jun/2023")
                        .setTitle("Acerca de")
                        .setIcon(R.drawable.nota)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No hace nada se cierra el alert
                            }
                        })
                        .create()
                        .show();
            }
        });


        // Establecemos el Adapter para el ListView, el adapter será un objeto MiAdaptador
        // MiAdaptador adaptador = new MiAdaptador ( this, lstTitulos, lstContenidos, imagen );
        adaptador = new MiAdaptador(this, lstTitulos, lstContenidos, imagen, lstPaths);
        lstvNotas.setAdapter(adaptador);

        // Establecemos un listener para el evento onItemClick del ListView
        lstvNotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String titulo = arrayAdapter.getItem(i);
                int j = lstTitulos.indexOf(titulo);

                AlertDialog2(lstPaths.get(i).toString(), j);

            }
        });

        //Presionar un elemento de la lista para mostrar en un AlertDialog las opciones que podemos realizar
        lstvNotas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Toast.makeText(getApplicationContext(),"SE selecciono" + i,Toast.LENGTH_SHORT).show();
                String titulo = arrayAdapter.getItem(i);
                int j = lstTitulos.indexOf(titulo);
                AlertDialog(lstTitulos.get(j).toString(), j);

                return true;
            }
        });
        //HAGO LO DE LA BASE DE DATOS Y ANADO LO DEL METODO STOREDATA
        mydb = new MyDatabaseHelper(this);
        storeDataInArray();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{READ_EXTERNAL_STORAGE},
                    LOCATION_REQUEST_CODE);
        }
    }
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }
    private String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission")
                    .setMessage("Please give the Storage permission")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick( DialogInterface dialog, int which ) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                                activityResultLauncher.launch(intent);
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                activityResultLauncher.launch(intent);
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this, permissions, 30);
        }
    }


    //METODO DE LA BASE DE DATOS PARA TRAER EL CONTENIDO
     public void storeDataInArray(){

        lstId.clear();
         lstTitulos.clear();
         lstContenidos.clear();
         lstPaths.clear();
        Cursor cursor = mydb.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this,"No datos",Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()){
                lstId.add(cursor.getString(0));
                lstTitulos.add(cursor.getString(1));
                lstContenidos.add(cursor.getString(2));
                lstPaths.add(cursor.getString(3));

            }
        }
    }

    public void AlertDialog(String titulo, int posicion) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Opciones").setMessage("Que deseas hacer con la nota: " + titulo)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {



                        String ruta = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                                + "DCIM" + File.separator;
                        String []posTit = lstTitulos.get(posicion).split( " 🎤",2);
                        File archivo = new File(ruta, posTit[0] + ".3gp");

                        if(archivo.exists())
                        {
                            archivo.delete();

                        }

                        String id;
                        id = mydb.getId(lstTitulos.get(posicion));
                        mydb.deleteOneRow(id);

                        storeDataInArray();

                        adaptador = new MiAdaptador(getApplicationContext(), lstTitulos, lstContenidos, imagen, lstPaths);
                        arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, lstTitulos);
                        lstvNotas.setAdapter(adaptador);
                        adaptador.notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        indiceLst = posicion;

                        Intent intent = new Intent(MainActivity.this, EditarNotaActivity.class);
                        // Pasamos como argumento el valor del campo Usuario
                        ArrayList list = new ArrayList<>();

                        String id;
                        id = lstId.get(posicion);
                        String dato;
                        dato = lstContenidos.get(posicion);
                        String titulo;
                        titulo = lstTitulos.get(posicion);
                        String path;
                        path = lstPaths.get(posicion);



                        list.add(id);
                        list.add(titulo);
                        list.add(dato);
                        list.add(path);

                        intent.putStringArrayListExtra("valor", list);

                        startActivityForResult(intent, REQUEST_CODE);

                        dialogInterface.dismiss();
                    }
                }).show();

    }

    public void AlertDialog2(String path, int posicion){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.nota2).setTitle(lstTitulos.get(posicion)).setMessage(lstContenidos.get(posicion))
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Reproducir", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        File audio = new File(lstPaths.get(posicion).toString());
                        //FileOutputStream archivo;

                        if(audio.exists()){



                       // if(l)
                        //Codigo para reproducir el audio
                        mediaPlayer = new MediaPlayer();
                        try{

                            mediaPlayer.setDataSource(lstPaths.get(posicion));
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {

                                    mediaPlayer.start();

                                }
                            });
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {

                                    mediaPlayer.release();

                                }
                            });
                            mediaPlayer.prepare();
                        }catch (IOException ex){

                            Toast.makeText(getApplicationContext(), "Fallo al reproducir el audio", Toast.LENGTH_SHORT).show();
                        }
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "Reproduciendo el audio", Toast.LENGTH_SHORT).show();
                    }else{
                            Toast.makeText(getApplicationContext(), "No existe audio para esta nota", Toast.LENGTH_SHORT).show();


                        }
                    }

                })
                .create().show();

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                storeDataInArray();
                adaptador = new MiAdaptador(getApplicationContext(), lstTitulos, lstContenidos, imagen, lstPaths);
                //PUSE AQUI EL ADAPTADOR PARA QUE SE REFLEJE LOS CAMBIOS EN EN SEGUNDO ARRAY
                lstvNotas.setAdapter(adaptador);
                //OrdenarArraylist(lstTitulos);
            } else if (resultCode == RESULT_CANCELED) {

            }

            else if (resultCode == 1) {
                storeDataInArray();
                adaptador = new MiAdaptador(MainActivity.this, lstTitulos, lstContenidos, imagen, lstPaths);
                arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, lstTitulos);
                lstvNotas.setAdapter(adaptador);
                //OrdenarArraylist(lstTitulos);
                List<String> listNew = organizedAlphabeticList(lstTitulos);
            }
        } else if (requestCode == CODIGO_ARCHIVO_SELECCIONADO && resultCode == RESULT_OK) {


            Uri fileUri = data.getData();
            File file = new File(fileUri.getPath());
            int errores=0;


            try {
                //Añadi esto del inputstream reader porwue si colocacab el file directamente de daba un error
                BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(fileUri)));
                String line = null;

                while ((line = reader.readLine()) != null) {

                    //ESTO ES LO ORIGINAL
                    /*
                    String[] contenido = line.trim().split("\\|",3);
                    if (contenido.length >= 3) {
                        String listaTit = contenido[0];
                        String listaCont = contenido[1];
                        String listaPath = contenido[2];

                     */
                    String[] contenido = line.trim().split("\\|",-1);//-1 indica que haga todas la separaciones
                    if (contenido.length == 3) {
                        String listaTit = contenido[0];
                        String listaCont = contenido[1];
                        String listaPath = contenido[2];
                        mydb.addConcept(listaTit,listaCont,listaPath);
                        storeDataInArray();

                    }
                    else if (contenido.length == 4){
                        String listaTit = contenido[0];
                        listaTit = listaTit.toUpperCase();
                        String listaCont = listaTit + ": "+contenido[2];
                        String listaPath = contenido[3];
                        mydb.addConcept(listaTit,listaCont,listaPath);
                        storeDataInArray();
                    }
                    else if (contenido.length == 5){
                        String listaTit = contenido[0];
                        String listaCont = listaTit + ": "+contenido[2];
                        String listaPath = contenido[3];
                       /* if(listaPath == null){

                        }

                        */

                        mydb.addConcept(listaTit,listaCont,listaPath);
                        storeDataInArray();
                    }
                    else
                      //  Toast.makeText(getApplicationContext(),"Txt no valido",Toast.LENGTH_SHORT).show();
                    errores++;

                }

                reader.close();
                adaptador = new MiAdaptador(MainActivity.this, lstTitulos, lstContenidos, imagen, lstPaths);
                arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, lstTitulos);

                if(errores == 0) {
                    Toast.makeText(this, "Importado con exito", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Parcialmente, "+errores+" no importados",Toast.LENGTH_SHORT).show();
                }
                lstvNotas.setAdapter(adaptador);
            } catch (IOException e) {
                //e.printStackTrace();
                Toast.makeText(this,"Error no se a podido abrir", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view_menu, menu);

        //MenuInflater inflater2 = getMenuInflater();
        //inflater2.inflate(R.menu.menu_eliminar, menu);

        MenuItem buscador = menu.findItem(R.id.accion_buscar);
        //AQUI INFLAMOS NUESTRO MENU DE IMPORTAR ELIMINAR ETC
        getMenuInflater().inflate(R.menu.menu_eliminar, menu);
        //MenuItem eliminar = menu.findItem(R.id.btn_eliminar);
       // importar = menu.findItem(R.id.btn_importar);
        //exportar = menu.findItem(R.id.btn_exportar);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(buscador);
        //Button botonEliminar = (Button) MenuItemCompat.getActionView(eliminar) ;


        //arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, lstTitulos);
        //adaptador = new MiAdaptador(this,lstTitulos,lstContenidos,imagen);
        //lstvNotas.setAdapter(adaptador);
        //getMenuInflater().inflate(R.menu.search_view_menu, menu);





        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            //EN ESTE METODO SUCEDE TODA LA MAGIA DEL SEARCHVIEW PARA QUE FUNCIONE
            @Override
            public boolean onQueryTextChange(String newText) {
                //MiAdaptador adaptader = new MiAdaptador(MainActivity.this,lstTitulos,lstContenidos,imagen);

                //lstFiltrado.addAll(lstTitulos);
                arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, lstTitulos);
                MainActivity.this.arrayAdapter.getFilter().filter(newText);
                lstvNotas.setAdapter(arrayAdapter);

                return true;

            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                lstvNotas.setAdapter(adaptador);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    //EN ESTE METODO CREO LAS ACCIONES PARA CADA COMPONENTE DEL MENU


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.btn_importar){
            //CODIGO PARA ABRIR UN ARCHIVO
  //------------------------------------------------------------------------------------------------
            //Creamos el intent para abrir el explorador de archivos.
            Intent intent = new Intent ( Intent.ACTION_GET_CONTENT );
            //Agregamos el filtro para para que tome aplicaciones que puedan abrir todos los archivos.
            intent.setType             ( "text/plain");
            //Agregamos la categoría de la aplicación, default.
            intent.addCategory         ( Intent.CATEGORY_DEFAULT );

            //Tratamos de abrir el explorador de archivos. Si no se puede, lanzamos un toast con la excepción.
            try {
                startActivityForResult ( Intent.createChooser ( intent, "Seleccione una opción." ),
                        CODIGO_ARCHIVO_SELECCIONADO );
            } catch ( ActivityNotFoundException e ) {
                Toast.makeText (
                                this,
                                "Explorador de archivos no encontrado.\nPor favor instale un exploador de archivos.",
                                Toast.LENGTH_LONG )
                        .show ();
            }
            //------------------------------------------------------------------------------------------------

        } else if (id == R.id.btn_exportar) {

            final EditText nombre = new EditText(this);//creamos el edit text para que ingrese el nombre de la nota
            nombre.setInputType(InputType.TYPE_CLASS_TEXT);//le decimos el tipo de input

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nombre del .txt").setView(nombre).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //CODIGO PARA EXPORTAR LAS NOTAS
                    String FILENAME = nombre.getText().toString();

                        if ( isAlmExtEscribible () ) {
                            // Obtener la ruta raiz del almacenamiento externo
                            File rutaExt = Environment.getExternalStorageDirectory();

                            // Formamos la ruta donde guardaremos el archivo, en este caso en la carpeta DCIM
                            String rutaArchivo = rutaExt.getAbsolutePath() + "/DCIM";

                            // Asociamos un File para el archivo en la ruta anterior
                            //File archivo = new File(rutaArchivo, "MiArchivo.txt");
                            File archivo = new File(rutaArchivo, FILENAME+".txt");
                            // Creamos, escribimos y cerramos el archivo
                            try {

                                BufferedWriter br = new BufferedWriter(
                                        new FileWriter(archivo));
                                for (int j = 0; j < lstTitulos.size(); j++) {
                                    br.write(lstTitulos.get(j) + "|");

                                    br.write(lstContenidos.get(j) + "|");

                                    br.write(lstPaths.get(j));//AGREGE LOS PATHS
                                    br.newLine();


                                }
                                br.close();
                                Toast.makeText ( getApplicationContext(), "Texto ha sido guardado", Toast.LENGTH_LONG ).show();
                            } catch (IOException ex) {
                                Toast.makeText ( getApplicationContext(), "ERROR: " + ex, Toast.LENGTH_LONG ).show();
                            }
                        } else
                            Toast.makeText ( getApplicationContext(), "El almacenamiento externo no esta MONTADO", Toast.LENGTH_SHORT ).show();


                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                     Toast.makeText(MainActivity.this,"Cancelado",Toast.LENGTH_SHORT).show();
                }
            }).create().show();

        } else if (id == R.id.btn_eliminar) {
            lstvNotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            });
            adaptador = new MiAdaptador(MainActivity.this, lstTitulos, lstContenidos, imagen, lstPaths);
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, lstTitulos);
            lstvNotas.setAdapter(arrayAdapter);
            lstvNotas.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            btnEliminarNotas.setVisibility(View.VISIBLE);


            btnEliminarNotas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
                    dialogo.setTitle("Aviso")
                            .setIcon(null)
                            .setMessage("Esta seguro que desea eliminar las notas seleccionadas?")
                            .setCancelable(false)
                            .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    SparseBooleanArray elementosMarcados = lstvNotas.getCheckedItemPositions();
                                    for(int j = 0; j < lstTitulos.size()-1; j++){
                                        //for(int j = lstTitulos.size()-1; j > 0; j--){
                                        for(int k = elementosMarcados.size()-1; k > 0; k--) {
                                            int llave = elementosMarcados.keyAt(j);
                                            int posicion = k;

                                            boolean valor = elementosMarcados.get(llave);
                                            if (valor) {
                                                lstTitulos.remove(lstvNotas.getItemAtPosition(llave).toString());
                                                lstContenidos.remove(lstvNotas.getItemAtPosition(llave).toString());
                                                lstPaths.remove(lstvNotas.getItemAtPosition(llave).toString());
                                                //seleccionados.clear();
                                            }
                                        }
                                        //lstTitulos.remove()
                                    }
                                    //seleccionados = new ArrayList<String>();
                                    //SparseBooleanArray elementosMarcados = lstvNotas.getCheckedItemPositions();

                                    adaptador = new MiAdaptador(getApplicationContext(), lstTitulos, lstContenidos, imagen, lstPaths);
                                    arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, lstTitulos);
                                    lstvNotas.setAdapter(adaptador);
                                    adaptador.notifyDataSetChanged();
                                    dialogInterface.dismiss();

                                    //Intent intent = new Intent(ConstruyeAgenda.this, Vincular.class);
                                    //startActivity(intent);
                                }
                            }).create().show();
                }
            });
            Toast.makeText(this,"Este boton aun no tiene funcionalidad",Toast.LENGTH_SHORT).show();

        }


        return super.onOptionsItemSelected(item);
    }
    public boolean isAlmExtEscribible () {
        String estado = Environment.getExternalStorageState ();
        if ( estado.equals ( Environment.MEDIA_MOUNTED ) ) {
            return true;
        }
        return false;
    }

    private void animatefab() {
        if (isOpen) {
            fab_Principal.startAnimation(rotateForward);
            fab_Agregar.startAnimation(fabClose);
            fab_AcercaDe.startAnimation(fabClose);
            fab_Agregar.setClickable(false);
            fab_AcercaDe.setClickable(false);
            isOpen = false;
        } else {
            fab_Principal.startAnimation(rotateBackward);
            fab_Agregar.startAnimation(fabOpen);
            fab_AcercaDe.startAnimation(fabOpen);
            fab_Agregar.setClickable(true);
            fab_AcercaDe.setClickable(true);
            isOpen = true;
        }
    }






    public static List<String> organizedAlphabeticList(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            Collator collator = Collator.getInstance();

            public int compare(String o1, String o2) {
                return collator.compare(o1, o2);
            }
        });
        return list;
    }


    //----------------------------------------------------------------------------------------------
    class MiAdaptador extends ArrayAdapter {
        private Context context;
        private List<String> titulos;
        private List<String> contenidos;
        private int[] imagen;
        private List<String> paths;


        public MiAdaptador(Context c, List<String> titulos, List<String> contenidos, int[] imagen, List<String>paths) {
            super(c, R.layout.list_fila_texto_imagen, R.id.txtvTitulo, titulos);
            context = c;
            this.titulos = titulos;
            this.contenidos = contenidos;
            this.imagen = imagen;
            this.paths = paths;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.list_fila_texto_imagen, parent, false);

            }
            ImageView imagen = convertView.findViewById(R.id.imgvLogo);
            TextView titulo = convertView.findViewById(R.id.txtvTitulo);
            TextView contenido = convertView.findViewById(R.id.txtvContenido);


            imagen.setImageResource(this.imagen[0]);
            titulo.setText(titulos.get(position));
            contenido.setText(contenidos.get(position));



            return convertView;
        }

    }






}

