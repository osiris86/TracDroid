/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Database;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import de.suwes.TracDroid.Database.Models.Filterfield;

/**
 * Klasse zum Zugriff auf die News-Tabelle
 * @author Osiris
 */
public class FilterfieldsProvider
{
    public static Uri getContentURI()
    {
        String str = "content://de.suwes.TracDroid.Database/filterfields";
        return Uri.parse(str);
    }
    
    /**
     * Verfügbare Spalten
     */
    public static String[] COLUMNS = new String[] {
        "id", 
        "filterid", 
        "field", 
        "operator",
        "value"
    };
    
    /**
     * Methode zur Ausführung eines Querys auf die News-Tabelle
     * @param database
     * @param context
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return 
     */
    public static Cursor query(DatabaseHelper database, Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) 
    {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables("filterfields");

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                        selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(context.getContentResolver(), uri);

        return cursor;
    }

    public static String getType(Uri uri) {
        return null;
    }

    /**
     * Methode zum Einfügen eines Datensatzes in die News-Tabelle
     * @param database
     * @param context
     * @param uri
     * @param values
     * @return 
     */
    public static Uri insert(DatabaseHelper database, Context context, Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = sqlDB.insert("filterfields", null, values);
                
        context.getContentResolver().notifyChange(uri, null);
        return Uri.parse("filterfields/" + id);
    }

    /**
     * Methode zum Löschen eines Datensatzes in der News-Tabelle
     * @param database
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return 
     */
    public static int delete(DatabaseHelper database, Context context, Uri uri, String selection, String[] selectionArgs) 
    {
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = sqlDB.delete("filterfields", selection, selectionArgs);
                
        context.getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Methode zum Updaten eines Datensatzes in der News-Tabelle
     * @param database
     * @param context
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return 
     */
    public static int update(DatabaseHelper database, Context context, Uri uri, ContentValues values, String selection, String[] selectionArgs) 
    {
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = sqlDB.update("filterfields", values, selection, selectionArgs);
                
        context.getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
    
    /**
     * Methode zum prüfen, ob die übergebenen Spalten alle in der Tabelle vorkommen
     * @param projection 
     */
    private static void checkColumns(String[] projection) {
        
        if (projection != null) 
        {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(FilterfieldsProvider.COLUMNS));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) 
            {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
    
    
    
    /**
     * Konvertiert das Objekt in ContentValues
     * @param f
     * @return 
     */
    public static ContentValues getContentValues(Filterfield f)
    {
        ContentValues values = new ContentValues();
        values.put("id", f.getID());
        values.put("filterid", f.getFilterID());
        values.put("field", f.getField());
        values.put("operator", f.getOperator());
        values.put("value", f.getValue());
        return values;
    }
    
    /**
     * Konvertiert einen Cursor in eine Liste
     * @param c Der Cursor der konvertiert werden soll
     * @return Die fertige Liste
     * @throws ParseException Falls die Datumskonvertierung schief geht
     */
    public static List<Filterfield> getListFromCursor(Cursor c) throws ParseException
    {
    	List<Filterfield> ret = new ArrayList<Filterfield>();

        c.moveToFirst();
        while (!c.isAfterLast())
        {
            ret.add(new Filterfield(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4)));
            c.moveToNext();
        }
        c.close();
        
        return ret;
    }
}
