package com.example.lecteur_de_musique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Attributs********************
    ListView listDesChansons;
    String[] chansons;
    //Attrubuts Menu*****************
    NavigationView navVue;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;


    //Creations****************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CreationMenu();
        listDesChansons = (ListView) findViewById(R.id.ListeDesChanson);
        permissionDeLecture();

    }
    public void CreationMenu(){
        navVue = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLo);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.menu_open,R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navVue.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemChansons:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.itemChercher:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        searchActivity();
                        break;
                    case R.id.itemPlayList:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        playlistActivity();
                        break;
                    case R.id.itemPartager:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        partager();
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void searchActivity(){
        Intent intent = new Intent(this,Cherche_Activity.class);
        startActivity(intent);
    }
    public void playlistActivity(){
        Intent intent = new Intent(this,PlaylistsActivity.class);
        startActivity(intent);
    }
    public void partager(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome app I'm using!");
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }
    //****************************************

    //Prendre la permisssion de stokage et trouver les chansons*************************
    public void permissionDeLecture() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                afficherChansons();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    public ArrayList<File> trouverChanson(File fich){

        ArrayList<File> liste = new ArrayList<File>();
        File[] fichs = fich.listFiles();

        for(File unFich : fichs) {
            if(unFich.isDirectory() && !unFich.isHidden())
                liste.addAll(trouverChanson(unFich));
            else{
                if(unFich.getName().endsWith(".mp3") || unFich.getName().endsWith(".wav"))
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
    //*************************************************************************

    //adaptateur de style de vue des chansons**********
    class customAdapter extends BaseAdapter{

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
    //********************************************

    //Menu*********************************


    //*************************************
}