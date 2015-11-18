package com.sqlite.cric_grap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by userws49 on 11/11/2015.
 */
public class SQLiteManagement {
    private Context context;
    DBHelper dbHelper = null;
    SQLiteDatabase sqLiteDatabase = null;

    public SQLiteManagement(Context context) {
        this.context = context;

    }

    public void open() {
        dbHelper = new DBHelper(context);
        try {
            sqLiteDatabase = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {

        try {
            if (dbHelper != null) {
                dbHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int registration(String userName, String email_ID, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME, userName);
        contentValues.put(DBHelper.EMAIL_ID, email_ID);
        contentValues.put(DBHelper.PASSWORD, password);

        long result = sqLiteDatabase.insert(DBHelper.TABLE_NAME, null, contentValues);
        if (result > 0) {
            return 1;
        } else {
            return 0;
        }

    }

    public void userLoginCheck(String email, String password) {
        String query = ("SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE " + DBHelper.EMAIL_ID + "=" + email + " AND " + DBHelper.PASSWORD + "=" + password);
        Cursor cursor=sqLiteDatabase.rawQuery(query, null);
        while(cursor.moveToNext()){
            Log.d("Data from database",cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
        }
    }

    public void delete() {
        try {

            sqLiteDatabase.delete(DBHelper.TABLE_NAME, null, null);
            System.out.println("DeleteTable Gets Called");
        } catch (Exception exception) {
            System.out.println("DeleteTable one Gets Exception");
        }

    }

    private static class DBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "CRIC_DATABASE";
        private static final int DATABASE_VERSION = 1;
        private static final String TABLE_NAME = "CRIC_LOGIN";
        private static final String NAME = "name";
        private static final String PASSWORD = "password";
        private static final String EMAIL_ID = "email_ID";
        private Context context;
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + NAME + " VARCHAR2(255)," + PASSWORD + " VARCHAR2(255)," + EMAIL_ID + " VARCHAR2(255))";
        private static final String Drop_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
                System.out.println("Table Created");

            } catch (SQLException e
                    ) {
                e.printStackTrace();
                System.out.println("Table Creation is failed");

            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(Drop_TABLE);
                onCreate(db);
                System.out.println("Table UpGraded");

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Table UpGraded is failed");

            }

        }
    }

}
