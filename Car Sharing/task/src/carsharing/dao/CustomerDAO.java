package carsharing.dao;

import java.util.List;

public interface CustomerDAO {



    public List<Customer> findCustomersSorted();

    public boolean insertCustomer(Customer customer);

    public void rentCar(Customer customer, Car car);

    public boolean returnCar(Customer customer);

    public String[] getRentedCar(Customer customer);
}
