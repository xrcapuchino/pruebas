package com.example.saber_share.model;

import com.google.gson.annotations.SerializedName;

public class HistorialDto {
    private Integer idHistorial;

    private String fechapago;
    private Double pago;

    @SerializedName(value = "usuarioId", alternate = {"usuario_idUsuario", "Usuario_idUsuario"})
    private Integer usuarioId;

    @SerializedName(value = "servicioId", alternate = {"servicio_idServicios", "Servicio_idServicios"})
    private Integer servicioId;

    @SerializedName(value = "cursoId", alternate = {"curso_idCurso", "Curso_idCurso"})
    private Integer cursoId;

    private String tituloCurso;
    private String tituloServicio;

    public HistorialDto() {}

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