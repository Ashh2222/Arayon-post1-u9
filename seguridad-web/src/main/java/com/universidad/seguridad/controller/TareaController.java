package com.universidad.seguridad.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.universidad.seguridad.model.Tarea;
import com.universidad.seguridad.service.TareaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService service;

    public TareaController(TareaService service) {
        this.service = service;
    }
    @InitBinder("tarea")
    public void permitirSoloCamposDelFormulario(org.springframework.web.bind.WebDataBinder binder) {
         binder.setAllowedFields("titulo", "descripcion");
        }

    // Lista las tareas del usuario autenticado
    @GetMapping
    public String listar(Model model, Authentication auth) {
        model.addAttribute("tareas", service.listarDeUsuario(auth.getName()));
        model.addAttribute("tarea", new Tarea());
        return "tareas/lista";
    }

    // Crea una tarea para el usuario autenticado
    @PostMapping
    public String crear(@Valid @ModelAttribute("tarea") Tarea tarea,
                        BindingResult result,
                        Model model,
                        Authentication auth) {
        if (result.hasErrors()) {
            model.addAttribute("tareas", service.listarDeUsuario(auth.getName()));
            return "tareas/lista";
        }
        service.crear(auth.getName(), tarea);
        return "redirect:/tareas";
    }

    // Buscador: usa @Query parametrizado, limitado a las tareas del usuario
    @GetMapping("/buscar")
    public String buscar(@RequestParam(defaultValue = "") String texto,
                         Model model,
                         Authentication auth) {
        model.addAttribute("tareas", service.buscarDe(auth.getName(), texto));
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("texto", texto);
        return "tareas/lista";
    }
}
