package com.blogs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.blogs.dto.CreateDTO;
import com.blogs.dto.ResponseDTO;
import com.blogs.exception.ResourceNotFoundException;
import com.blogs.mapper.BlogMapper;
import com.blogs.model.Blog;
import com.blogs.repository.BlogRepository;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    // Nombre de archivo propio de las pruebas, para no pisar imágenes reales.
    private static final String NOMBRE_IMAGEN = "imagen-prueba-unitaria.png";

    @Mock
    private BlogRepository blogRepo;

    @Mock
    private BlogMapper blogMapper;

    @Mock
    private MultipartFile imagenMock;

    @InjectMocks
    private BlogService blogService;

    /** Borra la imagen que el servicio escribe en uploads/blogs durante las pruebas. */
    @AfterEach
    void limpiarArchivos() throws IOException {
        Files.deleteIfExists(Paths.get("uploads/blogs", NOMBRE_IMAGEN));
    }

    /* ===================== obtenerBlogs ===================== */

    @Test
    @DisplayName("obtenerBlogs: devuelve todos los blogs convertidos a ResponseDTO")
    void obtenerBlogs_devuelveListaMapeada() {
        Blog blog1 = new Blog(1, "BL1", "Titulo 1", "Contenido 1", "img1.png");
        Blog blog2 = new Blog(2, "BL2", "Titulo 2", "Contenido 2", "img2.png");
        ResponseDTO dto1 = new ResponseDTO("BL1", "Titulo 1", "Contenido 1", "img1.png");
        ResponseDTO dto2 = new ResponseDTO("BL2", "Titulo 2", "Contenido 2", "img2.png");

        when(blogRepo.findAll()).thenReturn(List.of(blog1, blog2));
        when(blogMapper.toResponseDTO(blog1)).thenReturn(dto1);
        when(blogMapper.toResponseDTO(blog2)).thenReturn(dto2);

        List<ResponseDTO> resultado = blogService.obtenerBlogs();

        assertEquals(2, resultado.size());
        assertEquals(List.of(dto1, dto2), resultado);
        verify(blogRepo).findAll();
    }

    /* ===================== obtenerBlogsByCodigo ===================== */

    @Test
    @DisplayName("obtenerBlogsByCodigo: con un código existente devuelve el DTO del blog")
    void obtenerBlogsByCodigo_existente_devuelveDTO() {
        Blog blog = new Blog(1, "BL1", "Titulo", "Contenido", "img.png");
        ResponseDTO dto = new ResponseDTO("BL1", "Titulo", "Contenido", "img.png");

        when(blogRepo.findByCodigo("BL1")).thenReturn(blog);
        when(blogMapper.toResponseDTO(blog)).thenReturn(dto);

        ResponseDTO resultado = blogService.obtenerBlogsByCodigo("BL1");

        assertSame(dto, resultado);
        // El servicio consulta el repositorio más de una vez, por eso atLeastOnce().
        verify(blogRepo, atLeastOnce()).findByCodigo("BL1");
    }

    @Test
    @DisplayName("obtenerBlogsByCodigo: con un código inexistente lanza ResourceNotFoundException")
    void obtenerBlogsByCodigo_inexistente_lanzaResourceNotFound() {
        // findByCodigo no está stubbeado: el mock devuelve null (blog no encontrado).
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> blogService.obtenerBlogsByCodigo("BL99"));

        assertEquals("No se encontró el blog BL99", ex.getMessage());
        verifyNoInteractions(blogMapper);
    }

    /* ===================== registrarBlog ===================== */

    @Test
    @DisplayName("registrarBlog: guarda la imagen, genera el código BL + id y guarda el blog")
    void registrarBlog_valido_generaCodigoYGuarda() throws IOException {
        MockMultipartFile imagen = new MockMultipartFile(
                "imagen", NOMBRE_IMAGEN, "image/png", "contenido-de-imagen".getBytes());
        CreateDTO dto = new CreateDTO("Titulo", "Contenido", imagen);

        // El mapper devuelve la entidad; el id 5 simula el que asignaría la BD al guardar.
        Blog entidad = new Blog(5, null, "Titulo", "Contenido", null);
        ResponseDTO esperado = new ResponseDTO("BL5", "Titulo", "Contenido", NOMBRE_IMAGEN);

        when(blogMapper.toEntity(dto)).thenReturn(entidad);
        when(blogRepo.save(any(Blog.class))).thenAnswer(inv -> inv.getArgument(0));
        when(blogMapper.toResponseDTO(any(Blog.class))).thenReturn(esperado);

        ResponseDTO resultado = blogService.registrarBlog(dto);

        assertSame(esperado, resultado);

        // Se guarda dos veces: primero para obtener el id, luego con el código ya asignado.
        ArgumentCaptor<Blog> captor = ArgumentCaptor.forClass(Blog.class);
        verify(blogRepo, times(2)).save(captor.capture());
        Blog guardado = captor.getValue();
        assertEquals("BL5", guardado.getCodigo());
        assertEquals(NOMBRE_IMAGEN, guardado.getImagen());

        // Además el archivo quedó escrito en disco.
        assertTrue(Files.exists(Paths.get("uploads/blogs", NOMBRE_IMAGEN)));
    }

    @Test
    @DisplayName("registrarBlog: si falla la lectura de la imagen lanza IOException y no guarda nada")
    void registrarBlog_errorAlLeerImagen_lanzaIOExceptionYNoGuarda() throws IOException {
        CreateDTO dto = new CreateDTO("Titulo", "Contenido", imagenMock);

        when(blogMapper.toEntity(dto)).thenReturn(new Blog());
        when(imagenMock.getOriginalFilename()).thenReturn(NOMBRE_IMAGEN);
        when(imagenMock.getInputStream()).thenThrow(new IOException("Error de lectura simulado"));

        assertThrows(IOException.class, () -> blogService.registrarBlog(dto));

        verify(blogRepo, never()).save(any(Blog.class));
    }

    /* ===================== actualizarBlog ===================== */

    @Test
    @DisplayName("actualizarBlog: si el blog no existe lanza ResourceNotFoundException y no guarda nada")
    void actualizarBlog_inexistente_lanzaResourceNotFoundYNoGuarda() {
        when(blogRepo.findByCodigo("BL99")).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> blogService.actualizarBlog("BL99", new CreateDTO("Nuevo", "Nuevo", null)));

        assertEquals("No se encontró el blog con código BL99", ex.getMessage());
        verify(blogRepo, never()).save(any(Blog.class));
        verifyNoInteractions(blogMapper);
    }

    @Test
    @DisplayName("actualizarBlog: actualiza título y contenido y conserva la imagen si no se envía otra")
    void actualizarBlog_conTituloYContenido_actualizaYConservaImagen() throws IOException {
        Blog existente = new Blog(1, "BL1", "Titulo viejo", "Contenido viejo", "vieja.png");
        ResponseDTO esperado = new ResponseDTO("BL1", "Titulo nuevo", "Contenido nuevo", "vieja.png");

        when(blogRepo.findByCodigo("BL1")).thenReturn(existente);
        when(blogRepo.save(existente)).thenReturn(existente);
        when(blogMapper.toResponseDTO(existente)).thenReturn(esperado);

        ResponseDTO resultado = blogService.actualizarBlog(
                "BL1", new CreateDTO("Titulo nuevo", "Contenido nuevo", null));

        assertSame(esperado, resultado);
        assertEquals("Titulo nuevo", existente.getTitulo());
        assertEquals("Contenido nuevo", existente.getContenido());
        assertEquals("vieja.png", existente.getImagen());
        verify(blogRepo).save(existente);
    }

    @Test
    @DisplayName("actualizarBlog: ignora campos nulos o en blanco y no sobrescribe los datos existentes")
    void actualizarBlog_camposVaciosONulos_noSobrescribe() throws IOException {
        Blog existente = new Blog(1, "BL1", "Titulo original", "Contenido original", "img.png");
        ResponseDTO esperado = new ResponseDTO("BL1", "Titulo original", "Contenido original", "img.png");

        when(blogRepo.findByCodigo("BL1")).thenReturn(existente);
        when(blogRepo.save(existente)).thenReturn(existente);
        when(blogMapper.toResponseDTO(existente)).thenReturn(esperado);

        // titulo en blanco, contenido nulo e imagen nula: no debería cambiar nada.
        blogService.actualizarBlog("BL1", new CreateDTO("   ", null, null));

        assertEquals("Titulo original", existente.getTitulo());
        assertEquals("Contenido original", existente.getContenido());
        assertEquals("img.png", existente.getImagen());
    }

    @Test
    @DisplayName("actualizarBlog: si se envía una imagen nueva la guarda y actualiza el nombre en el blog")
    void actualizarBlog_conImagenNueva_actualizaImagen() throws IOException {
        Blog existente = new Blog(1, "BL1", "Titulo", "Contenido", "vieja.png");
        MockMultipartFile imagenNueva = new MockMultipartFile(
                "imagen", NOMBRE_IMAGEN, "image/png", "imagen-nueva".getBytes());
        ResponseDTO esperado = new ResponseDTO("BL1", "Titulo", "Contenido", NOMBRE_IMAGEN);

        when(blogRepo.findByCodigo("BL1")).thenReturn(existente);
        when(blogRepo.save(existente)).thenReturn(existente);
        when(blogMapper.toResponseDTO(existente)).thenReturn(esperado);

        ResponseDTO resultado = blogService.actualizarBlog(
                "BL1", new CreateDTO(null, null, imagenNueva));

        assertNotNull(resultado);
        assertEquals(NOMBRE_IMAGEN, existente.getImagen());
        assertTrue(Files.exists(Paths.get("uploads/blogs", NOMBRE_IMAGEN)));
    }

    /* ===================== eliminarBlog ===================== */

    @Test
    @DisplayName("eliminarBlog: si el blog existe lo elimina y devuelve el mensaje de confirmación")
    void eliminarBlog_existente_eliminaYDevuelveMensaje() {
        when(blogRepo.findByCodigo("BL1")).thenReturn(new Blog(1, "BL1", "T", "C", "i.png"));

        String resultado = blogService.eliminarBlog("BL1");

        assertEquals("El blog BL1 ha sido eliminado", resultado);
        verify(blogRepo).deleteByCodigo("BL1");
    }

    @Test
    @DisplayName("eliminarBlog: si el blog no existe lanza ResourceNotFoundException y no elimina nada")
    void eliminarBlog_inexistente_lanzaResourceNotFoundYNoElimina() {
        when(blogRepo.findByCodigo("BL99")).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> blogService.eliminarBlog("BL99"));

        assertEquals("No se encontró el blog con código BL99", ex.getMessage());
        verify(blogRepo, never()).deleteByCodigo(any(String.class));
    }
}