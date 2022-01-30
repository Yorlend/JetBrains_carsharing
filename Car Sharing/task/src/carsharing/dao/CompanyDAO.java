package carsharing.dao;

import java.util.List;

public interface CompanyDAO {

    List<Company> findAllSorted();
    boolean insertCompany(Company company);
}
