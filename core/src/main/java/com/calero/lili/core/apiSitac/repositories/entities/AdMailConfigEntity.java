package com.calero.lili.core.apiSitac.repositories.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ad_mails_config")
public class AdMailConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConfig;
    private String usuario;
    private String emailFrom;
    private String url;
    private String password;

    private String consumerKey;
    private String consumerSecret;

}
