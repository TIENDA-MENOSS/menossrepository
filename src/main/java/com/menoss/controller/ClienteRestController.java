
package com.menoss.controller;

import com.menoss.model.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(originPatterns = {
    "http://localhost:*",
    "https://localhost:*",
    "https://*.app.github.dev"
})
public class ClienteRestController {

    private final List<Cliente> clientes = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(0);

    // LISTAR CLIENTES
    @GetMapping
    public synchronized List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }

    // BUSCAR CLIENTE POR ID
    @GetMapping("/{id}")
    public synchronized ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return clientes.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // REGISTRAR CLIENTE
    @PostMapping
    public synchronized ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        if (!esValido(cliente)) {
            return ResponseEntity.badRequest().build();
        }

        cliente.setId(contador.incrementAndGet());
        clientes.add(cliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    // ACTUALIZAR CLIENTE
    @PutMapping("/{id}")
    public synchronized ResponseEntity<Cliente> actualizar(
            @PathVariable Long id,
            @RequestBody Cliente datos) {

        if (!esValido(datos)) {
            return ResponseEntity.badRequest().build();
        }

        for (Cliente cliente : clientes) {
            if (cliente.getId().equals(id)) {
                cliente.setNombre(datos.getNombre());
                cliente.setApellido(datos.getApellido());
                cliente.setCorreo(datos.getCorreo());
                cliente.setTelefono(datos.getTelefono());
                cliente.setDireccion(datos.getDireccion());

                return ResponseEntity.ok(cliente);
            }
        }

        return ResponseEntity.notFound().build();
    }

    // ELIMINAR CLIENTE
    @DeleteMapping("/{id}")
    public synchronized ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = clientes.removeIf(c -> c.getId().equals(id));

        return eliminado
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private boolean esValido(Cliente cliente) {
        return cliente != null
                && tieneTexto(cliente.getNombre())
                && tieneTexto(cliente.getApellido())
                && tieneTexto(cliente.getCorreo())
                && cliente.getCorreo().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                && tieneTexto(cliente.getTelefono())
                && tieneTexto(cliente.getDireccion());
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
