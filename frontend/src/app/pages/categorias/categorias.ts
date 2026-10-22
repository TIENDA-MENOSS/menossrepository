
import {
  Component,
  OnInit,
  inject,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  Categoria,
  CategoriaFormulario,
  CategoriaService
} from '../../services/categoria';

@Component({
  selector: 'app-categorias',
  imports: [CommonModule, FormsModule],
  templateUrl: './categorias.html',
  styleUrl: './categorias.css'
})
export class Categorias implements OnInit {

  private categoriaService = inject(CategoriaService);
  private cdr = inject(ChangeDetectorRef);

  categorias: Categoria[] = [];
  cargando = true;
  guardando = false;
  error = '';
  mensaje = '';
  idEditando: number | null = null;

  formulario: CategoriaFormulario = this.formularioVacio();

  ngOnInit(): void {
    this.cargarCategorias();
  }

  formularioVacio(): CategoriaFormulario {
    return {
      nombre: '',
      descripcion: ''
    };
  }

  cargarCategorias(): void {
    this.cargando = true;
    this.error = '';

    this.categoriaService.listar().subscribe({
      next: (datos) => {
        this.categorias = datos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'No se pudieron cargar las categorías.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  guardar(): void {
    if (
      !this.formulario.nombre.trim() ||
      !this.formulario.descripcion.trim()
    ) {
      this.error = 'Completa todos los campos.';
      return;
    }

    this.guardando = true;
    this.error = '';
    this.mensaje = '';
    this.cdr.detectChanges();

    const datos = { ...this.formulario };

    const solicitud = this.idEditando === null
      ? this.categoriaService.crear(datos)
      : this.categoriaService.actualizar(this.idEditando, datos);

    solicitud.subscribe({
      next: () => {
        this.mensaje = this.idEditando === null
          ? 'Categoría registrada correctamente.'
          : 'Categoría actualizada correctamente.';

        this.cancelar();
        this.guardando = false;
        this.cdr.detectChanges();
        this.cargarCategorias();
      },
      error: () => {
        this.error = 'No se pudo guardar la categoría.';
        this.guardando = false;
        this.cdr.detectChanges();
      }
    });
  }

  editar(categoria: Categoria): void {
    this.idEditando = categoria.id;

    this.formulario = {
      nombre: categoria.nombre,
      descripcion: categoria.descripcion
    };

    this.error = '';
    this.mensaje = '';
  }

  cancelar(): void {
    this.idEditando = null;
    this.formulario = this.formularioVacio();
  }

  eliminar(categoria: Categoria): void {
    if (!confirm(`¿Eliminar la categoría ${categoria.nombre}?`)) {
      return;
    }

    this.error = '';
    this.mensaje = '';

    this.categoriaService.eliminar(categoria.id).subscribe({
      next: () => {
        this.mensaje = 'Categoría eliminada correctamente.';
        this.cdr.detectChanges();
        this.cargarCategorias();
      },
      error: () => {
        this.error = 'No se pudo eliminar la categoría.';
        this.cdr.detectChanges();
      }
    });
  }
}

