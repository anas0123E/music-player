package com.example.lecteur_de_musique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Cherche_Activity extends AppCompatActivity {
    ListView listDesChansons;
    String[] chansons;
    EditText et;

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Chercher une chanson");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_cherche);
        listDesChansons = findViewById(R.id.ListeDesChanson);
        et = findViewById(R.id.chercheInput);
        afficherChansons();
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                afficherChansons();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public ArrayList<File> trouverChanson(File fich){

        ArrayList<File> liste = new ArrayList<File>();
        File[] fichs = fich.listFiles();

        for(File unFich : fichs) {
            if(unFich.isDirectory() && !unFich.isHidden())
                liste.addAll(trouverChanson(unFich));
            else{
                if( unFich.getName().startsWith(et.getText().toString()) && (unFich.getName().endsWith(".mp3") || unFich.getName().endsWith(".wav")))
                {
                    liste.add(unFich);
                }
            }
        }

        return liste;
    }
    public void afficherChansons() {
        final ArrayList<File> mesChansons = trouverChanson(Environment.getExternalStorageDirectory());

        chansons = new String[mesChansons.size()];

        for(int i = 0; i<mesChansons.size();i++)
            chansons[i] = mesChansons.get(i).getName().toString().replace(".mp3","").replace(".wav","");

        customAdapter monAdapter = new customAdapter();
        listDesChansons.setAdapter(monAdapter);
        listDesChansons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String NomChans = (String) listDesChansons.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(),ActivityLecteur.class).putExtra("chansons",mesChansons)
                        .putExtra("nomChanson",NomChans)
                        .putExtra("pos",position));
            }
        });

    }
    class customAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return chansons.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View maVue = getLayoutInflater().inflate(R.layout.liste_chanson,null);
            TextView nomChanson = maVue.findViewById(R.id.txtNomChans);
            nomChanson.setSelected(true);
            nomChanson.setText(chansons[position]);

            return maVue;
        }
    }
}