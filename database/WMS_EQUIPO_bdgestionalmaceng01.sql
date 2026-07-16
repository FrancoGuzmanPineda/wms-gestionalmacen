-- ============================================================
-- SISTEMA DE GESTIÓN DE ALMACENES (WMS)
-- Base de datos: bdgestionalmaceng01
-- Motor requerido: MySQL 8.0+
--
-- VERSIÓN DEMOSTRATIVA PARA DISTRIBUIDORA DE BEBIDAS
-- Incluye control de stock por ubicación, recuperación de contraseña,
-- 20 productos de bebidas, movimientos y despachos coherentes.
--
-- ADVERTENCIA: elimina y vuelve a crear la base de datos.
-- ============================================================

SET NAMES utf8mb4;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS;
SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS;
SET @OLD_SQL_SAFE_UPDATES = @@SQL_SAFE_UPDATES;

SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

DROP DATABASE IF EXISTS `bdgestionalmaceng01`;

CREATE DATABASE `bdgestionalmaceng01`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `bdgestionalmaceng01`;

-- ============================================================
-- 1. TABLAS MAESTRAS
-- ============================================================

CREATE TABLE `categorias` (
  `id_categoria` INT NOT NULL AUTO_INCREMENT,
  `nombre_categoria` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) DEFAULT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `uk_categorias_nombre` (`nombre_categoria`),
  CONSTRAINT `chk_categorias_estado`
    CHECK (`estado` IN ('Activo', 'Inactivo'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tipos` (
  `id_tipo` INT NOT NULL AUTO_INCREMENT,
  `nombre_tipo` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) DEFAULT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_tipo`),
  UNIQUE KEY `uk_tipos_nombre` (`nombre_tipo`),
  CONSTRAINT `chk_tipos_estado`
    CHECK (`estado` IN ('Activo', 'Inactivo'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tipos_movimiento` (
  `id_tipo_movimiento` INT NOT NULL AUTO_INCREMENT,
  `nombre_tipo_movimiento` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) DEFAULT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_tipo_movimiento`),
  UNIQUE KEY `uk_tipos_movimiento_nombre` (`nombre_tipo_movimiento`),
  CONSTRAINT `chk_tipos_movimiento_estado`
    CHECK (`estado` IN ('Activo', 'Inactivo'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tipos_orden` (
  `id_tipo_orden` INT NOT NULL AUTO_INCREMENT,
  `nombre_tipo_orden` VARCHAR(50) NOT NULL,
  `descripcion` VARCHAR(255) DEFAULT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_tipo_orden`),
  UNIQUE KEY `uk_tipos_orden_nombre` (`nombre_tipo_orden`),
  CONSTRAINT `chk_tipos_orden_estado`
    CHECK (`estado` IN ('Activo', 'Inactivo'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tipos_tercero` (
  `id_tipo_tercero` INT NOT NULL AUTO_INCREMENT,
  `nombre_tipo_tercero` VARCHAR(50) NOT NULL,
  `descripcion` VARCHAR(255) DEFAULT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_tipo_tercero`),
  UNIQUE KEY `uk_tipos_tercero_nombre` (`nombre_tipo_tercero`),
  CONSTRAINT `chk_tipos_tercero_estado`
    CHECK (`estado` IN ('Activo', 'Inactivo'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ubicaciones` (
  `id_ubicacion` INT NOT NULL AUTO_INCREMENT,
  `codigo_estante` VARCHAR(50) NOT NULL,
  `pasillo` VARCHAR(50) DEFAULT NULL,
  `tipo_ubicacion` VARCHAR(50) DEFAULT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_ubicacion`),
  UNIQUE KEY `uk_ubicaciones_codigo` (`codigo_estante`),
  CONSTRAINT `chk_ubicaciones_estado`
    CHECK (`estado` IN ('Activo', 'Inactivo'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `usuarios` (
  `id_usuario` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  `correo` VARCHAR(255) NOT NULL,
  `clave` VARCHAR(255) NOT NULL,
  `rol` VARCHAR(255) DEFAULT NULL,
  `estado` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `uk_usuarios_correo` (`correo`),
  CONSTRAINT `chk_usuarios_estado`
    CHECK (`estado` IN (0, 1))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `password_reset_tokens` (
  `id_token` BIGINT NOT NULL AUTO_INCREMENT,
  `id_usuario` BIGINT NOT NULL,
  `token_hash` CHAR(64) NOT NULL,
  `fecha_creacion` DATETIME NOT NULL,
  `fecha_expiracion` DATETIME NOT NULL,
  `utilizado` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_token`),
  UNIQUE KEY `uk_password_reset_token_hash` (`token_hash`),
  KEY `idx_password_reset_usuario` (`id_usuario`),
  KEY `idx_password_reset_expiracion` (`fecha_expiracion`),
  CONSTRAINT `fk_password_reset_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `usuarios` (`id_usuario`)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT `chk_password_reset_utilizado`
    CHECK (`utilizado` IN (0, 1))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 2. PRODUCTOS Y TERCEROS
-- ============================================================

CREATE TABLE `productos` (
  `id_producto` INT NOT NULL AUTO_INCREMENT,
  `id_categoria` INT NOT NULL,
  `id_tipo` INT NOT NULL,
  `nombre` VARCHAR(150) NOT NULL,
  `codigo` VARCHAR(50) NOT NULL,
  `costo` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `precio_venta` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `stock_minimo` INT NOT NULL DEFAULT 0,
  `unidad_medida` VARCHAR(20) DEFAULT NULL,
  `stock_actual` INT NOT NULL DEFAULT 0,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `uk_productos_codigo` (`codigo`),
  KEY `idx_productos_categoria` (`id_categoria`),
  KEY `idx_productos_tipo` (`id_tipo`),
  KEY `idx_productos_estado` (`estado`),
  CONSTRAINT `fk_productos_categoria`
    FOREIGN KEY (`id_categoria`)
    REFERENCES `categorias` (`id_categoria`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_productos_tipo`
    FOREIGN KEY (`id_tipo`)
    REFERENCES `tipos` (`id_tipo`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `chk_productos_costo`
    CHECK (`costo` >= 0),
  CONSTRAINT `chk_productos_precio`
    CHECK (`precio_venta` >= 0),
  CONSTRAINT `chk_productos_stock_minimo`
    CHECK (`stock_minimo` >= 0),
  CONSTRAINT `chk_productos_stock_actual`
    CHECK (`stock_actual` >= 0),
  CONSTRAINT `chk_productos_estado`
    CHECK (`estado` IN ('Activo', 'Inactivo'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `inventario_ubicaciones` (
  `id_inventario_ubicacion` INT NOT NULL AUTO_INCREMENT,
  `id_producto` INT NOT NULL,
  `id_ubicacion` INT NOT NULL,
  `cantidad` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id_inventario_ubicacion`),
  UNIQUE KEY `uk_inventario_producto_ubicacion`
    (`id_producto`, `id_ubicacion`),
  KEY `idx_inventario_ubicacion` (`id_ubicacion`),
  CONSTRAINT `fk_inventario_producto`
    FOREIGN KEY (`id_producto`)
    REFERENCES `productos` (`id_producto`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_inventario_ubicacion`
    FOREIGN KEY (`id_ubicacion`)
    REFERENCES `ubicaciones` (`id_ubicacion`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `chk_inventario_cantidad`
    CHECK (`cantidad` >= 0)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `terceros` (
  `id_tercero` INT NOT NULL AUTO_INCREMENT,
  `id_tipo_tercero` INT NOT NULL,
  `nombre` VARCHAR(150) NOT NULL,
  `ruc` VARCHAR(20) DEFAULT NULL,
  `contacto` VARCHAR(100) DEFAULT NULL,
  `telefono` VARCHAR(20) DEFAULT NULL,
  `direccion` VARCHAR(255) DEFAULT NULL,
  `correo` VARCHAR(100) DEFAULT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_tercero`),
  UNIQUE KEY `uk_terceros_ruc` (`ruc`),
  KEY `idx_terceros_tipo` (`id_tipo_tercero`),
  CONSTRAINT `fk_terceros_tipo`
    FOREIGN KEY (`id_tipo_tercero`)
    REFERENCES `tipos_tercero` (`id_tipo_tercero`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `chk_terceros_estado`
    CHECK (`estado` IN ('Activo', 'Inactivo'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 3. ÓRDENES
-- ============================================================

CREATE TABLE `ordenes` (
  `id_orden` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_tipo_orden` INT NOT NULL,
  `id_tercero` INT NOT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
  PRIMARY KEY (`id_orden`),
  KEY `idx_ordenes_tipo` (`id_tipo_orden`),
  KEY `idx_ordenes_tercero` (`id_tercero`),
  KEY `idx_ordenes_fecha` (`fecha`),
  CONSTRAINT `fk_ordenes_tipo`
    FOREIGN KEY (`id_tipo_orden`)
    REFERENCES `tipos_orden` (`id_tipo_orden`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_ordenes_tercero`
    FOREIGN KEY (`id_tercero`)
    REFERENCES `terceros` (`id_tercero`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `chk_ordenes_estado`
    CHECK (`estado` IN (
      'Pendiente',
      'En proceso',
      'Aprobada',
      'Completada',
      'Cancelada'
    ))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `detalle_ordenes` (
  `id_detalle_orden` INT NOT NULL AUTO_INCREMENT,
  `id_orden` INT NOT NULL,
  `id_producto` INT NOT NULL,
  `cantidad` INT NOT NULL,
  `precio_unitario` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `subtotal` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `descuento` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`id_detalle_orden`),
  UNIQUE KEY `uk_detalle_orden_producto` (`id_orden`, `id_producto`),
  KEY `idx_detalle_producto` (`id_producto`),
  CONSTRAINT `fk_detalle_orden`
    FOREIGN KEY (`id_orden`)
    REFERENCES `ordenes` (`id_orden`)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT `fk_detalle_producto`
    FOREIGN KEY (`id_producto`)
    REFERENCES `productos` (`id_producto`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `chk_detalle_cantidad`
    CHECK (`cantidad` > 0),
  CONSTRAINT `chk_detalle_precio`
    CHECK (`precio_unitario` >= 0),
  CONSTRAINT `chk_detalle_subtotal`
    CHECK (`subtotal` >= 0),
  CONSTRAINT `chk_detalle_descuento`
    CHECK (`descuento` >= 0),
  CONSTRAINT `chk_detalle_estado`
    CHECK (`estado` IN (
      'Activo',
      'Recibido',
      'Preparado',
      'Despachado',
      'Faltante',
      'Cancelado'
    ))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 4. OPERACIONES DE INVENTARIO
-- ============================================================

CREATE TABLE `movimientos` (
  `id_movimiento` INT NOT NULL AUTO_INCREMENT,
  `cantidad` INT NOT NULL,
  `fecha` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo_movimiento` VARCHAR(50) NOT NULL,
  `id_tipo_movimiento` INT NOT NULL,
  `id_producto` INT NOT NULL,
  `id_ubicacion` INT NOT NULL,
  `id_usuario` BIGINT NOT NULL,
  `id_orden` INT DEFAULT NULL,
  `observacion` VARCHAR(255) DEFAULT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Completado',
  PRIMARY KEY (`id_movimiento`),
  KEY `idx_movimientos_tipo` (`id_tipo_movimiento`),
  KEY `idx_movimientos_producto` (`id_producto`),
  KEY `idx_movimientos_ubicacion` (`id_ubicacion`),
  KEY `idx_movimientos_usuario` (`id_usuario`),
  KEY `idx_movimientos_orden` (`id_orden`),
  KEY `idx_movimientos_fecha` (`fecha`),
  KEY `idx_movimientos_tipo_texto` (`tipo_movimiento`),
  CONSTRAINT `fk_movimientos_tipo`
    FOREIGN KEY (`id_tipo_movimiento`)
    REFERENCES `tipos_movimiento` (`id_tipo_movimiento`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_movimientos_producto`
    FOREIGN KEY (`id_producto`)
    REFERENCES `productos` (`id_producto`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_movimientos_ubicacion`
    FOREIGN KEY (`id_ubicacion`)
    REFERENCES `ubicaciones` (`id_ubicacion`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_movimientos_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `usuarios` (`id_usuario`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_movimientos_orden`
    FOREIGN KEY (`id_orden`)
    REFERENCES `ordenes` (`id_orden`)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT `chk_movimientos_cantidad`
    CHECK (`cantidad` > 0),
  CONSTRAINT `chk_movimientos_estado`
    CHECK (`estado` IN ('Pendiente', 'Completado', 'Anulado')),
  CONSTRAINT `chk_movimientos_tipo_texto`
    CHECK (`tipo_movimiento` IN (
      'Ingreso',
      'Salida',
      'Ajuste',
      'Transferencia'
    ))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `despachos` (
  `id_despacho` INT NOT NULL AUTO_INCREMENT,
  `id_producto` INT NOT NULL,
  `id_ubicacion` INT NOT NULL,
  `cantidad` INT NOT NULL,
  `fecha_salida` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_llegada` DATETIME DEFAULT NULL,
  `fecha_registro` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `empresa_destino` VARCHAR(255) DEFAULT NULL,
  `conductor` VARCHAR(255) DEFAULT NULL,
  `placa_vehiculo` VARCHAR(255) DEFAULT NULL,
  `guia_remision` VARCHAR(255) DEFAULT NULL,
  `observaciones` VARCHAR(255) DEFAULT NULL,
  `estado` VARCHAR(255) NOT NULL DEFAULT 'Completado',
  `id_usuario` BIGINT NOT NULL,
  PRIMARY KEY (`id_despacho`),
  KEY `idx_despachos_producto` (`id_producto`),
  KEY `idx_despachos_ubicacion` (`id_ubicacion`),
  KEY `idx_despachos_usuario` (`id_usuario`),
  KEY `idx_despachos_fecha` (`fecha_salida`),
  KEY `idx_fecha_registro` (`fecha_registro`),
  CONSTRAINT `fk_despachos_producto`
    FOREIGN KEY (`id_producto`)
    REFERENCES `productos` (`id_producto`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_despachos_ubicacion`
    FOREIGN KEY (`id_ubicacion`)
    REFERENCES `ubicaciones` (`id_ubicacion`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_despachos_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `usuarios` (`id_usuario`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `chk_despachos_cantidad`
    CHECK (`cantidad` > 0)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `conteos` (
  `id_conteo` INT NOT NULL AUTO_INCREMENT,
  `id_producto` INT NOT NULL,
  `id_ubicacion` INT NOT NULL,
  `id_usuario` BIGINT NOT NULL,
  `cantidad_contada` INT NOT NULL,
  `fecha_conteo` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `observaciones` TEXT DEFAULT NULL,
  `estado` VARCHAR(20) NOT NULL DEFAULT 'Finalizado',
  PRIMARY KEY (`id_conteo`),
  KEY `idx_conteos_producto` (`id_producto`),
  KEY `idx_conteos_ubicacion` (`id_ubicacion`),
  KEY `idx_conteos_usuario` (`id_usuario`),
  KEY `idx_conteos_fecha` (`fecha_conteo`),
  CONSTRAINT `fk_conteos_producto`
    FOREIGN KEY (`id_producto`)
    REFERENCES `productos` (`id_producto`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_conteos_ubicacion`
    FOREIGN KEY (`id_ubicacion`)
    REFERENCES `ubicaciones` (`id_ubicacion`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_conteos_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `usuarios` (`id_usuario`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `chk_conteos_cantidad`
    CHECK (`cantidad_contada` >= 0),
  CONSTRAINT `chk_conteos_estado`
    CHECK (`estado` IN (
      'Pendiente',
      'Finalizado',
      'Aprobado',
      'Anulado'
    ))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

-- ============================================================
-- 5. DATOS MAESTROS INICIALES
-- ============================================================

START TRANSACTION;

INSERT INTO `categorias`
  (`id_categoria`, `nombre_categoria`, `descripcion`, `estado`)
VALUES
  (1, 'Gaseosas', 'Bebidas carbonatadas personales y familiares', 'Activo'),
  (2, 'Agua', 'Agua de mesa con y sin gas', 'Activo'),
  (3, 'Bebidas deportivas', 'Bebidas para hidratación deportiva', 'Activo'),
  (4, 'Jugos y néctares', 'Bebidas de fruta listas para consumir', 'Activo'),
  (5, 'Bebidas energéticas', 'Bebidas energizantes en presentación personal', 'Activo');

INSERT INTO `tipos`
  (`id_tipo`, `nombre_tipo`, `descripcion`, `estado`)
VALUES
  (1, 'Botella personal', 'Presentaciones de consumo individual', 'Activo'),
  (2, 'Botella familiar', 'Presentaciones familiares de un litro o más', 'Activo'),
  (3, 'Pack promocional', 'Paquetes de varias unidades', 'Activo'),
  (4, 'Lata', 'Presentaciones en lata', 'Activo');

INSERT INTO `tipos_movimiento`
  (`id_tipo_movimiento`, `nombre_tipo_movimiento`, `descripcion`, `estado`)
VALUES
  (1, 'Ingreso', 'Entrada de mercancía al almacén', 'Activo'),
  (2, 'Salida', 'Salida de mercancía del almacén', 'Activo'),
  (3, 'Ajuste', 'Regularización autorizada de inventario', 'Activo'),
  (4, 'Transferencia', 'Traslado entre ubicaciones', 'Activo');

INSERT INTO `tipos_orden`
  (`id_tipo_orden`, `nombre_tipo_orden`, `descripcion`, `estado`)
VALUES
  (1, 'Orden de ingreso', 'Orden asociada a recepción de mercancía', 'Activo'),
  (2, 'Orden de salida', 'Orden asociada a despacho de mercancía', 'Activo');

INSERT INTO `tipos_tercero`
  (`id_tipo_tercero`, `nombre_tipo_tercero`, `descripcion`, `estado`)
VALUES
  (1, 'Proveedor', 'Empresa que suministra bebidas', 'Activo'),
  (2, 'Cliente', 'Cliente que recibe productos', 'Activo');

INSERT INTO `ubicaciones`
  (`id_ubicacion`, `codigo_estante`, `pasillo`, `tipo_ubicacion`, `estado`)
VALUES
  (1, 'A-01', 'Pasillo A', 'Gaseosas personales', 'Activo'),
  (2, 'A-02', 'Pasillo A', 'Gaseosas familiares', 'Activo'),
  (3, 'A-03', 'Pasillo A', 'Gaseosas sin azúcar', 'Activo'),
  (4, 'B-01', 'Pasillo B', 'Agua', 'Activo'),
  (5, 'B-02', 'Pasillo B', 'Bebidas deportivas', 'Activo'),
  (6, 'B-03', 'Pasillo B', 'Jugos y néctares', 'Activo'),
  (7, 'C-01', 'Pasillo C', 'Alta rotación', 'Activo'),
  (8, 'C-02', 'Pasillo C', 'Stock de reserva', 'Activo'),
  (9, 'D-01', 'Pasillo D', 'Bebidas energéticas', 'Activo'),
  (10, 'D-02', 'Pasillo D', 'Recepción temporal', 'Activo');

-- Contraseña inicial para ambas cuentas: 123456
INSERT INTO `usuarios`
  (`id_usuario`, `nombre`, `correo`, `clave`, `rol`, `estado`)
VALUES
  (1, 'Administrador', 'admin@wms.com',
   '$2y$10$3t7WwZiOWAmYMep9Mu2MP.Hlm0xxATouugVEkjiGh/8VrOkJKsLY6',
   'ADMIN', 1),
  (2, 'Supervisor', 'supervisor@wms.com',
   '$2y$10$3t7WwZiOWAmYMep9Mu2MP.Hlm0xxATouugVEkjiGh/8VrOkJKsLY6',
   'SUPERVISOR', 1);

INSERT INTO `terceros`
  (`id_tercero`, `id_tipo_tercero`, `nombre`, `ruc`, `contacto`,
   `telefono`, `direccion`, `correo`, `estado`)
VALUES
  (1, 1, 'Proveedor principal de bebidas', '20111111111',
   'Área comercial', '999111111', 'Lima', 'ventas@proveedor.local', 'Activo'),
  (2, 2, 'Bodega San Martín', '20222222222',
   'Encargado de compras', '999222222', 'Lima', 'compras@bodega.local', 'Activo'),
  (3, 2, 'Minimarket El Sol', '20333333333',
   'Administración', '999333333', 'Lima', 'pedidos@minimarket.local', 'Activo');

-- Los nombres de marca son referenciales para un proyecto académico.
-- Códigos, precios y cantidades son datos simulados.
INSERT INTO `productos`
  (`id_producto`, `id_categoria`, `id_tipo`, `nombre`, `codigo`,
   `costo`, `precio_venta`, `stock_minimo`, `unidad_medida`,
   `stock_actual`, `estado`)
VALUES
  (1, 1, 1, 'Inca Kola Original 500 ml', 'INKA-500-ORI', 2.10, 3.00, 15, 'Botella', 0, 'Activo'),
  (2, 1, 1, 'Inca Kola Sin Azúcar 500 ml', 'INKA-500-ZERO', 2.20, 3.10, 10, 'Botella', 0, 'Activo'),
  (3, 1, 2, 'Inca Kola Original 1.5 L', 'INKA-15L-ORI', 4.50, 6.00, 12, 'Botella', 0, 'Activo'),
  (4, 1, 1, 'Coca-Cola Original 500 ml', 'COCA-500-ORI', 2.20, 3.20, 15, 'Botella', 0, 'Activo'),
  (5, 1, 1, 'Coca-Cola Sin Azúcar 500 ml', 'COCA-500-ZERO', 2.30, 3.30, 10, 'Botella', 0, 'Activo'),
  (6, 1, 2, 'Coca-Cola Original 1.5 L', 'COCA-15L-ORI', 4.70, 6.20, 12, 'Botella', 0, 'Activo'),
  (7, 1, 1, 'Sprite 500 ml', 'SPRITE-500', 2.10, 3.00, 12, 'Botella', 0, 'Activo'),
  (8, 1, 2, 'Sprite 1.5 L', 'SPRITE-15L', 4.30, 5.80, 10, 'Botella', 0, 'Activo'),
  (9, 1, 1, 'Fanta Naranja 500 ml', 'FANTA-500-NAR', 2.10, 3.00, 10, 'Botella', 0, 'Activo'),
  (10, 1, 2, 'Fanta Naranja 1.5 L', 'FANTA-15L-NAR', 4.30, 5.80, 10, 'Botella', 0, 'Activo'),
  (11, 1, 2, 'Kola Real Cola 3 L', 'KR-COLA-3L', 4.80, 6.50, 10, 'Botella', 0, 'Activo'),
  (12, 1, 2, 'Kola Real Naranja 3 L', 'KR-NAR-3L', 4.80, 6.50, 10, 'Botella', 0, 'Activo'),
  (13, 1, 1, 'Guaraná Backus 500 ml', 'GUARANA-500', 2.20, 3.20, 10, 'Botella', 0, 'Activo'),
  (14, 1, 2, 'Guaraná Backus 1.5 L', 'GUARANA-15L', 4.50, 6.00, 10, 'Botella', 0, 'Activo'),
  (15, 2, 1, 'Agua San Luis sin gas 625 ml', 'SLUIS-625-SG', 1.20, 2.00, 20, 'Botella', 0, 'Activo'),
  (16, 2, 2, 'Agua San Luis sin gas 2.5 L', 'SLUIS-25L-SG', 3.20, 4.50, 10, 'Botella', 0, 'Activo'),
  (17, 3, 1, 'Powerade Mountain Blast 500 ml', 'POW-MB-500', 2.80, 4.00, 10, 'Botella', 0, 'Activo'),
  (18, 3, 1, 'Powerade Frutas Tropicales 500 ml', 'POW-FT-500', 2.80, 4.00, 10, 'Botella', 0, 'Activo'),
  (19, 4, 2, 'Del Valle Durazno 1 L', 'DELVALLE-DUR-1L', 3.50, 5.00, 8, 'Botella', 0, 'Activo'),
  (20, 5, 4, 'Volt Green 300 ml', 'VOLT-GREEN-300', 2.20, 3.50, 12, 'Lata', 0, 'Activo');

-- Stock final de cada producto distribuido entre ubicaciones.
INSERT INTO `inventario_ubicaciones`
  (`id_producto`, `id_ubicacion`, `cantidad`)
VALUES
  (1, 1, 30), (1, 7, 25), (1, 8, 15),
  (2, 3, 18),
  (3, 2, 28), (3, 8, 12),
  (4, 1, 24), (4, 7, 48), (4, 8, 12),
  (5, 3, 20), (5, 8, 10),
  (6, 2, 36), (6, 8, 12),
  (7, 1, 32), (7, 7, 16),
  (8, 2, 20),
  (9, 1, 26),
  (11, 2, 22), (11, 8, 8),
  (12, 2, 16),
  (13, 1, 20), (13, 7, 10),
  (14, 2, 18),
  (15, 4, 50), (15, 7, 30),
  (16, 4, 24),
  (17, 5, 4),
  (18, 5, 14),
  (19, 6, 20),
  (20, 9, 6);

-- El stock total se deriva de la suma de todas las ubicaciones.
UPDATE `productos` p
SET p.`stock_actual` = (
  SELECT COALESCE(SUM(iu.`cantidad`), 0)
  FROM `inventario_ubicaciones` iu
  WHERE iu.`id_producto` = p.`id_producto`
);

-- Historial de ingresos. En cinco casos el ingreso es mayor al stock final
-- porque posteriormente existe una salida registrada.
INSERT INTO `movimientos`
  (`cantidad`, `fecha`, `tipo_movimiento`, `id_tipo_movimiento`,
   `id_producto`, `id_ubicacion`, `id_usuario`, `id_orden`,
   `observacion`, `estado`)
VALUES
  (40, CURRENT_TIMESTAMP - INTERVAL 25 DAY, 'Ingreso', 1, 1, 1, 1, NULL, 'Ingreso inicial de Inca Kola', 'Completado'),
  (25, CURRENT_TIMESTAMP - INTERVAL 24 DAY, 'Ingreso', 1, 1, 7, 2, NULL, 'Reposición de alta rotación', 'Completado'),
  (15, CURRENT_TIMESTAMP - INTERVAL 23 DAY, 'Ingreso', 1, 1, 8, 1, NULL, 'Stock de reserva', 'Completado'),
  (18, CURRENT_TIMESTAMP - INTERVAL 22 DAY, 'Ingreso', 1, 2, 3, 2, NULL, 'Ingreso de presentación sin azúcar', 'Completado'),
  (28, CURRENT_TIMESTAMP - INTERVAL 21 DAY, 'Ingreso', 1, 3, 2, 1, NULL, 'Ingreso de gaseosa familiar', 'Completado'),
  (12, CURRENT_TIMESTAMP - INTERVAL 20 DAY, 'Ingreso', 1, 3, 8, 2, NULL, 'Reserva de gaseosa familiar', 'Completado'),
  (24, CURRENT_TIMESTAMP - INTERVAL 19 DAY, 'Ingreso', 1, 4, 1, 1, NULL, 'Ingreso de Coca-Cola personal', 'Completado'),
  (60, CURRENT_TIMESTAMP - INTERVAL 18 DAY, 'Ingreso', 1, 4, 7, 2, NULL, 'Lote para alta rotación', 'Completado'),
  (12, CURRENT_TIMESTAMP - INTERVAL 17 DAY, 'Ingreso', 1, 4, 8, 1, NULL, 'Stock de reserva', 'Completado'),
  (20, CURRENT_TIMESTAMP - INTERVAL 16 DAY, 'Ingreso', 1, 5, 3, 2, NULL, 'Ingreso de bebida sin azúcar', 'Completado'),
  (10, CURRENT_TIMESTAMP - INTERVAL 15 DAY, 'Ingreso', 1, 5, 8, 1, NULL, 'Reserva sin azúcar', 'Completado'),
  (36, CURRENT_TIMESTAMP - INTERVAL 14 DAY, 'Ingreso', 1, 6, 2, 2, NULL, 'Ingreso de Coca-Cola familiar', 'Completado'),
  (12, CURRENT_TIMESTAMP - INTERVAL 13 DAY, 'Ingreso', 1, 6, 8, 1, NULL, 'Stock de reserva', 'Completado'),
  (32, CURRENT_TIMESTAMP - INTERVAL 12 DAY, 'Ingreso', 1, 7, 1, 2, NULL, 'Ingreso de Sprite personal', 'Completado'),
  (16, CURRENT_TIMESTAMP - INTERVAL 11 DAY, 'Ingreso', 1, 7, 7, 1, NULL, 'Reposición de alta rotación', 'Completado'),
  (20, CURRENT_TIMESTAMP - INTERVAL 10 DAY, 'Ingreso', 1, 8, 2, 2, NULL, 'Ingreso de Sprite familiar', 'Completado'),
  (26, CURRENT_TIMESTAMP - INTERVAL 9 DAY, 'Ingreso', 1, 9, 1, 1, NULL, 'Ingreso de Fanta personal', 'Completado'),
  (22, CURRENT_TIMESTAMP - INTERVAL 8 DAY, 'Ingreso', 1, 11, 2, 2, NULL, 'Ingreso de Kola Real Cola', 'Completado'),
  (8, CURRENT_TIMESTAMP - INTERVAL 8 DAY, 'Ingreso', 1, 11, 8, 1, NULL, 'Reserva de Kola Real Cola', 'Completado'),
  (16, CURRENT_TIMESTAMP - INTERVAL 7 DAY, 'Ingreso', 1, 12, 2, 2, NULL, 'Ingreso de Kola Real Naranja', 'Completado'),
  (20, CURRENT_TIMESTAMP - INTERVAL 7 DAY, 'Ingreso', 1, 13, 1, 1, NULL, 'Ingreso de Guaraná personal', 'Completado'),
  (10, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 'Ingreso', 1, 13, 7, 2, NULL, 'Reposición de Guaraná', 'Completado'),
  (18, CURRENT_TIMESTAMP - INTERVAL 6 DAY, 'Ingreso', 1, 14, 2, 1, NULL, 'Ingreso de Guaraná familiar', 'Completado'),
  (60, CURRENT_TIMESTAMP - INTERVAL 5 DAY, 'Ingreso', 1, 15, 4, 2, NULL, 'Ingreso de agua personal', 'Completado'),
  (30, CURRENT_TIMESTAMP - INTERVAL 5 DAY, 'Ingreso', 1, 15, 7, 1, NULL, 'Agua para alta rotación', 'Completado'),
  (24, CURRENT_TIMESTAMP - INTERVAL 4 DAY, 'Ingreso', 1, 16, 4, 2, NULL, 'Ingreso de agua familiar', 'Completado'),
  (12, CURRENT_TIMESTAMP - INTERVAL 4 DAY, 'Ingreso', 1, 17, 5, 1, NULL, 'Ingreso de bebida deportiva', 'Completado'),
  (14, CURRENT_TIMESTAMP - INTERVAL 3 DAY, 'Ingreso', 1, 18, 5, 2, NULL, 'Ingreso de Powerade tropical', 'Completado'),
  (20, CURRENT_TIMESTAMP - INTERVAL 3 DAY, 'Ingreso', 1, 19, 6, 1, NULL, 'Ingreso de bebida Del Valle', 'Completado'),
  (15, CURRENT_TIMESTAMP - INTERVAL 2 DAY, 'Ingreso', 1, 20, 9, 2, NULL, 'Ingreso de bebida energética', 'Completado');

-- Salidas coherentes con los despachos insertados a continuación.
INSERT INTO `movimientos`
  (`cantidad`, `fecha`, `tipo_movimiento`, `id_tipo_movimiento`,
   `id_producto`, `id_ubicacion`, `id_usuario`, `id_orden`,
   `observacion`, `estado`)
VALUES
  (10, CURRENT_TIMESTAMP - INTERVAL 4 DAY, 'Salida', 2, 1, 1, 1, NULL, 'Despacho | Destino: Bodega San Martín | Guía: T001-000001', 'Completado'),
  (12, CURRENT_TIMESTAMP - INTERVAL 3 DAY, 'Salida', 2, 4, 7, 2, NULL, 'Despacho | Destino: Minimarket El Sol | Guía: T001-000002', 'Completado'),
  (10, CURRENT_TIMESTAMP - INTERVAL 2 DAY, 'Salida', 2, 15, 4, 1, NULL, 'Despacho | Destino: Distribuciones Norte | Guía: T001-000003', 'Completado'),
  (8, CURRENT_TIMESTAMP - INTERVAL 1 DAY, 'Salida', 2, 17, 5, 2, NULL, 'Despacho | Destino: Gimnasio Energía | Guía: T001-000004', 'Completado'),
  (9, CURRENT_TIMESTAMP - INTERVAL 6 HOUR, 'Salida', 2, 20, 9, 1, NULL, 'Despacho | Destino: Market Central | Guía: T001-000005', 'Completado');

INSERT INTO `despachos`
  (`id_producto`, `id_ubicacion`, `cantidad`, `fecha_salida`,
   `empresa_destino`, `conductor`, `placa_vehiculo`, `guia_remision`,
   `observaciones`, `estado`, `id_usuario`)
VALUES
  (1, 1, 10, CURRENT_TIMESTAMP - INTERVAL 4 DAY,
   'Bodega San Martín', 'Carlos Mendoza', 'BCA-321', 'T001-000001',
   'Entrega programada de gaseosas personales', 'Completado', 1),
  (4, 7, 12, CURRENT_TIMESTAMP - INTERVAL 3 DAY,
   'Minimarket El Sol', 'Luis Ramírez', 'ABC-456', 'T001-000002',
   'Pedido de alta rotación', 'Completado', 2),
  (15, 4, 10, CURRENT_TIMESTAMP - INTERVAL 2 DAY,
   'Distribuciones Norte', 'José Torres', 'D8H-762', 'T001-000003',
   'Entrega de agua sin gas', 'Completado', 1),
  (17, 5, 8, CURRENT_TIMESTAMP - INTERVAL 1 DAY,
   'Gimnasio Energía', 'Mario López', 'F5R-908', 'T001-000004',
   'Salida de bebida deportiva', 'Completado', 2),
  (20, 9, 9, CURRENT_TIMESTAMP - INTERVAL 6 HOUR,
   'Market Central', 'Ana Pérez', 'G7T-114', 'T001-000005',
   'Salida parcial de bebida energética', 'Completado', 1);

COMMIT;

-- ============================================================
-- 6. PROCEDIMIENTOS ALMACENADOS
-- Se omite DEFINER para facilitar la instalación en otros equipos.
-- ============================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS `sp_BuscarUsuarioPorId`$$
CREATE PROCEDURE `sp_BuscarUsuarioPorId`(
  IN p_id_usuario BIGINT
)
BEGIN
  SELECT
    `id_usuario`,
    `nombre`,
    `correo`,
    `rol`,
    `estado`
  FROM `usuarios`
  WHERE `id_usuario` = p_id_usuario;
END$$

DROP PROCEDURE IF EXISTS `sp_EditarUsuario`$$
CREATE PROCEDURE `sp_EditarUsuario`(
  IN p_id_usuario BIGINT,
  IN p_nombre VARCHAR(255),
  IN p_correo VARCHAR(255),
  IN p_clave VARCHAR(255),
  IN p_rol VARCHAR(50),
  IN p_estado TINYINT
)
BEGIN
  UPDATE `usuarios`
  SET
    `nombre` = p_nombre,
    `correo` = p_correo,
    `clave` = CASE
                WHEN p_clave IS NULL OR TRIM(p_clave) = ''
                  THEN `clave`
                ELSE p_clave
              END,
    `rol` = p_rol,
    `estado` = p_estado
  WHERE `id_usuario` = p_id_usuario;
END$$

DROP PROCEDURE IF EXISTS `sp_InsertarUsuario`$$
CREATE PROCEDURE `sp_InsertarUsuario`(
  IN p_nombre VARCHAR(255),
  IN p_correo VARCHAR(255),
  IN p_clave VARCHAR(255),
  IN p_rol VARCHAR(50)
)
BEGIN
  INSERT INTO `usuarios`
    (`nombre`, `correo`, `clave`, `rol`, `estado`)
  VALUES
    (p_nombre, p_correo, p_clave, p_rol, 1);
END$$

DROP PROCEDURE IF EXISTS `sp_ListarUsuariosActivos`$$
CREATE PROCEDURE `sp_ListarUsuariosActivos`()
BEGIN
  SELECT
    `id_usuario`,
    `nombre`,
    `correo`,
    `rol`,
    `estado`
  FROM `usuarios`
  WHERE `estado` = 1
  ORDER BY `nombre`;
END$$

DELIMITER ;

-- ============================================================
-- 7. RESTAURAR CONFIGURACIÓN DE LA SESIÓN
-- ============================================================

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;

-- Verificación rápida:
-- SHOW TABLES;
-- SELECT * FROM usuarios;
-- SELECT * FROM tipos_movimiento;
-- SELECT * FROM productos ORDER BY id_producto;
-- SELECT * FROM inventario_ubicaciones ORDER BY id_producto, id_ubicacion;
-- SELECT * FROM movimientos ORDER BY fecha DESC;
-- SELECT * FROM password_reset_tokens;

