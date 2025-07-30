package com.beyond.meongnyang.pet.Repository;

import com.beyond.meongnyang.pet.Entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
}
