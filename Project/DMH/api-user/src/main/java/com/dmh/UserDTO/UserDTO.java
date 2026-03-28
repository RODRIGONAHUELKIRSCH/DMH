package com.dmh.UserDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    private String telefono;

    @NotBlank
    private String dni;

    @Email
    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pwd;

    @NotBlank
    private String cvu;

    @NotBlank
    private String alias;

    @NotBlank
    private String keycloakId;

    public UserDTO(){}

    public UserDTO(String nombre,String apellido,String telefono,String DNI,String email,String pwd, String cvu,String alias,String keycloakId ){

        this.nombre=nombre;
        this.apellido=apellido;
        this.telefono=telefono;
        this.dni=DNI;
        this.email=email;
        this.pwd=pwd;
        this.cvu=cvu;
        this.alias=alias;
        this.keycloakId=keycloakId;
    }


    public UserDTO(String Nombre, String email){
        this.nombre=Nombre;
        this.email=email;
    }
    public UserDTO(String email){
        this.email=email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCvu() {
        return cvu;
    }

    public void setCvu(String cvu) {
        this.cvu = cvu;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getKeycloakId() {
        return keycloakId;
    }
    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

}