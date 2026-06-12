package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.BusinessParticipation;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.Income;
import pt.ipp.isep.dei.domain.PositionEntry;
import pt.ipp.isep.dei.domain.SubsidyEntry;
import pt.ipp.isep.dei.dto.DeclarationDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper (ESOFT &mdash; DTO pattern) that converts {@link Declaration} domain
 * objects into {@link DeclarationDTO}, so the consultation UIs (US09 and US10)
 * can show the declarations without depending on the domain layer. Each entry
 * section is converted to one display line per entry.
 */
public class DeclarationMapper {

    /**
     * Converts a single declaration into its DTO.
     *
     * @param declaration the domain declaration.
     * @return the corresponding {@link DeclarationDTO}.
     */
    public DeclarationDTO toDTO(Declaration declaration) {
        List<String> positions = new ArrayList<>();
        for (PositionEntry e : declaration.getPositionEntries()) {
            positions.add(e.toString());
        }
        List<String> incomes = new ArrayList<>();
        double totalIncome = 0;
        for (Income e : declaration.getIncomes()) {
            incomes.add(e.toString());
            totalIncome = totalIncome + e.getAmount();
        }
        List<String> subsidies = new ArrayList<>();
        for (SubsidyEntry e : declaration.getSubsidyEntries()) {
            subsidies.add(e.toString());
        }
        List<String> assets = new ArrayList<>();
        for (AssetEntry e : declaration.getAssetEntries()) {
            assets.add(e.toString());
        }
        List<String> businessParticipations = new ArrayList<>();
        for (BusinessParticipation e : declaration.getBusinessParticipations()) {
            businessParticipations.add(e.toString());
        }

        return new DeclarationDTO(
                declaration.getId(),
                declaration.getType().toString(),
                declaration.getSubmissionDate(),
                declaration.getStatus().toString(),
                declaration.toString(),
                declaration.getDetails(),
                positions, incomes, subsidies, assets, businessParticipations,
                totalIncome);
    }

    /**
     * Converts a list of declarations into DTOs, keeping the order.
     *
     * @param declarations the domain declarations.
     * @return the corresponding list of {@link DeclarationDTO}.
     */
    public List<DeclarationDTO> toDTO(List<Declaration> declarations) {
        List<DeclarationDTO> dtos = new ArrayList<>();
        for (Declaration d : declarations) {
            dtos.add(toDTO(d));
        }
        return dtos;
    }
}
