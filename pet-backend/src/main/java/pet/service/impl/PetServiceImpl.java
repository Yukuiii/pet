package pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pet.dto.*;
import pet.entity.HealthRecord;
import pet.entity.Pet;
import pet.mapper.HealthRecordMapper;
import pet.mapper.PetMapper;
import pet.service.PetService;
import pet.vo.HealthRecordVO;
import pet.vo.PetVO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetMapper petMapper;
    private final HealthRecordMapper healthRecordMapper;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public List<PetVO> listMyPets(Long userId) {
        ensureUserId(userId);
        LambdaQueryWrapper<Pet> qw = new LambdaQueryWrapper<>();
        qw.eq(Pet::getUserId, userId);
        List<Pet> pets = petMapper.selectList(qw);
        List<PetVO> vos = new ArrayList<>();
        for (Pet p : pets) {
            vos.add(toVO(p));
        }
        return vos;
    }

    @Override
    public PetVO createPet(Long userId, PetCreateDTO dto) {
        ensureUserId(userId);
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new RuntimeException("宠物姓名不能为空");
        }
        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setName(dto.getName().trim());
        pet.setBreed(dto.getBreed());
        pet.setGender(dto.getGender());
        pet.setBirthday(dto.getBirthday());
        pet.setCreateTime(LocalDateTime.now());
        pet.setUpdateTime(LocalDateTime.now());
        petMapper.insert(pet);
        return toVO(pet);
    }

    @Override
    public PetVO updatePet(Long userId, Long petId, PetUpdateDTO dto) {
        Pet pet = getOwnedPet(userId, petId);
        if (dto.getName() != null) {
            String name = dto.getName().trim();
            if (name.isEmpty()) {
                throw new RuntimeException("宠物姓名不能为空");
            }
            pet.setName(name);
        }
        if (dto.getBreed() != null) pet.setBreed(dto.getBreed());
        if (dto.getGender() != null) pet.setGender(dto.getGender());
        if (dto.getBirthday() != null) pet.setBirthday(dto.getBirthday());
        if (dto.getPhoto() != null) pet.setPhoto(dto.getPhoto());
        pet.setUpdateTime(LocalDateTime.now());
        petMapper.updateById(pet);
        return toVO(pet);
    }

    @Override
    public void deletePet(Long userId, Long petId) {
        Pet pet = getOwnedPet(userId, petId);
        petMapper.deleteById(pet.getId());
    }

    @Override
    public PetVO uploadPetPhoto(Long userId, Long petId, MultipartFile file) {
        Pet pet = getOwnedPet(userId, petId);
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择宠物照片文件");
        }
        String contentType = file.getContentType();
        Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new RuntimeException("仅支持上传 jpg/png/gif/webp 图片");
        }
        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
        String filename = UUID.randomUUID() + extension;
        Path photoDir = Paths.get(uploadDir, "pet_photos");
        try {
            Files.createDirectories(photoDir);
            Path target = photoDir.resolve(filename).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("照片上传失败");
        }
        pet.setPhoto("/uploads/pet_photos/" + filename);
        pet.setUpdateTime(LocalDateTime.now());
        petMapper.updateById(pet);
        return toVO(pet);
    }

    @Override
    public List<HealthRecordVO> listHealthRecords(Long userId, Long petId) {
        Pet pet = getOwnedPet(userId, petId);
        LambdaQueryWrapper<HealthRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(HealthRecord::getPetId, pet.getId()).orderByDesc(HealthRecord::getRecordTime);
        List<HealthRecord> list = healthRecordMapper.selectList(qw);
        List<HealthRecordVO> vos = new ArrayList<>();
        for (HealthRecord hr : list) {
            vos.add(toVO(hr));
        }
        return vos;
    }

    @Override
    public HealthRecordVO createHealthRecord(Long userId, Long petId, HealthRecordCreateDTO dto) {
        Pet pet = getOwnedPet(userId, petId);
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new RuntimeException("标题不能为空");
        }
        HealthRecord hr = new HealthRecord();
        hr.setPetId(pet.getId());
        hr.setRecordTime(dto.getRecordTime() == null ? LocalDateTime.now() : dto.getRecordTime());
        hr.setTitle(dto.getTitle().trim());
        hr.setContent(dto.getContent());
        hr.setCreateTime(LocalDateTime.now());
        hr.setUpdateTime(LocalDateTime.now());
        healthRecordMapper.insert(hr);
        return toVO(hr);
    }

    @Override
    public HealthRecordVO updateHealthRecord(Long userId, Long petId, Long recordId, HealthRecordUpdateDTO dto) {
        Pet pet = getOwnedPet(userId, petId);
        HealthRecord hr = healthRecordMapper.selectById(recordId);
        if (hr == null || !hr.getPetId().equals(pet.getId())) {
            throw new RuntimeException("健康记录不存在或无权限");
        }
        if (dto.getRecordTime() != null) hr.setRecordTime(dto.getRecordTime());
        if (dto.getTitle() != null) {
            String t = dto.getTitle().trim();
            if (t.isEmpty()) throw new RuntimeException("标题不能为空");
            hr.setTitle(t);
        }
        if (dto.getContent() != null) hr.setContent(dto.getContent());
        hr.setUpdateTime(LocalDateTime.now());
        healthRecordMapper.updateById(hr);
        return toVO(hr);
    }

    @Override
    public void deleteHealthRecord(Long userId, Long petId, Long recordId) {
        Pet pet = getOwnedPet(userId, petId);
        HealthRecord hr = healthRecordMapper.selectById(recordId);
        if (hr == null || !hr.getPetId().equals(pet.getId())) {
            throw new RuntimeException("健康记录不存在或无权限");
        }
        healthRecordMapper.deleteById(recordId);
    }

    private Pet getOwnedPet(Long userId, Long petId) {
        ensureUserId(userId);
        Pet pet = petMapper.selectById(petId);
        if (pet == null || !pet.getUserId().equals(userId)) {
            throw new RuntimeException("宠物不存在或无权限");
        }
        return pet;
    }

    private void ensureUserId(Long userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
    }

    private PetVO toVO(Pet p) {
        PetVO vo = new PetVO();
        vo.setId(p.getId());
        vo.setName(p.getName());
        vo.setBreed(p.getBreed());
        vo.setGender(p.getGender());
        vo.setBirthday(p.getBirthday());
        vo.setPhoto(p.getPhoto());
        return vo;
    }

    private HealthRecordVO toVO(HealthRecord hr) {
        HealthRecordVO vo = new HealthRecordVO();
        vo.setId(hr.getId());
        vo.setRecordTime(hr.getRecordTime());
        vo.setTitle(hr.getTitle());
        vo.setContent(hr.getContent());
        return vo;
    }
}

