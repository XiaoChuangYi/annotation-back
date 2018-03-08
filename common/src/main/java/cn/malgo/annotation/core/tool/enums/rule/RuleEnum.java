package cn.malgo.annotation.core.tool.enums.rule;

/**
 * @author 张钟
 * @date 2017/10/25
 */
public enum RuleEnum {

    BASE_RULE("基础规则"),

    DISEASE_RULE("疾病规则"),

    PING_AN_RULE("平安规则"),

    DRUG_RULE("药物规则"),

    FAMILY_RULE("家庭规则"),

    SURGERY_RULE("手术规则"),

    SYMPTOM_RULE("症状规则"),

    RADIATION_RULE("放疗规则"),

    IMAGE_EXAM_RULE("影像规则"),

    ;

    private String message;

    RuleEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
