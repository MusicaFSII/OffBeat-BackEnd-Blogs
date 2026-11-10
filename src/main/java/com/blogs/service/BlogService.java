package com.blogs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blogs.dto.BlogDTO;
import com.blogs.mapper.BlogMapper;
import com.blogs.model.Blog;
import com.blogs.repository.BlogRepository;

@Service
public class BlogService {

    @Autowired 
    private BlogRepository blogRepo;
    private BlogMapper blogMapper;

    /*##-------------------------------METODOS PARA OBTENER BLOGS (GET)-------------------------------##*/

    /*------------Método para obtener todos los blogs------------*/
    public List<Blog> obtenerBlogs(){
        return blogRepo.findAll();
    }

    /*------------Método para obtener blogs por el código------------*/
    public Blog obtenerBlogsByCodigo(String codigo){
        return blogRepo.findByCodigo(codigo);
    }

    /*##-------------------------------METODOS PARA AGREGAR BLOGS (POST)-------------------------------*/

    /*------------Método para agregar nuevos blogs------------*/
    public Blog registrarBlog(BlogDTO dto){
        Blog blog = blogMapper.toEntity(dto);
        return blogRepo.save(blog);
    }

    /*##-------------------------------METODOS PARA ACTUALIZAR BLOGS (PUT)-------------------------------##*/

    /*------------Método para actualizar blogs------------*/
    public Blog actualizarBlog(String codigo, BlogDTO dto){
        Blog existing = blogRepo.findByCodigo(codigo);
        if(existing == null){
            return null;
        }
        Blog blogActualizar = blogMapper.toEntity(dto);
        blogActualizar.setIdBlog(existing.getIdBlog());
        blogActualizar.setCodigo(codigo);

        if(dto.getImagen() == null || dto.getImagen().isEmpty()){
            blogActualizar.setImagen(existing.getImagen());
        }
        return blogRepo.save(blogActualizar);
    }

    /*##-------------------------------METODOS PARA ELIMINAR BLOGS (DELETE)-------------------------------##*/
    public String eliminarBlog(String codigo){
        blogRepo.deleteByCodigo(codigo);
        return "El blog" + codigo + "ha sido eliminado";
    }

}
