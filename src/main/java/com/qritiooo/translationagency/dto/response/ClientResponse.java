package com.qritiooo.translationagency.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Client response DTO")
public class ClientResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
}

