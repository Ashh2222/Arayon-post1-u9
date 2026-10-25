package com.universidad.seguridad.dto;

import com.universidad.seguridad.model.Tarea;

// DTO: evita serializar propietario.contrasenia (hash) en el JSON
public record TareaDto(Long id, String titulo, String descripcion, boolean completada) {
    public static TareaDto de(Tarea t) {
        return new TareaDto(t.getId(), t.getTitulo(), t.getDescripcion(), t.isCompletada());
    }
}