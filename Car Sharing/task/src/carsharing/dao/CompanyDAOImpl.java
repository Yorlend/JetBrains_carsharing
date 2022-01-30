package carsharing.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import carsharing.DbManager;

public class CompanyDAOImpl implements CompanyDAO {
    @Override
    public List<Company> findAllSorted() {
        List<Company> res = new ArrayList<>();
        try {
            var dbInstance = DbManager.getInstance(null);

            Statement statement = dbInstance.getConnection().createStatement();
            ResultSet rs = statement.executeQuery("select id, name from company order by id");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                res.add(new Company(id, name));
            }

            statement.close();
        } catch(SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return res;
    }

    @Override
    public boolean insertCompany(Company company) {
        try {
            String companyName = company.getName();
            var dbInstance = DbManager.getInstance(null);
            PreparedStatement prep = dbInstance.getConnection()
                    .prepareStatement("insert into company (name) values ( ? )");

            prep.setString(1, companyName);
            prep.executeUpdate();

            return true;
        } catch(SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return false;
    }
}
