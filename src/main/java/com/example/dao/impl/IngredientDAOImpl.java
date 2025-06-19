package com.example.dao.impl;

import com.example.dao.IngredientDAO;
import com.example.db.DatabaseConnection;
import com.example.model.Ingredient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * IngredientDAOImpl
 * - 재료(Ingredient) 관련 DB 접근 및 CRUD 구현체
 * - MySQL과 연동하여 재료 저장, 조회, 수정, 삭제, 검색 등 처리
 */
public class IngredientDAOImpl implements IngredientDAO {
    // DB 커넥션 관리 객체
    private final DatabaseConnection dbConnection;

    /**
     * 생성자 - 싱글톤 DB 커넥션 객체 초기화
     */
    public IngredientDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 재료 저장 (INSERT)
     */
    @Override
    public void save(Ingredient ingredient) {
        String sql = "INSERT INTO ingredients (name, available_quantity) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ingredient.getName());
            pstmt.setInt(2, ingredient.getAvailableQuantity());
            pstmt.executeUpdate();
            // 생성된 PK(id) 세팅
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ingredient.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("재료 저장 중 오류 발생", e);
        }
    }

    /**
     * id로 재료 단건 조회 (SELECT)
     */
    @Override
    public Ingredient findById(int id) {
        String sql = "SELECT * FROM ingredients WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createIngredientFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("재료 조회 중 오류 발생", e);
        }
        return null;
    }

    /**
     * 전체 재료 목록 조회 (SELECT)
     */
    @Override
    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM ingredients";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ingredients.add(createIngredientFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("재료 목록 조회 중 오류 발생", e);
        }
        return ingredients;
    }

    /**
     * 재료 정보 수정 (UPDATE)
     */
    @Override
    public void update(Ingredient ingredient) {
        String sql = "UPDATE ingredients SET name = ?, available_quantity = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ingredient.getName());
            pstmt.setInt(2, ingredient.getAvailableQuantity());
            pstmt.setInt(3, ingredient.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("재료 수정 중 오류 발생", e);
        }
    }

    /**
     * 재료 삭제 (DELETE)
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM ingredients WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("재료 삭제 중 오류 발생", e);
        }
    }

    /**
     * 특정 레시피에 사용된 재료 목록 조회 (JOIN)
     */
    @Override
    public List<Ingredient> findByRecipeId(int recipeId) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT i.* FROM ingredients i " +
                    "JOIN recipe_ingredients ri ON i.id = ri.ingredient_id " +
                    "WHERE ri.recipe_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(createIngredientFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("레시피의 재료 목록 조회 중 오류 발생", e);
        }
        return ingredients;
    }

    /**
     * 재료 이름으로 단건 조회 (SELECT)
     */
    @Override
    public Ingredient findByName(String name) {
        String sql = "SELECT * FROM ingredients WHERE name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createIngredientFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("재료 이름으로 조회 중 오류 발생", e);
        }
        return null;
    }

    /**
     * ResultSet에서 Ingredient 객체 생성 (공통 유틸)
     */
    private Ingredient createIngredientFromResultSet(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(rs.getInt("id"));
        ingredient.setName(rs.getString("name"));
        ingredient.setAvailableQuantity(rs.getInt("available_quantity"));
        return ingredient;
    }
} 