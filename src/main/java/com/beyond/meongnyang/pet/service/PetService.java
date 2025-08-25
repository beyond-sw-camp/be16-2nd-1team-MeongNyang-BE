package com.beyond.meongnyang.pet.service;

import com.beyond.meongnyang.common.service.CommonService;
import com.beyond.meongnyang.common.service.S3UploadService;
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
import org.springframework.security.access.AccessDeniedException;
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
    private final CommonService commonService;
    private final S3UploadService s3UploadService;

    // 애완동물 등록
    public void register(PetRegisterReq req, MultipartFile file) {
        User user = commonService.getCurrentUser();
        Species species = speciesRepository.findById(req.getSpeciesId())
                .orElseThrow(() -> new EntityNotFoundException("종 정보가 없습니다."));

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = s3UploadService.upload(file);
        }
        req.setUrl(imageUrl);

        Pet pet = petRepository.save(req.toEntity(user, species));
        if(user.getMainPetId() == null) {
            user.changeMainPet(pet.getId());
        }
    }


    // 유저가 등록한 애완동물 목록
    public PetListRes findByUser(Long userId) {
        User user;
        if(userId != null){
            user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
        } else {
            user = commonService.getCurrentUser();
        }
        List<Pet> pets =this.petRepository.findAllByUserAndDelYn(user, "N");
        return PetListRes.fromEntity(user, pets);
    }

    // 애완동물 수정 펫 수정하기 선택 시 해당 petId 가져옴
    public void updatePet(Long petId, PetRegisterReq req, MultipartFile file) {
        User user = commonService.getCurrentUser();

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("펫 정보가 틀립니다."));

        Species species = speciesRepository.findById(req.getSpeciesId())
                .orElseThrow(() -> new EntityNotFoundException("종 정보가 없습니다."));

        if (!pet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인 소유의 반려동물만 수정할 수 있습니다.");
        }

        if (file != null && !file.isEmpty()) {
            if (pet.getPetProfileUrl() != null) {
                s3UploadService.delete(pet.getPetProfileUrl());
            }
            String newImageUrl = s3UploadService.upload(file);
            req.setUrl(newImageUrl);
        } else {
            req.setUrl(pet.getPetProfileUrl());
        }
        pet.updatePet(req, species);
    }

    // 등록한 애완동물 삭제
    public String deletPet(Long id) {
        Pet pet = this.petRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("펫 정보가 틀립니다."));
        pet.delPet();
        return pet.getName();
    }

    // 펫 등록된 상태인지 확인
    public Boolean checkPetExist(){
        User user = commonService.getCurrentUser();
        if (user.getMainPetId() == null) {
            throw new AccessDeniedException("등록된 펫이 없습니다. 펫 등록 후 사용 가능합니다.");
        } else {
            return true;
        }
    }
}
