package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.web.dto.EmployeRequest;
import tn.esprit.autoloc.web.dto.EmployeResponse;

public interface IEmployeService {
    EmployeResponse create(EmployeRequest request);
    EmployeResponse findById(Long id);
    List<EmployeResponse> findAll();
    EmployeResponse update(Long id, EmployeRequest request);
    void deleteById(Long id);
}
