package com.universidad.seguridad.controller;

import com.universidad.seguridad.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class ApiAdminController {

    private final UsuarioService service;

    public ApiAdminController(UsuarioService service) {
        this.service = service;
    }

    public record UsuarioDto(Long id, String email, String rol) {}

    // DTO sin hash de contraseña
    @GetMapping("/usuarios")
    public List<UsuarioDto> usuarios() {
        return service.listarTodos().stream()
            .map(u -> new UsuarioDto(u.getId(), u.getEmail(), u.getRol()))
            .toList();
    }
}