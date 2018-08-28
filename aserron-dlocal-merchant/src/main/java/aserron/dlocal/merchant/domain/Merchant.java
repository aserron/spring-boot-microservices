/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.merchant.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.validation.annotation.Validated;

@Entity
@Validated
@Table(name = "merchants")
public class Merchant {

    /**
     * Merchant ID
     */
    @Id
    @NotNull
    @Positive
    @Column(name = "id")
    private Long id;
    
    /**
     * Merchant Name
     */
    @NotBlank
    private String  name;

    private Merchant(){};
    
    public Merchant(Long id, String name) {
        this.id   = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}

