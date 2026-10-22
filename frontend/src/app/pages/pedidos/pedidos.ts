
import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Pedido,
  PedidoFormulario,
  PedidoService
} from '../../services/pedido';
import { Cliente, ClienteService } from '../../services/cliente';
import { Producto, ProductoService } from '../../services/producto';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css'
})
export class Pedidos implements OnInit {

  private pedidoService = inject(PedidoService);
  private clienteService = inject(ClienteService);
  private productoService = inject(ProductoService);
  private cdr = inject(ChangeDetectorRef);

  pedidos: Pedido[] = [];
  clientes: Cliente[] = [];
  productos: Producto[] = [];
  cargando = false;
  guardando = false;
  mensaje = '';
  error = '';
  idEditando: number | null = null;

  formulario: PedidoFormulario = this.formularioVacio();

  ngOnInit(): void {
  this.cargarPedidos();
  this.cargarClientes();
  this.cargarProductos();
  }

cargarClientes(): void {
  this.clienteService.listar().subscribe({
    next: (datos) => {
      this.clientes = datos;
      this.cdr.detectChanges();
    },
    error: () => {
      this.error = 'No se pudieron cargar los clientes.';
      this.cdr.detectChanges();
    }
  });
}

cargarProductos(): void {
  this.productoService.listar().subscribe({
    next: (datos) => {
      this.productos = datos;
      this.cdr.detectChanges();
    },
    error: () => {
      this.error = 'No se pudieron cargar los productos.';
      this.cdr.detectChanges();
    }
  });
}

  formularioVacio(): PedidoFormulario {
    return {
      cliente: '',
      producto: '',
      cantidad: 1,
      total: 0,
      estado: 'Pendiente'
    };
  }

  cargarPedidos(): void {
    this.cargando = true;
    this.error = '';

    this.pedidoService.listar().subscribe({
      next: (datos) => {
        this.pedidos = datos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'No se pudieron cargar los pedidos.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  
calcularTotal(): void {
  const productoSeleccionado = this.productos.find(
    producto => producto.nombre === this.formulario.producto
  );

  const cantidad = Number(this.formulario.cantidad);

  if (productoSeleccionado && Number.isFinite(cantidad) && cantidad > 0) {
    this.formulario.total = Number(
      (productoSeleccionado.precio * cantidad).toFixed(2)
    );
  } else {
    this.formulario.total = 0;
  }
}

  
  guardar(): void {
    this.mensaje = '';
    this.error = '';

    const datos: PedidoFormulario = {
      cliente: this.formulario.cliente.trim(),
      producto: this.formulario.producto.trim(),
      cantidad: Number(this.formulario.cantidad),
      total: Number(this.formulario.total),
      estado: this.formulario.estado
    };

    if (!datos.cliente || !datos.producto || !datos.estado) {
      this.error = 'Completa todos los campos.';
      return;
    }

    if (!Number.isInteger(datos.cantidad) || datos.cantidad <= 0) {
      this.error = 'La cantidad debe ser un número entero mayor que cero.';
      return;
    }

    if (!Number.isFinite(datos.total) || datos.total < 0) {
      this.error = 'El total debe ser un número válido mayor o igual a cero.';
      return;
    }

    this.guardando = true;
    this.cdr.detectChanges();

    const operacion = this.idEditando === null
      ? this.pedidoService.crear(datos)
      : this.pedidoService.actualizar(this.idEditando, datos);

    operacion.subscribe({
      next: () => {
        this.mensaje = this.idEditando === null
          ? 'Pedido registrado correctamente.'
          : 'Pedido actualizado correctamente.';

        this.cancelarEdicion();
        this.guardando = false;
        this.cargarPedidos();
      },
      error: () => {
        this.error = 'No se pudo guardar el pedido.';
        this.guardando = false;
        this.cdr.detectChanges();
      }
    });
  }

  editar(pedido: Pedido): void {
    this.idEditando = pedido.id;
    this.formulario = {
      cliente: pedido.cliente,
      producto: pedido.producto,
      cantidad: pedido.cantidad,
      total: pedido.total,
      estado: pedido.estado
    };

    this.mensaje = '';
    this.error = '';
  }

  eliminar(pedido: Pedido): void {
    if (!confirm(`¿Eliminar el pedido #${pedido.id}?`)) {
      return;
    }

    this.pedidoService.eliminar(pedido.id).subscribe({
      next: () => {
        this.mensaje = 'Pedido eliminado correctamente.';
        this.error = '';
        this.cargarPedidos();
      },
      error: () => {
        this.error = 'No se pudo eliminar el pedido.';
        this.cdr.detectChanges();
      }
    });
  }

  cancelarEdicion(): void {
    this.idEditando = null;
    this.formulario = this.formularioVacio();
  }
}
