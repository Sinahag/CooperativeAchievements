package ca.cmpt276.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * this class is about the help screen, player can find useful guideline on the screen.
 */
public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setTitle("Help Info");
    }
}