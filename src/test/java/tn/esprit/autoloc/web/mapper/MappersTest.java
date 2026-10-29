package tn.esprit.autoloc.web.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.ModePaiement;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.RoleEmploye;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.AgenceRequest;
import tn.esprit.autoloc.web.dto.AgenceResponse;
import tn.esprit.autoloc.web.dto.ClientRequest;
import tn.esprit.autoloc.web.dto.ClientResponse;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;
import tn.esprit.autoloc.web.dto.EmployeRequest;
import tn.esprit.autoloc.web.dto.EmployeResponse;
import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;
import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.MaintenanceResponse;
import tn.esprit.autoloc.web.dto.PaiementRequest;
import tn.esprit.autoloc.web.dto.PaiementResponse;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;
import tn.esprit.autoloc.web.dto.VehiculeRequest;
import tn.esprit.autoloc.web.dto.VehiculeResponse;

class MappersTest {

    @Test
    @DisplayName("Test AgenceMapper: toEntity et toResponse")
    void testAgenceMapper() {
        AgenceRequest req = new AgenceRequest();
        req.setNom("Agence Sousse");
        req.setVille("Sousse");
        req.setAdresse("Corniche");
        req.setTelephone("+21673000000");

        Agence entity = AgenceMapper.toEntity(req);
        assertEquals("Agence Sousse", entity.getNom());
        assertEquals("Sousse", entity.getVille());

        entity.setIdAgence(5L);
        AgenceResponse res = AgenceMapper.toResponse(entity);
        assertEquals(5L, res.getIdAgence());
        assertEquals("Agence Sousse", res.getNom());
    }

    @Test
    @DisplayName("Test ClientMapper: toEntity et toResponse")
    void testClientMapper() {
        ClientRequest req = new ClientRequest();
        req.setNom("Trabelsi");
        req.setPrenom("Karim");
        req.setEmail("karim@test.tn");
        req.setTelephone("+21699000111");
        req.setNumPermis("TN-87654321");
        req.setDateInscription(LocalDate.of(2025, 1, 15));

        Client entity = ClientMapper.toEntity(req);
        assertEquals("Trabelsi", entity.getNom());
        assertEquals("karim@test.tn", entity.getEmail());

        entity.setIdClient(12L);
        ClientResponse res = ClientMapper.toResponse(entity);
        assertEquals(12L, res.getIdClient());
        assertEquals("Trabelsi", res.getNom());
        assertEquals("karim@test.tn", res.getEmail());
    }

    @Test
    @DisplayName("Test ContratMapper et PaiementMapper: toEntity, toResponse et cascade paiements")
    void testContratEtPaiementMapper() {
        ContratRequest req = new ContratRequest();
        req.setDateSignature(LocalDate.of(2025, 2, 1));
        req.setMontantTotal(new BigDecimal("1200.00"));
        req.setValide(true);

        PaiementRequest pReq = new PaiementRequest();
        pReq.setMontant(new BigDecimal("600.00"));
        pReq.setDatePaiement(LocalDate.of(2025, 2, 1));
        pReq.setModePaiement(ModePaiement.CARTE);
        req.getPaiements().add(pReq);

        Contrat entity = ContratMapper.toEntity(req);
        assertEquals(new BigDecimal("1200.00"), entity.getMontantTotal());
        assertEquals(1, entity.getPaiements().size());
        assertEquals(entity, entity.getPaiements().get(0).getContrat());

        entity.setIdContrat(42L);
        entity.getPaiements().get(0).setIdPaiement(101L);

        ContratResponse res = ContratMapper.toResponse(entity);
        assertEquals(42L, res.getIdContrat());
        assertEquals(1, res.getPaiements().size());
        assertEquals(101L, res.getPaiements().get(0).getIdPaiement());
        assertEquals(42L, res.getPaiements().get(0).getContratId());

        PaiementResponse pRes = PaiementMapper.toResponse(entity.getPaiements().get(0));
        assertEquals(101L, pRes.getIdPaiement());
        assertEquals(42L, pRes.getContratId());
    }

    @Test
    @DisplayName("Test VehiculeMapper avec Agence et Equipements dénormalisés")
    void testVehiculeMapper() {
        VehiculeRequest req = new VehiculeRequest();
        req.setImmatriculation("234-TN-5678");
        req.setMarque("Renault");
        req.setModele("Clio 5");
        req.setCategorie(CategorieVehicule.CITADINE);
        req.setTarifJournalier(new BigDecimal("90.00"));
        req.setStatut(StatutVehicule.DISPONIBLE);

        Vehicule entity = VehiculeMapper.toEntity(req);
        assertEquals("234-TN-5678", entity.getImmatriculation());
        assertEquals(CategorieVehicule.CITADINE, entity.getCategorie());

        entity.setIdVehicule(7L);
        Agence agence = new Agence();
        agence.setIdAgence(3L);
        agence.setNom("Agence Ennasr");
        entity.setAgence(agence);

        Equipement eq = new Equipement();
        eq.setIdEquipement(1L);
        eq.setLibelle("GPS");
        entity.setEquipements(List.of(eq));

        VehiculeResponse res = VehiculeMapper.toResponse(entity);
        assertEquals(7L, res.getIdVehicule());
        assertEquals(3L, res.getAgenceId());
        assertEquals("Agence Ennasr", res.getAgenceNom());
        assertEquals(1, res.getEquipements().size());
        assertEquals("GPS", res.getEquipements().get(0).getLibelle());
    }

    @Test
    @DisplayName("Test ReservationMapper avec Client, Vehicule et Contrat dénormalisés")
    void testReservationMapper() {
        ReservationRequest req = new ReservationRequest();
        req.setDateDebut(LocalDate.of(2025, 3, 1));
        req.setDateFin(LocalDate.of(2025, 3, 5));
        req.setStatut(StatutReservation.CONFIRMEE);

        Reservation entity = ReservationMapper.toEntity(req);
        entity.setIdReservation(20L);

        Client client = new Client();
        client.setIdClient(15L);
        client.setNom("Gharbi");
        client.setPrenom("Sami");
        entity.setClient(client);

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(8L);
        vehicule.setImmatriculation("199-TN-9999");
        entity.setVehicule(vehicule);

        ReservationResponse res = ReservationMapper.toResponse(entity);
        assertEquals(20L, res.getIdReservation());
        assertEquals(15L, res.getClientId());
        assertEquals("Gharbi Sami", res.getClientNomComplet());
        assertEquals(8L, res.getVehiculeId());
        assertEquals("199-TN-9999", res.getVehiculeImmatriculation());
    }
}
