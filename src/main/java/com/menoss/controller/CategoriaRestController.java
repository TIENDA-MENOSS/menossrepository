
package com.menoss.controller;

import com.menoss.model.Categoria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/categorias")

@CrossOrigin(origins = {
    "http://localhost:4200",
    "http://localhost:4300",
    "https://localhost:4300",
    "https://animated-waddle-596r56pr4pghp75r-4300.app.github.dev"
})

public class CategoriaRestController {

    private final List<Categoria> categorias = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(3);

    public CategoriaRestController() {
        categorias.add(new Categoria(1L, "Alimentos", "Productos de primera necesidad"));
        categorias.add(new Categoria(2L, "Limpieza", "Productos de limpieza para el hogar"));
        categorias.add(new Categoria(3L, "Higiene", "Productos de higiene personal"));
    }

    @GetMapping
    public synchronized List<Categoria> listar() {
        return new ArrayList<>(categorias);
    }

    @GetMapping("/{id}")
    public synchronized ResponseEntity<Categoria> obtener(@PathVariable Long id) {
        return categorias.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public synchronized ResponseEntity<Categoria> crear(@RequestBody Categoria categoria) {
        categoria.setId(contador.incrementAndGet());
        categorias.add(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @PutMapping("/{id}")
    public synchronized ResponseEntity<Categoria> actualizar(
            @PathVariable Long id,
            @RequestBody Categoria datos) {

        for (Categoria categoria : categorias) {
            if (categoria.getId().equals(id)) {
                categoria.setNombre(datos.getNombre());
                categoria.setDescripcion(datos.getDescripcion());

                return ResponseEntity.ok(categoria);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public synchronized ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = categorias.removeIf(c -> c.getId().equals(id));

        if (eliminado) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
