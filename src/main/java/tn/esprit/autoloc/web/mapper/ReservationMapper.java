package tn.esprit.autoloc.web.mapper;

import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;

/**
 * Mapper manuel pour la conversion entre l'entité Reservation et ses DTOs.
 */
public class ReservationMapper {

    private ReservationMapper() {
        // Utilitaire statique — instanciation interdite
    }

    /**
     * Convertit un ReservationRequest en entité Reservation.
     * Les associations (Vehicule, Client, Contrat) sont résolues par le service.
     */
    public static Reservation toEntity(ReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(request.getDateDebut());
        reservation.setDateFin(request.getDateFin());
        reservation.setStatut(request.getStatut());
        return reservation;
    }

    /**
     * Convertit une entité Reservation en ReservationResponse,
     * incluant les informations dénormalisées du véhicule et du client.
     */
    public static ReservationResponse toResponse(Reservation reservation) {
        Vehicule vehicule = reservation.getVehicule();
        Client client = reservation.getClient();
        Contrat contrat = reservation.getContrat();

        String clientNomComplet = client != null
                ? client.getNom() + " " + client.getPrenom()
                : null;

        return new ReservationResponse(
                reservation.getIdReservation(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                reservation.getStatut(),
                vehicule != null ? vehicule.getIdVehicule() : null,
                vehicule != null ? vehicule.getImmatriculation() : null,
                client != null ? client.getIdClient() : null,
                clientNomComplet,
                contrat != null ? contrat.getIdContrat() : null
        );
    }
}
