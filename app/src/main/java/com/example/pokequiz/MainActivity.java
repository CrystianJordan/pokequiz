package com.example.pokequiz;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    ImageView img;
    TextView roundImg;
    TextView timer;
    Button opt1;
    Button opt2;
    Button opt3;
    Button opt4;
    JSONArray pokedex;
    ArrayList<Integer> options;
    String[] names;
    int right;
    int rightCount;
    int round;
    JSONObject rightPokemon;
    JSONObject pokemon;
    String imgUrl;
    String rightName;
    ArrayList<String> optionsNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        roundImg = findViewById(R.id.rodada);
        img = findViewById(R.id.pokemonImg);
        opt1 = findViewById(R.id.opt1);
        opt2 = findViewById(R.id.opt2);
        opt3 = findViewById(R.id.opt3);
        opt4 = findViewById(R.id.opt4);
        timer = findViewById(R.id.timer);
        Pokedex object = new Pokedex();
        object.downloadJSON();
        pokedex = object.loadJSONobject();
        rightCount = 0;
        round = 1;
        roundImg.setText(String.valueOf(round) + "/30");
        newGame();
        opt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfRight(opt1.getText().toString());
            }
        });
        opt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfRight(opt2.getText().toString());
            }
        });
        opt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfRight(opt3.getText().toString());
            }
        });
        opt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfRight(opt4.getText().toString());
            }
        });

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisecondsUntilDone) {

                // Coundown is counting down (every second)

                timer.setText("00:"+ String.valueOf(millisecondsUntilDone / 1000));

            }

            public void onFinish() {

                if (rightCount >= 0 && rightCount <= 10) {
                    Intent i = new Intent(MainActivity.this, Newbie.class);
                    i.putExtra("acertos", String.valueOf(rightCount));
                    startActivity(i);
                } else if (rightCount >= 11 && rightCount <= 20) {
                    Intent i = new Intent(MainActivity.this, ExperientTreiner.class);
                    i.putExtra("acertos", String.valueOf(rightCount));
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, PokemonMaster.class);
                    i.putExtra("acertos", String.valueOf(rightCount));
                    startActivity(i);
                }

            }
        }.start();
    }


    public void newGame() {
        try {
            Random rnd = new Random();

            optionsNames = new ArrayList<>();
            options = new ArrayList<>();
            options.add(1);
            options.add(2);
            options.add(3);
            options.add(4);
            right = rnd.nextInt(4);

            rightPokemon = pokedex.getJSONObject(rnd.nextInt(151));
            imgUrl = rightPokemon.getString("img");
            downloadImage(imgUrl);
            rightName = rightPokemon.getString("name");
            optionsNames.add("");
            optionsNames.add("");
            optionsNames.add("");
            optionsNames.add("");


            setButtons();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkIfRight(String option) {
        if (option.equals(rightName)) {
            rightCount++;
            Toast.makeText(getApplicationContext(),"Acertou", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Errou,o correto é "+rightName, Toast.LENGTH_SHORT).show();
        }
        round++;
        if (round == 30) {
            if (rightCount >= 0 && rightCount <= 10) {
                Intent i = new Intent(MainActivity.this, Newbie.class);
                i.putExtra("acertos", String.valueOf(rightCount));
                startActivity(i);
            } else if (rightCount >= 11 && rightCount <= 20) {
                Intent i = new Intent(MainActivity.this, ExperientTreiner.class);
                i.putExtra("acertos", String.valueOf(rightCount));
                startActivity(i);
            } else {
                Intent i = new Intent(MainActivity.this, PokemonMaster.class);
                i.putExtra("acertos", String.valueOf(rightCount));
                startActivity(i);
            }
        } else {
            roundImg.setText(String.valueOf(round) + "/30");
            newGame();
        }


    }

    public void setButtons() {
        try {
            Random rnd = new Random();

            for (int i = 0; i < options.size(); i++) {
                pokemon = pokedex.getJSONObject(rnd.nextInt(151));
                if (i == right) {

                    optionsNames.add(i, rightName);
                } else {
                    optionsNames.add(i, pokemon.getString("name"));
                }


            }
            opt1.setText(optionsNames.get(0));
            opt2.setText(optionsNames.get(1));
            opt3.setText(optionsNames.get(2));
            opt4.setText(optionsNames.get(3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadImage(String imageUrl) {
        ImageDownloader imageDownloader = new ImageDownloader();
        imageUrl = imageUrl.replace("http", "https");
        try {
            Bitmap imagem = imageDownloader.execute(imageUrl).get();
            img.setImageBitmap(imagem);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}