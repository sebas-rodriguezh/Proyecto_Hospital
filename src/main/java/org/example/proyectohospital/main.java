package org.example.proyectohospital;

import org.example.proyectohospital.Modelo.ListaPacientes;
import org.example.proyectohospital.Modelo.ListaPersonal;
import org.example.proyectohospital.Modelo.Medico;
import org.example.proyectohospital.Modelo.Paciente;
import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;

class Main {
    public static void main(String[] args) {
        System.out.println("¡Hola, mundo desde Java!");
        System.out.println("Main para probar cada una de las funcionales del Modelo.");
        Paciente paciente = new Paciente();
        paciente.setNombre("Maria");
        paciente.setTelefono(334434);
        paciente.setFechaNacimiento(LocalDate.now());
        System.out.println(paciente.toString());

        ListaPacientes listaPacientes = new ListaPacientes();
        ListaPersonal listaPersonal = new ListaPersonal();

        Boolean response = listaPersonal.existePersonalConEseID("34324");

        Paciente paciente2 = new Paciente(2434343, LocalDate.now(), "Luis Medrano","34324");

        if (listaPacientes.insertarPaciente(paciente2,response)) {
            System.out.println("Se logró insertar paciente.");
        }
        else {
            System.out.println("NO se logró insertar paciente");

        }

        Boolean responde2 = listaPacientes.existeAlguienConEseID("34324"); //True

        if (listaPersonal.insertarPersonal(new Medico("Luis","34324","53454","Obstetra"),responde2)) {
            System.out.println("Se logró insertar PERSONAL");
        } else {
            System.out.println("NO se logró insertar PERSONAL");
        }
        System.out.println("ggs");
    }

}