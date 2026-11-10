package com.blogs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogs.dto.BlogDTO;
import com.blogs.model.Blog;
import com.blogs.service.BlogService;

@RestController 
@RequestMapping("api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService){
        this.blogService = blogService;
    }

    /*##-------------------------------METODOS GET-------------------------------##*/

    /*------------Método para obtener todos los blogs (EndPoint base)------------*/
    @GetMapping 
    public ResponseEntity<List<Blog>> obtenerBlogs(){
        List<Blog> blog = blogService.obtenerBlogs();
        return ResponseEntity.ok(blog);
    }

    /*------------Método para obtener blog por su código------------*/
    @GetMapping("/{codigo}")
    public ResponseEntity<Blog> obtenerProductoByCodigo(@PathVariable("codigo") String codigo){
        Blog blog = blogService.obtenerBlogsByCodigo(codigo);
        return ResponseEntity.ok(blog);
    }

    /*##-------------------------------METODOS POST-------------------------------##*/

    /*------------Método para guardar nuevos blogs------------*/
    @PostMapping("/guardar")
    public ResponseEntity<Blog> guardarProducto(@RequestBody BlogDTO dto){
        Blog blogNuevo = blogService.registrarBlog(dto);
        return ResponseEntity.ok(blogNuevo);
    }

    /*##-------------------------------METODOS PUT-------------------------------##*/

    /*------------Método para guardar nuevos blogs------------*/
    @PutMapping("/actualizar/{codigo}")
    public ResponseEntity<Blog> actualizarProducto(@PathVariable("codigo") String codigo, @RequestBody BlogDTO dto){
        Blog blog = blogService.actualizarBlog(codigo, dto);
        return ResponseEntity.ok(blog);
    }

    /*##-------------------------------METODOS DELETE-------------------------------##*/
    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<Blog> eliminarProducto(@PathVariable("codigo") String codigo){
        blogService.eliminarBlog(codigo);
        return ResponseEntity.noContent().build();
    }
}
