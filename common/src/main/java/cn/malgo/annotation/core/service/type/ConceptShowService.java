package cn.malgo.annotation.core.service.type;

import cn.malgo.annotation.common.dal.mapper.ConceptShowMapper;
import cn.malgo.annotation.common.dal.model.ConceptShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cjl on 2017/11/22.
 */
@Service
public class ConceptShowService {
    @Autowired
    private ConceptShowMapper conceptShowMapper;

    /**
     * 根据传入的conceptId，查询所有父ID为传入conceptId的记录数
     * @param conceptId
     */
    public List<ConceptShow> selectAllByConcepId(String conceptId){
        List<ConceptShow> conceptShowList = conceptShowMapper.selectConceptByConceptId(conceptId);
        return conceptShowList;
    }


}
