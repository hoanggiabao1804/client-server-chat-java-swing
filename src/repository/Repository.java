package repository;

import java.util.List;

public interface Repository {

    public void importData(String path);

    public void exportData(String path);

    public List<Object> findAll();

    public Object findById(String id);

    public boolean existsById(String id);

    public Object save(Object save);

    public void deleteById(String id);

}
