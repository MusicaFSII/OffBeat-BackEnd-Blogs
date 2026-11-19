package com.blogs.mapper;

import org.springframework.stereotype.Component;

import com.blogs.dto.CreateDTO;
import com.blogs.dto.ResponseDTO;
import com.blogs.model.Blog;

@Component 
public class BlogMapper {

    public Blog toEntity(CreateDTO dto){
        if(dto == null){
            return null;
        }

        Blog blog = new Blog();

        blog.setTitulo(dto.getTitulo());
        blog.setContenido(dto.getContenido());
        blog.setImagen(dto.getImagen());
        
        return blog;
    };

    public ResponseDTO toResponseDTO(Blog blog){
        if(blog == null){
            return null;
        }

        return new ResponseDTO(
            blog.getCodigo(),
            blog.getTitulo(),
            blog.getContenido(),
            blog.getImagen()
        );
    }
}