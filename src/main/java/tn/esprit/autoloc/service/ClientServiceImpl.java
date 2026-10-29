package tn.esprit.autoloc.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.web.dto.ClientRequest;
import tn.esprit.autoloc.web.dto.ClientResponse;
import tn.esprit.autoloc.web.mapper.ClientMapper;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;

    @Override
    public ClientResponse create(ClientRequest request) {
        Client client = ClientMapper.toEntity(request);
        return ClientMapper.toResponse(clientRepository.save(client));
    }

    @Override
    public ClientResponse findById(Long id) {
        return clientRepository.findById(id)
                .map(ClientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    @Override
    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .map(ClientMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponse update(Long id, ClientRequest request) {
        Client existant = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        existant.setNom(request.getNom());
        existant.setPrenom(request.getPrenom());
        existant.setEmail(request.getEmail());
        existant.setTelephone(request.getTelephone());
        existant.setNumPermis(request.getNumPermis());
        existant.setDateInscription(request.getDateInscription());
        return ClientMapper.toResponse(clientRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }
        clientRepository.deleteById(id);
    }
}
