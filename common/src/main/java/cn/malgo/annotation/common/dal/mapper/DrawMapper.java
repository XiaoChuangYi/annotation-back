package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.Draw;
import cn.malgo.annotation.common.dal.util.CommonMapper;

import java.util.List;

public interface DrawMapper extends CommonMapper<Draw> {
    List<Draw> selectDrawLeftJoinType();
}