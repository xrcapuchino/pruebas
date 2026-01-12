package com.example.saber_share.model;

import com.google.gson.annotations.SerializedName;

public class HistorialDto {
    private Integer idHistorial;
    private String fechapago;
    private Double pago;

    // Mapeo JSON por si el backend manda "usuario_idUsuario" o "usuarioId"
    @SerializedName(value = "usuarioId", alternate = {"usuario_idUsuario", "Usuario_idUsuario"})
    private Integer usuarioId;

    private Integer servicioId;
    private Integer cursoId;

    // CAMPOS NUEVOS (Necesarios para el Adaptador)
    // El backend debe enviarlos, o vendrán null y manejaremos eso en el adapter
    private String tituloCurso;
    private String tituloServicio;

    public HistorialDto() {}

    // Getters y Setters
    public Integer getIdHistorial() { return idHistorial; }
    public void setIdHistorial(Integer idHistorial) { this.idHistorial = idHistorial; }

    public String getFechapago() { return fechapago; }
    public void setFechapago(String fechapago) { this.fechapago = fechapago; }

    public Double getPago() { return pago; }
    public void setPago(Double pago) { this.pago = pago; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Integer getServicioId() { return servicioId; }
    public void setServicioId(Integer servicioId) { this.servicioId = servicioId; }

    public Integer getCursoId() { return cursoId; }
    public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }

    public String getTituloCurso() { return tituloCurso; }
    public void setTituloCurso(String tituloCurso) { this.tituloCurso = tituloCurso; }

    public String getTituloServicio() { return tituloServicio; }
    public void setTituloServicio(String tituloServicio) { this.tituloServicio = tituloServicio; }
}