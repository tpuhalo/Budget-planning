package com.example.tihomir.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class insideOfObject extends showObjectsOfCategory {

    String insidecategory;
    String cost;
    String ID;
    ContentValues values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_of_object);
        Bundle bun = getIntent().getExtras();
        ID = bun.get("ID").toString();

        db = DBhelper.getReadableDatabase();
        values = DBhelper.getRowWithID(ID, db);

        TextView text = findViewById(R.id.viewForCategoryObject);
        text.setText(values.get("category").toString());
        insideCategory = values.get("category").toString();
        TextView text1 = findViewById(R.id.viewForObjectName);
        text1.setText(values.get("name").toString());
        TextView text2 = findViewById(R.id.viewDateOfObject);
        text2.setText(values.get("date").toString());
        TextView text3 = findViewById(R.id.viewForCostOfObject);
        cost = values.get("cost").toString();
        text3.setText(values.get("cost").toString());
        TextView text4 = findViewById(R.id.viewDescForObject);
        text4.setText(values.get("desc").toString());

    }

    public void onClickEdit(View v) {

        db = DBhelper.getWritableDatabase();
        final View viewAlert = getLayoutInflater().inflate(R.layout.fragment_edit_object, null);

        final TextView name = (EditText) viewAlert.findViewById(R.id.fragmentEditNameOfObject);
        final TextView cost = (EditText) viewAlert.findViewById(R.id.fragmentEditCostOFObject);
        cost.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final TextView desc = (EditText) viewAlert.findViewById(R.id.fragmentEditDescOfObject);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewAlert)
                .setTitle("Promjena:")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String name2 = name.getText().toString();
                                String cost2 = cost.getText().toString();
                                String desc2 = desc.getText().toString();

                                deleteObjectMethod(name2, cost2, desc2);
                            }
                        }
                )
                .setNegativeButton("Cancel", null).create();

        final AlertDialog alert = builder.create();
        alert.show();

    }

    public void deleteObjectMethod(String name3, String cost3, String desc3) {
        double number = 0;
        if (TextUtils.isEmpty(cost3)) {
            number = 0;
        } else {
            if (Double.parseDouble(cost) > Double.parseDouble(cost3))
                number = Double.parseDouble(cost) - Double.parseDouble(cost3);
            else number = Double.parseDouble(cost3) - Double.parseDouble(cost);
        }

        DBhelper.updateData(ID, insidecategory, name3,
                cost3, desc3, db, number);
        getApplicationContext().sendBroadcast(new Intent(DatabaseChangedReceiver.ACTION_DATABASE_CHANGED));
        finish();
        startActivity(getIntent());


    }


    public void onClickDelete(View v) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Jeste li sigurno da želite obrisati?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                        DBhelper.deleteObject(ID,insidecategory,cost,db);
                                dialog.dismiss();
                                exitActivity();
                            }
                        }
                )
                .setNegativeButton("Cancel", null).create();



        builder.show();

    }
    public void exitActivity() {
        this.finish();
    }


}
