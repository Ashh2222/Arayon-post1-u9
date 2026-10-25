package com.universidad.seguridad.controller;

import com.universidad.seguridad.dto.TareaDto;
import com.universidad.seguridad.service.TareaService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class ApiTareaController {

    private final TareaService service;

    public ApiTareaController(TareaService service) {
        this.service = service;
    }

    @GetMapping
    public List<TareaDto> misTareas(Authentication auth) {
        return service.listarDeUsuario(auth.getName()).stream()
            .map(TareaDto::de)
            .toList();
    }
}