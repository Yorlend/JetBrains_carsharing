package carsharing.dao;

import java.util.List;

public interface CarDAO {
    public List<Car> findAllSorted(Company company);
    public boolean insertCar(Car car);
}
