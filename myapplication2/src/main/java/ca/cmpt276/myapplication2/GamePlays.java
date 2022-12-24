package ca.cmpt276.myapplication2;

import static ca.cmpt276.myapplication2.model.ConfigManager.getThemeID;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ca.cmpt276.myapplication2.model.ConfigManager;
import ca.cmpt276.myapplication2.model.Configuration;
import ca.cmpt276.myapplication2.model.Game;
import ca.cmpt276.myapplication2.model.SharedPreferencesUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Game plays class, provides the method to initialize a game play and shows all games that have been played
 * Uses provided models to play the game and SharedPreferencesUtils to store games played
 * Also utilizes a firstPass boolean to show an help infographic until user creates first game
 */

public class GamePlays extends AppCompatActivity {
    private ConfigManager GameConfiguration;
    private int gameConfig = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameConfiguration = ConfigManager.getInstance();
        SharedPreferencesUtils.getConfigManagerToSharedPreferences(this);

        if(getFirstpass()){
            setContentView(R.layout.fragment_game_plays_intro);
            firstsetAddGamePlayButton();
        }else{
            setContentView(R.layout.activity_game_plays);
            setAddGamePlayButton();
            setConfigSpinner();
            populateListView();
            registerClickCallback();
        }

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Game Plays");
 
    }

    @Override
    public void onResume(){
        super.onResume();
        ConfigManager.clearBufferScore();
        GameConfiguration = ConfigManager.getInstance();
        SharedPreferencesUtils.getConfigManagerToSharedPreferences(this);
        if(getFirstpass()){
            setContentView(R.layout.fragment_game_plays_intro);
            firstsetAddGamePlayButton();
        }else{
            setContentView(R.layout.activity_game_plays);
            setAddGamePlayButton();
            setConfigSpinner();
            populateListView();
            registerClickCallback();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        GameConfiguration = ConfigManager.getInstance();
        SharedPreferencesUtils.getConfigManagerToSharedPreferences(this);
        if(getFirstpass()){
            setContentView(R.layout.fragment_game_plays_intro);
            firstsetAddGamePlayButton();
        }else{
            setContentView(R.layout.activity_game_plays);
            setAddGamePlayButton();
            setConfigSpinner();
            populateListView();
            registerClickCallback();
        }
    }

    private boolean getFirstpass(){
        SharedPreferences sharedPref;
        sharedPref = getSharedPreferences("firstPass", MODE_PRIVATE);
        boolean firstPass = sharedPref.getBoolean("firstPass", true);
        return firstPass;
    }

    

    private void setAddGamePlayButton() {
        Button btn = findViewById(R.id.addGameButton);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddGamePlay.makeIntent(GamePlays.this);
                startActivity(intent);
            }
        };
        btn.setOnClickListener(listener);
    }

    private void firstsetAddGamePlayButton() {
        Button btn = findViewById(R.id.firstgameButton);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddGamePlay.makeIntent(GamePlays.this);
                startActivity(intent);
            }
        };
        btn.setOnClickListener(listener);
    }

    private void setConfigSpinner() {
        Spinner spinner = findViewById(R.id.configurationSpinner);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                GameConfiguration.getConfigListNamesWithAll());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gameConfig=i;
                if (i == 0){ // when the selected is all
                    populateListView();
                } else { // when the selected is not all
                    populateListView(GameConfiguration.getConfigList().get(i-1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void populateListView() {
        ArrayList<String> listData = new ArrayList<>();
        GameConfiguration = ConfigManager.getInstance();


        // Create list of items
        for(int i =0; i < GameConfiguration.getNumConfigurations(); i++) {
            for (int j = 0; j < GameConfiguration.getConfigList().get(i).getGamesListSize(); j++) {
                listData.add(GameConfiguration.getConfigList().get(i).
                                        getGamesList().get(j).
                                        getRecord());
            }
        }

        // Build Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,   // activity context
                R.layout.list_items, // layout to use
                listData);      // data

        // Configure the list view
        ListView list = findViewById(R.id.listGamesPlayed);
        list.setAdapter(adapter);
    }


    // called when the user specifies in the game config spinner
    private void populateListView(Configuration configuration) {
        ArrayList<String> listData = new ArrayList<>();
        GameConfiguration = ConfigManager.getInstance();


        // Create list of items
        for (int i = 0; i < configuration.getGamesListSize(); i++) {
            listData.add(
                    configuration.getGamesList().get(i).getRecord());
        }


        // Build Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,   // activity context
                R.layout.list_items, // layout to use
                listData);      // data

        // Configure the list view
        ListView list = findViewById(R.id.listGamesPlayed);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.listGamesPlayed);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int position, long id) {
                Game myGame = GameConfiguration.getConfigList().get(0).getGamesList().get(0);
                boolean set = false ;
                int base = 0;
                int i=gameConfig-1,j=position;
                if (gameConfig == 0){ // case for showing all game plays
                    for(i = 0; i < GameConfiguration.getNumConfigurations(); i++) {
                        for (j = 0; j < GameConfiguration.getConfigList().get(i).getGamesListSize(); j++) {
                            if ((base+j) == position){
                                myGame = GameConfiguration.getConfigList().get(i).getGamesList().get(j);
                                set = true;
                                break;
                            }
                        }
                        if(set){
                            break;
                        }
                        base+=j;
                    }
                }else{
                    myGame = GameConfiguration.getConfigList().get(gameConfig-1).
                            getGamesList().get(position);
                }
                FragmentManager manager = getSupportFragmentManager();
                CongratulationsFragment dialog = new CongratulationsFragment();
                dialog.setCurrentGame(GamePlays.this, myGame,i,j,getThemeID(myGame.getTheme()));
                dialog.show(manager,"MessageDialog");

            }
        });
    }
}