package com.example.calculadoraipv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class IPv6ResultActivity extends AppCompatActivity {
    private Button btnBack1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipv6_result);
        btnBack1 = findViewById(R.id.btn_back1);
        // Obtener los valores pasados desde la actividad anterior
        Intent intent = getIntent();
        String ipAddress = intent.getStringExtra("ip_address");
        String fullAddress = intent.getStringExtra("full_address");
        String totalAddresses = intent.getStringExtra("total_addresses");
        String network = intent.getStringExtra("network");
        String ipRange = intent.getStringExtra("ip_range");

        // Configurar los valores en los TextViews
        TextView tvIpAddress = findViewById(R.id.tv_ipv6_ip_address);
        TextView tvFullAddress = findViewById(R.id.tv_ipv6_full_address);
        TextView tvTotalAddresses = findViewById(R.id.tv_ipv6_total_addresses);
        TextView tvNetwork = findViewById(R.id.tv_ipv6_network);
        TextView tvIpRange = findViewById(R.id.tv_ipv6_ip_range);

        tvIpAddress.setText("IP Address: " + ipAddress);
        tvFullAddress.setText("Full IP Address: " + fullAddress);
        tvTotalAddresses.setText("Total IP Addresses: " + totalAddresses);
        tvNetwork.setText("Network: " + network);
        tvIpRange.setText("IP Range: " + ipRange);
        // Configurar el botón de regreso
        btnBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Regresa a la actividad anterior
            }
        });
    }


}
