package com.blogs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class ResponseDTO {
    private String codigo;
    private String titulo;
    private String contenido;
    private String imagen;
}
