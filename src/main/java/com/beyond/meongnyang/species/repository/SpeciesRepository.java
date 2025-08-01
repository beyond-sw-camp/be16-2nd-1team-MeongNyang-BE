package com.beyond.meongnyang.species.repository;

import com.beyond.meongnyang.species.entity.Species;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {
   List<Species> findAll(Specification<Species> specification);
}
