package com.blogs.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogs.dto.CreateDTO;
import com.blogs.dto.ResponseDTO;
import com.blogs.service.BlogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/v1/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService){
        this.blogService = blogService;
    }

    /*##-------------------------------METODOS GET-------------------------------##*/

    /*------------Método para obtener todos los blogs (EndPoint base)------------*/

    @Operation(summary = "Obtener todos los blogs", description = "Devuelve todos los blogs")
    @ApiResponse(responseCode = "200", description = "Lista de blogs obtenida correctamente")
    @GetMapping 
    public ResponseEntity<List<ResponseDTO>> obtenerBlogs(){
        return ResponseEntity.ok(blogService.obtenerBlogs());
    }

    /*------------Método para obtener blog por su código------------*/

    @Operation(summary = "Obtener blog por código", description = "Devuelve el blog por su código")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blog encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe un blog con ese código")
    })
    @GetMapping("/{codigo}")
    public ResponseEntity<ResponseDTO> obtenerBlogByCodigo(@PathVariable("codigo") String codigo){
        ResponseDTO blog = blogService.obtenerBlogsByCodigo(codigo);
        if (blog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blog);
    }

    /*##-------------------------------METODOS POST-------------------------------##*/

    /*------------Método para guardar nuevos blogs------------*/

    @Operation(summary = "Guardar nuevo blog", description = "Crea un blog con título, contenido e imagen")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Blog creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación en los datos enviados")
    })
    @PostMapping("/guardar")
    public ResponseEntity<ResponseDTO> guardarBlog(@Valid @ModelAttribute CreateDTO dto)throws IOException{
        return ResponseEntity.status(HttpStatus.CREATED).body(blogService.registrarBlog(dto) );
    }

    /*##-------------------------------METODOS PUT-------------------------------##*/

    /*------------Método para guardar nuevos blogs------------*/

    @Operation(summary = "Actualizar blog", description = "Actualiza los campos enviados del blog indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blog actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No existe un blog con ese código")
    })
    @PutMapping("/actualizar/{codigo}")
    public ResponseEntity<ResponseDTO> actualizarBlog(@PathVariable("codigo") String codigo, @ModelAttribute CreateDTO dto)throws IOException{
        ResponseDTO blog = blogService.actualizarBlog(codigo, dto);
        if (blog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blog);
    }

    /*##-------------------------------METODOS DELETE-------------------------------##*/
    
    @Operation(summary = "Eliminar blog", description = "Elimina el blog por su código")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Blog eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "No existe un blog con ese código")
    })
    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<Void> eliminarBlog(@PathVariable("codigo") String codigo){
        if (blogService.eliminarBlog(codigo) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}