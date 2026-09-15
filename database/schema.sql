-- ============================================
-- Tabla de productos
-- ============================================

CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    sku         VARCHAR(100) NOT NULL UNIQUE,
    nombre      VARCHAR(100) NOT NULL,
    precio      NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    cantidad    INTEGER NOT NULL DEFAULT 0 CHECK (cantidad >= 0),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================
-- Función: validar_sku_unico
-- Verifica si un sku ya existe antes de insertar
-- ============================================
CREATE OR REPLACE FUNCTION validar_sku_unico(p_sku VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN NOT EXISTS (
        SELECT 1 FROM products WHERE sku = p_sku
    );
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- Función: actualizar_stock
-- Modifica la cantidad de un producto,
-- evitando que el stock quede en negativo
-- ============================================
CREATE OR REPLACE FUNCTION actualizar_stock(p_product_id BIGINT, p_cantidad_delta INTEGER)
RETURNS VOID AS $$
DECLARE
    v_cantidad_actual INTEGER;
BEGIN
    SELECT cantidad INTO v_cantidad_actual FROM products WHERE id = p_product_id;

    IF v_cantidad_actual IS NULL THEN
        RAISE EXCEPTION 'Producto con id % no existe', p_product_id;
    END IF;

    IF (v_cantidad_actual + p_cantidad_delta) < 0 THEN
        RAISE EXCEPTION 'Stock insuficiente: cantidad actual %, se intentó restar %', v_cantidad_actual, p_cantidad_delta;
    END IF;

    UPDATE products
    SET cantidad = cantidad + p_cantidad_delta,
        updated_at = NOW()
    WHERE id = p_product_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- Tabla de usuarios
-- ============================================

CREATE TABLE users (
	id			BIGSERIAL PRIMARY KEY,
	username    VARCHAR(50) NOT NULL UNIQUE,
	password	VARCHAR(255) NOT NULL,
	role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'LECTOR'))
);