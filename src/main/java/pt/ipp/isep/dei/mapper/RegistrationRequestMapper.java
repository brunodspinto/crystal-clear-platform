package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.dto.RegistrationRequestDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper (ESOFT DTO pattern) that converts {@link RegistrationRequest} domain
 * objects into {@link RegistrationRequestDTO}. The controller delegates this
 * conversion to the mapper so the domain layer is not exposed to the UI.
 */
public class RegistrationRequestMapper {

    /**
     * Converts a single registration request into its DTO.
     *
     * @param request the domain registration request.
     * @return the corresponding {@link RegistrationRequestDTO}.
     */
    public RegistrationRequestDTO toDTO(RegistrationRequest request) {
        return new RegistrationRequestDTO(
                request.getFullName(), request.getEmail(), request.getRole(),
                request.getSubmissionDate(), request.getIdentificationDocument());
    }

    /**
     * Converts a list of registration requests into a list of DTOs.
     *
     * @param requests the domain registration requests.
     * @return the corresponding list of {@link RegistrationRequestDTO}.
     */
    public List<RegistrationRequestDTO> toDTO(List<RegistrationRequest> requests) {
        List<RegistrationRequestDTO> dtos = new ArrayList<>();
        for (RegistrationRequest request : requests) {
            dtos.add(toDTO(request));
        }
        return dtos;
    }
}
