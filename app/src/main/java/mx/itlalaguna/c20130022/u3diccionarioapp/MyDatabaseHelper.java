package mx.itlalaguna.c20130022.u3diccionarioapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "Diccionario.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME= "my_concepts";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "concept_title";
    private static final String COLUMN_DESCRIPTION = "concept_descritpion";
    private static final String COLUMN_PATH = "concept_path";



    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_DESCRIPTION + " TEXT, " +
                        COLUMN_PATH + " TEXT);";
        db.execSQL(query);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
        onCreate(db);

    }
    public void addConcept(String titulo, String descripcion, String path){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(COLUMN_TITLE, titulo);
        cv.put(COLUMN_DESCRIPTION, descripcion);
        cv.put(COLUMN_PATH, path);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1)
        {
            Toast.makeText(context, "Fallo la insercion",Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(context.getApplicationContext(),"Añadido con exito",Toast.LENGTH_SHORT).show();
        }
    }

    //AQUI QUIERO ORDENAR
    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TITLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,null);

        }
        return cursor;

    }
    void updateData(String row_id, String titulo, String descripcion, String path){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE,titulo);
        cv.put(COLUMN_DESCRIPTION,descripcion);
        cv.put(COLUMN_PATH,path);
        long result = db.update(TABLE_NAME, cv,"_id=?",new String[]{row_id});
        if(result == -1){
            Toast.makeText(context,"Fallo la actualizacion",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,"Exito",Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME,"_id=?",new String[]{row_id});
        if(result == -1){
            Toast.makeText(context,"No se pudo borrar",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,"Exito",Toast.LENGTH_SHORT).show();
        }
    }

    String getId(String titulo){

        String query = "SELECT " +COLUMN_ID+" FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + " = '" + titulo + "'";
        SQLiteDatabase db = this.getReadableDatabase();


        String result = null;

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,null);

            if (cursor.moveToFirst()) {
                result = cursor.getString(0);
            }
        }

        return result;
    }

}
