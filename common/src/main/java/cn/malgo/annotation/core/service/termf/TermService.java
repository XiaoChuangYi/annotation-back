package cn.malgo.annotation.core.service.termf;

import cn.malgo.annotation.common.dal.mapper.TermMapper;
import cn.malgo.annotation.common.dal.model.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cjl on 2017/11/28.
 */
@Service
public class TermService {

    @Autowired
    private TermMapper termMapper;

    /**
     * 按条件查询
     */
//    public List<Term> selectTermsByConceptId(String conceptId){
//        List<Term> termList=termMapper.s
//    }

}
