package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.Tag;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper extends CommonMapper<Tag> {
    int insertBatch(@Param("listTag")List<String> listTag);
}