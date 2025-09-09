package com.beyond.meongnyang.species.service;

import com.beyond.meongnyang.species.dto.SpeciesListRes;
import com.beyond.meongnyang.species.dto.SpeciesSearchReq;
import com.beyond.meongnyang.species.entity.Species;
import com.beyond.meongnyang.species.repository.SpeciesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor

public class SpeciesService {
    private final SpeciesRepository speciesRepository;

    public List<SpeciesListRes> findAll() {
        List<SpeciesListRes> speciesList = this.speciesRepository.findAll().stream().map(species -> SpeciesListRes.fromEntity(species)).toList();
        return speciesList;
    }

    // 종 검색 비즈니스 로직
    public List<SpeciesListRes> findAll(SpeciesSearchReq speciesSearchReq) {
        Specification<Species> specification = new Specification<Species>() {
            @Override
            public Predicate toPredicate(Root<Species> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (speciesSearchReq.getPetOrder() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("petOrder"), speciesSearchReq.getPetOrder()));
                }
                if (speciesSearchReq.getSpecies() != null) {
                    predicates.add(criteriaBuilder.like(root.get("species"), "%" + speciesSearchReq.getSpecies() + "%"));
                }
                if (speciesSearchReq.getSize() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("size"), speciesSearchReq.getSize()));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };

        List<Species> speciesList = this.speciesRepository.findAll(specification);

        return speciesList.stream().map(species -> SpeciesListRes.fromEntity(species)).toList();
    }
}
