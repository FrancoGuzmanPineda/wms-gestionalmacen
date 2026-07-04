# Productos como subcategoría de Inventario

Cambios aplicados:

1. Se eliminó **Productos** del menú lateral de todas las vistas.
2. En las pantallas `/productos`, el menú lateral mantiene **Inventario** como módulo activo.
3. En `/inventario` se agregaron las pestañas visuales:
   - **Existencias** (activa)
   - **Productos**
4. En `/productos` y en el formulario de productos se agregaron las mismas pestañas, dejando **Productos** activa.
5. No se modificaron controladores, servicios, entidades, repositorios ni rutas. La URL `/productos` continúa funcionando.

Resultado visual:

```text
Inventario
├── Existencias  -> /inventario
└── Productos    -> /productos
```

Para comprobarlo, ejecutar el proyecto e ingresar a:

- `http://localhost:8080/inventario`
- `http://localhost:8080/productos`
