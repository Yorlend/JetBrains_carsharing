package carsharing.dao;

import carsharing.DbManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {
    @Override
    public List<Customer> findCustomersSorted() {
        List<Customer> res = new ArrayList<>();
        try {
            var dbInstance = DbManager.getInstance(null);

            Statement statement = dbInstance.getConnection().createStatement();
            ResultSet rs = statement.executeQuery("select * from customer order by id");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String rId = rs.getString("rented_car_id");
                Integer rented = rId != null ? Integer.valueOf(rId) : null;

                res.add(new Customer(id, name, rented));
            }

            statement.close();
        } catch(SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return res;
    }

    public boolean insertCustomer(Customer customer) {
        try {
            var dbInstance = DbManager.getInstance(null);

            PreparedStatement prep = dbInstance.getConnection()
                    .prepareStatement("insert into customer (name) values ( ? )");

            prep.setString(1, customer.getName());
            prep.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void rentCar(Customer customer, Car car) {
        try {
            var dbInstance = DbManager.getInstance(null);

            PreparedStatement prep = dbInstance.getConnection()
                    .prepareStatement("update customer set rented_car_id = ? where id = ?");

            prep.setInt(1, car.getId());
            prep.setInt(2, customer.getId());
            prep.executeUpdate();
            customer.setRented_car_id(car.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean returnCar(Customer customer) {
        if (customer.getRented_car_id() == null) {
            return false;
        }

        try {
            var dbInstance = DbManager.getInstance(null);

            PreparedStatement prep = dbInstance.getConnection()
                    .prepareStatement("update customer set rented_car_id = null where id = ?");

            prep.setInt(1, customer.getId());
            prep.executeUpdate();

            customer.setRented_car_id(null);
            prep.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String[] getRentedCar(Customer customer) {
        if (customer.getRented_car_id() == null) {
            return null;
        }

        try {
            var dbInstance = DbManager.getInstance(null);

            Statement statement = dbInstance.getConnection().createStatement();
            ResultSet rs = statement.executeQuery(String
                    .format("select car.name, company.name from car join company on car.company_id = company.id" +
                            " where car.id = %d", customer.getRented_car_id()));

            rs.next();

            String carName = rs.getString("car.name");
            String companyName = rs.getString("company.name");

            statement.close();

            return new String[]{carName, companyName};
        } catch(SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return null;
    }
}
