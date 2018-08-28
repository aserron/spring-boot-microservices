/*
 * Merchant REST Controller
 */
package aserron.dlocal.demo.pm.data.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.validation.annotation.Validated;

@Entity
@Validated
@Table(name = "merchants")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 313053012802449544L;
    
        

    @Id    
    private Long id;
    
    private String name;

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
