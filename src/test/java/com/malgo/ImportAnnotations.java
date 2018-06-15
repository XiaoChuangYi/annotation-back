package com.malgo;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;

class ImportAnnotations {
  public static void main(String[] args) {
    try {
      final Connection buildConnection =
          DriverManager.getConnection(
              "jdbc:mysql://rm-bp1it0w59f9n2hquj5o.mysql.rds.aliyuncs.com:3306/annotation_build?useUnicode=true&characterEncoding=utf8&useSSL=false",
              "annotation_build",
              "2y5zO037f8QLuVzD");
      copySentences(buildConnection);
      copyWords(buildConnection);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void copyWords(Connection connection) throws SQLException {
    final Statement queryStmt = connection.createStatement();
    final PreparedStatement insertStmt =
        connection.prepareStatement(
            "INSERT INTO annotation_combine ("
                + "annotation_type, "
                + "assignee, "
                + "creator, "
                + "delete_token, "
                + "gmt_created, "
                + "gmt_modified, "
                + "reviewer, "
                + "state, "
                + "term, "
                + "final_annotation, "
                + "is_task, "
                + "manual_annotation, "
                + "reviewed_annotation"
                + ") VALUES ("
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?);");
    insertStmt.setInt(1, 0);
    insertStmt.setInt(3, 0);
    insertStmt.setInt(4, 0);
    insertStmt.setInt(7, 0);
    insertStmt.setInt(11, 0);
    insertStmt.setString(12, "");

    final ResultSet queryResult =
        queryStmt.executeQuery(
            "select term, final_annotation, state, gmt_created, gmt_modified from annotation_word_pos");
    while (queryResult.next()) {
      final String state = queryResult.getString("state");
      final String term = queryResult.getString("term");
      String finalText = queryResult.getString("final_annotation");
      if (StringUtils.isBlank(finalText)) {
        finalText = "";
      }
      final Timestamp gmtCreated = queryResult.getTimestamp("gmt_created");
      final Timestamp gmtModified = queryResult.getTimestamp("gmt_modified");

      insertStmt.setInt(2, 1);
      insertStmt.setTimestamp(5, gmtCreated);
      insertStmt.setTimestamp(6, gmtModified);
      insertStmt.setString(8, state.equals("FINISH") ? "examinePass" : "unDistributed");
      insertStmt.setString(9, term);
      insertStmt.setString(10, finalText);
      insertStmt.setString(13, state.equals("FINISH") ? finalText : "");
      insertStmt.executeUpdate();
    }
  }

  private static void copySentences(Connection connection) throws SQLException {
    final Statement queryStmt = connection.createStatement();
    final PreparedStatement insertStmt =
        connection.prepareStatement(
            "INSERT INTO annotation_combine ("
                + "annotation_type, "
                + "assignee, "
                + "creator, "
                + "delete_token, "
                + "gmt_created, "
                + "gmt_modified, "
                + "reviewer, "
                + "state, "
                + "term, "
                + "final_annotation, "
                + "is_task, "
                + "manual_annotation, "
                + "reviewed_annotation"
                + ") VALUES ("
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?, "
                + "?);");
    insertStmt.setInt(1, 1);
    insertStmt.setInt(3, 0);
    insertStmt.setInt(4, 0);
    insertStmt.setInt(7, 0);
    insertStmt.setInt(11, 0);
    insertStmt.setString(12, "");

    final ResultSet queryResult =
        queryStmt.executeQuery(
            "select origin_text, annotation_text, final_annotation_text, state, gmt_created, gmt_modified from annotation_sentence");
    while (queryResult.next()) {
      final String state = queryResult.getString("state");

      if (state.equals("已审核") || state.equals("已标注")) {
        final String term = queryResult.getString("origin_text");
        final String finalText = queryResult.getString("annotation_text");
        final String reviewed = queryResult.getString("final_annotation_text");
        final Timestamp gmtCreated = queryResult.getTimestamp("gmt_created");
        final Timestamp gmtModified = queryResult.getTimestamp("gmt_modified");

        insertStmt.setInt(2, state.equals("已审核") ? 1 : 21);
        insertStmt.setTimestamp(5, gmtCreated);
        insertStmt.setTimestamp(6, gmtModified);
        insertStmt.setString(8, state.equals("已审核") ? "examinePass" : "preExamine");
        insertStmt.setString(9, term);
        insertStmt.setString(10, finalText);
        insertStmt.setString(13, reviewed);
        insertStmt.executeUpdate();
      }
    }
  }
}
