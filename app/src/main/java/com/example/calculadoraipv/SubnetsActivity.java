package com.example.calculadoraipv;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.pdf.PdfDocument;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.graphics.pdf.PdfDocument.Page;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SubnetsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubnetAdapter adapter;
    private List<SubnetInfo> subnetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subnets);

        recyclerView = findViewById(R.id.recyclerViewSubnets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String baseIp = getIntent().getStringExtra("baseIp"); // Recupera la dirección IP del Intent
        subnetList = calculateSubnets(baseIp);

        adapter = new SubnetAdapter(subnetList);
        recyclerView.setAdapter(adapter);

        Button exportPdfButton = findViewById(R.id.exportPdfButton);
        exportPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportSubnetsToPdf(subnetList);
            }
        });
    }

    // Método modificado para calcular todas las subredes /30
    private List<SubnetInfo> calculateSubnets(String baseIp) {
        List<SubnetInfo> subnets = new ArrayList<>();

        // Mantiene los primeros tres octetos de la IP base
        String[] octets = baseIp.split("\\.");
        if (octets.length != 4) {
            Toast.makeText(this, "Invalid IP address format", Toast.LENGTH_SHORT).show();
            return subnets;
        }

        String baseIpPrefix = octets[0] + "." + octets[1] + "." + octets[2] + ".";

        int baseIpInt = ipToInteger(baseIpPrefix + "0"); // Convierte el prefijo a entero
        int subnetSize = 4;  // /30 tiene 4 direcciones

        for (int i = 0; i < 256; i += subnetSize) {
            String networkAddress = integerToIp(baseIpInt + i);
            String usableHostRange = integerToIp(baseIpInt + i + 1) + " - " + integerToIp(baseIpInt + i + 2);
            String broadcastAddress = integerToIp(baseIpInt + i + 3);

            subnets.add(new SubnetInfo(networkAddress, usableHostRange, broadcastAddress));
        }
        return subnets;
    }

    // Convierte una IP en formato String a entero
    private int ipToInteger(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        int ip = 0;
        for (int i = 0; i < 4; i++) {
            ip |= Integer.parseInt(octets[i]) << (24 - (8 * i));
        }
        return ip;
    }

    // Convierte un entero a dirección IP en formato String
    private String integerToIp(int ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }

    private void exportSubnetsToPdf(List<SubnetInfo> subnetList) {
        String pdfPath = getExternalFilesDir(null) + "/subnets.pdf";
        try {
            if (subnetList.isEmpty()) {
                Toast.makeText(this, "Subnet list is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            PdfDocument pdf = new PdfDocument();
            PageInfo pageInfo = new PageInfo.Builder(595, 842, 1).create();
            Page page = pdf.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            int y = 50;  // Posición inicial en el eje y
            for (SubnetInfo subnet : subnetList) {
                // Si la posición y excede el límite de la página, crea una nueva página
                if (y > pageInfo.getPageHeight() - 50) {
                    pdf.finishPage(page);
                    page = pdf.startPage(pageInfo);
                    canvas = page.getCanvas();
                    y = 50;  // Restablecer la posición y para la nueva página
                }

                canvas.drawText("Network Address: " + subnet.getNetworkAddress(), 50, y, paint);
                y += 30;
                canvas.drawText("Usable Host Range: " + subnet.getUsableHostRange(), 50, y, paint);
                y += 30;
                canvas.drawText("Broadcast Address: " + subnet.getBroadcastAddress(), 50, y, paint);
                y += 50;  // Dejar un espacio entre subredes
            }

            pdf.finishPage(page);

            try (FileOutputStream fos = new FileOutputStream(pdfPath)) {
                pdf.writeTo(fos);
            }

            pdf.close();
            Toast.makeText(this, "PDF exported to " + pdfPath, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to export PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
