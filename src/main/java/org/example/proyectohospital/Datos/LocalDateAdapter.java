package org.example.proyectohospital.Datos;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String v) {
        return (v == null || v.isBlank()) ? null : LocalDate.parse(v);
    }
    @Override
    public String marshal(LocalDate v) {
        return (v == null) ? null : v.toString(); // ISO-8601
    }
}
