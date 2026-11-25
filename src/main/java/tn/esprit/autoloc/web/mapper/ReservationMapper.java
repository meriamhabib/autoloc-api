package tn.esprit.autoloc.web.mapper;

import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;

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
        return buildReservation(request.getDateDebut(), request.getDateFin(), request.getStatut());
    }

    /**
     * Convertit un ReservationWithContratRequestDTO en entité Reservation.
     */
    public static Reservation toEntity(ReservationWithContratRequestDTO request) {
        return buildReservation(request.getDateDebut(), request.getDateFin(), request.getStatut());
    }

    private static Reservation buildReservation(
            java.time.LocalDate dateDebut,
            java.time.LocalDate dateFin,
            tn.esprit.autoloc.domain.StatutReservation statut) {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setStatut(statut);
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

        return new ReservationResponse(
                reservation.getIdReservation(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                reservation.getStatut(),
                vehicule != null ? vehicule.getIdVehicule() : null,
                vehicule != null ? vehicule.getImmatriculation() : null,
                client != null ? client.getIdClient() : null,
                formatNomComplet(client),
                contrat != null ? contrat.getIdContrat() : null
        );
    }

    /**
     * Convertit une entité Reservation en ReservationResponseDTO.
     */
    public static ReservationResponseDTO toResponseDTO(Reservation reservation) {
        Vehicule vehicule = reservation.getVehicule();
        Client client = reservation.getClient();
        Contrat contrat = reservation.getContrat();

        return new ReservationResponseDTO(
                reservation.getIdReservation(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                reservation.getStatut(),
                vehicule != null ? vehicule.getIdVehicule() : null,
                vehicule != null ? vehicule.getImmatriculation() : null,
                client != null ? client.getIdClient() : null,
                formatNomComplet(client),
                contrat != null ? contrat.getIdContrat() : null,
                contrat != null ? contrat.getDateSignature() : null
        );
    }

    private static String formatNomComplet(Client client) {
        return client != null ? client.getNom() + " " + client.getPrenom() : null;
    }
}
