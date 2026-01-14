package com.example.saber_share.model;

public class MensajeDto {
    private Integer idMensaje;
    private String contenido;
    private String fechaEnvio; // Antes era "fecha"

    // Ahora usamos objetos completos para saber nombre del remitente
    private UsuarioDto remitente;
    private UsuarioDto destinatario;

    public MensajeDto() {}

    // Constructor rápido para enviar
    public MensajeDto(String contenido, Integer idRemitente, Integer idDestinatario) {
        this.contenido = contenido;
        this.remitente = new UsuarioDto();
        this.remitente.setIdUsuario(idRemitente);
        this.destinatario = new UsuarioDto();
        this.destinatario.setIdUsuario(idDestinatario);
    }

    // Getters y Setters
    public Integer getIdMensaje() { return idMensaje; }
    public void setIdMensaje(Integer idMensaje) { this.idMensaje = idMensaje; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(String fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public UsuarioDto getRemitente() { return remitente; }
    public void setRemitente(UsuarioDto remitente) { this.remitente = remitente; }

    public UsuarioDto getDestinatario() { return destinatario; }
    public void setDestinatario(UsuarioDto destinatario) { this.destinatario = destinatario; }
}