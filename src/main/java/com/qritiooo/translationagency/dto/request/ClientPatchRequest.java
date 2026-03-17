package com.qritiooo.translationagency.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Client patch request")
public class ClientPatchRequest {
    @Size(max = 100, message = "firstName must be at most 100 characters")
    @Schema(description = "Client first name", example = "Anna")
    private String firstName;

    @Size(max = 100, message = "lastName must be at most 100 characters")
    @Schema(description = "Client last name", example = "Kovalenko")
    private String lastName;

    @Email(message = "email must be valid")
    @Size(max = 255, message = "email must be at most 255 characters")
    @Schema(description = "Client email", example = "anna@example.com")
    private String email;
}
