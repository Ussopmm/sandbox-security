package io.ussopm.jwt_authentication.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "t_jwt_user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private Date creationTime;

    private String role;

    private String email;

    @Column(unique = true, length = 2000)
    private String token;

    private int failedAttempts;

    private Date lockTime;

    private Date passwordUpdateTime;

    @ManyToOne
    @JoinColumn(name = "authority_id", referencedColumnName = "id")
    private Authority authority;

}
