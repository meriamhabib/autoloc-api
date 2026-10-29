package tn.esprit.autoloc.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.web.dto.EmployeRequest;
import tn.esprit.autoloc.web.dto.EmployeResponse;
import tn.esprit.autoloc.web.mapper.EmployeMapper;

@Service
@RequiredArgsConstructor
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;
    private final IAgenceRepository agenceRepository;

    @Override
    public EmployeResponse create(EmployeRequest request) {
        validerEmploye(request);
        Employe employe = EmployeMapper.toEntity(request);
        if (request.getAgenceId() != null) {
            Agence agence = agenceRepository.findById(request.getAgenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", request.getAgenceId()));
            employe.setAgence(agence);
        }
        return EmployeMapper.toResponse(employeRepository.save(employe));
    }

    @Override
    public EmployeResponse findById(Long id) {
        return employeRepository.findById(id)
                .map(EmployeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }

    @Override
    public List<EmployeResponse> findAll() {
        return employeRepository.findAll().stream()
                .map(EmployeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeResponse update(Long id, EmployeRequest request) {
        validerEmploye(request);
        Employe existant = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));

        existant.setNom(request.getNom());
        existant.setPrenom(request.getPrenom());
        existant.setRole(request.getRole());

        if (request.getAgenceId() != null) {
            Agence agence = agenceRepository.findById(request.getAgenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", request.getAgenceId()));
            existant.setAgence(agence);
        } else {
            existant.setAgence(null);
        }

        return EmployeMapper.toResponse(employeRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employe", id);
        }
        employeRepository.deleteById(id);
    }

    private void validerEmploye(EmployeRequest request) {
        if (request.getNom() == null || request.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'employé est obligatoire");
        }
        if (request.getPrenom() == null || request.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom de l'employé est obligatoire");
        }
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Le rôle/poste de l'employé est obligatoire");
        }
    }
}
