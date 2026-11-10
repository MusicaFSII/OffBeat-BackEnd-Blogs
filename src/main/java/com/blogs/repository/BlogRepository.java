package com.blogs.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blogs.model.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

    Blog findByCodigo(String codigo);

    Blog deleteByCodigo(String codigo);
}
