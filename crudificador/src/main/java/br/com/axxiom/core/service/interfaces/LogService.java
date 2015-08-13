package br.com.axxiom.core.service.interfaces;

import java.util.List;

public interface LogService<T> {
    void logInsert(T entity);

    void logUpdate(T entity);

    void logDelete(T entity);

    void logBatchDelete(List<T> entities);
}
