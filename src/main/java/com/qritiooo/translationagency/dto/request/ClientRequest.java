package com.qritiooo.translationagency.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    private String firstName;
    private String lastName;
    private String email;
}

