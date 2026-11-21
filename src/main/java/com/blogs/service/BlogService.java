package com.blogs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blogs.dto.CreateDTO;
import com.blogs.dto.ResponseDTO;
import com.blogs.exception.ResourceNotFoundException;
import com.blogs.mapper.BlogMapper;
import com.blogs.model.Blog;
import com.blogs.repository.BlogRepository;

import jakarta.transaction.Transactional;

@Service
public class BlogService {

    @Autowired 
    private BlogRepository blogRepo;
    @Autowired 
    private BlogMapper blogMapper;

    /*##-------------------------------METODOS PARA OBTENER BLOGS (GET)-------------------------------##*/

    /*------------Método para obtener todos los blogs------------*/
    public List<ResponseDTO> obtenerBlogs(){
        return blogRepo.findAll().stream().map(blogMapper::toResponseDTO).toList();
    }

    /*------------Método para obtener blogs por el código------------*/
    public ResponseDTO obtenerBlogsByCodigo(String codigo){
        Blog blog = blogRepo.findByCodigo(codigo);

        if (blog == null) {
            throw new ResourceNotFoundException("No se encontró el blog " + codigo);
        }
        return blogMapper.toResponseDTO(blogRepo.findByCodigo(codigo));
    }

    /*##-------------------------------METODOS PARA AGREGAR BLOGS (POST)-------------------------------*/

    /*------------Método para agregar nuevos blogs------------*/
    public ResponseDTO registrarBlog(CreateDTO dto) throws IOException {

        Blog blog = blogMapper.toEntity(dto);

        Path ruta = Paths.get("uploads/blogs", dto.getImagen().getOriginalFilename());
        Files.createDirectories(ruta.getParent());
        Files.copy(
            dto.getImagen().getInputStream(),
            ruta,
            StandardCopyOption.REPLACE_EXISTING
        );

        blog.setImagen(dto.getImagen().getOriginalFilename());

        blog = blogRepo.save(blog);

        blog.setCodigo("BL" + blog.getIdBlog());

        return blogMapper.toResponseDTO(blogRepo.save(blog));
    }

    /*##-------------------------------METODOS PARA ACTUALIZAR BLOGS (PUT)-------------------------------##*/

    /*------------Método para actualizar blogs------------*/
    public ResponseDTO actualizarBlog(String codigo, CreateDTO dto) throws IOException {

        Blog existing = blogRepo.findByCodigo(codigo);

        if (existing == null) {
            throw new ResourceNotFoundException("No se encontró el blog con código " + codigo);
        }

        if (dto.getTitulo() != null && !dto.getTitulo().isBlank()) {
            existing.setTitulo(dto.getTitulo());
        }

        if (dto.getContenido() != null && !dto.getContenido().isBlank()) {
            existing.setContenido(dto.getContenido());
        }

        if (dto.getImagen() != null && !dto.getImagen().isEmpty()) {

            Path ruta = Paths.get(
                "uploads/blogs",
                dto.getImagen().getOriginalFilename()
            );

            Files.createDirectories(ruta.getParent());

            Files.copy(
                dto.getImagen().getInputStream(),
                ruta,
                StandardCopyOption.REPLACE_EXISTING
            );

            existing.setImagen(dto.getImagen().getOriginalFilename());
        }

        return blogMapper.toResponseDTO(blogRepo.save(existing));
    }

    /*##-------------------------------METODOS PARA ELIMINAR BLOGS (DELETE)-------------------------------##*/
    
    @Transactional 
    public String eliminarBlog(String codigo){
        if (blogRepo.findByCodigo(codigo) == null) {
            throw new ResourceNotFoundException("No se encontró el blog con código " + codigo);
        }
        blogRepo.deleteByCodigo(codigo);
        return "El blog " + codigo + " ha sido eliminado";
    }

}
