package com.example.saber_share.model;

import com.google.gson.annotations.SerializedName;

public class HistorialDto {
    private int idHistorial;
    private Double pago;
    private String fechapago;

    @SerializedName("usuarioId") // Aseguramos que lea 'usuarioId' del JSON
    private Integer usuarioId;

    // CORRECCIÓN: Usamos Integer (IDs) y String (Títulos) para coincidir con el Backend
    private Integer cursoId;
    private Integer servicioId;

    private String tituloCurso;
    private String tituloServicio;
    // Agrega el campo y sus getter/setter
    private String nombreAlumno;

    public String getNombreAlumno() { return nombreAlumno; }
    public void setNombreAlumno(String nombreAlumno) { this.nombreAlumno = nombreAlumno; }
    // Getters y Setters
    public int getIdHistorial() { return idHistorial; }
    public void setIdHistorial(int idHistorial) { this.idHistorial = idHistorial; }

    public Double getPago() { return pago; }
    public void setPago(Double pago) { this.pago = pago; }

    public String getFechapago() { return fechapago; }
    public void setFechapago(String fechapago) { this.fechapago = fechapago; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    // Estos son los métodos que te faltaban:
    public Integer getCursoId() { return cursoId; }
    public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }

    public Integer getServicioId() { return servicioId; }
    public void setServicioId(Integer servicioId) { this.servicioId = servicioId; }

    public String getTituloCurso() { return tituloCurso; }
    public void setTituloCurso(String tituloCurso) { this.tituloCurso = tituloCurso; }

    public String getTituloServicio() { return tituloServicio; }
    public void setTituloServicio(String tituloServicio) { this.tituloServicio = tituloServicio; }
}