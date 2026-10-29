package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.service.IMaintenanceService;
import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.MaintenanceResponse;

@Tag(name = "Maintenances", description = "Gestion des opérations de maintenance")
@RestController
@RequestMapping("/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;

    @Operation(summary = "Lister toutes les maintenances")
    @ApiResponse(responseCode = "200", description = "Liste des opérations de maintenance récupérée avec succès")
    @GetMapping
    public List<MaintenanceResponse> findAll() {
        return maintenanceService.findAll();
    }

    @Operation(summary = "Consulter une maintenance par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance trouvée"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable")
    })
    @GetMapping("/{id}")
    public MaintenanceResponse findById(@Parameter(description = "Identifiant de la maintenance") @PathVariable Long id) {
        return maintenanceService.findById(id);
    }

    @Operation(summary = "Créer une nouvelle opération de maintenance")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Maintenance créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou contraintes non respectées")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceResponse create(@Valid @RequestBody MaintenanceRequest request) {
        return maintenanceService.create(request);
    }

    @Operation(summary = "Modifier une opération de maintenance existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de modification invalides"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable")
    })
    @PutMapping("/{id}")
    public MaintenanceResponse update(
            @Parameter(description = "Identifiant de la maintenance") @PathVariable Long id,
            @Valid @RequestBody MaintenanceRequest request) {
        return maintenanceService.update(id, request);
    }

    @Operation(summary = "Supprimer une opération de maintenance")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Maintenance supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de la maintenance") @PathVariable Long id) {
        maintenanceService.deleteById(id);
    }
}
