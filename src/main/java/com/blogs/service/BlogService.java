package com.blogs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blogs.dto.CreateDTO;
import com.blogs.dto.ResponseDTO;
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
        return blogMapper.toResponseDTO(blogRepo.findByCodigo(codigo));
    }

    /*##-------------------------------METODOS PARA AGREGAR BLOGS (POST)-------------------------------*/

    /*------------Método para agregar nuevos blogs------------*/
    public ResponseDTO registrarBlog(CreateDTO dto){
        Blog blog = blogMapper.toEntity(dto);
        blog.setCodigo("BL" + blog.getIdBlog());  
        return blogMapper.toResponseDTO(blogRepo.save(blog));
    }

    /*##-------------------------------METODOS PARA ACTUALIZAR BLOGS (PUT)-------------------------------##*/

    /*------------Método para actualizar blogs------------*/
    public ResponseDTO actualizarBlog(String codigo, CreateDTO dto) {
    Blog existing = blogRepo.findByCodigo(codigo);
        if (existing == null) {
            return null;
        }

        if (dto.getTitulo() != null && !dto.getTitulo().isBlank()) {
            existing.setTitulo(dto.getTitulo());
        }
        if (dto.getContenido() != null && !dto.getContenido().isBlank()) {
            existing.setContenido(dto.getContenido());
        }
        if (dto.getImagen() != null && !dto.getImagen().isBlank()) {
            existing.setImagen(dto.getImagen());
        }
        return blogMapper.toResponseDTO(blogRepo.save(existing));
    }

    /*##-------------------------------METODOS PARA ELIMINAR BLOGS (DELETE)-------------------------------##*/
    
    @Transactional 
    public String eliminarBlog(String codigo){
        if (blogRepo.findByCodigo(codigo) == null) {
            return null; 
        }
        blogRepo.deleteByCodigo(codigo);
        return "El blog " + codigo + " ha sido eliminado";
    }

}
