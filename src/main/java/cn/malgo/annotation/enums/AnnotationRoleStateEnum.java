package cn.malgo.annotation.enums;

/** Created by cjl on 2018/6/14. */
public enum AnnotationRoleStateEnum {
  admin(1),
  auditor(2),
  labelStaff(3),
  practiceStaff(4);

  private int role;

  AnnotationRoleStateEnum(int role) {
    this.role = role;
  }

  public static AnnotationRoleStateEnum valueOf(int role) {
    if (role <= 0 || role > values().length) {
      return null;
    }

    return values()[role - 1];
  }

  public int getRole() {
    return this.role;
  }
}
