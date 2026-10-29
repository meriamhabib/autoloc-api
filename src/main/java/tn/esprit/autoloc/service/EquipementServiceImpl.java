package tn.esprit.autoloc.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;
import tn.esprit.autoloc.web.mapper.EquipementMapper;

@Service
@RequiredArgsConstructor
public class EquipementServiceImpl implements IEquipementService {

    private final IEquipementRepository equipementRepository;

    @Override
    public EquipementResponse create(EquipementRequest request) {
        validerEquipement(request);
        Equipement equipement = EquipementMapper.toEntity(request);
        return EquipementMapper.toResponse(equipementRepository.save(equipement));
    }

    @Override
    public EquipementResponse findById(Long id) {
        return equipementRepository.findById(id)
                .map(EquipementMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
    }

    @Override
    public List<EquipementResponse> findAll() {
        return equipementRepository.findAll().stream()
                .map(EquipementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EquipementResponse update(Long id, EquipementRequest request) {
        validerEquipement(request);
        Equipement existant = equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
        existant.setLibelle(request.getLibelle());
        return EquipementMapper.toResponse(equipementRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!equipementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipement", id);
        }
        equipementRepository.deleteById(id);
    }

    private void validerEquipement(EquipementRequest request) {
        if (request.getLibelle() == null || request.getLibelle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé de l'équipement est obligatoire");
        }
    }
}
