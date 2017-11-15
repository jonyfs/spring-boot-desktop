package br.com.jonyfs.message;

import br.com.jonyfs.domain.BaseEntity;
import br.com.jonyfs.user.User;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Message extends BaseEntity {

    private static final long serialVersionUID = -3578703994250242306L;

    @OneToOne
    private User from;
    @OneToOne
    private User to;

    private String body;
}
