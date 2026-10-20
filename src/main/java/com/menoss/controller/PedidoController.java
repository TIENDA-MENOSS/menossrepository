
package com.menoss.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PedidoController {

    @GetMapping("/pedido")
    public String mostrarPedido(HttpSession session) {

        if (session.getAttribute("usuarioCorreo") == null) {
            return "redirect:/login";
        }

        return "pedido";
    }

    @PostMapping("/pedido/confirmar")
    public String confirmarPedido(
            @RequestParam String nombre,
            @RequestParam String direccion,
            @RequestParam String distrito,
            @RequestParam String telefono,
            @RequestParam String resumen,
            HttpSession session,
            Model model) {

        if (session.getAttribute("usuarioCorreo") == null) {
            return "redirect:/login";
        }

        if (nombre.isBlank()
                || direccion.isBlank()
                || distrito.isBlank()
                || !telefono.matches("\\d{9}")
                || resumen.isBlank()) {

            model.addAttribute(
                    "error",
                    "Completa correctamente los datos de entrega"
            );

            return "pedido";
        }

        String codigo = "MEN-" +
                System.currentTimeMillis();

        model.addAttribute("codigo", codigo);
        model.addAttribute("nombre", nombre);
        model.addAttribute("direccion", direccion);
        model.addAttribute("distrito", distrito);
        model.addAttribute("telefono", telefono);
        model.addAttribute("resumen", resumen);

        return "confirmacion";
    }
}
