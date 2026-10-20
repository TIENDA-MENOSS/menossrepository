
package com.menoss.controller;

import com.menoss.model.Usuario;
import com.menoss.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(
            UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // MOSTRAR FORMULARIO
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {

        if (session.getAttribute("usuarioCorreo") != null) {
            return "redirect:/";
        }

        return "login";
    }

    // INICIAR SESION
    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        boolean valido = usuarioService.validarLogin(
                correo, password
        );

        if (valido) {
            Usuario usuario =
                    usuarioService.buscarPorCorreo(correo);

            session.setAttribute(
                    "usuarioCorreo",
                    usuario.getCorreo()
            );

            session.setAttribute(
                    "usuarioNombre",
                    usuario.getNombre()
            );

            return "redirect:/";
        }

        model.addAttribute(
                "mensaje",
                "Correo o contraseña incorrectos"
        );

        return "login";
    }

    // CERRAR SESION
    @PostMapping("/logout")
    public String cerrarSesion(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}
