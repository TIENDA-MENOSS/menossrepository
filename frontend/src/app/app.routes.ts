import { Routes } from '@angular/router';

import { Inicio } from './pages/inicio/inicio';
import { Productos } from './pages/productos/productos';
import { Categorias } from './pages/categorias/categorias';
import { Clientes } from './pages/clientes/clientes';
import { Pedidos } from './pages/pedidos/pedidos';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'productos', component: Productos },
  { path: 'categorias', component: Categorias },
  { path: 'clientes', component: Clientes },
  { path: 'pedidos', component: Pedidos },
  { path: '**', redirectTo: '' }
];