-- Datos de ejemplo
INSERT INTO products (sku, nombre, precio, cantidad) VALUES
('SKU-001', 'Laptop HP 15 pulgadas', 12500.00, 15),
('SKU-002', 'Mouse inalámbrico Logitech', 350.50, 80),
('SKU-003', 'Teclado mecánico RGB', 899.99, 30),
('SKU-004', 'Monitor 24 pulgadas Full HD', 3200.00, 10),
('SKU-005', 'Audífonos Bluetooth', 599.00, 3),
('SKU-006', 'Silla ergonómica de oficina', 4500.00, 0);

-- Usuarios de prueba (contraseñas en texto plano documentadas en el README)
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$GOu0EqLy9k0qo3k74HbUNuV.T1uFyq2cA/3beMac8flWA43HMghm2', 'ADMIN'),
('lector', '$2a$10$LkgO9AcXoVs/HAGGufyTYuTt8DlYnAkhxzKzaeFvH0FWtE5YbU2z.', 'LECTOR');