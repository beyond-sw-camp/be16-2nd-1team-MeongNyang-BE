package com.beyond.meongnyang.pet.repository;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findAllByUserAndDelYn(User user, String delYn);
    @Query("select p.petProfileUrl from Pet p where p.id = :id")
    Optional<String> findPetProfileUrlById(@Param("id") Long id);
}
