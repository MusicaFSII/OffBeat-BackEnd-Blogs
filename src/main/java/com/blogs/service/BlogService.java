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
import java.io.InputStream;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BlogService {

    @Autowired 
    private BlogRepository blogRepo;
    @Autowired 
    private BlogMapper blogMapper;

   @Value("${app.storage.path:uploads/blogs}")
    private String directorioUploads;

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
        return blogMapper.toResponseDTO(blog);
    }
    /*##-------------------------------METODOS PARA AGREGAR BLOGS (POST)-------------------------------*/

    /*------------Método para agregar nuevos blogs------------*/
    @Transactional
    public ResponseDTO registrarBlog(CreateDTO dto) throws IOException {
        if (blogRepo.existsByTitulo(dto.getTitulo())) {
            throw new IllegalArgumentException("Ya existe un blog con ese título");
        }

        Blog blog = blogMapper.toEntity(dto);
        blog.setImagen(guardarImagen(dto.getImagen()));
        blog.setCodigo(UUID.randomUUID().toString().substring(0, 8)); // temporal

        blog = blogRepo.save(blog);
        blog.setCodigo("BL" + blog.getIdBlog());

        return blogMapper.toResponseDTO(blogRepo.save(blog));
    }

    /*##-------------------------------METODOS PARA ACTUALIZAR BLOGS (PUT)-------------------------------##*/

    /*------------Método para actualizar blogs------------*/
    @Transactional
    public ResponseDTO actualizarBlog(String codigo, CreateDTO dto) throws IOException {
        Blog existing = blogRepo.findByCodigo(codigo);
        if (existing == null) {
            throw new ResourceNotFoundException("No se encontró el blog con código " + codigo);
        }

        if (dto.getTitulo() != null && !dto.getTitulo().isBlank()) {
            if (blogRepo.existsByTituloAndCodigoNot(dto.getTitulo(), codigo)) {
                throw new IllegalArgumentException("Ya existe un blog con ese título");
            }
            existing.setTitulo(dto.getTitulo());
        }
        if (dto.getContenido() != null && !dto.getContenido().isBlank()) {
            existing.setContenido(dto.getContenido());
        }
        if (dto.getImagen() != null && !dto.getImagen().isEmpty()) {
            existing.setImagen(guardarImagen(dto.getImagen()));
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

    private String guardarImagen(MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("La imagen es obligatoria");
        }
        String original = Paths.get(archivo.getOriginalFilename()).getFileName().toString();
        String nombre = UUID.randomUUID() + "_" + original;

        Path ruta = Paths.get(directorioUploads).resolve(nombre);
        Files.createDirectories(ruta.getParent());

        try (InputStream in = archivo.getInputStream()) {
            Files.copy(in, ruta, StandardCopyOption.REPLACE_EXISTING);
        }
        return nombre;
    }
}
