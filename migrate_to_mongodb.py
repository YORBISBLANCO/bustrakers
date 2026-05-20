#!/usr/bin/env python3
"""
Script para migrar datos de MySQL a MongoDB
Uso: python3 migrate_mysql_to_mongodb.py
Requisitos: mysql-connector-python, pymongo
pip install mysql-connector-python pymongo
"""

import mysql.connector
from pymongo import MongoClient
from datetime import datetime
import sys

# Configuración MySQL
MYSQL_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'Root',
    'database': 'yorbisbd'
}

# Configuración MongoDB
MONGO_URI = 'mongodb://localhost:27017/proyectobd'

def migrate_usuarios():
    """Migra tabla usuarios a colección MongoDB"""
    try:
        mysql_conn = mysql.connector.connect(**MYSQL_CONFIG)
        mysql_cursor = mysql_conn.cursor(dictionary=True)
        
        mongo_client = MongoClient(MONGO_URI)
        mongo_db = mongo_client['proyectobd']
        usuarios_collection = mongo_db['usuarios']
        
        # Limpiar colección existente
        usuarios_collection.delete_many({})
        
        # Leer de MySQL
        mysql_cursor.execute("SELECT * FROM usuario")
        usuarios = mysql_cursor.fetchall()
        
        # Insertar en MongoDB
        for usuario in usuarios:
            mongo_usuario = {
                'username': usuario.get('username'),
                'password': usuario.get('password'),
                'email': usuario.get('email'),
                'nombres': usuario.get('nombres'),
                'apellidos': usuario.get('apellidos'),
                'role': usuario.get('role', 'ROLE_USER'),
                'fechaRegistro': int(datetime.now().timestamp() * 1000),
                'activo': True
            }
            usuarios_collection.insert_one(mongo_usuario)
        
        print(f"✅ {len(usuarios)} usuarios migrados")
        
        mysql_cursor.close()
        mysql_conn.close()
        mongo_client.close()
        
    except Exception as e:
        print(f"❌ Error migrando usuarios: {e}")
        return False
    
    return True

def migrate_rutas():
    """Migra tabla rutas a colección MongoDB"""
    try:
        mysql_conn = mysql.connector.connect(**MYSQL_CONFIG)
        mysql_cursor = mysql_conn.cursor(dictionary=True)
        
        mongo_client = MongoClient(MONGO_URI)
        mongo_db = mongo_client['proyectobd']
        rutas_collection = mongo_db['rutas']
        
        # Limpiar colección
        rutas_collection.delete_many({})
        
        # Leer de MySQL
        mysql_cursor.execute("""
            SELECT id, nombre, horaAproximada 
            FROM ruta
        """)
        rutas = mysql_cursor.fetchall()
        
        # Insertar en MongoDB
        for ruta in rutas:
            mongo_ruta = {
                'nombre': ruta.get('nombre'),
                'horaAproximada': str(ruta.get('horaAproximada')) if ruta.get('horaAproximada') else None,
                'barrios': [],  # Necesita query adicional o actualización manual
                'fechaCreacion': int(datetime.now().timestamp() * 1000),
                'activa': True
            }
            rutas_collection.insert_one(mongo_ruta)
        
        print(f"✅ {len(rutas)} rutas migradas")
        
        mysql_cursor.close()
        mysql_conn.close()
        mongo_client.close()
        
    except Exception as e:
        print(f"❌ Error migrando rutas: {e}")
        return False
    
    return True

def migrate_buses():
    """Migra tabla buses a colección MongoDB"""
    try:
        mysql_conn = mysql.connector.connect(**MYSQL_CONFIG)
        mysql_cursor = mysql_conn.cursor(dictionary=True)
        
        mongo_client = MongoClient(MONGO_URI)
        mongo_db = mongo_client['proyectobd']
        buses_collection = mongo_db['buses']
        
        # Limpiar colección
        buses_collection.delete_many({})
        
        # Leer de MySQL
        mysql_cursor.execute("""
            SELECT b.id, b.placa, b.modelo, b.color, b.conductor, r.id as ruta_id, r.nombre as ruta_nombre
            FROM bus b
            LEFT JOIN ruta r ON b.ruta_id = r.id
        """)
        buses = mysql_cursor.fetchall()
        
        # Insertar en MongoDB
        for bus in buses:
            mongo_bus = {
                'placa': bus.get('placa'),
                'modelo': bus.get('modelo'),
                'color': bus.get('color'),
                'conductor': bus.get('conductor'),
                'rutaId': str(bus.get('ruta_id')) if bus.get('ruta_id') else None,
                'rutaNombre': bus.get('ruta_nombre'),
                'estado': 'activo',
                'fechaRegistro': int(datetime.now().timestamp() * 1000),
                'activo': True
            }
            buses_collection.insert_one(mongo_bus)
        
        print(f"✅ {len(buses)} buses migrados")
        
        mysql_cursor.close()
        mysql_conn.close()
        mongo_client.close()
        
    except Exception as e:
        print(f"❌ Error migrando buses: {e}")
        return False
    
    return True

def migrate_paradas():
    """Migra tabla paradas a colección MongoDB"""
    try:
        mysql_conn = mysql.connector.connect(**MYSQL_CONFIG)
        mysql_cursor = mysql_conn.cursor(dictionary=True)
        
        mongo_client = MongoClient(MONGO_URI)
        mongo_db = mongo_client['proyectobd']
        paradas_collection = mongo_db['paradas']
        
        # Limpiar colección
        paradas_collection.delete_many({})
        
        # Leer de MySQL
        mysql_cursor.execute("""
            SELECT p.id, p.nombre, p.ubicacion, p.referencia, p.orden, r.id as ruta_id, r.nombre as ruta_nombre
            FROM parada p
            LEFT JOIN ruta r ON p.ruta_id = r.id
        """)
        paradas = mysql_cursor.fetchall()
        
        # Insertar en MongoDB
        for parada in paradas:
            mongo_parada = {
                'nombre': parada.get('nombre'),
                'ubicacion': parada.get('ubicacion'),
                'referencia': parada.get('referencia'),
                'orden': parada.get('orden'),
                'rutaId': str(parada.get('ruta_id')) if parada.get('ruta_id') else None,
                'rutaNombre': parada.get('ruta_nombre'),
                'fechaCreacion': int(datetime.now().timestamp() * 1000),
                'activa': True
            }
            paradas_collection.insert_one(mongo_parada)
        
        print(f"✅ {len(paradas)} paradas migradas")
        
        mysql_cursor.close()
        mysql_conn.close()
        mongo_client.close()
        
    except Exception as e:
        print(f"❌ Error migrando paradas: {e}")
        return False
    
    return True

def migrate_barrios():
    """Migra tabla barrios a colección MongoDB"""
    try:
        mysql_conn = mysql.connector.connect(**MYSQL_CONFIG)
        mysql_cursor = mysql_conn.cursor(dictionary=True)
        
        mongo_client = MongoClient(MONGO_URI)
        mongo_db = mongo_client['proyectobd']
        barrios_collection = mongo_db['barrios']
        
        # Limpiar colección
        barrios_collection.delete_many({})
        
        # Leer de MySQL
        mysql_cursor.execute("SELECT * FROM barrio")
        barrios = mysql_cursor.fetchall()
        
        # Insertar en MongoDB
        for barrio in barrios:
            mongo_barrio = {
                'nombre': barrio.get('nombre'),
                'localidad': barrio.get('localidad'),
                'latitud': barrio.get('latitud'),
                'longitud': barrio.get('longitud'),
                'fechaCreacion': int(datetime.now().timestamp() * 1000),
                'activo': True
            }
            barrios_collection.insert_one(mongo_barrio)
        
        print(f"✅ {len(barrios)} barrios migrados")
        
        mysql_cursor.close()
        mysql_conn.close()
        mongo_client.close()
        
    except Exception as e:
        print(f"❌ Error migrando barrios: {e}")
        return False
    
    return True

def migrate_contactos():
    """Migra tabla contacto_mensaje a colección MongoDB"""
    try:
        mysql_conn = mysql.connector.connect(**MYSQL_CONFIG)
        mysql_cursor = mysql_conn.cursor(dictionary=True)
        
        mongo_client = MongoClient(MONGO_URI)
        mongo_db = mongo_client['proyectobd']
        contactos_collection = mongo_db['contacto_mensajes']
        
        # Limpiar colección
        contactos_collection.delete_many({})
        
        # Leer de MySQL
        mysql_cursor.execute("SELECT * FROM contacto_mensaje")
        contactos = mysql_cursor.fetchall()
        
        # Insertar en MongoDB
        for contacto in contactos:
            mongo_contacto = {
                'nombre': contacto.get('nombre'),
                'apellido': contacto.get('apellido'),
                'telefono': contacto.get('telefono'),
                'email': contacto.get('email'),
                'mensaje': contacto.get('mensaje'),
                'fechaCreacion': int(datetime.now().timestamp() * 1000),
                'leido': False
            }
            contactos_collection.insert_one(mongo_contacto)
        
        print(f"✅ {len(contactos)} mensajes de contacto migrados")
        
        mysql_cursor.close()
        mysql_conn.close()
        mongo_client.close()
        
    except Exception as e:
        print(f"❌ Error migrando contactos: {e}")
        return False
    
    return True

def main():
    """Ejecuta todas las migraciones"""
    print("=" * 60)
    print("🚀 MIGRACIÓN MYSQL → MONGODB")
    print("=" * 60)
    
    print("\n📋 Iniciando migración de datos...")
    
    migration_functions = [
        ('Usuarios', migrate_usuarios),
        ('Rutas', migrate_rutas),
        ('Buses', migrate_buses),
        ('Paradas', migrate_paradas),
        ('Barrios', migrate_barrios),
        ('Contactos', migrate_contactos),
    ]
    
    success_count = 0
    for name, func in migration_functions:
        print(f"\n🔄 Migrando {name}...")
        if func():
            success_count += 1
    
    print("\n" + "=" * 60)
    print(f"✅ Migración completada: {success_count}/{len(migration_functions)} tablas")
    print("=" * 60)

if __name__ == '__main__':
    main()
