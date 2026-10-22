
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Producto,
  ProductoFormulario,
  ProductoService
} from '../../services/producto';

@Component({
  selector: 'app-productos',
  imports: [CommonModule, FormsModule],
  templateUrl: './productos.html',
  styleUrl: './productos.css'
})
export class Productos implements OnInit {

  private productoService = inject(ProductoService);
  private cdr = inject(ChangeDetectorRef);

  productos: Producto[] = [];
  cargando = true;
  guardando = false;
  error = '';
  mensaje = '';
  idEditando: number | null = null;

  formulario: ProductoFormulario = this.formularioVacio();

  ngOnInit(): void {
    this.cargarProductos();
  }

  formularioVacio(): ProductoFormulario {
    return {
      nombre: '',
      descripcion: '',
      precio: 0,
      stock: 0
    };
  }

  cargarProductos(): void {
  this.cargando = true;
  this.error = '';

  this.productoService.listar().subscribe({
    next: (datos) => {
      this.productos = datos;
      this.cargando = false;
      this.cdr.detectChanges();
    },
    error: () => {
      this.error = 'No se pudieron cargar los productos.';
      this.cargando = false;
      this.cdr.detectChanges();
    }
  });
  } 

  guardar(): void {
    if (
      !this.formulario.nombre.trim() ||
      !this.formulario.descripcion.trim() ||
      !Number.isFinite(this.formulario.precio) ||
      this.formulario.precio <= 0 ||
      !Number.isInteger(this.formulario.stock) ||
      this.formulario.stock < 0
    ) {
      this.error = 'Completa correctamente todos los campos.';
      return;
    }

    this.guardando = true;
    this.error = '';
    this.mensaje = '';

    const datos = { ...this.formulario };

    const solicitud = this.idEditando === null
      ? this.productoService.crear(datos)
      : this.productoService.actualizar(this.idEditando, datos);

    
      solicitud.subscribe({
       next: () => {
        this.mensaje = this.idEditando === null
        ? 'Producto registrado correctamente.'
        : 'Producto actualizado correctamente.';

        this.cancelar();
        this.guardando = false;
        this.cdr.detectChanges();
        this.cargarProductos();
        },
        error: () => {
        this.error = 'No se pudo guardar el producto.';
        this.guardando = false;
        this.cdr.detectChanges();
      }
    });
  }

  editar(producto: Producto): void {
    this.idEditando = producto.id;
    this.formulario = {
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      precio: producto.precio,
      stock: producto.stock
    };
    this.error = '';
    this.mensaje = '';
  }

  cancelar(): void {
    this.idEditando = null;
    this.formulario = this.formularioVacio();
  }

  eliminar(producto: Producto): void {
    if (!confirm(`¿Eliminar el producto ${producto.nombre}?`)) {
      return;
    }

    this.error = '';
    this.mensaje = '';

    this.productoService.eliminar(producto.id).subscribe({
      next: () => {
        this.mensaje = 'Producto eliminado correctamente.';
        this.cargarProductos();
      },
      error: () => {
        this.error = 'No se pudo eliminar el producto.';
      }
    });
  }
}
