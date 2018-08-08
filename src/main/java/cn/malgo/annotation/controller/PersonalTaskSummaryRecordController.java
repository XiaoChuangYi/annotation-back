package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.PersonalTaskSummaryRecordBiz;
import cn.malgo.annotation.request.PersonalTaskSummaryRecordRequest;
import cn.malgo.annotation.vo.PersonalTaskRankSummaryVO;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class PersonalTaskSummaryRecordController {

  private final PersonalTaskSummaryRecordBiz personalTaskSummaryRecordBiz;

  public PersonalTaskSummaryRecordController(
      final PersonalTaskSummaryRecordBiz personalTaskSummaryRecordBiz) {
    this.personalTaskSummaryRecordBiz = personalTaskSummaryRecordBiz;
  }

  @RequestMapping(value = "/get-personal-summary", method = RequestMethod.GET)
  public Response<List<PersonalTaskRankSummaryVO>> getPersonalSummary(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      PersonalTaskSummaryRecordRequest request) {
    return new Response<>(personalTaskSummaryRecordBiz.process(request, userAccount));
  }
}
