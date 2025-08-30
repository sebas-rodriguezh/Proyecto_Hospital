package org.example.proyectohospital;

import org.example.proyectohospital.Logica.*;
import org.example.proyectohospital.Modelo.*;

import java.time.LocalDate;

class Main {
    public static void main(String[] args) {
        Hospital hospi=Hospital.getInstance();
       Paciente paciente = new Paciente(70599270, java.time.LocalDate.of(2005,11,17),"Alejandro","119510334");
       Paciente paciente2 = new Paciente(88276784, java.time.LocalDate.of(2006,1,15),"Isa","7465443");
       Paciente paciente3 = new Paciente(70599270, java.time.LocalDate.of(2012,4,7),"gabriel","486543");

       Medico medico = new Medico("Sebas","11951444","132456","Cirujano");
       Medico medico2 = new Medico("Amparo","15454313","22222","X");
       Medico medico3 = new Medico("Fabiola","151444","33333","Y");
       Administrador admin1= new Administrador("Roberto","159951","4444");
       Farmaceuta farmaceuta= new Farmaceuta("Sofia","147258","1236547899");

       Receta receta= new Receta("1",medico,paciente,LocalDate.now(),LocalDate.of(2026,10,8),1);
       Receta receta2= new Receta("2",medico2,paciente2,LocalDate.now(),LocalDate.of(2027,4,2),2);
       Receta receta3= new Receta("3",medico3,paciente3,LocalDate.now(),LocalDate.of(2028,3,20),3);
       Medicamento medicamento= new Medicamento("aspirina","100mg","123");
       Medicamento medicamento1= new Medicamento("Gravol","Frasco liqyuido","321");
       DetalleMedicamento detalleMedicamento2= new DetalleMedicamento(medicamento1,"654321",4,1,"Tomar solamente cuando hay mareos");
       DetalleMedicamento detalleMedicamento= new DetalleMedicamento(medicamento,"123456",8,4,"Tomar por 4 dias cada noche");

       Hospital.getInstance().getRecetas().insertarReceta(receta);
       Hospital.getInstance().getRecetas().insertarReceta(receta2);
       Hospital.getInstance().getRecetas().insertarReceta(receta3);


       Hospital.getInstance().getRecetas().agregarDetalle("1",detalleMedicamento);
       Hospital.getInstance().getRecetas().agregarDetalle("1",detalleMedicamento2);
       Hospital.getInstance().getRecetas().agregarDetalle("2",detalleMedicamento2);
       Hospital.getInstance().getRecetas().agregarDetalle("3",detalleMedicamento);

        GestorPacientes GestorPacientes = new GestorPacientes();
        GestorPersonal GestorPersonales=new GestorPersonal();

        GestorPacientes.insertarPaciente(paciente,GestorPersonales.existePersonalConEseID(paciente.getId()));
        GestorPacientes.insertarPaciente(paciente2,GestorPersonales.existePersonalConEseID(paciente2.getId()));
        GestorPacientes.insertarPaciente(paciente3,GestorPersonales.existePersonalConEseID(paciente3.getId()));

        GestorMedicamentos GestorMedicamentos=new GestorMedicamentos();
        GestorMedicamentos.insertarMedicamento(medicamento);
        GestorMedicamentos.insertarMedicamento(medicamento1);


        GestorPersonales.insertarPersonal(medico, GestorPacientes.existeAlguienConEseID(medico.getId()));
        GestorPersonales.insertarPersonal(medico2,GestorPacientes.existeAlguienConEseID(medico2.getId()));
        GestorPersonales.insertarPersonal(medico3,GestorPacientes.existeAlguienConEseID(medico3.getId()));
        GestorPersonales.insertarPersonal(admin1,GestorPacientes.existeAlguienConEseID(admin1.getId()));
        GestorPersonales.insertarPersonal(farmaceuta,GestorPacientes.existeAlguienConEseID(farmaceuta.getId()));


        hospi.setMedicamentos(GestorMedicamentos);
        hospi.setPacientes(GestorPacientes);
        hospi.setPersonal(GestorPersonales);

        System.out.println(hospi.getRecetas().mostrarTodasLasRecetas());
        System.out.println(hospi.getMedicamentos().mostrarTodosLosMedicamentos());
        System.out.println(hospi.getPacientes().mostrarTodosLosPacientes());
        System.out.println(hospi.getPersonal().mostrarTodoElPersonal());
        System.out.println("Hola");
    }

}

