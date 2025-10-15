USE sistema_hospital;

CREATE TABLE personal (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    clave VARCHAR(100) NOT NULL,
    tipo ENUM('Medico', 'Administrador', 'Farmaceuta') NOT NULL,
    especialidad VARCHAR(50) NULL
);

INSERT INTO personal (id, nombre, clave, tipo, especialidad) VALUES
('admin', 'Administrador Principal', '1234', 'Administrador', NULL),
('1111', 'Dr. Juan Herrera', '1111', 'Medico', 'Cardiologia'),
('2222', 'Carlos Rodríguez', '2222', 'Farmaceuta', NULL);

USE sistema_hospital;
SELECT * FROM personal; 