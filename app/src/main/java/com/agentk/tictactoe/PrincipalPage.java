package com.agentk.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import com.agentk.tictactoe.model.RowItem;
import com.vorlonsoft.android.rate.AppRate;

import java.util.ArrayList;
import java.util.Locale;

public class PrincipalPage extends AppCompatActivity {

    Locale myLocale;
    Context context = this;
    MediaPlayers mp;
    static boolean musicActive;
    String lang;
    String [] lgeTab={"en","fr","es"};//{"en","fr","es"}
    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_page);

       lang=getDefaults("langue",getApplicationContext());

        if(lang!=null) {
            myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            this.setContentView(R.layout.activity_principal_page);
        }

        AppRate.with(this)
                .setInstallDays((byte) 0)           // default 10, 0 means install day
                .setLaunchTimes((byte) 3)           // default 10
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);

        mp=new MediaPlayers(this);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#97979e")));

        ImageButton shareIcon=findViewById(R.id.shareIcon);
        shareIcon.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
               sharingIntent.setType("text/plain");
               String shareBodyText = "https://play.google.com/store/apps/details?id=com.agentk.tictactoe";
               sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");
               sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
               startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
           }
       });

        Button singlePlayer=findViewById(R.id.singlePlayer);
        Button twoPlayer=findViewById(R.id.twoPlayer);
        Button score=findViewById(R.id.scores);

        String son=getDefaults("musicOn",getApplicationContext());
        if(son==null||son.equals("0")) {
            setDefaults("musicOn", "0", getApplicationContext());
            musicActive=true;
        }else{
            if(son.equals("1")) musicActive=false;
        }

        if(getDefaults("level",getApplicationContext())==null)
        setDefaults("level","1",getApplicationContext());
        singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                Intent secondeActivite = new Intent(getApplicationContext(), LevelActivity.class);
                startActivity(secondeActivite);
            }
        });
        twoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                Intent secondeActivite = new Intent(getApplicationContext(), TwoPlayersActivity.class);
                startActivity(secondeActivite);
            }
        });
        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.playMusic(1);
                Intent secondeActivite = new Intent(getApplicationContext(), ScoreActivity.class);
                startActivity(secondeActivite);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        MenuItem soundItem = menu.findItem(R.id.action_sound);

        if(musicActive) {
            soundItem.setIcon(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode_off));
        }else {
            soundItem.setIcon(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sound:
                // User chose the "Settings" item, show the app settings UI...
                if(musicActive){
                    item.setIcon(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode));
                    setDefaults("musicOn", "1", getApplicationContext());
                    musicActive=false;
                }else {
                    item.setIcon(getResources().getDrawable(android.R.drawable.ic_lock_silent_mode_off));
                    setDefaults("musicOn", "0", getApplicationContext());
                    musicActive=true;
                    mp.playMusic(1);
                }
                return true;

            case R.id.action_lge:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                final Dialog alertDialog = new Dialog(context);
//                alertDialog.setTitle("Alert");
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.lgeinfo);

                ListView listview = alertDialog.findViewById(R.id.listView);

                ArrayList<RowItem> rowItems=new ArrayList<>();

                rowItems.add(new RowItem("English",R.drawable.gb));
                rowItems.add(new RowItem("Français",R.drawable.fr));
                rowItems.add(new RowItem("Español",R.drawable.es));

                CustomListViewAdapter adapter = new CustomListViewAdapter(this,
                        R.layout.lge_view, rowItems);
                listview.setAdapter(adapter);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setDefaults("langue", lgeTab[position], getApplicationContext());
                        alertDialog.dismiss();
                        setLocale(lgeTab[position]);
                    }
                });
                alertDialog.show();
                return true;

            case R.id.action_help:

                Intent secondeActivite = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(secondeActivite);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void setLocale(String lang) {

        myLocale = new Locale(lang);

        Resources res = getResources();

        DisplayMetrics dm = res.getDisplayMetrics();

        Configuration conf = res.getConfiguration();

        conf.locale = myLocale;

        res.updateConfiguration(conf, dm);

        recreate();

    }

}
