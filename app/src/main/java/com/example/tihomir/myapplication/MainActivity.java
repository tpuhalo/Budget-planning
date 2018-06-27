package com.example.tihomir.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements
        View.OnClickListener {


    Cursor cursorCategory = null;
    private SimpleCursorAdapter adapter;
    private ListView listView;
    DatabaseHelper DBhelper;
    SQLiteDatabase db;
    String[] cate = {"Režije", "Kućne potrepštine", "Stanarina", "Zabava", "Auto", "Umjetnost", "Kino", "Ostalo"};
    public ArrayList seletedCategories = new ArrayList();
    String currentMonth = "1990", currentYear = "1";
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBhelper = new DatabaseHelper(this, null, null, 1);
        db = DBhelper.getWritableDatabase();


        displayAdapter();
        DatabaseChangedReceiver mReceiver = new DatabaseChangedReceiver() {
            public void onReceive(Context context, Intent intent) {
                displayAdapter();
            }

        };

        cursorCategory = DBhelper.getCategoryInfo(db);
        if (cursorCategory.getCount() == 0) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Odaberite kategorije")
                    .setMultiChoiceItems(cate, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                seletedCategories.add(indexSelected);
                            } else if (seletedCategories.contains(indexSelected)) {
                                seletedCategories.remove(Integer.valueOf(indexSelected));
                            }
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            for (int i = 0; i < seletedCategories.size(); i++) {

                                makingCategory(cate[Integer.parseInt(seletedCategories.get(i).toString())]);

                            }

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
        }


        //buttons initialization
        Button addCategory = findViewById(R.id.addCategoryButton);
        Button info = findViewById(R.id.infoButton);
        Button delete = findViewById(R.id.deleteCategoryButton);
        Button refresh = findViewById(R.id.refreshButton);
        addCategory.setOnClickListener(this);
        info.setOnClickListener(this);
        delete.setOnClickListener(this);
        refresh.setOnClickListener(this);

    }


    //do something when the button is pressed
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.addCategoryButton:

                final View viewForDialog = getLayoutInflater().inflate(R.layout.fragment_add_category, null);

                final TextView textForName = (EditText) viewForDialog.findViewById(R.id.selectNameOfCategory);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Upišite ime kategorije")
                        .setView(viewForDialog)
                        .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (!TextUtils.isEmpty(textForName.getText().toString()))
                                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.emptyTextView), Toast.LENGTH_LONG).show();

                                else makingCategory(textForName.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", null).create();
                dialog.show();
                break;


            case R.id.deleteCategoryButton:

                db = DBhelper.getReadableDatabase();

                cursorCategory = DBhelper.getCategoryInfo(db);
                final SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,
                        R.layout.only_text_view,
                        cursorCategory,
                        new String[]{
                                "Category"
                        },
                        new int[]{R.id.textviewfordelete},
                        0);

                final Dialog dialog21 = new Dialog(this);
                dialog21.setContentView(R.layout.fragment_delete_category);
                dialog21.setTitle("Koju kategoriju želite obrisati?");

                final ListView listDelete = dialog21.findViewById(R.id.listViewForDeletingCat);
                listDelete.setAdapter(adapter2);

                listDelete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long id) {
                        Cursor c = (Cursor) listDelete.getAdapter().getItem(position);

                        DBhelper.deleteCategory(c.getString(1), db);
                        dialog21.dismiss();
                        getApplicationContext().sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));
                        finish();
                        startActivity(getIntent());
                    }
                });

                dialog21.show();


                break;
            case R.id.infoButton:
                AlertDialog infoDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Informacije")
                        .setView(getLayoutInflater().inflate(R.layout.info_message, null))
                        .setPositiveButton("Ok", null).create();

                infoDialog.show();
                break;

            case R.id.refreshButton:
                getApplicationContext().sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));
                finish();
                startActivity(getIntent());


            default:
                break;

        }


    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }


    public void makingCategory(String Name) {
        db = DBhelper.getWritableDatabase();
        DBhelper.insertDataForCategory(Name, 0 + "", 0 + "", db);
        displayAdapter();
    }


    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }


    public void displayAdapter() {

        db = DBhelper.getReadableDatabase();
        cursorCategory = DBhelper.getCategoryInfo(db);

        adapter = new SimpleCursorAdapter(this,
                R.layout.category,
                cursorCategory,
                new String[]{
                        "Category",
                        "Sum",
                        "Number",
                },
                new int[]{R.id.nameOfTheCategory, R.id.allTheCost, R.id.numberOfBills},
                0);


        listView = findViewById(R.id.listViewForCategory);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {

                Intent showCategory = new Intent(getApplicationContext(), showObjectsOfCategory.class);
                Cursor c = (Cursor) listView.getAdapter().getItem(position);

                //putting info about pressed list item
                Bundle bun = new Bundle();
                bun.putString("name", c.getString(1));
                showCategory.putExtras(bun);
                startActivity(showCategory);
            }
        });
    }

}



