
import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Cliente,
  ClienteFormulario,
  ClienteService
} from '../../services/cliente';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './clientes.html',
  styleUrl: './clientes.css'
})
export class Clientes implements OnInit {

  private clienteService = inject(ClienteService);
  private cdr = inject(ChangeDetectorRef);

  clientes: Cliente[] = [];
  cargando = false;
  guardando = false;
  mensaje = '';
  error = '';
  idEditando: number | null = null;

  formulario: ClienteFormulario = this.formularioVacio();

  ngOnInit(): void {
    this.cargarClientes();
  }

  formularioVacio(): ClienteFormulario {
    return {
      nombre: '',
      apellido: '',
      correo: '',
      telefono: '',
      direccion: ''
    };
  }

  cargarClientes(): void {
    this.cargando = true;
    this.error = '';

    this.clienteService.listar().subscribe({
      next: (datos) => {
        this.clientes = datos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'No se pudieron cargar los clientes.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  guardar(): void {
    this.mensaje = '';
    this.error = '';

    const datos: ClienteFormulario = {
      nombre: this.formulario.nombre.trim(),
      apellido: this.formulario.apellido.trim(),
      correo: this.formulario.correo.trim(),
      telefono: this.formulario.telefono.trim(),
      direccion: this.formulario.direccion.trim()
    };

    if (Object.values(datos).some(valor => !valor)) {
      this.error = 'Completa todos los campos.';
      return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(datos.correo)) {
      this.error = 'Ingresa un correo válido.';
      return;
    }

    this.guardando = true;

    const operacion = this.idEditando === null
      ? this.clienteService.crear(datos)
      : this.clienteService.actualizar(this.idEditando, datos);

    operacion.subscribe({
      next: () => {
        this.mensaje = this.idEditando === null
          ? 'Cliente registrado correctamente.'
          : 'Cliente actualizado correctamente.';

        this.cancelarEdicion();
        this.guardando = false;
        this.cargarClientes();
      },
      error: () => {
        this.error = 'No se pudo guardar el cliente.';
        this.guardando = false;
        this.cdr.detectChanges();
      }
    });
  }

  editar(cliente: Cliente): void {
    this.idEditando = cliente.id;
    this.formulario = {
      nombre: cliente.nombre,
      apellido: cliente.apellido,
      correo: cliente.correo,
      telefono: cliente.telefono,
      direccion: cliente.direccion
    };
    this.mensaje = '';
    this.error = '';
  }

  eliminar(cliente: Cliente): void {
    if (!confirm(`¿Eliminar a ${cliente.nombre} ${cliente.apellido}?`)) {
      return;
    }

    this.clienteService.eliminar(cliente.id).subscribe({
      next: () => {
        this.mensaje = 'Cliente eliminado correctamente.';
        this.error = '';
        this.cargarClientes();
      },
      error: () => {
        this.error = 'No se pudo eliminar el cliente.';
        this.cdr.detectChanges();
      }
    });
  }

  cancelarEdicion(): void {
    this.idEditando = null;
    this.formulario = this.formularioVacio();
  }
}
