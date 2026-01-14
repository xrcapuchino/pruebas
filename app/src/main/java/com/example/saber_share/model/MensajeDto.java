package com.example.saber_share.model;

import java.util.Date;

public class MensajeDto {
    private int idMensaje;
    private String contenido;
    private String fecha; // Lo manejaremos como String para simplificar en la vista
    private UsuarioDto remitente;
    private UsuarioDto destinatario;

    // Constructor vacío
    public MensajeDto() {}

    // Constructor para enviar (a veces solo ocupamos IDs, pero este sirve para recibir)
    public MensajeDto(String contenido, UsuarioDto remitente, UsuarioDto destinatario) {
        this.contenido = contenido;
        this.remitente = remitente;
        this.destinatario = destinatario;
    }

    public int getIdMensaje() { return idMensaje; }
    public void setIdMensaje(int idMensaje) { this.idMensaje = idMensaje; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public UsuarioDto getRemitente() { return remitente; }
    public void setRemitente(UsuarioDto remitente) { this.remitente = remitente; }

    public UsuarioDto getDestinatario() { return destinatario; }
    public void setDestinatario(UsuarioDto destinatario) { this.destinatario = destinatario; }
}