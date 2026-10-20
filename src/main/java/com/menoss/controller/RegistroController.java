package com.menoss.controller;

import com.menoss.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistroController {

    private final UsuarioService usuarioService;

    public RegistroController(
            UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String password,
            Model model) {

        String mensaje = usuarioService.registrar(
                nombre, correo, password);

        model.addAttribute("mensaje", mensaje);

        return "registro";
    }
}
