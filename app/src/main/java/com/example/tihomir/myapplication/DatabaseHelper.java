package com.example.tihomir.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Baza";

    public static final String tableName = "objects";
    public static final String ID = "_id";
    public static final String CATEGORY = "Category";
    public static final String NAME = "Name";
    public static final String VALUTE = "Valute";
    public static final String COST = "Cost";
    public static final String DATE = "Date";
    public static final String DESC = "Desc";
    public static final String SPINNER = "Month";


    public static final String tableOfCategory = "categories";
    public static final String IDCATEGORY = "_id";
    public static final String TABLECATEGORY = "Category";
    public static final String SUMOFCOST = "Sum";
    public static final String NUMBEROFBILLS = "Number";


    private static final String TAG = "MyActivity";


    private static final int DATABASE_VERSION = 1;

    private static final String tablename_CREATE =
            " CREATE TABLE " + tableName + "( "
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NAME + " TEXT, "
                    + CATEGORY + " TEXT, "
                    + VALUTE + " TEXT, "
                    + COST + " INTEGER, "
                    + DATE + " TEXT, "
                    + DESC + " TEXT )";


    private static final String tableOfCategory_CREATE =
            "CREATE TABLE " + tableOfCategory + " ( "
                    + IDCATEGORY + " integer PRIMARY KEY AUTOINCREMENT, "
                    + TABLECATEGORY + " TEXT, "
                    + SUMOFCOST + " INTEGER, "
                    + NUMBEROFBILLS + " TEXT )";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(tablename_CREATE);
        db.execSQL(tableOfCategory_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + tableName);
        db.execSQL(" DROP TABLE IF EXISTS " + tableOfCategory);
        onCreate(db);
    }


    //insert data for category table
    public void insertDataForCategory(String category, String sum, String num, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(TABLECATEGORY, category);
        contentValues.put(SUMOFCOST, sum);
        contentValues.put(NUMBEROFBILLS, num);


        db.insert(tableOfCategory, null, contentValues);

    }

    public void updateCategory(String category, String cost, SQLiteDatabase db) {

        db.execSQL("UPDATE " + tableOfCategory + " SET " + NUMBEROFBILLS + " = " + NUMBEROFBILLS + " + 1 "
                        + " WHERE " + CATEGORY + " = ? ",
                new String[]{category});
        db.execSQL("UPDATE " + tableOfCategory + " SET " + SUMOFCOST + " = " + SUMOFCOST + " + " + cost
                        + " WHERE " + CATEGORY + " = ? ",
                new String[]{category});
    }


    public void insertData(String category, String name, String valute, String cost, String date, String desc, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(CATEGORY, category);
        contentValues.put(NAME, name);
        contentValues.put(VALUTE, valute);
        contentValues.put(COST, cost);
        contentValues.put(DATE, date);
        contentValues.put(DESC, desc);



        db.insert(tableName, null, contentValues);

    }

    public void updateSumOfCategoryWhenObjetIsDeleted(SQLiteDatabase db, String category, String cost) {
        db.execSQL("UPDATE " + tableOfCategory + " SET " + NUMBEROFBILLS + " = " + NUMBEROFBILLS + " - 1 "
                        + " WHERE " + CATEGORY + " = ? ",
                new String[]{category});
        db.execSQL("UPDATE " + tableOfCategory + " SET " + SUMOFCOST + " = " + SUMOFCOST + " - " + cost
                        + " WHERE " + CATEGORY + " = ? ",
                new String[]{category});


    }

    public void deleteObject(String id, String category, String cost, SQLiteDatabase db) {

        db.delete(tableName, ID + " = ?", new String[]{id});
        updateSumOfCategoryWhenObjetIsDeleted(db, category, cost);


    }


    public void deleteCategory(String category, SQLiteDatabase db) {

        db.delete(tableOfCategory, TABLECATEGORY + " = ?", new String[]{category});
        deleteObjectOfCategory(category, db);

    }

    public void deleteObjectOfCategory(String category, SQLiteDatabase db) {

        db.delete(tableName, CATEGORY + " = ?", new String[]{category});

    }


    //retrieves data from DB with specific category
    //only can be implemented in API >= 16
    public Cursor getAllFromCategory(String category, SQLiteDatabase db) throws SQLException {

        Cursor mCursor = null;
        String what = CATEGORY + " = ? ";
        String[] select = {category};
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN && category != null) {
            mCursor = db.query(true, tableName, new String[]{
                            "rowid _id",
                            CATEGORY,
                            NAME,
                            VALUTE,
                            COST,
                            DATE,
                            DESC,
                    },
                    what,
                    select,
                    null,
                    null,
                    null,
                    null);
        }

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    public ContentValues getRowWithID(String id, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        Cursor mCursor = null;


        mCursor = db.query(true, tableName, new String[]{
                        "rowid _id",
                        CATEGORY,
                        NAME,
                        VALUTE,
                        COST,
                        DATE,
                        DESC},
                ID + "=?",
                new String[]{id},
                null,
                null,
                null,
                null);
        if (mCursor.moveToNext()) {
            values.put("category", mCursor.getString(1));
            values.put("name", mCursor.getString(2));
            values.put("cost", mCursor.getString(4));
            values.put("date", mCursor.getString(5));
            values.put("desc", mCursor.getString(6));

        }
        mCursor.close();
        return values;
    }


    public Cursor getCategoryInfoForObject(String category, SQLiteDatabase db) throws SQLException {
        Cursor mCursor = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mCursor = db.query(true, tableOfCategory, new String[]{
                            "rowid _id",
                            TABLECATEGORY,
                            SUMOFCOST,
                            NUMBEROFBILLS},
                    TABLECATEGORY + "=?",
                    new String[]{category},
                    null,
                    null,
                    null,
                    null);
        }

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //get category information
    public Cursor getCategoryInfo(SQLiteDatabase db) throws SQLException {
        Cursor mCursor = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            mCursor = db.query(true, tableOfCategory, new String[]{
                            "rowid _id",
                            TABLECATEGORY,
                            SUMOFCOST,
                            NUMBEROFBILLS,
                    },
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        }

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //updating particular data
    public void updateData(String id, String category, String name, String cost, String desc, SQLiteDatabase db, double number) {
        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(name)) {
            values.put(NAME, name);
            db.update(tableName, values, ID + "=?", new String[]{id});
        }
        values.clear();
        if (!TextUtils.isEmpty(cost)) {
            values.put(COST, cost);
            db.update(tableName, values, ID + "=?", new String[]{id});

            db.execSQL("UPDATE " + tableOfCategory + " SET " + SUMOFCOST + " = " + SUMOFCOST
                    + " + " + number + " WHERE " + TABLECATEGORY + " = ? ", new String[]{category});

        }
        values.clear();
        if (!TextUtils.isEmpty(desc)) {
            values.put(DESC, desc);
            db.update(tableName, values, ID + "=?", new String[]{id});
        }


    }

}