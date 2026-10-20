
package com.menoss.service;

import com.menoss.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsuarioService {

    private final Map<String, Usuario> usuarios =
            new ConcurrentHashMap<>();

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    public String registrar(String nombre,
                            String correo,
                            String password) {

        if (nombre == null || nombre.isBlank()
                || correo == null || correo.isBlank()
                || password == null) {
            return "Todos los campos son obligatorios";
        }

        nombre = nombre.trim();
        correo = correo.trim().toLowerCase();

        if (!correo.matches(
                "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Correo electrónico inválido";
        }

        if (password.length() < 8) {
            return "Contraseña demasiado corta";
        }

        Usuario usuario = new Usuario(
                nombre, correo, encoder.encode(password));

        Usuario anterior =
                usuarios.putIfAbsent(correo, usuario);

        if (anterior != null) {
            return "El correo ya está registrado";
        }

        return "Registro exitoso";
    }

    public boolean validarLogin(String correo,
                                String password) {

        if (correo == null || password == null) {
            return false;
        }

        Usuario usuario = usuarios.get(
                correo.trim().toLowerCase()
        );

        if (usuario == null) {
            return false;
        }

        return encoder.matches(
                password,
                usuario.getPassword()
        );
    }

    public Usuario buscarPorCorreo(String correo) {
        if (correo == null) {
            return null;
        }

        return usuarios.get(
                correo.trim().toLowerCase()
        );
    }
}
