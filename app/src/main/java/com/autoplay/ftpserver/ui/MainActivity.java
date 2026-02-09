package com.autoplay.ftpserver.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.autoplay.ftpserver.R;
import com.autoplay.ftpserver.service.FtpServerService;

public class MainActivity extends AppCompatActivity {
    private EditText portInput;
    private TextView portHint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portInput = findViewById(R.id.port_input);
        portHint = findViewById(R.id.port_hint);
        Button startButton = findViewById(R.id.start_button);
        Button stopButton = findViewById(R.id.stop_button);

        int savedPort = FtpServerSettings.getPort(this);
        portInput.setText(String.valueOf(savedPort));
        updateHint(savedPort);

        portInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Integer port = parsePort(s.toString());
                if (port != null) {
                    FtpServerSettings.setPort(MainActivity.this, port);
                    updateHint(port);
                }
            }
        });

        startButton.setOnClickListener(v -> {
            Integer port = parsePort(portInput.getText().toString());
            if (port == null) {
                Toast.makeText(this, R.string.invalid_port, Toast.LENGTH_SHORT).show();
                return;
            }
            FtpServerSettings.setPort(this, port);
            Intent intent = new Intent(this, FtpServerService.class);
            intent.setAction(FtpServerService.ACTION_START);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            Toast.makeText(this, R.string.server_starting, Toast.LENGTH_SHORT).show();
        });

        stopButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FtpServerService.class);
            intent.setAction(FtpServerService.ACTION_STOP);
            startService(intent);
            Toast.makeText(this, R.string.server_stopping, Toast.LENGTH_SHORT).show();
        });
    }

    private Integer parsePort(String value) {
        try {
            int port = Integer.parseInt(value.trim());
            if (port < 1 || port > 65535) {
                return null;
            }
            return port;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void updateHint(int port) {
        portHint.setText(getString(R.string.port_hint, port));
    }
}
