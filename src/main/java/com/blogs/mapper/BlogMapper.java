package com.blogs.mapper;

import java.util.Base64;

import org.springframework.stereotype.Component;

import com.blogs.dto.BlogDTO;
import com.blogs.model.Blog;

@Component 
public class BlogMapper {

    public Blog toEntity(BlogDTO dto){
        if(dto == null){
            return null;
        }

        Blog blog = new Blog();

        blog.setTitulo(dto.getTitulo());
        blog.setContenido(dto.getContenido());
        
        if(dto.getImagen() != null && !dto.getImagen().isEmpty()){
            try{
                String base64Data = dto.getImagen();
                byte[] imagenBytes = Base64.getDecoder().decode(base64Data);
                blog.setImagen(imagenBytes);
            }catch (Exception e){
                blog.setImagen(null);
            }
        }
        
        return blog;
    };


    
}
