package cn.malgo.annotation.service.impl.excel;

import cn.malgo.annotation.constants.OutsourcePriceConsts;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dto.User;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.service.SettlementListExportService;
import cn.malgo.annotation.service.UserCenterService;
import cn.malgo.annotation.service.feigns.UserCenterClient;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletResponse;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SettlementListExportServiceImpl implements SettlementListExportService {

  private final UserCenterService userCenterService;
  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationRepository annotationRepository;

  public SettlementListExportServiceImpl(
      final UserCenterService userCenterService,
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationRepository annotationRepository) {
    this.userCenterService = userCenterService;
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationRepository = annotationRepository;
  }

  @Override
  public void exportPersonalSummaryInfo2Excel(
      HttpServletResponse response, long taskId, long assigneeId) throws Exception {
    response.setHeader("Content-Disposition", "attachment;filename=malgo.xls");
    WritableWorkbook workbook = null;
    try {
      workbook = Workbook.createWorkbook(response.getOutputStream());
      WritableSheet sheet = workbook.createSheet("??????????????????????????????", 0);
      setExcelColumn(sheet);

      final List<AnnotationNew> annotationNews = getAnnotationNews(taskId, assigneeId);
      final Map<Long, String> taskMap = getTaskMap();
      final Map<Long, String> userMap = getUserMap();

      IntStream.range(0, annotationNews.size())
          .forEach(
              k -> {
                final AnnotationNew annotationNew = annotationNews.get(k);
                sheet.setColumnView(k + 1, 16);
                try {
                  sheet.setRowView(k + 1, 350);
                } catch (RowsExceededException e) {
                  e.printStackTrace();
                }

                try {
                  // ??????
                  sheet.addCell(
                      new Label(0, k + 1, taskMap.getOrDefault(annotationNew.getTaskId(), "?????????")));

                  // ??????
                  sheet.addCell(
                      new Label(
                          1, k + 1, userMap.getOrDefault(annotationNew.getAssignee(), "?????????")));

                  // ID
                  sheet.addCell(new Label(2, k + 1, String.valueOf(annotationNew.getId())));

                  // ??????
                  sheet.addCell(new Number(3, k + 1, getCurrentAnnotatedWordNum(annotationNew)));

                  // F1
                  sheet.addCell(
                      new Number(
                          4,
                          k + 1,
                          annotationNew.getF1(),
                          new WritableCellFormat(NumberFormats.PERCENT_FLOAT)));

                  // ??????
                  sheet.addCell(new Label(5, k + 1, "???100???3???"));

                  // ???????????????
                  sheet.addCell(
                      new Number(
                          6, k + 1, getCurrentRecordTotalPrice(annotationNew).doubleValue()));

                } catch (WriteException e) {
                  e.printStackTrace();
                }
              });

    } catch (IOException | WriteException e) {
      e.printStackTrace();
    }

    if (workbook != null) {
      workbook.write();
      workbook.close();
    }
  }

  private void setExcelTitle(WritableSheet sheet) throws WriteException {
    sheet.mergeCells(0, 0, 6, 0);
    WritableFont titleFont =
        new WritableFont(
            WritableFont.createFont("??????"),
            12,
            WritableFont.BOLD,
            false,
            UnderlineStyle.NO_UNDERLINE,
            Colour.WHITE);
    WritableCellFormat titleFormat = new WritableCellFormat();
    titleFormat.setFont(titleFont);
    titleFormat.setAlignment(Alignment.CENTRE);
    titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
    titleFormat.setBackground(Colour.BLACK);
    titleFormat.setWrap(true);
    sheet.addCell(new Label(0, 0, "??????????????????????????????", titleFormat));
  }

  private int getCurrentAnnotatedWordNum(AnnotationNew annotationNew) {
    return annotationNew.getTerm().length();
  }

  private Map<Long, String> getTaskMap() {
    return annotationTaskRepository
        .findAll()
        .parallelStream()
        .collect(Collectors.toMap(AnnotationTask::getId, AnnotationTask::getName));
  }

  private Map<Long, String> getUserMap() {
    return userCenterService
        .getUsersByUserCenter()
        .parallelStream()
        .collect(Collectors.toMap(User::getUserId, User::getNickName));
  }

  private void setExcelColumn(final WritableSheet sheet) throws WriteException {
    final int rowIndex = 0;
    sheet.addCell(new Label(0, rowIndex, "????????????"));
    sheet.addCell(new Label(1, rowIndex, "????????????"));
    sheet.addCell(new Label(2, rowIndex, "ANID"));
    sheet.addCell(new Label(3, rowIndex, "????????????"));
    sheet.addCell(new Label(4, rowIndex, "?????????"));
    sheet.addCell(new Label(5, rowIndex, "??????"));
    sheet.addCell(new Label(6, rowIndex, "??????"));
  }

  private List<AnnotationNew> getAnnotationNews(final long taskId, final long assigneeId) {
    List<AnnotationNew> annotationNews;
    if (taskId != 0 && assigneeId == 0) {
      annotationNews =
          annotationRepository.findByTaskIdAndStateIn(
              taskId, Arrays.asList(AnnotationStateEnum.PRE_CLEAN, AnnotationStateEnum.CLEANED));
    } else if (taskId == 0 && assigneeId != 0) {
      annotationNews =
          annotationRepository.findByAssigneeAndStateIn(
              assigneeId,
              Arrays.asList(AnnotationStateEnum.PRE_CLEAN, AnnotationStateEnum.CLEANED));
    } else {
      annotationNews =
          annotationRepository.findAllByTaskIdAndAssigneeAndStateIn(
              taskId,
              assigneeId,
              Arrays.asList(AnnotationStateEnum.PRE_CLEAN, AnnotationStateEnum.CLEANED));
    }
    return annotationNews;
  }

  private BigDecimal getCurrentRecordTotalPrice(final AnnotationNew annotationNew) {
    return BigDecimal.valueOf(OutsourcePriceConsts.PRICE_STAGES.get(0).getPrice())
        .multiply(BigDecimal.valueOf(annotationNew.getF1()))
        .multiply(BigDecimal.valueOf(getCurrentAnnotatedWordNum(annotationNew)))
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
  }
}
