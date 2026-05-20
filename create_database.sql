-- Script para crear la base de datos MySQL
CREATE DATABASE IF NOT EXISTS proyectobd;
USE proyectobd;

-- Crear tabla admin_code si no existe
CREATE TABLE IF NOT EXISTS admin_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar códigos de administrador por defecto
INSERT IGNORE INTO admin_code (codigo) VALUES
('ADMIN2026'),
('ADMIN2025'),
('ADMIN2024'),
('MASTERADMIN'),
('SUPERADMIN');

-- Crear tabla usuario si no existe (para referencia)
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(255),
    apellidos VARCHAR(255),
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'ROLE_USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Aquí puedes agregar las tablas si es necesario
-- Por ejemplo:
-- CREATE TABLE ruta (...);