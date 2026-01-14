package pet.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import pet.dto.HealthRecordCreateDTO;
import pet.dto.HealthRecordUpdateDTO;
import pet.dto.PetCreateDTO;
import pet.dto.PetUpdateDTO;
import pet.vo.HealthRecordVO;
import pet.vo.PetVO;

public interface PetService {
    /**
     * 获取当前用户的宠物列表
     */
    List<PetVO> listMyPets(Long userId);

    /**
     * 创建宠物档案
     */
    PetVO createPet(Long userId, PetCreateDTO dto);

    /**
     * 更新宠物档案
     */
    PetVO updatePet(Long userId, Long petId, PetUpdateDTO dto);

    /**
     * 删除宠物档案
     */
    void deletePet(Long userId, Long petId);

    /**
     * 上传并更新宠物照片
     */
    PetVO uploadPetPhoto(Long userId, Long petId, MultipartFile file);

    /**
     * 获取宠物健康记录列表
     */
    List<HealthRecordVO> listHealthRecords(Long userId, Long petId);

    /**
     * 创建健康记录
     */
    HealthRecordVO createHealthRecord(Long userId, Long petId, HealthRecordCreateDTO dto);

    /**
     * 更新健康记录
     */
    HealthRecordVO updateHealthRecord(Long userId, Long petId, Long recordId, HealthRecordUpdateDTO dto);

    /**
     * 删除健康记录
     */
    void deleteHealthRecord(Long userId, Long petId, Long recordId);
}
