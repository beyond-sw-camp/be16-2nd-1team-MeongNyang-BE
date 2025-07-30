package com.beyond.meongnyang.species.dto;

import com.beyond.meongnyang.species.entity.Size;
import com.beyond.meongnyang.species.entity.Species;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SpeciesListRes {
    private Long speciesId;
    private String petOrder;
    private String species;
    private Size size;

    public static SpeciesListRes fromEntity(Species species) {
        return SpeciesListRes.builder()
                .speciesId(species.getId())
                .petOrder(species.getPetOrder())
                .species(species.getSpecies())
                .size(species.getSize())
                .build();
    }
}
