package com.beyond.meongnyang.pet.service;

import com.beyond.meongnyang.common.S3UploadService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {
    private final PetRepository petRepository;
    private final SpeciesRepository speciesRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    // 애완동물 등록
    public void register(PetRegisterReq req, MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보가 틀립니다."));
        Species species = speciesRepository.findById(req.getSpeciesId())
                .orElseThrow(() -> new EntityNotFoundException("종 정보가 없습니다."));

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = s3UploadService.upload(file);
        }

        // 업로드한 URL을 DTO에 반영
        req.setUrl(imageUrl);

        petRepository.save(req.toEntity(user, species));
    }


    // 유저가 등록한 애완동물 목록
    public PetListRes findByUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("사용자 정보가 틀립니다."));
        List<Pet> pets =this.petRepository.findAllByUserAndIsDel(user, "N");
        return PetListRes.fromEntity(user, pets);
    }

    // 애완동물 수정 펫 수정하기 선택 시 해당 petId 가져옴
    public void updatePet(Long petId, PetRegisterReq req, MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보가 틀립니다."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("펫 정보가 틀립니다."));

        Species species = speciesRepository.findById(req.getSpeciesId())
                .orElseThrow(() -> new EntityNotFoundException("종 정보가 없습니다."));

        // 전체 교체 방식일 경우 기존 이미지 삭제
        if (file != null && !file.isEmpty()) {
            if (pet.getPetProfileUrl() != null) {
                s3UploadService.delete(pet.getPetProfileUrl()); // 기존 이미지 삭제
            }
            String newImageUrl = s3UploadService.upload(file);
            req.setUrl(newImageUrl);
        } else {
            // 이미지 안 보냈으면 기존 이미지 유지
            req.setUrl(pet.getPetProfileUrl());
        }

        // 엔티티 수정
        pet.updatePet(req, species);
    }

    // 등록한 애완동물 삭제
    public String deletPet(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("사용자 정보가 틀립니다."));
        Pet pet = this.petRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("펫 정보가 틀립니다."));
        pet.delPet();
        return pet.getName();
    }
}
