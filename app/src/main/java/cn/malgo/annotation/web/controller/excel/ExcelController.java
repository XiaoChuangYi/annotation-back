package cn.malgo.annotation.web.controller.excel;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.model.Concept;
import cn.malgo.annotation.common.util.ExcelUtil;
import cn.malgo.annotation.core.service.term.AtomicTermService;
import cn.malgo.annotation.web.result.ResultVO;
import com.github.pagehelper.Page;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by cjl on 2017/11/27.
 */
@RestController
@RequestMapping(value = "/excel")
public class ExcelController {

    @Autowired
    private AtomicTermService atomicTermService;

    @RequestMapping(value = "/db2excel.do")
    public ResultVO dbToExcel(HttpServletResponse response){
        Page<AnAtomicTerm> pageInfo=atomicTermService.QueryAll(1,4261);
        String fileName = "原子术语"+System.currentTimeMillis()+".xls"; //文件名
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
        //将文件存到指定位置
        try {
            setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultVO.success();
    }
    private void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            response.reset();
            response.setContentType("application/vnd.ms-excel");
//            response.setHeader("Content-disposition", "attachment;filename=student.xls");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
