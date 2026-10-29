package tn.esprit.autoloc.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.web.dto.AgenceRequest;
import tn.esprit.autoloc.web.dto.AgenceResponse;
import tn.esprit.autoloc.web.mapper.AgenceMapper;

@Service
@RequiredArgsConstructor
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;

    @Override
    public AgenceResponse create(AgenceRequest request) {
        Agence agence = AgenceMapper.toEntity(request);
        return AgenceMapper.toResponse(agenceRepository.save(agence));
    }

    @Override
    public AgenceResponse findById(Long id) {
        return agenceRepository.findById(id)
                .map(AgenceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    @Override
    public List<AgenceResponse> findAll() {
        return agenceRepository.findAll().stream()
                .map(AgenceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AgenceResponse update(Long id, AgenceRequest request) {
        Agence existante = agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
        existante.setNom(request.getNom());
        existante.setVille(request.getVille());
        existante.setAdresse(request.getAdresse());
        existante.setTelephone(request.getTelephone());
        return AgenceMapper.toResponse(agenceRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id);
        }
        agenceRepository.deleteById(id);
    }
}
