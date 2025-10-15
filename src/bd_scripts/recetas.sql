USE sistema_hospital;

CREATE TABLE recetas (
    id VARCHAR(20) PRIMARY KEY,
    personal_id VARCHAR(20) NOT NULL,
    paciente_id VARCHAR(20) NOT NULL,
    fecha_prescripcion DATE NOT NULL,
    fecha_retiro DATE NOT NULL,
    estado ENUM('1', '2', '3', '4') NOT NULL DEFAULT '1',

    FOREIGN KEY (personal_id) REFERENCES personal(id) ON DELETE CASCADE,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE
);

USE sistema_hospital;
SELECT * FROM recetas; 
