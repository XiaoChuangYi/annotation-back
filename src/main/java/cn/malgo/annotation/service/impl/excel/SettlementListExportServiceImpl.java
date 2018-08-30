package cn.malgo.annotation.service.impl.excel;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.service.SettlementListExportService;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SettlementListExportServiceImpl implements SettlementListExportService {

  private final UserAccountRepository userAccountRepository;
  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationRepository annotationRepository;

  public SettlementListExportServiceImpl(
      final UserAccountRepository userAccountRepository,
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationRepository annotationRepository) {
    this.userAccountRepository = userAccountRepository;
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
      WritableSheet sheet = workbook.createSheet("麦歌标注系统结算清单", 0);
      setExcelColumn(sheet);
      final List<AnnotationNew> annotationNews = getAnnotationNews(taskId, assigneeId);
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
                  // 批次
                  sheet.addCell(
                      new Label(
                          0, k + 1, getTaskMap().getOrDefault(annotationNew.getTaskId(), "无批次")));

                  // 姓名
                  sheet.addCell(
                      new Label(
                          1, k + 1, getUserMap().getOrDefault(annotationNew.getAssignee(), "无名氏")));

                  // ID
                  sheet.addCell(new Label(2, k + 1, String.valueOf(annotationNew.getId())));

                  // 字数
                  sheet.addCell(
                      new Number(
                          3,
                          k + 1,
                          getCurrentAnnotatedWordNum(annotationNew),
                          new WritableCellFormat(NumberFormats.PERCENT_FLOAT)));

                  // F1
                  sheet.addCell(
                      new Number(
                          4,
                          k + 1,
                          new BigDecimal(getF1(annotationNew))
                              .setScale(2, BigDecimal.ROUND_HALF_UP)
                              .doubleValue()));

                  // 单价
                  sheet.addCell(new Label(5, k + 1, "每100字3元"));

                  // 当前条价格
                  sheet.addCell(
                      new Number(
                          6, k + 1, getCurrentRecordTotalPrice(annotationNew).doubleValue()));

                } catch (WriteException e) {
                  e.printStackTrace();
                }
              });

    } catch (IOException e) {
      e.printStackTrace();
    } catch (RowsExceededException e) {
      e.printStackTrace();
    } catch (WriteException e) {
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
            WritableFont.createFont("黑体"),
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
    sheet.addCell(new Label(0, 0, "麦歌标注系统结算清单", titleFormat));
  }

  @NotNull
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
    return userAccountRepository
        .findAll()
        .parallelStream()
        .collect(Collectors.toMap(UserAccount::getId, UserAccount::getAccountName));
  }

  private void setExcelColumn(final WritableSheet sheet) throws WriteException {
    final int rowIndex = 0;
    sheet.addCell(new Label(0, rowIndex, "批次名称"));
    sheet.addCell(new Label(1, rowIndex, "人员名称"));
    sheet.addCell(new Label(2, rowIndex, "ANID"));
    sheet.addCell(new Label(3, rowIndex, "标注字数"));
    sheet.addCell(new Label(4, rowIndex, "准确率"));
    sheet.addCell(new Label(5, rowIndex, "单价"));
    sheet.addCell(new Label(6, rowIndex, "合价"));
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
    return BigDecimal.valueOf(3)
        .multiply(BigDecimal.valueOf(getF1(annotationNew)))
        .multiply(BigDecimal.valueOf(getCurrentAnnotatedWordNum(annotationNew)))
        .divide(BigDecimal.valueOf(100))
        .setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  private double getF1(final AnnotationNew annotationNew) {
    final Double precisionRate = annotationNew.getPrecisionRate();
    final Double recallRate = annotationNew.getRecallRate();

    if (precisionRate == null || recallRate == null) {
      return 0d;
    } else if (precisionRate + recallRate == 0) {
      return 0d;
    } else {
      return 2 * precisionRate * recallRate / (precisionRate + recallRate);
    }
  }
}
