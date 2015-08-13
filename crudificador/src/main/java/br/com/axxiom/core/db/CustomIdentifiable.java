package br.com.axxiom.core.db;

import java.io.Serializable;

public interface CustomIdentifiable<T extends Serializable> extends Serializable {
    T getId();

    void setId(T id);

}
