package com.proaula.aula.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "contacto_mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactoMensaje {
    @Id
    private String id;

    @NotBlank
    private String nombre;
    
    @NotBlank
    private String apellido;
    
    private String telefono;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String mensaje;

    private long fechaCreacion = System.currentTimeMillis();
    private boolean leido = false;
}
