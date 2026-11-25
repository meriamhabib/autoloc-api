package tn.esprit.autoloc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.controller.ContratController;
import tn.esprit.autoloc.web.controller.GlobalExceptionHandler;
import tn.esprit.autoloc.web.controller.ReservationController;
import tn.esprit.autoloc.web.controller.VehiculeController;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

class AffectationServicesTest {

    private Map<Long, Reservation> reservationsStore;
    private Map<Long, Client> clientsStore;
    private Map<Long, Vehicule> vehiculesStore;
    private Map<Long, Equipement> equipementsStore;
    private Map<Long, Maintenance> maintenancesStore;
    private Map<Long, Contrat> contratsStore;

    private AtomicLong reservationSeq;
    private AtomicLong contratSeq;

    private ReservationServiceImpl reservationService;
    private VehiculeServiceImpl vehiculeService;
    private ContratServiceImpl contratService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        reservationsStore = new HashMap<>();
        clientsStore = new HashMap<>();
        vehiculesStore = new HashMap<>();
        equipementsStore = new HashMap<>();
        maintenancesStore = new HashMap<>();
        contratsStore = new HashMap<>();
        reservationSeq = new AtomicLong(100);
        contratSeq = new AtomicLong(200);

        IReservationRepository reservationRepo = proxyRepo(IReservationRepository.class, (method, args) -> switch (method) {
            case "findById" -> Optional.ofNullable(reservationsStore.get(args[0]));
            case "findFirstByDateDebutAndDateFin" -> reservationsStore.values().stream()
                    .filter(r -> args[0].equals(r.getDateDebut()) && args[1].equals(r.getDateFin()))
                    .findFirst();
            case "save" -> {
                Reservation r = (Reservation) args[0];
                if (r.getIdReservation() == null) {
                    r.setIdReservation(reservationSeq.incrementAndGet());
                }
                reservationsStore.put(r.getIdReservation(), r);
                yield r;
            }
            default -> throw new UnsupportedOperationException(method);
        });

        IClientRepository clientRepo = proxyRepo(IClientRepository.class, (method, args) -> switch (method) {
            case "findById" -> Optional.ofNullable(clientsStore.get(args[0]));
            case "findFirstByNomIgnoreCaseAndPrenomIgnoreCase" -> clientsStore.values().stream()
                    .filter(c -> c.getNom().equalsIgnoreCase((String) args[0])
                            && c.getPrenom().equalsIgnoreCase((String) args[1]))
                    .findFirst();
            default -> throw new UnsupportedOperationException(method);
        });

        IVehiculeRepository vehiculeRepo = proxyRepo(IVehiculeRepository.class, (method, args) -> switch (method) {
            case "findById" -> Optional.ofNullable(vehiculesStore.get(args[0]));
            case "findByImmatriculationIgnoreCase" -> vehiculesStore.values().stream()
                    .filter(v -> v.getImmatriculation().equalsIgnoreCase((String) args[0]))
                    .findFirst();
            case "save" -> {
                Vehicule v = (Vehicule) args[0];
                vehiculesStore.put(v.getIdVehicule(), v);
                yield v;
            }
            default -> throw new UnsupportedOperationException(method);
        });

        IEquipementRepository equipementRepo = proxyRepo(IEquipementRepository.class, (method, args) -> switch (method) {
            case "findById" -> Optional.ofNullable(equipementsStore.get(args[0]));
            default -> throw new UnsupportedOperationException(method);
        });

        IMaintenanceRepository maintenanceRepo = proxyRepo(IMaintenanceRepository.class, (method, args) -> switch (method) {
            case "findFirstByDescriptionIgnoreCase" -> maintenancesStore.values().stream()
                    .filter(m -> m.getDescription().equalsIgnoreCase((String) args[0]))
                    .findFirst();
            case "save" -> {
                Maintenance m = (Maintenance) args[0];
                maintenancesStore.put(m.getIdMaintenance(), m);
                yield m;
            }
            default -> throw new UnsupportedOperationException(method);
        });

        IContratRepository contratRepo = proxyRepo(IContratRepository.class, (method, args) -> switch (method) {
            case "findFirstByDateSignature" -> contratsStore.values().stream()
                    .filter(c -> args[0].equals(c.getDateSignature()))
                    .findFirst();
            case "save" -> {
                Contrat c = (Contrat) args[0];
                if (c.getIdContrat() == null) {
                    c.setIdContrat(contratSeq.incrementAndGet());
                }
                contratsStore.put(c.getIdContrat(), c);
                yield c;
            }
            default -> throw new UnsupportedOperationException(method);
        });

        IAgenceRepository agenceRepo = proxyRepo(IAgenceRepository.class, (m, a) -> Optional.empty());

        reservationService = new ReservationServiceImpl(reservationRepo, vehiculeRepo, clientRepo, contratRepo);
        vehiculeService = new VehiculeServiceImpl(vehiculeRepo, agenceRepo, equipementRepo, maintenanceRepo);
        contratService = new ContratServiceImpl(contratRepo, reservationRepo);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new ReservationController(reservationService),
                        new VehiculeController(vehiculeService),
                        new ContratController(contratService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("affecterReservationAClient lie la réservation et le client de façon bidirectionnelle")
    void testAffecterReservationAClient() {
        Client client = new Client();
        client.setIdClient(1L);
        client.setNom("Ben Salah");
        client.setPrenom("Ahmed");
        clientsStore.put(1L, client);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(10L);
        reservation.setDateDebut(LocalDate.of(2026, 12, 1));
        reservation.setDateFin(LocalDate.of(2026, 12, 5));
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservationsStore.put(10L, reservation);

        ReservationResponseDTO dto = reservationService.affecterReservationAClient(10L, "ben salah", "ahmed");

        assertEquals(10L, dto.getIdReservation());
        assertEquals(1L, dto.getClientId());
        assertEquals("Ben Salah Ahmed", dto.getClientNomComplet());
        assertTrue(client.getReservations().contains(reservation));
    }

    @Test
    @DisplayName("affecterVehiculeAEquipement associe l'équipement au véhicule sans doublon")
    void testAffecterVehiculeAEquipement() {
        Vehicule vehicule = creerVehicule(2L, "234-TN-1000");
        vehiculesStore.put(2L, vehicule);

        Equipement eq = new Equipement();
        eq.setIdEquipement(5L);
        eq.setLibelle("GPS");
        equipementsStore.put(5L, eq);

        VehiculeDTO dto = vehiculeService.affecterVehiculeAEquipement(2L, 5L);
        vehiculeService.affecterVehiculeAEquipement(2L, 5L);

        assertEquals(2L, dto.getIdVehicule());
        assertEquals(1, dto.getEquipements().size());
        assertEquals("GPS", dto.getEquipements().get(0).getLibelle());
        assertEquals(1, vehicule.getEquipements().size());
        assertTrue(eq.getVehicules().contains(vehicule));
    }

    @Test
    @DisplayName("affecterMaintenanceAvehicule affecte la maintenance au véhicule par description et immatriculation")
    void testAffecterMaintenanceAvehicule() {
        Vehicule vehicule = creerVehicule(3L, "199-TN-8888");
        vehiculesStore.put(3L, vehicule);

        Maintenance maintenance = new Maintenance();
        maintenance.setIdMaintenance(7L);
        maintenance.setDateDebut(LocalDate.of(2026, 11, 20));
        maintenance.setDateFin(LocalDate.of(2026, 11, 22));
        maintenance.setDescription("Vidange complète");
        maintenancesStore.put(7L, maintenance);

        VehiculeDTO dto = vehiculeService.affecterMaintenanceAvehicule("Vidange complète", "199-TN-8888");

        assertEquals(3L, dto.getIdVehicule());
        assertEquals(1, dto.getMaintenances().size());
        assertEquals("Vidange complète", dto.getMaintenances().get(0).getDescription());
        assertEquals(vehicule, maintenance.getVehicule());
    }

    @Test
    @DisplayName("affecterContratAReservation lie un contrat et une réservation existants")
    void testAffecterContratAReservation() {
        LocalDate dateSignature = LocalDate.of(2026, 11, 25);
        LocalDate dateDebut = LocalDate.of(2026, 12, 10);
        LocalDate dateFin = LocalDate.of(2026, 12, 15);

        Contrat contrat = new Contrat();
        contrat.setIdContrat(30L);
        contrat.setDateSignature(dateSignature);
        contrat.setMontantTotal(new BigDecimal("600.00"));
        contrat.setValide(true);
        contratsStore.put(30L, contrat);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(40L);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservationsStore.put(40L, reservation);

        ContratDTO dto = contratService.affecterContratAReservation(dateSignature, dateDebut, dateFin);

        assertEquals(30L, dto.getIdContrat());
        assertEquals(40L, dto.getReservationId());
        assertEquals(contrat, reservation.getContrat());
        assertEquals(reservation, contrat.getReservation());
    }

    @Test
    @DisplayName("creerReservationEtContratAssocie crée la réservation et son contrat lié")
    void testCreerReservationEtContratAssocie() {
        Vehicule vehicule = creerVehicule(4L, "210-TN-4444");
        vehiculesStore.put(4L, vehicule);

        Client client = new Client();
        client.setIdClient(9L);
        client.setNom("Trabelsi");
        client.setPrenom("Sami");
        clientsStore.put(9L, client);

        ReservationWithContratRequestDTO req = new ReservationWithContratRequestDTO();
        req.setDateDebut(LocalDate.of(2026, 12, 1));
        req.setDateFin(LocalDate.of(2026, 12, 4));
        req.setStatut(StatutReservation.CONFIRMEE);
        req.setVehiculeId(4L);
        req.setClientId(9L);
        req.setDateSignature(LocalDate.of(2026, 11, 25));
        req.setMontantTotal(new BigDecimal("350.00"));
        req.setValide(true);

        ReservationResponseDTO dto = reservationService.creerReservationEtContratAssocie(req);

        assertNotNull(dto.getIdReservation());
        assertNotNull(dto.getContratId());
        assertEquals(LocalDate.of(2026, 11, 25), dto.getContratDateSignature());
        assertEquals("210-TN-4444", dto.getVehiculeImmatriculation());
        assertEquals("Trabelsi Sami", dto.getClientNomComplet());
        assertEquals(1, contratsStore.size());
        assertEquals(1, reservationsStore.size());
    }

    @Test
    @DisplayName("Les endpoints HTTP exposent les 5 services et valident les entrées")
    void testEndpointsRestAffectation() throws Exception {
        Client client = new Client();
        client.setIdClient(1L);
        client.setNom("Ben Salah");
        client.setPrenom("Ahmed");
        clientsStore.put(1L, client);

        Vehicule vehicule = creerVehicule(2L, "234-TN-1000");
        vehiculesStore.put(2L, vehicule);

        Equipement eq = new Equipement();
        eq.setIdEquipement(5L);
        eq.setLibelle("Siège bébé");
        equipementsStore.put(5L, eq);

        Maintenance maintenance = new Maintenance();
        maintenance.setIdMaintenance(7L);
        maintenance.setDescription("Changement pneus");
        maintenancesStore.put(7L, maintenance);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(10L);
        reservation.setDateDebut(LocalDate.of(2026, 12, 1));
        reservation.setDateFin(LocalDate.of(2026, 12, 3));
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservationsStore.put(10L, reservation);

        Contrat contrat = new Contrat();
        contrat.setIdContrat(30L);
        contrat.setDateSignature(LocalDate.of(2026, 11, 25));
        contrat.setMontantTotal(new BigDecimal("250.00"));
        contratsStore.put(30L, contrat);

        mockMvc.perform(put("/reservations/10/affecter-client")
                        .param("nom", "Ben Salah")
                        .param("prenom", "Ahmed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1L));

        mockMvc.perform(put("/vehicules/2/equipements/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equipements[0].libelle").value("Siège bébé"));

        mockMvc.perform(put("/vehicules/affecter-maintenance")
                        .param("description", "Changement pneus")
                        .param("immatriculation", "234-TN-1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maintenances[0].description").value("Changement pneus"));

        mockMvc.perform(put("/contrats/affecter-reservation")
                        .param("dateSignature", "2026-11-25")
                        .param("dateDebut", "2026-12-01")
                        .param("dateFin", "2026-12-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(10L));

        ReservationWithContratRequestDTO invalidDto = new ReservationWithContratRequestDTO();
        mockMvc.perform(post("/reservations/avec-contrat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreurs.dateDebut").exists());

        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.affecterReservationAClient(999L, "Inconnu", "Test"));
    }

    private Vehicule creerVehicule(Long id, String immatriculation) {
        Vehicule v = new Vehicule();
        v.setIdVehicule(id);
        v.setImmatriculation(immatriculation);
        v.setMarque("Peugeot");
        v.setModele("208");
        v.setCategorie(CategorieVehicule.CITADINE);
        v.setTarifJournalier(new BigDecimal("85.00"));
        v.setStatut(StatutVehicule.DISPONIBLE);
        v.setEquipements(new ArrayList<>());
        v.setMaintenances(new ArrayList<>());
        return v;
    }

    @FunctionalInterface
    private interface RepoHandler {
        Object handle(String methodName, Object[] args);
    }

    @SuppressWarnings("unchecked")
    private <T> T proxyRepo(Class<T> iface, RepoHandler handler) {
        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class<?>[]{iface},
                (proxy, method, args) -> handler.handle(method.getName(), args));
    }
}

