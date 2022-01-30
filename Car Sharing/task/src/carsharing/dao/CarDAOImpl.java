package carsharing.dao;

import carsharing.DbManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarDAOImpl implements CarDAO {
    @Override
    public List<Car> findAllSorted(Company company) {
        List<Car> res = new ArrayList<>();
        try {
            var dbInstance = DbManager.getInstance(null);

            Statement statement = dbInstance.getConnection().createStatement();
            ResultSet rs = statement.executeQuery(String
                    .format("select * from car where not exists(select * from customer " +
                            "where customer.rented_car_id = car.id) " +
                            "and company_id = %d order by id", company.getId()));

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int company_id = rs.getInt("company_id");

                res.add(new Car(id, name, company_id));
            }

            statement.close();
        } catch(SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return res;
    }

    @Override
    public boolean insertCar(Car car) {
        try {
            var dbInstance = DbManager.getInstance(null);
            PreparedStatement prep = dbInstance.getConnection()
                    .prepareStatement("insert into car (name, company_id) values ( ?, ? )");

            prep.setString(1, car.getName());
            prep.setInt(2, car.getCompany_id());
            prep.executeUpdate();

            return true;
        } catch(SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return false;
    }
}
