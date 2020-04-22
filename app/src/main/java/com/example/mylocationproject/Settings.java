package com.example.mylocationproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private Toolbar toolbar;

    Button btnChangingDistance;
    EditText etWarningDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        etWarningDistance = findViewById(R.id.etWarningDistance);
        btnChangingDistance = findViewById(R.id.btnDistance);

        final String str = etWarningDistance.toString();

            btnChangingDistance.setClickable(true);
            btnChangingDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String distance = etWarningDistance.getText().toString();


                    if (distance.matches("")) {
                        myWarning();
                    }else if (Integer.parseInt(distance)==0) {
                        myWarningZero();
                    }else{
                    changingDistance(distance);
                    }
                }
            });


        toolbar = findViewById(R.id.tbSettings);
        toolbar.setTitle("Settings");

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    private void changingDistance(String distance) {

        Intent intent = new Intent(getBaseContext(),Location.class);
        intent.putExtra("key",distance);
        Toast.makeText(getApplicationContext(), "Warning Distance was changed: "+distance,Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }


    private void myWarning() {
        Toast.makeText(this,"You didn't enter distance",Toast.LENGTH_SHORT).show();
    }

    private void myWarningZero() {
        Toast.makeText(getApplicationContext(), "You cannot enter Zero ("+0+")",Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent(this,Location.class);
            startActivity(intent);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.menu_profile);
        item.setVisible(false);
        MenuItem item2 = menu.findItem(R.id.menu_settings);
        item2.setVisible(false);
        MenuItem item3 = menu.findItem(R.id.menu_logout);
        item3.setVisible(false);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,Location.class);
        startActivity(intent);
        finish();
    }
}
