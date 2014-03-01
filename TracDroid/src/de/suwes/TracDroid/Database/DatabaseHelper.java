 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.suwes.TracDroid.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Klasse zur Erstellung der Datenbank
 * @author Osiris
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private static int DATABASE_VERSION = 6;
    /**
     * Erzeugt ein neues DatabaseHelper Objekt
     * @param context Der Kontext in welchem das AktuellesHelper Objekt erzeugt werden soll
     */
    public DatabaseHelper(Context context)
    {
        super(context, "Database.db", null, DATABASE_VERSION);
    }

    /**
     * Wird beim Erstellen der Datenbank ausgeführt. In der Regel also beim aller ersten Start der Anwendung
     * @param db 
     */
    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        String strCreate = "CREATE TABLE filter (id INTEGER PRIMARY KEY, name TEXT, defaultfilter INTEGER, groupby INTEGER, instance INTEGER);";
        db.execSQL(strCreate);

        strCreate = "CREATE TABLE filterfields (id INTEGER PRIMARY KEY, filterid INTEGER, field TEXT, operator TEXT, value TEXT);";
        db.execSQL(strCreate);
        
        strCreate = "CREATE TABLE instances (id INTEGER PRIMARY KEY, name TEXT, url TEXT, username TEXT, password TEXT, defaultinstance INTEGER, topleft TEXT, topright TEXT, bottomleft TEXT, bottomright TEXT, defaultfilter INTEGER);";
        db.execSQL(strCreate);

        String strInsert = "INSERT INTO filter (id, name, defaultfilter, groupby, instance) values (1, 'Active tickets', 1, 0, 0);";
        db.execSQL(strInsert);

        strInsert = "INSERT INTO filterfields (filterid, field, operator, value) values (1, 'status', '!=', 'closed');";
        db.execSQL(strInsert);
    }

    /**
     * Wird beim aktualisieren der Datenbank ausgeführt.
     * In der Regel also nach einer Aktualisierung der Anwendung.
     * @param db
     * @param oldVersion
     * @param newVersion 
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        if (oldVersion < 2)
        {
            db.execSQL("ALTER TABLE filter ADD defaultfilter INTEGER");
            db.execSQL("UPDATE filter SET defaultfilter = 0");
            db.execSQL("UPDATE filter SET defaultfilter = 1 WHERE id = (SELECT MIN(id) FROM filter)");
        }
        if (oldVersion < 3)
        {
            db.execSQL("ALTER TABLE filter ADD groupby INTEGER");
            db.execSQL("UPDATE filter SET groupby = 0");
        }
        if (oldVersion < 4)
        {
            db.execSQL("CREATE TABLE instances (id INTEGER PRIMARY KEY, name TEXT, url TEXT, username TEXT, password TEXT, defaultinstance INTEGER, topleft TEXT, topright TEXT, bottomleft TEXT, bottomright TEXT);");
        }
        if (oldVersion < 5)
        {
            db.execSQL("ALTER TABLE filter ADD instance INTEGER");
            db.execSQL("UPDATE filter SET instance = 0");
        }
        if (oldVersion < 6) 
        {
        	db.execSQL("ALTER TABLE instances ADD defaultfilter INTEGER");
        	db.execSQL("UPDATE instances SET defaultfilter = (SELECT MIN(id) FROM filter)");
        }
    }
}
