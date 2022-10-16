package online.samjones.database;

import java.util.List;
import java.util.Optional;

public interface EntityDAO<T> {

    boolean add(T entity);
    Optional<T> getOne(int id);
    List<T> getAll();
    boolean update(T entity);
    boolean delete(int id);

}
