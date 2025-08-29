package org.example.functions.funciones;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.example.functions.dto.ProductoDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductosFunction {

    @FunctionName("getAllProductos")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "productos"   // Ej: /api/productos
            )
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        List<ProductoDto> productos = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@dqxabcojf1x64nfc_tp?TNS_ADMIN=classes/Wallet_DQXABCOJF1X64NFC",
                "usuario_test",
                "Usuariotest2025")) {

            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT ID, NOMBRE, DESCRIPCION, PRECIO, STOCK FROM PRODUCTO");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductoDto p = new ProductoDto(
                        rs.getLong("ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("DESCRIPCION"),
                        rs.getDouble("PRECIO"),
                        rs.getInt("STOCK")
                );
                productos.add(p);
            }

        } catch (Exception e) {
            context.getLogger().severe("Error consultando Oracle: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener productos: " + e.getMessage())
                    .build();
        }

        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(productos) // Azure serializa la lista a JSON
                .build();
    }
}