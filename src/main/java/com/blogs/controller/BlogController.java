package com.blogs.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogs.dto.CreateDTO;
import com.blogs.dto.ResponseDTO;
import com.blogs.service.BlogService;

@CrossOrigin(origins="*")
@RestController 
@RequestMapping("/api/v1/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService){
        this.blogService = blogService;
    }

    /*##-------------------------------METODOS GET-------------------------------##*/

    /*------------Método para obtener todos los blogs (EndPoint base)------------*/
    @GetMapping 
    public ResponseEntity<List<ResponseDTO>> obtenerBlogs(){
        return ResponseEntity.ok(blogService.obtenerBlogs());
    }

    /*------------Método para obtener blog por su código------------*/
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
    @PostMapping("/guardar")
    public ResponseEntity<ResponseDTO> guardarBlog(@RequestBody CreateDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(blogService.registrarBlog(dto));
    }

    /*##-------------------------------METODOS PUT-------------------------------##*/

    /*------------Método para guardar nuevos blogs------------*/
    @PutMapping("/actualizar/{codigo}")
    public ResponseEntity<ResponseDTO> actualizarBlog(@PathVariable("codigo") String codigo, @RequestBody CreateDTO dto){
        ResponseDTO blog = blogService.actualizarBlog(codigo, dto);
        if (blog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blog);
    }

    /*##-------------------------------METODOS DELETE-------------------------------##*/
    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<Void> eliminarBlog(@PathVariable("codigo") String codigo){
        if (blogService.eliminarBlog(codigo) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}