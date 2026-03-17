package com.qritiooo.translationagency.dto.request;

import com.qritiooo.translationagency.api.validation.OnCreate;
import com.qritiooo.translationagency.api.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Client create/update request")
public class ClientRequest {
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "firstName is required")
    @Size(max = 100, message = "firstName must be at most 100 characters")
    @Schema(description = "Client first name", example = "Anna")
    private String firstName;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "lastName is required")
    @Size(max = 100, message = "lastName must be at most 100 characters")
    @Schema(description = "Client last name", example = "Kovalenko")
    private String lastName;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 255, message = "email must be at most 255 characters")
    @Schema(description = "Client email", example = "anna@example.com")
    private String email;
}

