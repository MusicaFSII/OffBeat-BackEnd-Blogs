package com.blogs.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class CreateDTO {
    
    @NotNull(message="El blog debe tener un titulo")
    private String titulo;
    @NotNull(message="El blog debe tener contenido")
    private String contenido;
    @NotNull(message="Es obligatorio agregar una imagen al blog")
    private MultipartFile imagen;
}
