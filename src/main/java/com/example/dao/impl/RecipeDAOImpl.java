package com.example.dao.impl;

import com.example.dao.RecipeDAO;
import com.example.db.DatabaseConnection;
import com.example.model.Recipe;
import com.example.dao.IngredientDAO;
import com.example.dao.impl.IngredientDAOImpl;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.model.Ingredient;
import java.util.Map;

/**
 * RecipeDAOImpl
 * - 레시피(Recipe) 관련 DB 접근 및 CRUD 구현체
 * - MySQL과 연동하여 레시피 저장, 조회, 수정, 삭제, 즐겨찾기 등 처리
 */
public class RecipeDAOImpl implements RecipeDAO {
    // DB 커넥션 관리 객체
    private final DatabaseConnection dbConnection;
    private final IngredientDAO ingredientDAO;

    /**
     * 생성자 - 싱글톤 DB 커넥션 및 재료 DAO 초기화
     */
    public RecipeDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.ingredientDAO = new IngredientDAOImpl();
    }

    /**
     * 레시피 저장 (INSERT, 레시피-재료 관계도 저장)
     */
    @Override
    public void save(Recipe recipe) {
        String sql = "INSERT INTO recipes (name, instructions) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getInstructions());
            pstmt.executeUpdate();
            // 생성된 PK(id) 세팅
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    recipe.setId(generatedKeys.getInt(1));
                }
            }
            // 레시피-재료 관계 저장
            for (Map.Entry<String, Integer> entry : recipe.getRequiredIngredientNames().entrySet()) {
                String ingName = entry.getKey();
                int qty = entry.getValue();
                String sql2 = "INSERT INTO recipe_ingredients (recipe_id, ingredient_name, required_quantity) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                    pstmt2.setInt(1, recipe.getId());
                    pstmt2.setString(2, ingName);
                    pstmt2.setInt(3, qty);
                    pstmt2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("레시피 저장 중 오류 발생", e);
        }
    }

    /**
     * id로 레시피 단건 조회 (LEFT JOIN으로 즐겨찾기 정보 포함)
     */
    @Override
    public Recipe findById(int id) {
        String sql = "SELECT r.*, f.rating, f.note, CASE WHEN f.recipe_id IS NOT NULL THEN 1 ELSE 0 END as is_favorite " +
                    "FROM recipes r " +
                    "LEFT JOIN favorite_recipes f ON r.id = f.recipe_id " +
                    "WHERE r.id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setName(rs.getString("name"));
                    recipe.setInstructions(rs.getString("instructions"));
                    recipe.setFavorite(rs.getBoolean("is_favorite"));
                    int rating = rs.getInt("rating");
                    if (rating >= 1 && rating <= 5) {
                        recipe.setRating(rating);
                    }
                    recipe.setLastCookedAt(rs.getTimestamp("last_cooked_at"));
                    recipe.setNote(rs.getString("note"));
                    // 재료 목록 채우기
                    fillRecipeIngredients(recipe, conn);
                    return recipe;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("레시피 조회 중 오류 발생", e);
        }
        return null;
    }

    /**
     * 전체 레시피 목록 조회 (LEFT JOIN으로 즐겨찾기 정보 포함)
     */
    @Override
    public List<Recipe> findAll() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT r.*, f.rating, f.note, CASE WHEN f.recipe_id IS NOT NULL THEN 1 ELSE 0 END as is_favorite " +
                    "FROM recipes r " +
                    "LEFT JOIN favorite_recipes f ON r.id = f.recipe_id";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setName(rs.getString("name"));
                recipe.setInstructions(rs.getString("instructions"));
                recipe.setFavorite(rs.getBoolean("is_favorite"));
                int rating2 = rs.getInt("rating");
                if (rating2 >= 1 && rating2 <= 5) {
                    recipe.setRating(rating2);
                }
                recipe.setLastCookedAt(rs.getTimestamp("last_cooked_at"));
                recipe.setNote(rs.getString("note"));
                // 재료 목록 채우기
                fillRecipeIngredients(recipe, conn);
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            throw new RuntimeException("레시피 목록 조회 중 오류 발생", e);
        }
        return recipes;
    }

    /**
     * 레시피 정보 수정 (UPDATE)
     */
    @Override
    public void update(Recipe recipe) {
        // recipes 테이블 업데이트
        String sql = "UPDATE recipes SET name = ?, instructions = ?, last_cooked_at = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getInstructions());
            pstmt.setTimestamp(3, recipe.getLastCookedAt());
            pstmt.setInt(4, recipe.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("레시피 수정 중 오류 발생", e);
        }
        
        // favorite_recipes 테이블 upsert (즐겨찾기 관련 정보만)
        if (recipe.isFavorite()) {
            String sql2 = "INSERT INTO favorite_recipes (recipe_id, rating, note) VALUES (?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE rating = VALUES(rating), note = VALUES(note)";
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                int rating = recipe.getRating();
                if (rating < 1 || rating > 5) rating = 1;
                pstmt.setInt(1, recipe.getId());
                pstmt.setInt(2, rating);
                pstmt.setString(3, recipe.getNote());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("favorite_recipes upsert 중 오류 발생", e);
            }
        }
    }

    /**
     * 레시피 삭제 (DELETE)
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM recipes WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("레시피 삭제 중 오류 발생", e);
        }
    }

    /**
     * 레시피에 재료 추가 (INSERT)
     */
    @Override
    public void addIngredientToRecipe(int recipeId, int ingredientId) {
        String sql = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, ingredientId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("레시피에 재료 추가 중 오류 발생", e);
        }
    }

    /**
     * 레시피에서 재료 제거 (DELETE)
     */
    @Override
    public void removeIngredientFromRecipe(int recipeId, int ingredientId) {
        String sql = "DELETE FROM recipe_ingredients WHERE recipe_id = ? AND ingredient_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, ingredientId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("레시피에서 재료 제거 중 오류 발생", e);
        }
    }

    /**
     * 즐겨찾기 추가 (INSERT)
     */
    @Override
    public void addToFavorites(int recipeId) {
        String sql = "INSERT INTO favorite_recipes (recipe_id) VALUES (?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("즐겨찾기 추가 중 오류 발생", e);
        }
    }

    /**
     * 즐겨찾기 해제 (DELETE)
     */
    @Override
    public void removeFromFavorites(int recipeId) {
        String sql = "DELETE FROM favorite_recipes WHERE recipe_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("즐겨찾기 제거 중 오류 발생", e);
        }
    }

    /**
     * 즐겨찾기 레시피 전체 조회 (JOIN)
     */
    @Override
    public List<Recipe> findAllFavorites() {
        List<Recipe> favorites = new ArrayList<>();
        String sql = "SELECT r.* FROM recipes r " +
                    "JOIN favorite_recipes f ON r.id = f.recipe_id " +
                    "ORDER BY f.created_at DESC";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setName(rs.getString("name"));
                recipe.setInstructions(rs.getString("instructions"));
                recipe.setFavorite(true);
                favorites.add(recipe);
            }
        } catch (SQLException e) {
            throw new RuntimeException("즐겨찾기 목록 조회 중 오류 발생", e);
        }
        return favorites;
    }

    /**
     * 해당 레시피가 즐겨찾기인지 여부 확인
     */
    @Override
    public boolean isFavorite(int recipeId) {
        String sql = "SELECT 1 FROM favorite_recipes WHERE recipe_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("즐겨찾기 상태 확인 중 오류 발생", e);
        }
    }

    /**
     * 레시피의 requiredIngredients를 recipe_ingredients 테이블에서 읽어와 채우는 메서드
     */
    private void fillRecipeIngredients(Recipe recipe, Connection conn) {
        String sql = "SELECT ingredient_name, required_quantity FROM recipe_ingredients WHERE recipe_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recipe.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("ingredient_name");
                    int qty = rs.getInt("required_quantity");
                    recipe.addIngredientNameAndQuantity(name, qty);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("레시피 재료 조회 중 오류 발생", e);
        }
    }

    /**
     * 마지막 요리 일자만 갱신
     */
    @Override
    public void updateLastCookedAt(int recipeId, java.sql.Timestamp lastCookedAt) {
        String sql = "UPDATE recipes SET last_cooked_at = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, lastCookedAt);
            pstmt.setInt(2, recipeId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("마지막 요리 일자 갱신 중 오류 발생", e);
        }
    }
} 