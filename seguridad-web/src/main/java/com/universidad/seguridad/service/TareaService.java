package com.universidad.seguridad.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.universidad.seguridad.model.Tarea;
import com.universidad.seguridad.model.Usuario;
import com.universidad.seguridad.repository.TareaRepository;
import com.universidad.seguridad.repository.UsuarioRepository;

@Service
public class TareaService {

    private final TareaRepository repo;
    private final UsuarioRepository usuarioRepo;

    public TareaService(TareaRepository repo, UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
    }

    // Aporte propio: se identifica al usuario por email, porque el
    // principal de Spring Security no expone el id.
    public List<Tarea> listarDeUsuario(String email) {
        return repo.findByPropietarioEmail(email);
    }

    // Solo ADMIN ve las tareas de todos
    @PreAuthorize("hasRole('ADMIN')")
    public List<Tarea> listarTodas() {
        return repo.findAll();
    }

    // Busqueda parametrizada, limitada a las tareas del usuario
    public List<Tarea> buscarDe(String email, String texto) {
        return repo.buscarPorTituloDe(email, texto);
    }

    @Transactional
    public Tarea crear(String email, Tarea tarea) {
        Usuario propietario = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        tarea.setPropietario(propietario);
        return repo.save(tarea);
    }
}