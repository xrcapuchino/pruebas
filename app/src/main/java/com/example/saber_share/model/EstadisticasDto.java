package com.example.saber_share.model;

public class EstadisticasDto {
    private Double totalGanancias;
    private Integer cantidadVentas;
    private Integer cursosVendidos;
    private Integer clasesVendidas;

    public Double getTotalGanancias() { return totalGanancias; }
    public void setTotalGanancias(Double totalGanancias) { this.totalGanancias = totalGanancias; }

    public Integer getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(Integer cantidadVentas) { this.cantidadVentas = cantidadVentas; }

    public Integer getCursosVendidos() { return cursosVendidos; }
    public void setCursosVendidos(Integer cursosVendidos) { this.cursosVendidos = cursosVendidos; }

    public Integer getClasesVendidas() { return clasesVendidas; }
    public void setClasesVendidas(Integer clasesVendidas) { this.clasesVendidas = clasesVendidas; }
}