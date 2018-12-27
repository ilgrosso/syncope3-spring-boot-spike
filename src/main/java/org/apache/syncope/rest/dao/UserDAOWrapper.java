package org.apache.syncope.rest.dao;

import java.util.List;
import org.apache.syncope.common.lib.types.CipherAlgorithm;
import org.apache.syncope.core.persistence.api.dao.RealmDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.EntityFactory;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDAOWrapper {

    @Autowired
    private EntityFactory entityFactory;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RealmDAO realmDAO;

    @Transactional(readOnly = true)
    public List<User> list() {
        return userDAO.findAll(0, 10);
    }

    @Transactional
    public User save(final String username) {
        User user = entityFactory.newEntity(User.class);
        user.setUsername(username);
        user.setRealm(realmDAO.getRoot());
        user.setPassword("Password123", CipherAlgorithm.SHA);
        return userDAO.save(user);
    }
}
