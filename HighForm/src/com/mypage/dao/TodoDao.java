package com.mypage.dao;

import com.mypage.Model.TodoItem;
import com.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoDao {

    /* ---------- C : INSERT ---------- */
    public void insert(TodoItem item) {
        String sql = """
            INSERT INTO TODO_ITEM (TITLE, CONTENT, DUE_DATE, DONE_YN)
            VALUES (?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[] { "ID" })) {

            ps.setString(1, item.getTitle());
            ps.setString(2, item.getContent());
            ps.setDate  (3, Date.valueOf(item.getDueDate()));
            ps.setString(4, item.isDone() ? "Y" : "N");
            ps.executeUpdate();

            /* 자동 생성 PK 조회 → 모델에 세팅 */
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) item.setId(rs.getLong(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /* ---------- R : SELECT by 특정 날짜 ---------- */
    public List<TodoItem> findByDate(LocalDate date) {
        String sql = "SELECT * FROM TODO_ITEM WHERE DUE_DATE = ? ORDER BY ID";
        List<TodoItem> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /* ---------- U : UPDATE ---------- */
    public void update(TodoItem item) {
        String sql = """
            UPDATE TODO_ITEM
               SET TITLE = ?, CONTENT = ?, DUE_DATE = ?, DONE_YN = ?
             WHERE ID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getTitle());
            ps.setString(2, item.getContent());
            ps.setDate  (3, Date.valueOf(item.getDueDate()));
            ps.setString(4, item.isDone() ? "Y" : "N");
            ps.setLong  (5, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /* ---------- D : DELETE ---------- */
    public void delete(long id) {
        String sql = "DELETE FROM TODO_ITEM WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /* ---------- 공통 ResultSet → Model 매핑 ---------- */
    private TodoItem map(ResultSet rs) throws SQLException {
        return new TodoItem(
                rs.getLong("ID"),
                rs.getString("TITLE"),
                rs.getString("CONTENT"),
                rs.getDate("DUE_DATE").toLocalDate(),
                "Y".equals(rs.getString("DONE_YN")));
    }
}
