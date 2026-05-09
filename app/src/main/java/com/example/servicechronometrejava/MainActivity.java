package com.example.servicechronometrejava;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvTemps;
    private Button btnStart, btnStop;
    private ChronometreService chronometreService;
    private boolean isBound = false;

    // Handler qui rafraîchit le TextView toutes les secondes
    private final Handler handler = new Handler();
    private final Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            if (isBound && chronometreService != null) {
                tvTemps.setText(chronometreService.getTempsFormate());
            }
            handler.postDelayed(this, 1000); // rappel dans 1 seconde
        }
    };

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChronometreService.LocalBinder binder = (ChronometreService.LocalBinder) service;
            chronometreService = binder.getService();
            isBound = true;
            handler.post(updateUI); // démarre le rafraîchissement
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            handler.removeCallbacks(updateUI); // arrête le rafraîchissement
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Demander permission notification (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        tvTemps = findViewById(R.id.tvTemps);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(v -> startChronoService());
        btnStop.setOnClickListener(v -> stopChronoService());
    }

    private void startChronoService() {
        Intent intent = new Intent(this, ChronometreService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void stopChronoService() {
        handler.removeCallbacks(updateUI); // stop le rafraîchissement
        tvTemps.setText("00:00");

        Intent intent = new Intent(this, ChronometreService.class);
        intent.setAction("STOP");
        stopService(intent);

        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateUI);
        if (isBound) {
            unbindService(connection);
        }
        super.onDestroy();
    }
}