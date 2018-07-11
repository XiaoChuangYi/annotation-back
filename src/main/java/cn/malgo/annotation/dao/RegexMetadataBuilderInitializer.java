package cn.malgo.annotation.dao;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuilderInitializer;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.IntegerType;

public class RegexMetadataBuilderInitializer implements MetadataBuilderInitializer {
  @Override
  public void contribute(
      final MetadataBuilder metadataBuilder, final StandardServiceRegistry serviceRegistry) {
    metadataBuilder.applySqlFunction(
        "rlike", new SQLFunctionTemplate(IntegerType.INSTANCE, "?1 rlike ?2"));
  }
}
