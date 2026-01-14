package pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pet.entity.HealthRecord;

@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}

