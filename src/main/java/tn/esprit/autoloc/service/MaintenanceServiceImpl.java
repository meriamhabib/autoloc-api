package tn.esprit.autoloc.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.MaintenanceResponse;
import tn.esprit.autoloc.web.mapper.MaintenanceMapper;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeRepository vehiculeRepository;

    @Override
    public MaintenanceResponse create(MaintenanceRequest request) {
        validerMaintenance(request);
        Maintenance maintenance = MaintenanceMapper.toEntity(request);
        if (request.getVehiculeId() != null) {
            Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", request.getVehiculeId()));
            maintenance.setVehicule(vehicule);
        }
        return MaintenanceMapper.toResponse(maintenanceRepository.save(maintenance));
    }

    @Override
    public MaintenanceResponse findById(Long id) {
        return maintenanceRepository.findById(id)
                .map(MaintenanceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
    }

    @Override
    public List<MaintenanceResponse> findAll() {
        return maintenanceRepository.findAll().stream()
                .map(MaintenanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceResponse update(Long id, MaintenanceRequest request) {
        validerMaintenance(request);
        Maintenance existante = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));

        existante.setDateDebut(request.getDateDebut());
        existante.setDateFin(request.getDateFin());
        existante.setDescription(request.getDescription());

        if (request.getVehiculeId() != null) {
            Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", request.getVehiculeId()));
            existante.setVehicule(vehicule);
        } else {
            existante.setVehicule(null);
        }

        return MaintenanceMapper.toResponse(maintenanceRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance", id);
        }
        maintenanceRepository.deleteById(id);
    }

    private void validerMaintenance(MaintenanceRequest request) {
        if (request.getDateDebut() != null && request.getDateFin() != null
                && request.getDateFin().isBefore(request.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
