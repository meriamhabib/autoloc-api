package tn.esprit.autoloc.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.AgenceRequest;
import tn.esprit.autoloc.web.dto.AgenceResponse;
import tn.esprit.autoloc.web.dto.ClientRequest;
import tn.esprit.autoloc.web.dto.ClientResponse;

@ExtendWith(MockitoExtension.class)
class ValidationAndExceptionHandlingTest {

    private MockMvc mockMvcAgence;
    private MockMvc mockMvcClient;
    private ObjectMapper objectMapper;

    @Mock
    private IAgenceService agenceService;

    @Mock
    private IClientService clientService;

    @InjectMocks
    private AgenceController agenceController;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvcAgence = MockMvcBuilders.standaloneSetup(agenceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvcClient = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Devrait retourner 400 avec la liste des erreurs de validation lorsque le DTO AgenceRequest est invalide")
    void devraisRejeterAgenceInvalide() throws Exception {
        AgenceRequest request = new AgenceRequest();
        request.setNom(""); // Blank -> violation @NotBlank
        request.setVille(""); // Blank -> violation @NotBlank

        mockMvcAgence.perform(post("/agences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statut").value(400))
                .andExpect(jsonPath("$.erreur").value("Erreur de validation des données"))
                .andExpect(jsonPath("$.erreurs.nom").exists())
                .andExpect(jsonPath("$.erreurs.ville").exists());
    }

    @Test
    @DisplayName("Devrait créer une agence et retourner 201 avec le DTO AgenceResponse")
    void devraisCreerAgenceValide() throws Exception {
        AgenceRequest request = new AgenceRequest();
        request.setNom("Agence Centrale Tunis");
        request.setVille("Tunis");
        request.setAdresse("Avenue Habib Bourguiba");
        request.setTelephone("+21671000000");

        AgenceResponse response = new AgenceResponse(1L, "Agence Centrale Tunis", "Tunis", "Avenue Habib Bourguiba", "+21671000000");
        when(agenceService.create(any(AgenceRequest.class))).thenReturn(response);

        mockMvcAgence.perform(post("/agences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAgence").value(1L))
                .andExpect(jsonPath("$.nom").value("Agence Centrale Tunis"))
                .andExpect(jsonPath("$.ville").value("Tunis"));
    }

    @Test
    @DisplayName("Devrait retourner 404 géré par GlobalExceptionHandler quand la ressource est introuvable")
    void devraisGererResourceNotFoundException() throws Exception {
        when(agenceService.findById(999L)).thenThrow(new ResourceNotFoundException("Agence", 999L));

        mockMvcAgence.perform(get("/agences/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statut").value(404))
                .andExpect(jsonPath("$.erreur").value("Ressource introuvable"))
                .andExpect(jsonPath("$.message").value("Agence introuvable avec l'identifiant 999"));
    }

    @Test
    @DisplayName("Devrait rejeter ClientRequest avec email invalide et champs obligatoires manquants")
    void devraisRejeterClientInvalide() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setNom(""); // Blank
        request.setPrenom(""); // Blank
        request.setEmail("email-non-valide"); // Email format error
        request.setTelephone(""); // Blank
        request.setNumPermis(""); // Blank
        request.setDateInscription(null); // NotNull error

        mockMvcClient.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statut").value(400))
                .andExpect(jsonPath("$.erreurs.email").exists())
                .andExpect(jsonPath("$.erreurs.nom").exists())
                .andExpect(jsonPath("$.erreurs.prenom").exists())
                .andExpect(jsonPath("$.erreurs.dateInscription").exists());
    }

    @Test
    @DisplayName("Devrait accepter ClientRequest valide et retourner 201 avec ClientResponse")
    void devraisCreerClientValide() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setNom("Ben Salah");
        request.setPrenom("Ahmed");
        request.setEmail("ahmed.bensalah@example.com");
        request.setTelephone("+21698000111");
        request.setNumPermis("TN-12345678");
        request.setDateInscription(LocalDate.now());

        ClientResponse response = new ClientResponse(
                10L,
                "Ben Salah",
                "Ahmed",
                "ahmed.bensalah@example.com",
                "+21698000111",
                "TN-12345678",
                LocalDate.now()
        );
        when(clientService.create(any(ClientRequest.class))).thenReturn(response);

        mockMvcClient.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idClient").value(10L))
                .andExpect(jsonPath("$.nom").value("Ben Salah"))
                .andExpect(jsonPath("$.email").value("ahmed.bensalah@example.com"));
    }
}
