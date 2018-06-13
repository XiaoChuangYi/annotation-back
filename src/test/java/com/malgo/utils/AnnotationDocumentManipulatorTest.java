package com.malgo.utils;

import cn.malgo.core.definition.Entity;
import com.malgo.utils.entity.AnnotationDocument;
import com.malgo.utils.entity.RelationEntity;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

@Slf4j
public class AnnotationDocumentManipulatorTest {
    @DataProvider(name = "brat-annotation")
    public Object[][] getBratData() {
        return new Object[][]{
                new Object[]{
                        "ddp+多帕菲",
                        "T2\tPharmaceutical\t0\t3\tddp\n" +
                                "T3\tToken\t3\t4\t+\n" +
                                "T1\tPharmaceutical\t4\t7\t多帕菲",
                        new Entity[]{
                                new Entity("T2", 0, 3, "Pharmaceutical", "ddp"),
                                new Entity("T3", 3, 4, "Token", "+"),
                                new Entity("T1", 4, 7, "Pharmaceutical", "多帕菲"),
                        },
                        new RelationEntity[0],
                },
                new Object[]{
                        "小趾囊肿切除术",
                        "T1\tBody-structure 0 2\t小趾\n" +
                                "T2\tAbnormality 2 4\t囊肿\n" +
                                "T3\tSurgery 4 6\t切除\n" +
                                "T4\tProcedure 6 7\t术\n",
                        new Entity[]{
                                new Entity("T1", 0, 2, "Body-structure", "小趾"),
                                new Entity("T2", 2, 4, "Abnormality", "囊肿"),
                                new Entity("T3", 4, 6, "Surgery", "切除"),
                                new Entity("T4", 6, 7, "Procedure", "术"),
                        },
                        new RelationEntity[0],
                },
                new Object[]{
                        "2007年在广州陆军总医院因“颈椎疾病”行手术，具体不详",
                        "T1\tBody-structure 0 4\t2007\n" +
                                "T2\tDisease 20 23\t行手术\n" +
                                "T3\tDisease 0 4\t2007\n" +
                                "R1\trelation1 source:T1 target:T2",
                        new Entity[]{
                                new Entity("T1", 0, 4, "Body-structure", "2007"),
                                new Entity("T2", 20, 23, "Disease", "行手术"),
                                new Entity("T3", 0, 4, "Disease", "2007"),
                        },
                        new RelationEntity[]{
                                new RelationEntity("R1", "relation1", "T1", "T2", "source", "target")
                        }

                }
        };
    }

    @Test(dataProvider = "brat-annotation")
    public void testParseBratAnnotation(final String text, final String annotation, final Entity[] entities, final RelationEntity[] relationEntities) {
        final AnnotationDocument document = new AnnotationDocument(text);
        AnnotationDocumentManipulator.parseBratAnnotation(annotation, document);
        Assert.assertEquals(Arrays.asList(entities), document.getEntities(), "entities not equal");
        Assert.assertEquals(Arrays.asList(relationEntities), document.getRelationEntities(), "relation entities not equal");
    }
}