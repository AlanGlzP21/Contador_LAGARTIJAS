package com.example.calculadoraipv;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // Variables para IPv4
    private EditText inputIPv4;
    private Spinner spinnerSubnet;

    // Variables para IPv6
    private EditText inputIPv6;
    private Spinner spinnerPrefixIPv6;

    // Método para validar IPv4
    private boolean isValidIPv4(String ipAddress) {
        String ipv4Pattern = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";
        return ipAddress.matches(ipv4Pattern);
    }

    // Método para validar IPv6
// Método para validar IPv6 con soporte para la notación abreviada (::)
    private boolean isValidIPv6(String ipAddress) {
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])$";
        return ipAddress.matches(ipv6Pattern);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);  // Asegúrate de que este es el nombre de tu XML

        // Referencias para IPv4
        inputIPv4 = findViewById(R.id.input_ipv4);
        spinnerSubnet = findViewById(R.id.spinner_subnet);
        Button btnCalculateIPv4 = findViewById(R.id.btn_calculate_ipv4);

        // Referencias para IPv6
        inputIPv6 = findViewById(R.id.input_ipv6);
        spinnerPrefixIPv6 = findViewById(R.id.spinner_prefix_ipv6);
        Button btnCalculateIPv6 = findViewById(R.id.btn_calculate_ipv6);

        // Lógica para el botón de calcular IPv4
        btnCalculateIPv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = inputIPv4.getText().toString();
                String subnetMask = spinnerSubnet.getSelectedItem().toString();

                if (TextUtils.isEmpty(ipAddress)) {
                    Toast.makeText(MainActivity.this, "Please enter a valid IP address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidIPv4(ipAddress)) {
                    Toast.makeText(MainActivity.this, "Invalid IPv4 format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Iniciar la nueva actividad y pasar los valores para IPv4
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("IP_ADDRESS", ipAddress);
                intent.putExtra("SUBNET_MASK", subnetMask);
                startActivity(intent);
            }
        });

        // Lógica para el botón de calcular IPv6
        btnCalculateIPv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipv6Address = inputIPv6.getText().toString();
                String prefixLength = spinnerPrefixIPv6.getSelectedItem().toString();

                if (TextUtils.isEmpty(ipv6Address)) {
                    Toast.makeText(MainActivity.this, "Please enter a valid IPv6 address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidIPv6(ipv6Address)) {
                    Toast.makeText(MainActivity.this, "Invalid IPv6 format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Eliminar el carácter "/" de la longitud del prefijo antes de pasarla a calculateTotalIPv6Addresses
                String numericPrefixLength = prefixLength.replace("/", "");

                // Realizar cálculos para IPv6
                String fullAddress = calculateFullIPv6Address(ipv6Address);
                String totalAddresses = calculateTotalIPv6Addresses(numericPrefixLength);
                String network = calculateIPv6Network(ipv6Address, numericPrefixLength);
                String ipRange = calculateIPv6Range(ipv6Address, numericPrefixLength);

                // Iniciar la nueva actividad y pasar los valores para IPv6
                Intent intent = new Intent(MainActivity.this, IPv6ResultActivity.class);
                intent.putExtra("ip_address", ipv6Address + "/" + numericPrefixLength);
                intent.putExtra("full_address", fullAddress);
                intent.putExtra("total_addresses", totalAddresses);
                intent.putExtra("network", network);
                intent.putExtra("ip_range", ipRange);
                startActivity(intent);
            }

        });


    }

    // Métodos para el cálculo de IPv6 (deben ajustarse según la lógica que implementes)
    private String calculateFullIPv6Address(String ipv6Address) {
        // Dividimos la dirección en caso de que haya "::"
        String[] parts = ipv6Address.split("::", 2);
        String[] leftSide = parts[0].split(":");      // Parte izquierda de la "::"
        String[] rightSide = parts.length > 1 ? parts[1].split(":") : new String[0];  // Parte derecha si existe

        // Calculamos cuántos bloques de 16 bits faltan
        int missingBlocks = 8 - (leftSide.length + rightSide.length);

        // Usamos un StringBuilder para construir la dirección completa
        StringBuilder fullAddress = new StringBuilder();

        // Añadimos la parte izquierda
        for (String part : leftSide) {
            fullAddress.append(String.format("%04x", Integer.parseInt(part, 16))).append(":");
        }

        // Añadimos los bloques faltantes (todos ceros)
        for (int i = 0; i < missingBlocks; i++) {
            fullAddress.append("0000:");
        }

        // Añadimos la parte derecha
        for (String part : rightSide) {
            fullAddress.append(String.format("%04x", Integer.parseInt(part, 16))).append(":");
        }

        // Eliminar el último ":" si queda, para asegurar que la dirección esté bien formateada
        if (fullAddress.charAt(fullAddress.length() - 1) == ':') {
            fullAddress.setLength(fullAddress.length() - 1);
        }

        return fullAddress.toString();
    }




    private String calculateTotalIPv6Addresses(String prefixLength) {
        // Calcula el número total de direcciones en la subred
        int prefix = Integer.parseInt(prefixLength);
        BigInteger totalAddresses = BigInteger.valueOf(2).pow(128 - prefix);

        // Formatea el número con comas
        NumberFormat formatter = NumberFormat.getInstance();
        return formatter.format(totalAddresses);
    }


    private String calculateIPv6Network(String ipv6Address, String prefixLength) {
        // Expande la dirección IPv6 a su forma completa
        String expandedAddress = expandIPv6Address(ipv6Address);
        int prefix = Integer.parseInt(prefixLength);

        // Convertir la dirección en una secuencia de bits
        BigInteger ipAsBigInt = ipv6ToBigInteger(expandedAddress);

        // Crear una máscara basada en el prefijo
        BigInteger mask = BigInteger.ONE.shiftLeft(128 - prefix).subtract(BigInteger.ONE).not();

        // Aplicar la máscara a la dirección IP para obtener la dirección de red
        BigInteger networkAsBigInt = ipAsBigInt.and(mask);

        // Convertir de nuevo a formato IPv6 y comprimir la dirección
        String network = bigIntegerToIPv6(networkAsBigInt, true); // Expansión completa para evitar problemas

        return compressIPv6Address(network); // Comprimir después de expandir completamente la dirección
    }


    // Método para comprimir correctamente una dirección IPv6 y mantener los ceros adecuados
    private String compressIPv6Address(String ipv6Address) {
        // Reemplazar bloques de ceros consecutivos por "::"
        ipv6Address = ipv6Address.replaceAll("(:0000)+", "::");

        // Eliminar ":::" si aparece accidentalmente
        ipv6Address = ipv6Address.replace(":::", "::");

        // Si la dirección termina en "::", añadir "0000" al final para completar la dirección
        if (ipv6Address.endsWith("::")) {
            ipv6Address += "0000";
        }

        return ipv6Address;
    }


    private String calculateIPv6Range(String ipv6Address, String prefixLength) {
        int prefix = Integer.parseInt(prefixLength);
        String network = calculateIPv6Network(ipv6Address, prefixLength);

        // Convertir la dirección de red en una secuencia de bits
        BigInteger networkAsBigInt = ipv6ToBigInteger(network);

        // Calcular la dirección de broadcast (todos los bits de host en 1)
        BigInteger broadcastAsBigInt = networkAsBigInt.add(BigInteger.ONE.shiftLeft(128 - prefix).subtract(BigInteger.ONE));

        // Convertir de nuevo las direcciones a formato IPv6
        String startRange = bigIntegerToIPv6(networkAsBigInt, true);  // Expansión completa
        String endRange = bigIntegerToIPv6(broadcastAsBigInt, true);  // Expansión completa

        return startRange + " - " + endRange;
    }


    // Función para expandir una dirección IPv6 comprimida a su forma completa
    private String expandIPv6Address(String ipv6Address) {
        String[] blocks = ipv6Address.split("::");
        StringBuilder expandedAddress = new StringBuilder();

        if (blocks.length == 2) {
            // Contamos cuántos bloques faltan en la parte "::"
            int missingBlocks = 8 - (blocks[0].split(":").length + blocks[1].split(":").length);

            // Expansión de la primera parte
            expandedAddress.append(blocks[0]);

            // Añadir los bloques "0000" faltantes
            for (int i = 0; i < missingBlocks; i++) {
                expandedAddress.append(":0000");
            }

            // Añadir la segunda parte expandida
            expandedAddress.append(":").append(blocks[1]);
        } else {
            expandedAddress.append(ipv6Address); // No hay "::", la dirección ya está completa
        }

        // Añadir ceros a la izquierda si los bloques no tienen 4 dígitos
        String[] fullBlocks = expandedAddress.toString().split(":");
        for (int i = 0; i < fullBlocks.length; i++) {
            while (fullBlocks[i].length() < 4) {
                fullBlocks[i] = "0" + fullBlocks[i];
            }
        }

        return String.join(":", fullBlocks);
    }





    // Función para convertir una dirección IPv6 a BigInteger
// Función para convertir una dirección IPv6 a BigInteger sin operaciones de red
    private BigInteger ipv6ToBigInteger(String ipv6Address) {
        // Dividimos la dirección en sus bloques de 16 bits
        String[] parts = ipv6Address.split(":");
        BigInteger result = BigInteger.ZERO;
        for (String part : parts) {
            // Si un bloque está vacío (debido a la notación ::), lo llenamos con ceros
            if (part.isEmpty()) {
                int missingBlocks = 8 - parts.length + 1;  // Calculamos cuántos bloques faltan
                result = result.shiftLeft(16 * missingBlocks); // Añadimos los ceros necesarios
            } else {
                result = result.shiftLeft(16);  // Desplazamos el resultado 16 bits a la izquierda
                result = result.add(new BigInteger(part, 16));  // Añadimos el valor del bloque actual
            }
        }
        return result;
    }


    // Función para convertir un BigInteger a formato de dirección IPv6
// Modifica este método para aceptar el segundo parámetro
    private String bigIntegerToIPv6(BigInteger bigInt, boolean expandFully) {
        try {
            // Convertimos BigInteger a un array de 16 bytes (128 bits)
            byte[] addressBytes = bigInt.toByteArray();

            // Ajustar el tamaño a 16 bytes si es necesario
            if (addressBytes.length > 16) {
                addressBytes = Arrays.copyOfRange(addressBytes, addressBytes.length - 16, addressBytes.length);
            } else if (addressBytes.length < 16) {
                byte[] paddedAddress = new byte[16];
                System.arraycopy(addressBytes, 0, paddedAddress, 16 - addressBytes.length, addressBytes.length);
                addressBytes = paddedAddress;
            }

            // Convertir el array de bytes en segmentos de 16 bits (bloques de IPv6)
            StringBuilder fullAddress = new StringBuilder();
            for (int i = 0; i < addressBytes.length; i += 2) {
                int segment = ((addressBytes[i] & 0xFF) << 8) | (addressBytes[i + 1] & 0xFF);
                fullAddress.append(String.format("%04x", segment));
                if (i < addressBytes.length - 2) {
                    fullAddress.append(":");
                }
            }

            // Si no se quiere expandir completamente (:: notación)
            if (!expandFully) {
                return fullAddress.toString().replaceAll("(:0{1,4}){2,}", "::");
            }

            return fullAddress.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}
