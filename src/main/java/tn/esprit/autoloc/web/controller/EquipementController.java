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
import tn.esprit.autoloc.service.IEquipementService;
import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;

@Tag(name = "Équipements", description = "Gestion des équipements des véhicules")
@RestController
@RequestMapping("/equipements")
@RequiredArgsConstructor
public class EquipementController {

    private final IEquipementService equipementService;

    @Operation(summary = "Lister tous les équipements")
    @ApiResponse(responseCode = "200", description = "Liste des équipements récupérée avec succès")
    @GetMapping
    public List<EquipementResponse> findAll() {
        return equipementService.findAll();
    }

    @Operation(summary = "Consulter un équipement par son identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement trouvé"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable")
    })
    @GetMapping("/{id}")
    public EquipementResponse findById(@Parameter(description = "Identifiant de l'équipement") @PathVariable Long id) {
        return equipementService.findById(id);
    }

    @Operation(summary = "Créer un nouvel équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Équipement créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou contraintes non respectées")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipementResponse create(@Valid @RequestBody EquipementRequest request) {
        return equipementService.create(request);
    }

    @Operation(summary = "Modifier un équipement existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de modification invalides"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable")
    })
    @PutMapping("/{id}")
    public EquipementResponse update(
            @Parameter(description = "Identifiant de l'équipement") @PathVariable Long id,
            @Valid @RequestBody EquipementRequest request) {
        return equipementService.update(id, request);
    }

    @Operation(summary = "Supprimer un équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Équipement supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'équipement") @PathVariable Long id) {
        equipementService.deleteById(id);
    }
}
