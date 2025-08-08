package com.beyond.meongnyang.pet.service;

import com.beyond.meongnyang.common.CommonService;
import com.beyond.meongnyang.pet.dto.PetListRes;
import com.beyond.meongnyang.pet.dto.PetRegisterReq;
import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.pet.repository.PetRepository;
import com.beyond.meongnyang.species.entity.Species;
import com.beyond.meongnyang.species.repository.SpeciesRepository;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {
    private final PetRepository petRepository;
    private final SpeciesRepository speciesRepository;
    private final UserRepository userRepository;
    private final CommonService commonService;

    // 애완동물 등록
    public void register(PetRegisterReq req) {
        User user = commonService.getCurrentUser();
        Species species = this.speciesRepository.findById(req.getSpeciesId()).orElseThrow(() -> new EntityNotFoundException("종 정보가 없습니다."));
        this.petRepository.save(req.toEntity(user, species));
    }

    // 유저가 등록한 애완동물 목록
    public PetListRes findByUser() {
        User user = commonService.getCurrentUser();
        List<Pet> pets =this.petRepository.findAllByUserAndIsDel(user, "N");
        return PetListRes.fromEntity(user, pets);
    }

    // 애완동물 수정 펫 수정하기 선택 시 해당 petId 가져옴
    public void updatePet(Long petId, PetRegisterReq req) {
        User user = commonService.getCurrentUser();
        Pet pet = this.petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException("펫 정보가 틀립니다."));
        Species species = this.speciesRepository.findById(req.getSpeciesId()).orElseThrow(() -> new EntityNotFoundException("종 정보가 없습니다."));
        pet.updatePet(req, species);
    }

    // 등록한 애완동물 삭제
    public String deletPet(Long id) {
        User user = commonService.getCurrentUser();
        Pet pet = this.petRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("펫 정보가 틀립니다."));
        pet.delPet();
        return pet.getName();
    }
}
