package dao;

import java.util.List;
import java.util.Optional;

public abstract class DAO <T, ID> {

    public ID insert(T entity) {throw new UnsupportedOperationException();}

    public void update(T entity) {throw new UnsupportedOperationException();}

    public void delete(ID id) {throw new UnsupportedOperationException();}

    public Optional<T> findById(ID id) {throw new UnsupportedOperationException();}

    public List<T> findAll() {throw new UnsupportedOperationException();}
}
