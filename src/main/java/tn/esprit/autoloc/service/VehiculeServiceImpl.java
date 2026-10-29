package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.VehiculeRequest;
import tn.esprit.autoloc.web.dto.VehiculeResponse;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;

    @Override
    public VehiculeResponse create(VehiculeRequest request) {
        validerVehicule(request);
        Vehicule vehicule = VehiculeMapper.toEntity(request);
        lierAgenceEtEquipements(vehicule, request);
        return VehiculeMapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    public VehiculeResponse findById(Long id) {
        return vehiculeRepository.findById(id)
                .map(VehiculeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    @Override
    public List<VehiculeResponse> findAll() {
        return vehiculeRepository.findAll().stream()
                .map(VehiculeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculeResponse update(Long id, VehiculeRequest request) {
        validerVehicule(request);
        Vehicule existant = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));

        existant.setImmatriculation(request.getImmatriculation());
        existant.setMarque(request.getMarque());
        existant.setModele(request.getModele());
        existant.setCategorie(request.getCategorie());
        existant.setTarifJournalier(request.getTarifJournalier());
        existant.setStatut(request.getStatut());
        lierAgenceEtEquipements(existant, request);

        return VehiculeMapper.toResponse(vehiculeRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    private void lierAgenceEtEquipements(Vehicule vehicule, VehiculeRequest request) {
        if (request.getAgenceId() != null) {
            Agence agence = agenceRepository.findById(request.getAgenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", request.getAgenceId()));
            vehicule.setAgence(agence);
        } else {
            vehicule.setAgence(null);
        }

        if (request.getEquipementIds() != null && !request.getEquipementIds().isEmpty()) {
            List<Equipement> equipements = equipementRepository.findAllById(request.getEquipementIds());
            vehicule.setEquipements(equipements);
        } else {
            vehicule.setEquipements(new ArrayList<>());
        }
    }

    private void validerVehicule(VehiculeRequest request) {
        if (request.getTarifJournalier() != null && request.getTarifJournalier().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le tarif journalier d'un véhicule ne peut pas être négatif");
        }
    }
}
