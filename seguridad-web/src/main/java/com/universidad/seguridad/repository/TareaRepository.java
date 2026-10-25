package com.universidad.seguridad.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.universidad.seguridad.model.Tarea;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // Aporte propio: el enunciado usaba el id, pero el principal de
    // Spring Security solo expone el email.
    List<Tarea> findByPropietarioEmail(String email);

    // SEGURO: parametro nombrado. JPA lo envia como PreparedStatement,
    // el valor nunca se interpreta como JPQL/SQL.
    // Version del enunciado: busca en las tareas de TODOS los usuarios.
    @Query("SELECT t FROM Tarea t WHERE t.titulo LIKE %:texto%")
    List<Tarea> buscarPorTitulo(@Param("texto") String texto);

    // SEGURO y filtrada por propietario (aporte propio): un USER
    // no debe ver tareas ajenas al buscar.
    @Query("SELECT t FROM Tarea t WHERE t.propietario.email = :email AND t.titulo LIKE %:texto%")
    List<Tarea> buscarPorTituloDe(@Param("email") String email,
                                  @Param("texto") String texto);

    // INSEGURO - NUNCA HACER ESTO (solo referencia en comentario):
    // @Query(value = "SELECT * FROM tareas WHERE titulo LIKE '%" +
    //     texto + "%'", nativeQuery = true)
    // Si texto = "' OR '1'='1", la consulta devuelve TODAS las tareas
    // sin filtrar, ignorando por completo el criterio de busqueda.
}