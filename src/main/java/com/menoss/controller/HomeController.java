
package com.menoss.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio(HttpSession session, Model model) {

        Object nombre = session.getAttribute("usuarioNombre");

        if (nombre != null) {
            model.addAttribute("usuarioNombre", nombre);
        }

        return "index";
    }
}
