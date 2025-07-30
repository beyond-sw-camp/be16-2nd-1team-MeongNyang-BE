package com.beyond.meongnyang.species.repository;

import com.beyond.meongnyang.species.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {

}
