package com.beyond.meongnyang.species.service;

import com.beyond.meongnyang.species.dto.SpeciesListRes;
import com.beyond.meongnyang.species.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor

public class SpeciesService {
    private final SpeciesRepository speciesRepository;

    public List<SpeciesListRes> findAll() {
        List<SpeciesListRes> speciesList = this.speciesRepository.findAll().stream().map(species -> SpeciesListRes.fromEntity(species)).toList();
        return speciesList;
    }
}
