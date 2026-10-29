package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;

public interface IReservationService {
    ReservationResponse create(ReservationRequest request);
    ReservationResponse findById(Long id);
    List<ReservationResponse> findAll();
    ReservationResponse update(Long id, ReservationRequest request);
    void deleteById(Long id);
}
