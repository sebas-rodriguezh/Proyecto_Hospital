USE sistema_hospital;

CREATE TABLE pacientes (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    fecha_nacimiento DATE NOT NULL
);

USE sistema_hospital;
SELECT * FROM pacientes; 