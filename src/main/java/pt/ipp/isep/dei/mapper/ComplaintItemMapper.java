package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.ComplaintItem;
import pt.ipp.isep.dei.dto.ComplaintItemDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper (ESOFT &mdash; DTO pattern) that converts {@link ComplaintItem} domain
 * objects into {@link ComplaintItemDTO}, so the UI can show the grievances of
 * the complaint being built without depending on the domain layer.
 */
public class ComplaintItemMapper {

    /**
     * Converts a single grievance into its DTO.
     *
     * @param item the domain grievance.
     * @return the corresponding {@link ComplaintItemDTO}.
     */
    public ComplaintItemDTO toDTO(ComplaintItem item) {
        return new ComplaintItemDTO(item.getDescription(), item.getComplaintDate(),
                item.getPoliticalFunction());
    }

    /**
     * Converts a list of grievances into a list of DTOs.
     *
     * @param items the domain grievances.
     * @return the corresponding list of {@link ComplaintItemDTO}.
     */
    public List<ComplaintItemDTO> toDTO(List<ComplaintItem> items) {
        List<ComplaintItemDTO> dtos = new ArrayList<>();
        for (ComplaintItem item : items) {
            dtos.add(toDTO(item));
        }
        return dtos;
    }
}
