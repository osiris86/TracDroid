/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.suwes.TracDroid.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Klasse zum Deligieren der Datenbankaufrufe an die einzelnen Provider-Klassen
 * @author Osiris
 */
public class DataProvider extends ContentProvider 
{
    private DatabaseHelper database;
    
    protected static final UriMatcher m_UriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    protected static final int FILTER_TABLE = 1;
    protected static final int FILTERFIELDS_TABLE = 2;
    protected static final int INSTANCE_TABLE = 3;
    

    @Override
    public boolean onCreate() {
        initMatcher();
        database = new DatabaseHelper(getContext());
        return false;
    }
    
    protected void initMatcher() {
        String str = "de.suwes.TracDroid.Database";
        m_UriMatcher.addURI(str, "filter", FILTER_TABLE);
        m_UriMatcher.addURI(str, "filterfields", FILTERFIELDS_TABLE);
        m_UriMatcher.addURI(str, "instance", INSTANCE_TABLE);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (m_UriMatcher.match(uri)) {
            case FILTER_TABLE:
                return FilterProvider.query(database, getContext(), uri, projection, selection, selectionArgs, sortOrder);
            case FILTERFIELDS_TABLE:
                return FilterfieldsProvider.query(database, getContext(), uri, projection, selection, selectionArgs, sortOrder);     
            case INSTANCE_TABLE:
                return InstanceProvider.query(database, getContext(), uri, projection, selection, selectionArgs, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
    }

    @Override
    public String getType(Uri uri) {
        switch (m_UriMatcher.match(uri)) {
            case FILTER_TABLE:
                return FilterProvider.getType(uri);
            case FILTERFIELDS_TABLE:
                return FilterfieldsProvider.getType(uri);
            case INSTANCE_TABLE:
                return InstanceProvider.getType(uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues cv) {
        switch (m_UriMatcher.match(uri)) {
            case FILTER_TABLE:
                return FilterProvider.insert(database, getContext(), uri, cv);
            case FILTERFIELDS_TABLE:
                return FilterfieldsProvider.insert(database, getContext(), uri, cv);
            case INSTANCE_TABLE:
                return InstanceProvider.insert(database, getContext(), uri, cv);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (m_UriMatcher.match(uri)) {
            case FILTER_TABLE:
                return FilterProvider.delete(database, getContext(), uri, selection, selectionArgs);
            case FILTERFIELDS_TABLE:
                return FilterfieldsProvider.delete(database, getContext(), uri, selection, selectionArgs);
            case INSTANCE_TABLE:
                return InstanceProvider.delete(database, getContext(), uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (m_UriMatcher.match(uri)) {
            case FILTER_TABLE:
                return FilterProvider.update(database, getContext(), uri, values, selection, selectionArgs);
            case FILTERFIELDS_TABLE:
                return FilterfieldsProvider.update(database, getContext(), uri, values, selection, selectionArgs);
            case INSTANCE_TABLE:
                return InstanceProvider.update(database, getContext(), uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
    
}
