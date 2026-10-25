package com.universidad.seguridad.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.universidad.seguridad.model.Usuario;
import com.universidad.seguridad.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional
    public void registrar(Usuario usuario) {
        if (repo.existsByEmail(usuario.getEmail())) {
            throw new IllegalStateException("El correo ya está registrado");
        }
        usuario.setContrasenia(encoder.encode(usuario.getContrasenia()));
        usuario.setRol("ROLE_USER"); // rol por defecto, nunca viene del formulario
        usuario.setActivo(true);
        repo.save(usuario);
    }

    // Aporte propio: el dashboard necesita el NOMBRE (no el correo)
    // para que la prueba de XSS tenga un campo que acepte el payload.
    public Usuario buscarPorEmail(String email) {
        return repo.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listarTodos() {
        return repo.findAll();
    }
}