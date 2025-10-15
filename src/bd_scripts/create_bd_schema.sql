CREATE DATABASE IF NOT EXISTS sistema_hospital;
USE sistema_hospital;

CREATE INDEX idx_personal_nombre ON personal(nombre);
CREATE INDEX idx_personal_tipo ON personal(tipo);
CREATE INDEX idx_pacientes_nombre ON pacientes(nombre);
CREATE INDEX idx_medicamentos_nombre ON medicamentos(nombre);
CREATE INDEX idx_recetas_personal ON recetas(personal_id);
CREATE INDEX idx_recetas_paciente ON recetas(paciente_id);
CREATE INDEX idx_recetas_estado ON recetas(estado);
CREATE INDEX idx_recetas_fecha ON recetas(fecha_prescripcion);
CREATE INDEX idx_detalle_medicamento ON detalle_medicamentos(medicamento_codigo);
