package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.AssetType;
import pt.ipp.isep.dei.dto.AssetEntryDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper (ESOFT &mdash; DTO pattern) that converts {@link AssetEntry} domain
 * objects into {@link AssetEntryDTO}, so the assets consultation UI (US11) can
 * show the entries without depending on the domain layer. Real estate details
 * are public; vehicle and stock details are marked sensitive so the UI masks
 * them for non-journalist users.
 */
public class AssetEntryMapper {

    /**
     * Converts a single asset entry into its DTO.
     *
     * @param entry the domain asset entry.
     * @return the corresponding {@link AssetEntryDTO}.
     */
    public AssetEntryDTO toDTO(AssetEntry entry) {
        String detail = "";
        boolean sensitive = false;
        if (entry.getAssetType() == AssetType.REAL_ESTATE && entry.getRealEstate() != null) {
            detail = "Real Estate: " + entry.getRealEstate().getDescription()
                    + " (" + entry.getRealEstate().getMunicipality() + ")";
        } else if (entry.getAssetType() == AssetType.VEHICLES && entry.getVehicleAsset() != null) {
            detail = "Vehicle: " + entry.getVehicleAsset();
            sensitive = true;
        } else if (entry.getAssetType() == AssetType.STOCKS && entry.getStockAsset() != null) {
            detail = "Stocks: " + entry.getStockAsset();
            sensitive = true;
        }
        return new AssetEntryDTO(entry.getAssetType().toString(), entry.getAssetValue(),
                detail, sensitive);
    }

    /**
     * Converts a list of asset entries into DTOs, keeping the order.
     *
     * @param entries the domain asset entries.
     * @return the corresponding list of {@link AssetEntryDTO}.
     */
    public List<AssetEntryDTO> toDTO(List<AssetEntry> entries) {
        List<AssetEntryDTO> dtos = new ArrayList<>();
        for (AssetEntry e : entries) {
            dtos.add(toDTO(e));
        }
        return dtos;
    }
}
