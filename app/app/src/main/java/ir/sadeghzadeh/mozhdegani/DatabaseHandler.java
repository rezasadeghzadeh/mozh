package ir.sadeghzadeh.mozhdegani;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ir.sadeghzadeh.mozhdegani.entity.City;
import ir.sadeghzadeh.mozhdegani.entity.Province;

/**
 * Created by reza on 11/11/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static String DB_PATH ;

    // Database Name
    private static final String DATABASE_NAME = "mozhdegani";

    // Contacts table name
    private static final String TABLE_CITY = "city";

    // Contacts Table Columns names
    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String PROVINCE_ID = "province_id";
    public SQLiteDatabase sqLiteDatabase;
    Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DB_PATH = "/data/data/"+  context.getApplicationInfo().packageName  +"/databases/";

        this.context  = context;
    }


    public void createDatabase() {
        boolean  dbExist  = checkDataBase();
        if(!dbExist){
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }
    }

    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    @Override
    public synchronized void close() {

        if(sqLiteDatabase != null)
            sqLiteDatabase.close();
        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<City> getCities(String provinceId){
        List<City> cities = new ArrayList<>();
        String  query  =  "select  *  from city where  province_id="+ provinceId;
        SQLiteDatabase db  = this.getWritableDatabase();
        Cursor cursor  = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.id  = cursor.getString(0);
                city.name  = cursor.getString(1) ;
                city.provinceId  = cursor.getString(2);
                cities.add(city);
            }while (cursor.moveToNext());
        }
        return cities;
    }

    public List<Province> getProvinces() {
        List<Province> provinces = new ArrayList<>();
        String  query  =  "select  *  from province";
        SQLiteDatabase db  = this.getWritableDatabase();
        Cursor cursor  = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.name  =  cursor.getString(0);
                province.id  = cursor.getString(1) ;
                provinces.add(province);
            }while (cursor.moveToNext());
        }
        return provinces;
    }
}
