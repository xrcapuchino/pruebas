package com.example.saber_share.model;

public class Publicacion {
    public static final String TIPO_CURSO = "CURSO";
    public static final String TIPO_CLASE = "CLASE";

    private String tipo; // "Entre curso y clave"
    private int idOriginal;
    private String titulo;
    private String descripcion;
    private double precio;
    private String autor; // Nombre del profe
    private String calificacion;
    private String imagenUrl;

    public Publicacion() {}
    public Publicacion(String tipo, int idOriginal, String titulo, String descripcion, double precio, String autor, String calificacion, String imagenUrl) {
        this.tipo = tipo;
        this.idOriginal = idOriginal;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.autor = autor;
        this.calificacion = calificacion;
        this.imagenUrl = imagenUrl;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdOriginal() {
        return idOriginal;
    }

    public void setIdOriginal(int idOriginal) {
        this.idOriginal = idOriginal;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}