package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;

public interface IEquipementService {
    EquipementResponse create(EquipementRequest request);
    EquipementResponse findById(Long id);
    List<EquipementResponse> findAll();
    EquipementResponse update(Long id, EquipementRequest request);
    void deleteById(Long id);
}
