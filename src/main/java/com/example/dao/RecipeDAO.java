package com.example.dao;

import com.example.model.Recipe;
import java.util.List;

/**
 * RecipeDAO
 * - 레시피(Recipe) 관련 DB 접근을 위한 인터페이스
 * - 구현체(RecipeDAOImpl)에서 실제 DB 연동 처리
 */
public interface RecipeDAO {
    /** 레시피 저장 (INSERT) */
    void save(Recipe recipe);
    /** id로 레시피 단건 조회 (SELECT) */
    Recipe findById(int id);
    /** 전체 레시피 목록 조회 (SELECT) */
    List<Recipe> findAll();
    /** 레시피 정보 수정 (UPDATE) */
    void update(Recipe recipe);
    /** 레시피 삭제 (DELETE) */
    void delete(int id);
    /** 레시피에 재료 추가 (INSERT) */
    void addIngredientToRecipe(int recipeId, int ingredientId);
    /** 레시피에서 재료 제거 (DELETE) */
    void removeIngredientFromRecipe(int recipeId, int ingredientId);
    
    // 즐겨찾기 관련 메소드
    /** 즐겨찾기 추가 (INSERT) */
    void addToFavorites(int recipeId);
    /** 즐겨찾기 해제 (DELETE) */
    void removeFromFavorites(int recipeId);
    /** 즐겨찾기 레시피 전체 조회 (JOIN) */
    List<Recipe> findAllFavorites();
    /** 해당 레시피가 즐겨찾기인지 여부 확인 */
    boolean isFavorite(int recipeId);
    /** 마지막 요리 일자만 갱신 (favorite_recipes에 upsert) */
    void updateLastCookedAt(int recipeId, java.sql.Timestamp lastCookedAt);
} 