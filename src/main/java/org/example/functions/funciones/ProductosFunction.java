package org.example.functions.funciones;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import org.example.functions.dto.ProductoDto;

import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class ProductosFunction {

    @FunctionName("getAllProductos")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "productos"
            ) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        List<ProductoDto> productos = new ArrayList<>();

        try {

            String walletPath = "C:\\Users\\Elias\\Documents\\proyectos\\fran\\azure-function-inv\\Wallet_DQXABCOJF1X64NFC".replace("\\", "/");;
                    // Opcionalmente permitir override por variable de entorno
            String walletEnv = System.getenv("ORACLE_WALLET_DIR");
            if (walletEnv != null && !walletEnv.isBlank()) {
                walletPath = walletEnv;
            }

            // 2) Conectar usando TNS_ADMIN hacia tu alias _tp del tnsnames.ora
            String url = "jdbc:oracle:thin:@dqxabcojf1x64nfc_tp?TNS_ADMIN=" + walletPath;
            String user = "usuario_test";
            String pass = "Usuariotest2025";

            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT ID, NOMBRE, DESCRIPCION, PRECIO, STOCK FROM PRODUCTOS");
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    productos.add(ProductoDto.builder()
                            .id(rs.getLong("ID"))
                            .nombre(rs.getString("NOMBRE"))
                            .descripcion(rs.getString("DESCRIPCION"))
                            .precio(rs.getDouble("PRECIO"))
                            .stock(rs.getInt("STOCK"))
                            .build());
                }
            }
        } catch (Exception e) {
            context.getLogger().severe("Error consultando Oracle: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener productos: " + e.getMessage())
                    .build();
        }

        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(productos) // Jackson lo serializa
                .build();
    }
}
