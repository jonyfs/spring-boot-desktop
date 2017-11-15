/**
 * This file was generated by the JPA Modeler
 */

package br.com.jonyfs.person;

import br.com.jonyfs.domain.BaseEntity;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;

/**
 * @author  jony
 */

@Entity
public class Person extends BaseEntity {

    private static final long serialVersionUID = 5971503303526442022L;

    @Basic
    private String name;

    @Basic
    private String telephone;

    @Email
    @Column(unique=true)
    @Basic
    private String email;


}