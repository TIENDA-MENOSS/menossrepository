
package com.menoss.controller;

import com.menoss.model.Pedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(originPatterns = {
    "http://localhost:*",
    "https://localhost:*",
    "https://*.app.github.dev"
})
public class PedidoRestController {

    private final List<Pedido> pedidos = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(0);

    @GetMapping
    public synchronized List<Pedido> listar() {
        return new ArrayList<>(pedidos);
    }

    @GetMapping("/{id}")
    public synchronized ResponseEntity<Pedido> obtener(@PathVariable Long id) {
        return pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public synchronized ResponseEntity<Pedido> crear(@RequestBody Pedido pedido) {

        if (!esValido(pedido)) {
            return ResponseEntity.badRequest().build();
        }

        pedido.setId(contador.incrementAndGet());
        pedidos.add(pedido);

        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @PutMapping("/{id}")
    public synchronized ResponseEntity<Pedido> actualizar(
            @PathVariable Long id,
            @RequestBody Pedido datos) {

        if (!esValido(datos)) {
            return ResponseEntity.badRequest().build();
        }

        for (Pedido pedido : pedidos) {
            if (pedido.getId().equals(id)) {
                pedido.setCliente(datos.getCliente());
                pedido.setProducto(datos.getProducto());
                pedido.setCantidad(datos.getCantidad());
                pedido.setTotal(datos.getTotal());
                pedido.setEstado(datos.getEstado());

                return ResponseEntity.ok(pedido);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public synchronized ResponseEntity<Void> eliminar(@PathVariable Long id) {

        boolean eliminado = pedidos.removeIf(p -> p.getId().equals(id));

        return eliminado
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private boolean esValido(Pedido pedido) {
        return pedido != null
                && tieneTexto(pedido.getCliente())
                && tieneTexto(pedido.getProducto())
                && pedido.getCantidad() > 0
                && Double.isFinite(pedido.getTotal())
                && pedido.getTotal() >= 0
                && tieneTexto(pedido.getEstado());
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
