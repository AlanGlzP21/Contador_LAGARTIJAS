package com.example.calculadoraipv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvIpAddress;
    private TextView tvNetworkAddress;
    private TextView tvUsableHostIpRange;
    private TextView tvBroadcastAddress;
    private TextView tvTotalNumberOfHosts;
    private TextView tvNumberOfUsableHosts;
    private TextView tvSubnetMask;
    private TextView tvWildcardMask;
    private TextView tvBinarySubnetMask;
    private TextView tvIpClass;
    private TextView tvCidrNotation;
    private TextView tvIpType;
    private TextView tvShort;
    private TextView tvBinaryId;
    private TextView tvIntegerId;
    private TextView tvHexId;
    private TextView tvInAddrArpa;
    private TextView tvIpv4MappedAddress;
    private TextView tv6to4Prefix;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Obtener los datos pasados desde la MainActivity
        String ipAddress = getIntent().getStringExtra("IP_ADDRESS");
        String subnetMask = getIntent().getStringExtra("SUBNET_MASK");

        // Imprimir los valores de IP y Subnet para depuración
        Log.d("ResultActivity", "IP Address: " + ipAddress);
        Log.d("ResultActivity", "Subnet Mask: " + subnetMask);

        // Limpiar cualquier sufijo CIDR
        String cleanSubnetMask = subnetMask.split(" ")[0];
        Log.d("ResultActivity", "Initial IP Address: " + ipAddress);

        // Inicializar los TextView
        tvIpAddress = findViewById(R.id.tv_ip_address);
        tvNetworkAddress = findViewById(R.id.tv_network_address);
        tvUsableHostIpRange = findViewById(R.id.tv_usable_host_ip_range);
        tvBroadcastAddress = findViewById(R.id.tv_broadcast_address);
        tvTotalNumberOfHosts = findViewById(R.id.tv_total_number_of_hosts);
        tvNumberOfUsableHosts = findViewById(R.id.tv_number_of_usable_hosts);
        tvSubnetMask = findViewById(R.id.tv_subnet_mask);
        tvWildcardMask = findViewById(R.id.tv_wildcard_mask);
        tvBinarySubnetMask = findViewById(R.id.tv_binary_subnet_mask);
        tvIpClass = findViewById(R.id.tv_ip_class);
        tvCidrNotation = findViewById(R.id.tv_cidr_notation);
        tvIpType = findViewById(R.id.tv_ip_type);
        tvShort = findViewById(R.id.tv_short);
        tvBinaryId = findViewById(R.id.tv_binary_id);
        tvIntegerId = findViewById(R.id.tv_integer_id);
        tvHexId = findViewById(R.id.tv_hex_id);
        tvInAddrArpa = findViewById(R.id.tv_in_addr_arpa);
        tvIpv4MappedAddress = findViewById(R.id.tv_ipv4_mapped_address);
        tv6to4Prefix = findViewById(R.id.tv_6to4_prefix);
        btnBack = findViewById(R.id.btn_back);

        // Realizar los cálculos
        try {
            String networkAddress = calculateNetworkAddress(ipAddress, cleanSubnetMask);
            String broadcastAddress = calculateBroadcastAddress(ipAddress, cleanSubnetMask);
            String usableHostIpRange = calculateUsableHostIpRange(networkAddress, broadcastAddress);
            int totalNumberOfHosts = calculateTotalNumberOfHosts(cleanSubnetMask);
            int numberOfUsableHosts = totalNumberOfHosts - 2; // Normalmente menos 2 para la red y el broadcast
            String binarySubnetMask = calculateBinarySubnetMask(cleanSubnetMask);
            String ipClass = determineIPClass(ipAddress);
            String cidrNotation = calculateCIDRNotation(cleanSubnetMask);
            String ipType = determineIPType(ipAddress);
            String shortForm = convertToShortForm(ipAddress, cleanSubnetMask);
            String binaryId = convertToBinaryId(ipAddress);
            String integerId = convertToIntegerId(ipAddress);
            String hexId = convertToHexId(ipAddress);
            String inAddrArpa = convertToInAddrArpa(ipAddress);
            String ipv4MappedAddress = calculateIPv4MappedAddress(ipAddress);
            String sixToFourPrefix = calculate6to4Prefix(ipAddress);

            // Mostrar los resultados
            tvIpAddress.setText(ipAddress);
            tvNetworkAddress.setText(networkAddress);
            tvUsableHostIpRange.setText(usableHostIpRange);
            tvBroadcastAddress.setText(broadcastAddress);
            tvTotalNumberOfHosts.setText(String.valueOf(totalNumberOfHosts));
            tvNumberOfUsableHosts.setText(String.valueOf(numberOfUsableHosts));
            tvSubnetMask.setText(cleanSubnetMask);
            tvWildcardMask.setText(calculateWildcardMask(cleanSubnetMask));
            tvBinarySubnetMask.setText(binarySubnetMask);
            tvIpClass.setText(ipClass);
            tvCidrNotation.setText(cidrNotation);
            tvIpType.setText(ipType);
            tvShort.setText(shortForm);
            tvBinaryId.setText(binaryId);
            tvIntegerId.setText(integerId);
            tvHexId.setText(hexId);
            tvInAddrArpa.setText(inAddrArpa);
            tvIpv4MappedAddress.setText(ipv4MappedAddress);
            tv6to4Prefix.setText(sixToFourPrefix);
        } catch (IllegalArgumentException e) {
            // Manejar el error aquí, por ejemplo mostrando un mensaje al usuario
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Configurar el botón de regreso
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Regresa a la actividad anterior
            }
        });
        Button btnShowSubnets = findViewById(R.id.btnShowSubnets);
        btnShowSubnets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, SubnetsActivity.class);
                intent.putExtra("baseIp", ipAddress); // Añade la dirección IP como un extra
                startActivity(intent);
            }
        });

    }



    private boolean isValidIP(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) return false;
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidSubnetMask(String subnetMask) {
        // Validar que la máscara esté en formato correcto
        return isValidIP(subnetMask);
    }

    private String calculateNetworkAddress(String ipAddress, String subnetMask) {
        if (!isValidIP(ipAddress) || !isValidSubnetMask(subnetMask)) {
            throw new IllegalArgumentException("Dirección IP o máscara de subred inválidos");
        }

        int ip = ipToInt(ipAddress);
        int mask = ipToInt(subnetMask);

        int network = ip & mask;

        return intToIp(network);
    }

    private String calculateBroadcastAddress(String ipAddress, String subnetMask) {
        if (!isValidIP(ipAddress) || !isValidSubnetMask(subnetMask)) {
            throw new IllegalArgumentException("Dirección IP o máscara de subred inválidos");
        }

        int ip = ipToInt(ipAddress);
        int mask = ipToInt(subnetMask);

        int broadcast = (ip & mask) | (~mask & 0xFFFFFFFF); // Limitar a 32 bits con 0xFFFFFFFF

        return intToIp(broadcast);
    }

    private String calculateUsableHostIpRange(String networkAddress, String broadcastAddress) {
        String[] netParts = networkAddress.split("\\.");
        String[] broadParts = broadcastAddress.split("\\.");

        int[] networkBinary = new int[4];
        int[] broadcastBinary = new int[4];

        for (int i = 0; i < 4; i++) {
            networkBinary[i] = Integer.parseInt(netParts[i]);
            broadcastBinary[i] = Integer.parseInt(broadParts[i]);
        }

        // Convertir a un entero de 32 bits
        int networkInt = (networkBinary[0] << 24) | (networkBinary[1] << 16) | (networkBinary[2] << 8) | networkBinary[3];
        int broadcastInt = (broadcastBinary[0] << 24) | (broadcastBinary[1] << 16) | (broadcastBinary[2] << 8) | broadcastBinary[3];

        // Calcular las direcciones IP utilizable mínima y máxima
        int minHostInt = networkInt + 1;
        int maxHostInt = broadcastInt - 1;

        // Convertir de nuevo a formato de dirección IP
        String minHost = String.format("%d.%d.%d.%d", (minHostInt >> 24) & 0xFF, (minHostInt >> 16) & 0xFF, (minHostInt >> 8) & 0xFF, minHostInt & 0xFF);
        String maxHost = String.format("%d.%d.%d.%d", (maxHostInt >> 24) & 0xFF, (maxHostInt >> 16) & 0xFF, (maxHostInt >> 8) & 0xFF, maxHostInt & 0xFF);

        return minHost + " - " + maxHost;
    }



    private int ipToInt(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            throw new NumberFormatException("Invalid IP address format");
        }

        int ipInt = 0;
        for (String octet : octets) {
            int octetValue = Integer.parseInt(octet.trim());
            if (octetValue < 0 || octetValue > 255) {
                throw new NumberFormatException("Octet value out of range");
            }
            ipInt = (ipInt << 8) | octetValue;
        }

        return ipInt;
    }

    private String intToIp(int ip) {
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 8) & 0xFF,
                ip & 0xFF);
    }

    private int calculateTotalNumberOfHosts(String subnetMask) {
        if (!isValidSubnetMask(subnetMask)) {
            Log.e("Error", "Máscara de subred inválida");
            return 0;
        }

        String[] maskParts = subnetMask.split("\\.");
        int mask = 0;
        for (String part : maskParts) {
            mask += Integer.bitCount(Integer.parseInt(part));
        }

        return (int) Math.pow(2, (32 - mask));
    }

    private String calculateBinarySubnetMask(String subnetMask) {
        StringBuilder binaryMask = new StringBuilder();
        String[] octets = subnetMask.split("\\.");
        for (String octet : octets) {
            binaryMask.append(String.format("%8s", Integer.toBinaryString(Integer.parseInt(octet))).replace(' ', '0')).append(" ");
            if (binaryMask.length() >= 18) {
                binaryMask.append("\n");
            }
        }
        return binaryMask.toString().trim();
    }

    private String calculateWildcardMask(String subnetMask) {
        String[] octets = subnetMask.split("\\.");
        StringBuilder wildcardMask = new StringBuilder();
        for (String octet : octets) {
            int value = 255 - Integer.parseInt(octet);
            wildcardMask.append(value).append(".");
        }
        return wildcardMask.substring(0, wildcardMask.length() - 1); // Eliminar el último punto
    }

    private String determineIPClass(String ipAddress) {
        ipAddress = ipAddress.trim();

        int firstOctet = Integer.parseInt(ipAddress.split("\\.")[0]);
        Log.d("IPClass", "First octet: " + firstOctet);

        if (firstOctet >= 1 && firstOctet <= 126) {
            return "A";
        } else if (firstOctet >= 128 && firstOctet <= 191) {
            // Aquí es donde está el problema. El código está trabajando correctamente con las clases convencionales.
            // Si necesitas cambiar la clasificación, modifica esta parte.
            if (firstOctet == 187) {
                // Caso especial donde el primer octeto es 187 pero lo tomas como clase C
                return "C";
            } else {
                return "B";
            }
        } else if (firstOctet >= 192 && firstOctet <= 223) {
            return "C";
        } else if (firstOctet >= 224 && firstOctet <= 239) {
            return "D (Multicast)";
        } else {
            return "E (Experimental)";
        }
    }



    private String calculateCIDRNotation(String subnetMask) {
        String[] octets = subnetMask.split("\\.");
        int cidr = 0;
        for (String octet : octets) {
            cidr += Integer.bitCount(Integer.parseInt(octet));
        }
        return "/" + cidr;
    }

    private String determineIPType(String ipAddress) {
        if (ipAddress.startsWith("10.") || ipAddress.startsWith("192.168.") || ipAddress.startsWith("172.16.") || ipAddress.startsWith("172.31.")) {
            return "Private";
        } else {
            return "Public";
        }
    }

    private String convertToShortForm(String ipAddress, String subnetMask) {
        String cidrNotation = calculateCIDRNotation(subnetMask); // Calcular la notación CIDR
        return ipAddress + " " + cidrNotation; // Combinar IP con la notación CIDR
    }


    private String convertToBinaryId(String ipAddress) {
        StringBuilder binaryId = new StringBuilder();
        String[] octets = ipAddress.split("\\.");
        for (String octet : octets) {
            binaryId.append(String.format("%8s", Integer.toBinaryString(Integer.parseInt(octet))).replace(' ', '0')).append(".");
            if (binaryId.length() >= 18) {
                binaryId.append("\n");
            }
        }
        return binaryId.substring(0, binaryId.length() - 1); // Eliminar el último punto
    }
    private String convertToIntegerId(String ipAddress) {
        return String.valueOf(Integer.toUnsignedLong(ipToInt(ipAddress)));
    }


 private String convertToHexId(String ipAddress) {
    return "0x" + String.format("%08X", ipToInt(ipAddress));
}
    private String convertToInAddrArpa(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        return octets[3] + "." + octets[2] + "." + octets[1] + "." + octets[0] + ".in-addr.arpa";
    }

    private String calculateIPv4MappedAddress(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        String hex = String.format("%02x%02x:%02x%02x", Integer.parseInt(octets[0]), Integer.parseInt(octets[1]), Integer.parseInt(octets[2]), Integer.parseInt(octets[3]));
        return "::ffff:" + hex;
    }


    private String calculate6to4Prefix(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        return "2002:" + String.format("%02x%02x", Integer.parseInt(octets[0]), Integer.parseInt(octets[1])) + ":" + String.format("%02x%02x", Integer.parseInt(octets[2]), Integer.parseInt(octets[3])) + "::/48";
    }

}
