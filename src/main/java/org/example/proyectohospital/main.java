package org.example.proyectohospital;

import org.example.proyectohospital.Modelo.*;
import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.List;

class Main {
    public static void main(String[] args) {
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

       receta.insertarDetalleMedicamento(detalleMedicamento);
       receta.insertarDetalleMedicamento(detalleMedicamento2);
       receta2.insertarDetalleMedicamento(detalleMedicamento2);
       receta3.insertarDetalleMedicamento(detalleMedicamento);

        System.out.println(receta.toString());

        ListaRecetas listaRecetas = new ListaRecetas();
        listaRecetas.insertarReceta(receta);
        listaRecetas.insertarReceta(receta2);
        listaRecetas.insertarReceta(receta3);

        ListaPacientes listaPacientes = new ListaPacientes();
        listaPacientes.insertarPaciente(paciente,false);
        listaPacientes.insertarPaciente(paciente2,false);
        listaPacientes.insertarPaciente(paciente3,false);

        ListaMedicamentos listaMedicamentos=new ListaMedicamentos();
        listaMedicamentos.insertarMedicamento(medicamento);
        listaMedicamentos.insertarMedicamento(medicamento1);

        ListaPersonal listaPersonales=new ListaPersonal();
        listaPersonales.insertarPersonal(medico,false);
        listaPersonales.insertarPersonal(medico2,false);
        listaPersonales.insertarPersonal(medico3,false);
        listaPersonales.insertarPersonal(admin1,false);
        listaPersonales.insertarPersonal(farmaceuta,false);


        Hospital hospi=Hospital.getInstance();
        hospi.setMedicamentos(listaMedicamentos);
        hospi.setRecetas(listaRecetas);
        hospi.setPacientes(listaPacientes);
        hospi.setPersonal(listaPersonales);

        System.out.println(hospi.getRecetas().mostrarTodasLasRecetas());
        System.out.println(hospi.getMedicamentos().mostrarTodosLosMedicamentos());
        System.out.println(hospi.getPacientes().mostrarTodosLosPacientes());
        System.out.println(hospi.getPersonal().mostrarTodoElPersonal());
    }

}

