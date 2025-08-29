package org.example.functions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
}
