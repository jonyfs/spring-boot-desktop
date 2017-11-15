package br.com.jonyfs.user;

import br.com.jonyfs.domain.BaseEntity;
import br.com.jonyfs.person.Person;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;


@Entity
public class User extends BaseEntity {

    private static final long serialVersionUID = -1094443673241810749L;

    @Basic
    private String password;

    @OneToOne(cascade = {CascadeType.ALL}, targetEntity = Person.class)
    private Person person;


}
