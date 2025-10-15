USE sistema_hospital;

CREATE TABLE medicamentos (
    codigo VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    presentacion VARCHAR(100) NOT NULL
);

USE sistema_hospital;
SELECT * FROM medicamentos; 