package tn.esprit.autoloc.web.mapper;

import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.web.dto.ClientRequest;
import tn.esprit.autoloc.web.dto.ClientResponse;

/**
 * Mapper manuel pour la conversion entre l'entité Client et ses DTOs.
 */
public class ClientMapper {

    private ClientMapper() {
        // Utilitaire statique — instanciation interdite
    }

    /**
     * Convertit un ClientRequest en entité Client.
     */
    public static Client toEntity(ClientRequest request) {
        Client client = new Client();
        client.setNom(request.getNom());
        client.setPrenom(request.getPrenom());
        client.setEmail(request.getEmail());
        client.setTelephone(request.getTelephone());
        client.setNumPermis(request.getNumPermis());
        client.setDateInscription(request.getDateInscription());
        return client;
    }

    /**
     * Convertit une entité Client en ClientResponse.
     */
    public static ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getIdClient(),
                client.getNom(),
                client.getPrenom(),
                client.getEmail(),
                client.getTelephone(),
                client.getNumPermis(),
                client.getDateInscription()
        );
    }
}
