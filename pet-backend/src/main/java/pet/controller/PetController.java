package pet.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pet.common.Result;
import pet.dto.HealthRecordCreateDTO;
import pet.dto.HealthRecordUpdateDTO;
import pet.dto.PetCreateDTO;
import pet.dto.PetUpdateDTO;
import pet.service.PetService;
import pet.vo.HealthRecordVO;
import pet.vo.PetVO;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    /**
     * 获取当前用户的宠物列表
     */
    @GetMapping
    public Result<List<PetVO>> list(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            return Result.success(petService.listMyPets(userId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建宠物档案（绑定当前用户）
     */
    @PostMapping
    public Result<PetVO> create(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody PetCreateDTO dto) {
        try {
            return Result.success(petService.createPet(userId, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新宠物档案（仅允许更新自己的宠物）
     */
    @PutMapping("/{petId}")
    public Result<PetVO> update(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long petId,
            @RequestBody PetUpdateDTO dto) {
        try {
            return Result.success(petService.updatePet(userId, petId, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除宠物档案（会级联删除健康记录）
     */
    @DeleteMapping("/{petId}")
    public Result<Void> delete(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long petId) {
        try {
            petService.deletePet(userId, petId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传并更新宠物照片
     */
    @PostMapping(value = "/{petId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<PetVO> uploadPhoto(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long petId,
            @RequestPart("file") MultipartFile file) {
        try {
            return Result.success(petService.uploadPetPhoto(userId, petId, file));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取某只宠物的健康记录列表（一对多）
     */
    @GetMapping("/{petId}/records")
    public Result<List<HealthRecordVO>> listRecords(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long petId) {
        try {
            return Result.success(petService.listHealthRecords(userId, petId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增健康记录（绑定宠物）
     */
    @PostMapping("/{petId}/records")
    public Result<HealthRecordVO> createRecord(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long petId,
            @RequestBody HealthRecordCreateDTO dto) {
        try {
            return Result.success(petService.createHealthRecord(userId, petId, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新健康记录（仅允许更新当前宠物下的记录）
     */
    @PutMapping("/{petId}/records/{recordId}")
    public Result<HealthRecordVO> updateRecord(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long petId,
            @PathVariable Long recordId,
            @RequestBody HealthRecordUpdateDTO dto) {
        try {
            return Result.success(petService.updateHealthRecord(userId, petId, recordId, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除健康记录（仅允许删除当前宠物下的记录）
     */
    @DeleteMapping("/{petId}/records/{recordId}")
    public Result<Void> deleteRecord(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long petId,
            @PathVariable Long recordId) {
        try {
            petService.deleteHealthRecord(userId, petId, recordId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
