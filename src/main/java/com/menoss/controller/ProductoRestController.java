
package com.menoss.controller;

import com.menoss.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {
    "http://localhost:4200",
    "http://localhost:4300",
    "https://localhost:4300",
    "https://animated-waddle-596r56pr4pghp75r-4300.app.github.dev"
})
public class ProductoRestController {

    private final List<Producto> productos = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(3);

    public ProductoRestController() {
        productos.add(new Producto(1L, "Arroz", "Arroz de 1 kg", 4.50, 20));
        productos.add(new Producto(2L, "Azúcar", "Azúcar de 1 kg", 4.00, 15));
        productos.add(new Producto(3L, "Aceite", "Aceite vegetal de 1 litro", 9.50, 10));
    }

    @GetMapping
    public synchronized List<Producto> listar() {
        return new ArrayList<>(productos);
    }

    @GetMapping("/{id}")
    public synchronized ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public synchronized ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        producto.setId(contador.incrementAndGet());
        productos.add(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @PutMapping("/{id}")
    public synchronized ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @RequestBody Producto datos) {

        for (Producto producto : productos) {
            if (producto.getId().equals(id)) {
                producto.setNombre(datos.getNombre());
                producto.setDescripcion(datos.getDescripcion());
                producto.setPrecio(datos.getPrecio());
                producto.setStock(datos.getStock());

                return ResponseEntity.ok(producto);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public synchronized ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = productos.removeIf(p -> p.getId().equals(id));

        if (eliminado) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
