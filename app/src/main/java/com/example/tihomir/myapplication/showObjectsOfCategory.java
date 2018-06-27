package com.example.tihomir.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;


public class showObjectsOfCategory extends MainActivity {

    String insideCategory;
    String date;
    String MONTH;
    String YEAR;
    ListView listviewInsideCategory;
    SimpleCursorAdapter adapterForObjects;
    private static final String TAG = "MyActivity";
    Cursor cursorForGetData;
    String code;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_for_inside_of_category);

        Bundle bun = getIntent().getExtras();


        insideCategory = bun.getString("name");
        if (insideCategory != null) {
            db = DBhelper.getReadableDatabase();
            cursorCategory = DBhelper.getCategoryInfoForObject(insideCategory, db);
            TextView name = findViewById(R.id.insideCategoryName);
            name.setText(insideCategory);
            TextView cost = findViewById(R.id.sumOfCostInsideCategory);
            cost.setText(cursorCategory.getString(2));
            TextView num = findViewById(R.id.numberOfBillsInsideCategory);
            num.setText(cursorCategory.getString(3));

        }

        SimpleDateFormat wholeDate = new SimpleDateFormat("dd/MM/yyyy");
        date = wholeDate.format(System.currentTimeMillis());

        listviewInsideCategory = findViewById(R.id.listViewForShowingObjects);


        getData();
    }

    protected void onPause() {
        super.onPause();
        db.close();
    }

    protected void onResume() {
        super.onResume();

        getData();
    }


    public void addButtonClick(View v) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.fragment_how_to_add_object, null);

        final AlertDialog alert = new AlertDialog.Builder(this).create();

        Button addManually = promptView.findViewById(R.id.addingObjectManually);

        Button addCam = promptView.findViewById(R.id.addingObjectCam);

        addManually.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addObjectManually();
                alert.dismiss();
            }
        });

        addCam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                addObjectWithCamera();
                alert.dismiss();
            }
        });

        alert.setView(promptView);

        alert.show();
    }

    public void deleteObject(View v) {

        db = DBhelper.getReadableDatabase();

        cursorCategory = DBhelper.getAllFromCategory(insideCategory, db);
        final android.support.v4.widget.SimpleCursorAdapter adapter2 = new android.support.v4.widget.SimpleCursorAdapter(this,
                R.layout.only_text_view,
                cursorCategory,
                new String[]{
                        "Name"
                },
                new int[]{R.id.textviewfordelete},
                0);

        final Dialog dialog21 = new Dialog(this);
        dialog21.setContentView(R.layout.fragment_delete_category);

        final ListView listDelete = dialog21.findViewById(R.id.listViewForDeletingCat);
        listDelete.setAdapter(adapter2);

        listDelete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                Cursor c = (Cursor) listDelete.getAdapter().getItem(position);

                DBhelper.deleteObject(c.getString(0), c.getString(1), c.getString(4), db);
                dialog21.dismiss();
                getApplicationContext().sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));
                finish();
                startActivity(getIntent());
            }
        });

        dialog21.show();
    }

    public void refreshClick(View v) {


        getApplicationContext().sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));
        finish();
        startActivity(getIntent());
    }


    private void addObjectManually() {
        db = DBhelper.getWritableDatabase();
        final View viewAlert = getLayoutInflater().inflate(R.layout.fragment_add_object, null);

        final TextView name = (EditText) viewAlert.findViewById(R.id.fragmentselectNameOfBill);
        final TextView cost = (EditText) viewAlert.findViewById(R.id.fragmentcostOfObject);
        cost.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final TextView desc = (EditText) viewAlert.findViewById(R.id.fragmentdescOfObject);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewAlert)
                .setTitle("Dodavanje troška")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                DBhelper.insertData(insideCategory, name.getText().toString(), "HRK",
                                        cost.getText().toString(), date, desc.getText().toString(), db);
                                DBhelper.updateCategory(insideCategory, cost.getText().toString(), db);
                                getData();
                                getApplicationContext().sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));
                                finish();
                                startActivity(getIntent());

                            }
                        }
                )
                .setNegativeButton("Cancel", null).create();

        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void addObjectWithCamera() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            db = DBhelper.getWritableDatabase();
            code = scanResult.toString();
            String[] string = code.split("\\r?\\n");

            String num = string[3];
            num = new StringBuilder(num).insert(num.length()-2, ".").toString();

            String desc = "Iban primatelja: "+string[10]+"\nModel: " + string[11] + "\nPoziv na broj: "+string[12]+ "\nOpis: "+string[14];

            DBhelper.insertData(insideCategory, string[7], string[2], num, date, desc, db);
            DBhelper.updateCategory(insideCategory,num,db);
            getData();
            getApplicationContext().sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));
            finish();
            startActivity(getIntent());

        } else
            Log.e("SCAN", "Skeniranje neuspješno.");
        DBhelper.close();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private void getData() {


        db = DBhelper.getWritableDatabase();
        cursorForGetData = DBhelper.getAllFromCategory(insideCategory, db);
        adapterForObjects = new SimpleCursorAdapter(this,
                R.layout.object,
                cursorForGetData,
                new String[]{
                        "Name",
                        "Date",
                        "Cost"},
                new int[]{R.id.nameOfTheBill, R.id.dateOfTheTransaction, R.id.costOfTheBill});


        listviewInsideCategory.setAdapter(adapterForObjects);

        listviewInsideCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {

                Intent showCategory = new Intent(getBaseContext(), insideOfObject.class);
                Cursor c = (Cursor) listviewInsideCategory.getAdapter().getItem(position);

                //putting info about pressed object
                Bundle bun = new Bundle();
                bun.putString("ID", c.getString(0));
                showCategory.putExtras(bun);

                startActivity(showCategory);
            }
        });
    }
}