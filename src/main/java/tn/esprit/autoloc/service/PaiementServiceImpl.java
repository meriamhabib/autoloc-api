package tn.esprit.autoloc.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IPaiementRepository;
import tn.esprit.autoloc.web.dto.PaiementResponse;
import tn.esprit.autoloc.web.mapper.PaiementMapper;

@Service
@RequiredArgsConstructor
public class PaiementServiceImpl implements IPaiementService {

    private final IPaiementRepository paiementRepository;

    @Override
    public PaiementResponse findById(Long id) {
        return paiementRepository.findById(id)
                .map(PaiementMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", id));
    }

    @Override
    public List<PaiementResponse> findAll() {
        return paiementRepository.findAll().stream()
                .map(PaiementMapper::toResponse)
                .collect(Collectors.toList());
    }
}
