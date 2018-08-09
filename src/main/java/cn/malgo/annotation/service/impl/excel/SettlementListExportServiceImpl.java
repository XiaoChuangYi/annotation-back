package cn.malgo.annotation.service.impl.excel;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.PersonalAnnotatedEstimatePriceRepository;
import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.service.SettlementListExportService;
import cn.malgo.annotation.utils.AnnotationConvert;
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
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SettlementListExportServiceImpl implements SettlementListExportService {

  private final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository;
  private final UserAccountRepository userAccountRepository;
  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationRepository annotationRepository;

  public SettlementListExportServiceImpl(
      final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository,
      final UserAccountRepository userAccountRepository,
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationRepository annotationRepository) {
    this.personalAnnotatedEstimatePriceRepository = personalAnnotatedEstimatePriceRepository;
    this.userAccountRepository = userAccountRepository;
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationRepository = annotationRepository;
  }

  @Override
  public void exportPersonalSummaryInfo2Excel(
      HttpServletResponse response, long taskId, long assigneeId) throws Exception {
    response.setHeader("Content-Disposition", "attachment;filename=malgo.xls");
    WritableWorkbook workbook = null;
    final boolean taskFlag = (taskId != 0 && assigneeId == 0);
    try {
      workbook = Workbook.createWorkbook(response.getOutputStream());
      WritableSheet sheet = workbook.createSheet("麦歌标注系统结算清单", 0);
      //      setExcelTitle(sheet);
      setExcelColumn(taskFlag, sheet);
      final List<AnnotationNew> annotationNews = getAnnotationNews(taskFlag, taskId, assigneeId);
      IntStream.range(0, annotationNews.size())
          .forEach(
              k -> {
                final AnnotationNew annotationNew = annotationNews.get(k);
                sheet.setColumnView(k, 16);
                try {
                  sheet.setRowView(k, 350);
                } catch (RowsExceededException e) {
                  e.printStackTrace();
                }
                try {
                  if (taskFlag) {
                    sheet.addCell(
                        new Label(
                            0, k, getUserMap().getOrDefault(annotationNew.getAssignee(), "无名氏")));
                  } else {
                    sheet.addCell(
                        new Label(
                            0, k, getTaskMap().getOrDefault(annotationNew.getTaskId(), "无批次")));
                  }
                  sheet.addCell(new Label(1, k, String.valueOf(annotationNew.getId())));
                  sheet.addCell(
                      new Label(
                          2, k, String.format("%d字", getCurrentAnnotatedWordNum(annotationNew))));
                  sheet.addCell(new Label(3, k, annotationNew.getPrecisionRate() * 100 + "%"));
                  sheet.addCell(
                      new Label(
                          4,
                          k,
                          String.format(
                              "每100字%d元",
                              getAnnotationUnitPrice(
                                  annotationNew.getTaskId(), annotationNew.getAssignee()))));
                  sheet.addCell(
                      new Label(
                          5, k, String.format("%d元", getCurrentRecordTotalPrice(annotationNew))));
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
    } finally {
      if (workbook != null) {
        workbook.write();
        workbook.close();
      }
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
    return AnnotationConvert.getEntitiesFromAnnotation(annotationNew.getFinalAnnotation())
        .stream()
        .mapToInt(value -> value.getTerm().length())
        .sum();
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

  private void setExcelColumn(final boolean taskFlag, final WritableSheet sheet)
      throws WriteException {
    final int rowIndex = 0;
    if (taskFlag) {
      sheet.addCell(new Label(0, rowIndex, "人员名称"));
    } else {
      sheet.addCell(new Label(0, rowIndex, "批次名称"));
    }
    sheet.addCell(new Label(1, rowIndex, "ANID"));
    sheet.addCell(new Label(2, rowIndex, "标注字数"));
    sheet.addCell(new Label(3, rowIndex, "准确率"));
    sheet.addCell(new Label(4, rowIndex, "单价"));
    sheet.addCell(new Label(5, rowIndex, "合价"));
  }

  private List<AnnotationNew> getAnnotationNews(
      final boolean taskFlag, final long taskId, final long assigneeId) {
    List<AnnotationNew> annotationNews;
    if (taskFlag) {
      annotationNews =
          annotationRepository.findByTaskIdEqualsAndStateIn(
              taskId, Arrays.asList(AnnotationStateEnum.PRE_CLEAN, AnnotationStateEnum.CLEANED));
    } else {
      annotationNews =
          annotationRepository.findByAssigneeEqualsAndStateIn(
              assigneeId,
              Arrays.asList(AnnotationStateEnum.PRE_CLEAN, AnnotationStateEnum.CLEANED));
    }
    return annotationNews;
  }

  private int getAnnotationUnitPrice(final long taskId, final long assigneeId) {
    final PersonalAnnotatedTotalWordNumRecord current =
        personalAnnotatedEstimatePriceRepository.findByTaskIdEqualsAndAssigneeIdEquals(
            taskId, assigneeId);
    if (current != null) {
      if (current.getTotalWordNum() >= 0 && current.getTotalWordNum() < 20000) {
        return 2;
      }
      if (current.getTotalWordNum() >= 20000 && current.getTotalWordNum() < 30000) {
        return 3;
      }
      if (current.getTotalWordNum() >= 30000 && current.getTotalWordNum() < 40000) {
        return 4;
      }
      if (current.getTotalWordNum() >= 40000) {
        return 6;
      }
    }
    return 0;
  }

  private BigDecimal getCurrentRecordTotalPrice(final AnnotationNew annotationNew) {
    return BigDecimal.valueOf(
            getAnnotationUnitPrice(annotationNew.getTaskId(), annotationNew.getAssignee()))
        .multiply(BigDecimal.valueOf(annotationNew.getPrecisionRate()))
        .multiply(BigDecimal.valueOf(getCurrentAnnotatedWordNum(annotationNew)));
  }
}
