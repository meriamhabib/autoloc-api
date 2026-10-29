package tn.esprit.autoloc.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;
import tn.esprit.autoloc.web.mapper.ReservationMapper;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IClientRepository clientRepository;
    private final IContratRepository contratRepository;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        validerReservation(request);
        Reservation reservation = ReservationMapper.toEntity(request);
        lierAssociations(reservation, request);
        return ReservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse findById(Long id) {
        return reservationRepository.findById(id)
                .map(ReservationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    @Override
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponse update(Long id, ReservationRequest request) {
        validerReservation(request);
        Reservation existante = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));

        existante.setDateDebut(request.getDateDebut());
        existante.setDateFin(request.getDateFin());
        existante.setStatut(request.getStatut());
        lierAssociations(existante, request);

        return ReservationMapper.toResponse(reservationRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }

    private void lierAssociations(Reservation reservation, ReservationRequest request) {
        if (request.getVehiculeId() != null) {
            Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", request.getVehiculeId()));
            reservation.setVehicule(vehicule);
        } else {
            reservation.setVehicule(null);
        }

        if (request.getClientId() != null) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));
            reservation.setClient(client);
        } else {
            reservation.setClient(null);
        }

        if (request.getContratId() != null) {
            Contrat contrat = contratRepository.findById(request.getContratId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contrat", request.getContratId()));
            reservation.setContrat(contrat);
        } else {
            reservation.setContrat(null);
        }
    }

    private void validerReservation(ReservationRequest request) {
        if (request.getDateDebut() != null && request.getDateFin() != null
                && request.getDateFin().isBefore(request.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
