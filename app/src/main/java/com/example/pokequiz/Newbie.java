package com.example.pokequiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Newbie extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newbie);
        TextView rightCount;
        Button play;
        rightCount = findViewById(R.id.acertos);
        play = findViewById(R.id.playAgain);

        Intent i = getIntent();
        String right = i.getStringExtra("acertos");
        rightCount.setText("Acertos: "+right+"/30");

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Newbie.this,MainActivity.class);
                startActivity(i);
            }
        });
    }
}
