package dev.ptit.charitymanagement.service;

import java.util.List;

public interface CRUDService<T, D, I>{
    T findById(I i);
    List<T> findAll();
    T create(D d);
    T update(I i, D d);
    void delete(I i);
}
