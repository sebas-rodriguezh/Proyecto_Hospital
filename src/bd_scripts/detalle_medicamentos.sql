USE sistema_hospital;

CREATE TABLE detalle_medicamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receta_id VARCHAR(20) NOT NULL,
    medicamento_codigo VARCHAR(20) NOT NULL,
    id_detalle VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    duracion INT NOT NULL CHECK (duracion > 0),
    indicacion TEXT NOT NULL,

    FOREIGN KEY (receta_id) REFERENCES recetas(id) ON DELETE CASCADE,
    FOREIGN KEY (medicamento_codigo) REFERENCES medicamentos(codigo) ON DELETE CASCADE,
    UNIQUE KEY unique_detalle_receta (receta_id, id_detalle)
);


USE sistema_hospital;
SELECT * FROM detalle_medicamentos; 