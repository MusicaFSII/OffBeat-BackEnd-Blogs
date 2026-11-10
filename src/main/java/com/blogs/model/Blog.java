package com.blogs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name="Blogs")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Blog {

    @Id 
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id_blog")
    private int idBlog;

    @Column(name="codigo_blog")
    private String codigo;

    @Column(name="titulo")
    private String titulo;

    @Column(name="contenido")
    private String contenido;

    @Column(name="imagen")
    private byte[] imagen;

}
