package br.com.axxiom.core.service;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import br.com.axxiom.core.db.CustomIdentifiable;
import br.com.axxiom.core.db.CustomRepository;
import br.com.axxiom.core.db.Specs;
import br.com.axxiom.core.service.interfaces.CustomCrudService;
import br.com.axxiom.core.service.interfaces.LogService;

public abstract class AbstractCustomCrudService<T extends CustomIdentifiable<D>, D extends Serializable> implements CustomCrudService<T, D> {
    private final Log logger = LogFactory.getLog(getClass());

    protected abstract CustomRepository<T, D> getRepository();

    protected final Class<T> entityClass;

    protected AbstractCustomCrudService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    @Transactional
    public T save(T entity) {
        // se o id está preenchido é update, chama o log antes da atualização
        // para os casos onde precisa-se saber o que foi alterado
        boolean update = false;
        if (entity.getId() != null) {
            getLogService().logUpdate(entity);
            update = true;
        }
        T savedEntity = getRepository().save(entity);
        getRepository().flush();
        getRepository().evict(savedEntity);
        if (!update) {
            getLogService().logInsert(savedEntity);
        }
        return savedEntity;
    }

    @Override
    @Transactional
    public void delete(D id) {
        T entity = get(id);
        getLogService().logDelete(entity);
        getRepository().delete(id);
    }

    @Override
    @Transactional
    public <F> void deleteByExample(F entity) {
        List<T> entities = find(entity);
        getLogService().logBatchDelete(entities);
        getRepository().deleteInBatch(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public T get(D id) {
        return getRepository().findOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public <F> List<T> find(F entity) {
        return getRepository().findAll(Specs.byExample(entityClass, entity));
    }

    @Override
    @Transactional(readOnly = true)
    public <F> List<T> find(F entity, Sort sort) {
        return getRepository().findAll(Specs.byExample(entityClass, entity), sort);
    }

    @Override
    @Transactional(readOnly = true)
    public <F> Page<T> find(F entity, Pageable page) {
        return getRepository().findAll(Specs.byExample(entityClass, entity), page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll(Sort sort) {
        return getRepository().findAll(sort);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable page) {
        return getRepository().findAll(page);
    }

    public String getUsername() {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return details.getUsername();
    }

    protected LogService<T> getLogService() {
        return new SimpleLogService();
    }

    private class SimpleLogService implements LogService<T> {
        @Override
        public void logInsert(T entity) {
            logger.debug("Usuário: " + getUsername() + " - Inserindo: " + entity);
        }

        @Override
        public void logUpdate(T entity) {
            logger.debug("Usuário: " + getUsername() + " - Atualizando: " + entity);
        }

        @Override
        public void logDelete(T entity) {
            logger.debug("Usuário: " + getUsername() + " - Excluindo: " + entity);
        }

        @Override
        public void logBatchDelete(List<T> entities) {
            for (T t : entities) {
                logDelete(t);
            }
        }
    }
}
