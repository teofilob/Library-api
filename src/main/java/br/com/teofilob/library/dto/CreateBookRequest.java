package br.com.teofilob.library.dto;

import java.util.Locale;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.ISBN;

public record CreateBookRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 200, message = "O título deve ter no máximo 200 caracteres")
        String title,

        @NotBlank(message = "O ISBN é obrigatório")
        @Pattern(regexp = "(?:[0-9]{9}[0-9X]|97[89][0-9]{10})",
                message = "Informe um ISBN-10 ou ISBN-13 válido")
        @ISBN(type = ISBN.Type.ANY, message = "O dígito verificador do ISBN é inválido")
        String isbn
) {
    public CreateBookRequest {
        if (title != null) {
            title = title.strip();
        }
        if (isbn != null) {
            isbn = isbn.replaceAll("[\\s\\p{Z}-]", "").toUpperCase(Locale.ROOT);
        }
    }
}
