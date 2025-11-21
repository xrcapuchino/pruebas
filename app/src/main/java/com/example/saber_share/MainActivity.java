package com.example.saber_share;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.saber_share.util.SessionManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button botoncito;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        botoncito = findViewById(R.id.botoncito);
        botoncito.setOnClickListener(this);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

    }

    @Override
    public void onClick(View view) {
        sessionManager.logoutUser();
    }
}