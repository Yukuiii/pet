package pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pet.entity.Announcement;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}

