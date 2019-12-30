package cn.malgo.annotation.request.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportStructuredDataReq {
    List<MultipartFile> multipartFiles;
}
