package cn.malgo.annotation.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjl on 2017/11/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class excel2mysql {

    @Test
    public void select(){
        List<Integer> startPositionList=new ArrayList<>();
        List<Integer> endPositionList=new ArrayList<>();
        startPositionList.add(1);
        startPositionList.add(2);
        startPositionList.add(3);
        startPositionList.add(4);
        endPositionList.add(3);
        endPositionList.add(4);
        endPositionList.add(5);
        endPositionList.add(6);
        List<Integer> commonList=new ArrayList<>();
        commonList.addAll(startPositionList);
        commonList.retainAll(endPositionList);
        startPositionList.removeAll(commonList);
        endPositionList.removeAll(commonList);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>打印<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(JSON.parseArray(JSON.toJSONString(commonList)));
        System.out.println(JSON.parseArray(JSON.toJSONString(startPositionList)));
        System.out.println(JSON.parseArray(JSON.toJSONString(endPositionList)));
    }

//    @Test
//    public  void readExcelAndInsetMysql(){
//        try {
//            Workbook workbook=null;
//            File file=new File("/Users/cjl/Desktop/Sinyoo核心叙词表.xlsx");
//            if(file.getName().endsWith("xlsx")){
//                workbook = new XSSFWorkbook(file);
//            }
//            List<ConceptShow> list=new ArrayList<>();
//            if(workbook!=null){
//                Sheet sheet=workbook.getSheetAt(0);
//                ConceptShow concept=null;
//                int start=62453;
////                int total=sheet.getLastRowNum();
//                int total=64158;
//                for(int rowNum=start;rowNum<=total;rowNum++){
//                    Row row=sheet.getRow(rowNum);
//                    if(row==null)
//                        continue;
//                    concept=new ConceptShow();
//                    for(int cellNum=0;cellNum<row.getPhysicalNumberOfCells();cellNum++){
//                        Cell cell=row.getCell(cellNum);
//                        if(cellNum==0)
//                            concept.setConceptId(cell==null?"0":cell.getStringCellValue());
//                        if(cellNum==1)
////                            if(cell.getCellType()==Cell.CELL_TYPE_ERROR)
////                            {
////                                concept.set
////                            }else{
//                            concept.setPconceptId(cell==null?"0":cell.getStringCellValue());
////                            }
//                        if(cellNum==4)
//                            concept.setConceptName(cell==null?"":cell.getStringCellValue());
//                        if(cellNum==8) {
//                            if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC)
//                            {
//                                concept.setConceptCode(String.valueOf(cell.getNumericCellValue()));
//                            }else
//                            {
//                            concept.setConceptCode(cell == null ? "" : String.valueOf(cell.getStringCellValue()));
//                            }
//                        }
//                            if(cellNum==9)
//                            concept.setConceptType(cell==null?"":(
//                                    cell.getStringCellValue().startsWith("新屿")
//                                            ?cell.getStringCellValue().replace("新屿","麦歌")
//                                            :cell.getStringCellValue()));
//                    }
//                    list.add(concept);
//                }
//                conceptService.insertBatch(list);
//            }
//
//        } catch (InvalidFormatException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
