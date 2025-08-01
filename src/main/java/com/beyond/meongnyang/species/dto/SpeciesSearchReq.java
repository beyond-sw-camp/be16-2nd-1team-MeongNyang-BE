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

public class SpeciesSearchReq {
    private String petOrder;
    private String species;
    private Size size;

    public static SpeciesSearchReq fromEntity(Species species) {
        return SpeciesSearchReq.builder()
                .petOrder(species.getPetOrder())
                .species(species.getSpecies())
                .size(species.getSize())
                .build();
    }
}
