package cn.malgo.annotation.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjl on 2018/1/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Tess4jTest {
//    @Test
//    public void Test(){
//        URL  pathName=Thread.currentThread().getContextClassLoader().getResource("static/img/ali-server.jpg");
//        System.out.println(pathName);
//        File imgFile =new File(pathName.getPath());
//        ITesseract instance = new Tesseract(); // JNA接口映射
//        System.out.println(imgFile.canRead());
//        try{
//            if(imgFile!=null){
//                String result=instance.doOCR(imgFile);
//                System.out.println(result);
//            }else {
//                System.out.println("no img");
//            }
//        }catch (TesseractException e){
//            System.out.println(e.getMessage());
//        }
//    }
    @Test
    public void testCmd(){
        URL  pathName=Thread.currentThread().getContextClassLoader().getResource("static/cmd/江西省门诊.jpeg");
        System.out.println(pathName);
        String shPath=pathName.getPath();
        String command = "/bin/sh tesseract " + shPath+" out";
        List<String> processList = new ArrayList<>();
        System.out.println(command);
        try {
            Runtime.getRuntime().exec(command);
//            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            File file=new File("/Users/cjl/out.txt");
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                processList.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<");
        for (String line : processList) {
            System.out.println(line);
        }

    }

}
