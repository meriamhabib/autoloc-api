package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;

public interface IContratService {
    ContratResponse create(ContratRequest request);
    ContratResponse findById(Long id);
    List<ContratResponse> findAll();
    ContratResponse update(Long id, ContratRequest request);
    void deleteById(Long id);
}
