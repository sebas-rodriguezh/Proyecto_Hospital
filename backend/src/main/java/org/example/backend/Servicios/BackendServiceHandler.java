package org.example.backend.Servicios;

import org.example.backend.Logica.*;
import org.example.proyectohospital.Modelo.Medicamento;
import org.example.proyectohospital.Modelo.Paciente;
import org.example.proyectohospital.Modelo.Personal;
import org.example.proyectohospital.Modelo.Receta;
import org.example.proyectohospital.shared.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class BackendServiceHandler implements Runnable {
    private final Socket socket;
    private final GestorRecetas gestorRecetas;
    private final GestorPersonal gestorPersonal;
    private final GestorPacientes gestorPacientes;
    private final GestorMedicamentos gestorMedicamentos;

    public BackendServiceHandler(Socket socket) {
        this.socket = socket;
        this.gestorRecetas = new GestorRecetas();
        this.gestorPersonal = new GestorPersonal();
        this.gestorPacientes = new GestorPacientes();
        this.gestorMedicamentos = new GestorMedicamentos();
    }

    @Override
    public void run() {
        try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

            while (true) {
                SolicitudBackend solicitud = (SolicitudBackend) input.readObject();
                Object respuesta = procesarSolicitud(solicitud);
                output.writeObject(respuesta);
                output.flush();
            }
        } catch (EOFException e) {
            System.out.println("Solicitud del frontend al backend procesada correctamente");

        } catch (Exception e) {
            System.err.println("Error procesando solicitud: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Object procesarSolicitud(SolicitudBackend solicitud) {
        try {
            switch (solicitud.getTipo()) {
                case "INSERTAR_RECETA":
                    Receta receta = (Receta) solicitud.getParametros().get("receta");
                    boolean guardada = gestorRecetas.insertarReceta(receta);
                    return new RespuestaBackend(guardada, guardada ? "Receta guardada" : "Error al guardar");

                case "OBTENER_MEDICAMENTOS":
                    List<Medicamento> medicamentos = gestorMedicamentos.findAll();
                    return new RespuestaBackend(true, "Medicamentos obtenidos", medicamentos);

                case "OBTENER_PACIENTES":
                    List<Paciente> pacientes = gestorPacientes.findAll();
                    return new RespuestaBackend(true, "Pacientes obtenidos", pacientes);

                case "LOGIN":
                    try {
                        String id = (String) solicitud.getParametros().get("id");
                        String clave = (String) solicitud.getParametros().get("clave");
                        Personal personal = gestorPersonal.verificarCredenciales(id, clave);
                        return new RespuestaBackend(personal != null,
                                personal != null ? "Login exitoso" : "Credenciales inválidas", personal);

                    } catch (Exception e) {
                        System.err.println("ERROR en caso LOGIN: " + e.getMessage());
                        e.printStackTrace();
                        return new RespuestaBackend(false, "Error en login: " + e.getMessage());
                    }

                case "OBTENER_MEDICOS":
                    List<Personal> medicos = gestorPersonal.obtenerPersonalPorTipo("Medico");
                    return new RespuestaBackend(true, "Médicos obtenidos", medicos);

                case "OBTENER_RECETAS_POR_ESTADO":
                    int estado = (Integer) solicitud.getParametros().get("estado");
                    List<Receta> recetasEstado = gestorRecetas.obtenerRecetasPorEstado(estado);
                    return new RespuestaBackend(true, "Recetas por estado obtenidas", recetasEstado);

                case "OBTENER_RECETAS":
                    List<Receta> todasRecetas = gestorRecetas.findAll();
                    return new RespuestaBackend(true, "Todas las recetas obtenidas", todasRecetas);

                case "VERIFICAR_ID_PERSONAL":
                    String idVerificar = (String) solicitud.getParametros().get("id");
                    boolean existe = gestorPersonal.existePersonalConEseID(idVerificar);
                    return new RespuestaBackend(true, "Verificación completada", existe);

                case "INSERTAR_PERSONAL":
                    Personal personalNuevo = (Personal) solicitud.getParametros().get("personal");
                    Personal personalCreado = gestorPersonal.create(personalNuevo);
                    return new RespuestaBackend(personalCreado != null,
                            personalCreado != null ? "Personal creado" : "Error al crear personal", personalCreado);

                case "ACTUALIZAR_PERSONAL":
                    Personal personalActualizar = (Personal) solicitud.getParametros().get("personal");
                    Personal personalActualizado = gestorPersonal.update(personalActualizar);
                    return new RespuestaBackend(personalActualizado != null,
                            personalActualizado != null ? "Personal actualizado" : "Error al actualizar", personalActualizado);

                case "ELIMINAR_PERSONAL":
                    String idEliminar = (String) solicitud.getParametros().get("id");
                    boolean eliminado = gestorPersonal.deleteById(idEliminar);
                    return new RespuestaBackend(eliminado,
                            eliminado ? "Personal eliminado" : "Error al eliminar personal");

                case "ACTUALIZAR_ESTADO_RECETA":
                    String idReceta = (String) solicitud.getParametros().get("idReceta");
                    int nuevoEstado = (Integer) solicitud.getParametros().get("nuevoEstado");
                    boolean estadoActualizado = gestorRecetas.actualizarEstadoReceta(idReceta, nuevoEstado);
                    return new RespuestaBackend(estadoActualizado,
                            estadoActualizado ? "Estado actualizado" : "Error al actualizar estado");

                case "OBTENER_FARMACEUTAS":
                    List<Personal> farmaceutas = gestorPersonal.obtenerPersonalPorTipo("Farmaceuta");
                    return new RespuestaBackend(true, "Farmacéuticos obtenidos", farmaceutas);

                case "OBTENER_DETALLES_RECETA":
                     String idRecetaDetalle = (String) solicitud.getParametros().get("idReceta");
                     Receta recetaParaDetalle = gestorRecetas.getRecetaPorID(idRecetaDetalle);
                    if (recetaParaDetalle != null) {
                        return new RespuestaBackend(true, "Detalles obtenidos", recetaParaDetalle.getDetalleMedicamentos());
                    } else {
                        return new RespuestaBackend(false, "Receta no encontrada");
                    }

                case "VERIFICAR_CODIGO_MEDICAMENTO":
                    String codigo = (String) solicitud.getParametros().get("codigo");
                    boolean existeMedicamento = gestorMedicamentos.existeMedicamentoConEseCodigo(codigo);
                    return new RespuestaBackend(true, "Verificación completada", existeMedicamento);

                case "INSERTAR_MEDICAMENTO":
                    Medicamento medicamentoNuevo = (Medicamento) solicitud.getParametros().get("medicamento");
                    boolean medicamentoCreado = gestorMedicamentos.insertarMedicamento(medicamentoNuevo);
                    return new RespuestaBackend(medicamentoCreado,
                            medicamentoCreado ? "Medicamento creado" : "Error al crear medicamento");

                case "ACTUALIZAR_MEDICAMENTO":
                    Medicamento medicamentoActualizar = (Medicamento) solicitud.getParametros().get("medicamento");
                    Medicamento medicamentoActualizado = gestorMedicamentos.update(medicamentoActualizar);
                    return new RespuestaBackend(medicamentoActualizado != null,
                            medicamentoActualizado  != null ? "Medicamento actualizado" : "Error al actualizar medicamento");

                case "ELIMINAR_MEDICAMENTO":
                    String codigoEliminar = (String) solicitud.getParametros().get("codigo");
                    boolean medicamentoEliminado = gestorMedicamentos.eliminar(codigoEliminar);
                    return new RespuestaBackend(medicamentoEliminado,
                            medicamentoEliminado ? "Medicamento eliminado" : "Error al eliminar medicamento");

                case "INSERTAR_PACIENTE":
                    Paciente pacienteNuevo = (Paciente) solicitud.getParametros().get("paciente");
                    boolean pacienteCreado = gestorPacientes.insertarPaciente(pacienteNuevo, false);
                    return new RespuestaBackend(pacienteCreado,
                            pacienteCreado ? "Paciente creado" : "Error al crear paciente");

                case "ACTUALIZAR_PACIENTE":
                    Paciente pacienteActualizar = (Paciente) solicitud.getParametros().get("paciente");
                    Paciente pacienteActualizado = gestorPacientes.update(pacienteActualizar);
                    return new RespuestaBackend(pacienteActualizado != null,
                            pacienteActualizado != null ? "Paciente actualizado" : "Error al actualizar paciente");

                case "ELIMINAR_PACIENTE":
                    String idPacienteEliminar = (String) solicitud.getParametros().get("id");
                    boolean pacienteEliminado = gestorPacientes.eliminar(idPacienteEliminar);
                    return new RespuestaBackend(pacienteEliminado,
                            pacienteEliminado ? "Paciente eliminado" : "Error al eliminar paciente");

                case "CAMBIAR_CLAVE":
                    String idUsuario = (String) solicitud.getParametros().get("id");
                    String claveActual = (String) solicitud.getParametros().get("claveActual");
                    String nuevaClave = (String) solicitud.getParametros().get("nuevaClave");

                    boolean claveCambiada = gestorPersonal.cambiarClave(idUsuario, claveActual, nuevaClave);
                    return new RespuestaBackend(claveCambiada,
                            claveCambiada ? "Clave cambiada" : "Error al cambiar clave");


                default:
                    return new RespuestaBackend(false, "Tipo de solicitud no válido: " + solicitud.getTipo());
            }
        } catch (Exception e) {
            return new RespuestaBackend(false, "Error: " + e.getMessage());
        }
    }
}