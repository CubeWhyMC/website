package org.cubewhy.api.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AuthorizeVO {
    String username;
    String role;
    String token;
    String email;
    Date expire;
}
