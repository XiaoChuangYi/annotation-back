package cn.malgo.annotation.config;

/** 标注系统 权限常量 */
public class PermissionConstant {

  private static final String ANNOTATION_BLOCK_LIST = "annotation:block:list";
  private static final String ANNOTATION_BLOCK_IMPORT = "annotation:block:insert"; // 新增语料
  private static final String ANNOTATION_BLOCK_INSERT = "annotation:block:add-to-batch"; // 添加语料到批次
  private static final String ANNOTATION_BLOCK_ABANDON = "annotation:block:abandon"; // 语料放弃
  private static final String ANNOTATION_BLOCK_BATCH_ENTITY_DELETE =
      "annotation:block:batch-entity-delete"; // 语料实体批量删除
  private static final String ANNOTATION_BLOCK_BATCH_RELATION_DELETE =
      "annotation:block:batch-entity-delete"; // 语料关联批量删除
  private static final String ANNOTATION_BLOCK_BATCH_RELATION_UPDATE =
      "annotation:block:batch-entity-update"; // 语料关联批量更新
  private static final String ANNOTATION_BLOCK_ERROR_FIX = "annotation:block:error-fix"; // 错误修复
  private static final String ANNOTATION_BLOCK_ERROR_SEARCH =
      "annotation:block:error-search"; // 错误查询

  private static final String ANNOTATION_BLOCK_ENTITY_SEARCH =
      "annotation:block:entity-search"; // 实体查询
  private static final String ANNOTATION_BLOCK_RELATION_SEARCH =
      "annotation:block:entity-search"; // 关联查询

  private static final String ANNOTATION_TYPE_LIST = "annotation:type:list"; // 标注类型列表

  private static final String ANNOTATION_RELATION_LIMIT_RULE =
      "annotation:relation-rule:list"; // 关联限制规则列表

  private static final String ANNOTATION_TASK_LIST = "annotation:task:list"; // 标注列表
  private static final String ANNOTATION_TASK_SINGLE = "annotation:task:single"; // 单条标注
  private static final String ANNOTATION_TASK_INSERT = "annotation:task:insert"; // 标注新增
  private static final String ANNOTATION_TASK_UPDATE = "annotation:task:update"; // 标注更新
  private static final String ANNOTATION_TASK_DELETE = "annotation:task:list"; // 标注删除
  private static final String ANNOTATION_TASK_COMMIT = "annotation:task:commit"; // 标注提交
  private static final String ANNOTATION_TASK_RECYCLE = "annotation:task:recycle"; // 标注回收
  private static final String ANNOTATION_TASK_DESIGNATE = "annotation:task:designate"; // 标注指派
  private static final String ANNOTATION_TASK_UNDISTRIBUTED_WORD_NUM =
      "annotation:task:undistributed-word-num"; // 获取未指派总字数

  private static final String ANNOTATION_BATCH_LIST = "annotation:batch:list"; // 批次列表
  private static final String ANNOTATION_BATCH_INSERT = "annotation:batch:insert"; // 批次新增
  private static final String ANNOTATION_BATCH_UPDATE = "annotation:batch:update"; // 批次结束
  private static final String ANNOTATION_BATCH_DETAILS = "annotation:batch:details"; // 批次详情
  private static final String ANNOTATION_BATCH_CLEANED = "annotation:batch:cleaned"; // 批次清洗

  private static final String ANNOTATION_SUMMARY_STAFF_ESTIMATE =
      "annotation:summary:staff-estimate"; // 人员评估
  private static final String ANNOTATION_SUMMARY_DOING_TASK =
      "annotation:summary:doing-task"; // 进行中批次评估
}
