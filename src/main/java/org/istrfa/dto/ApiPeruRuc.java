package org.istrfa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiPeruRuc {

    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private List<String> telefonos;
    private String tipo;
    private String estado;
    private String condicion;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private String fechaInscripcion;
    private String sistEmsion;
    private String sistContabilidad;
    private String actExterior;
    private List<String> actEconomicas;
    private List<String> cpPago;
    private List<String> sistElectronica;
    private String fechaEmisorFe;
    private List<String> cpeElectronico;
    private String fechaPle;
    private List<String> padrones;
    private String fechaBaja;
    private String profesion;
    private String ubigeo;
    private String capital;

    private Boolean success;//Este valior solo vendra cuando no haya informacion, cuando si hay no llega


}
