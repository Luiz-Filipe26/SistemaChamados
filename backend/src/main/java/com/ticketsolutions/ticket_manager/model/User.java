package com.ticketsolutions.ticket_manager.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private Long id;
    private String name;
    private String email;
    private LocalDate birthDate;
    private String role;
    private String location;

}
