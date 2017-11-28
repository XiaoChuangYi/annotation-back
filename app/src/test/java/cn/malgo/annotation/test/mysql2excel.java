package cn.malgo.annotation.test;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.util.ExcelUtil;
import cn.malgo.annotation.core.service.corpus.AtomicTermService;
import com.github.pagehelper.Page;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by cjl on 2017/11/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class mysql2excel {

    @Autowired
    private AtomicTermService atomicTermService;

    @Test
    public  void readMysqlAndToExcel(){
        Page<AnAtomicTerm> pageInfo=atomicTermService.QueryAll(1,10);
        String [] title = new String[]{"ID","术语","类型","状态","来源ID","生成时间","更新时间"};//标题
        List<AnAtomicTerm> anAtomicTermList=pageInfo.getResult();
        String [][]values = new String[anAtomicTermList.size()][];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(int i=0;i<anAtomicTermList.size();i++){
            values[i] = new String[title.length];
            //将对象内容转换成string
            AnAtomicTerm obj = anAtomicTermList.get(i);
            values[i][0] = obj.getId()+"";
            values[i][1] = obj.getTerm();
            values[i][2] = obj.getType();
            values[i][3] = obj.getState();
            values[i][4] = obj.getFromAnId();
            values[i][5] = sdf.format(obj.getGmtCreated());
            values[i][6] = sdf.format(obj.getGmtModified());
        }
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("原子术语表", title, values, null);
    }
}
