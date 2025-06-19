package com.example.dao;

import com.example.model.Ingredient;
import java.util.List;

/**
 * IngredientDAO
 * - 재료(Ingredient) 관련 DB 접근을 위한 인터페이스
 * - 구현체(IngredientDAOImpl)에서 실제 DB 연동 처리
 */
public interface IngredientDAO {
    /** 재료 저장 (INSERT) */
    void save(Ingredient ingredient);
    /** id로 재료 단건 조회 (SELECT) */
    Ingredient findById(int id);
    /** 전체 재료 목록 조회 (SELECT) */
    List<Ingredient> findAll();
    /** 재료 정보 수정 (UPDATE) */
    void update(Ingredient ingredient);
    /** 재료 삭제 (DELETE) */
    void delete(int id);
    /** 특정 레시피에 사용된 재료 목록 조회 (JOIN) */
    List<Ingredient> findByRecipeId(int recipeId);
    /** 재료 이름으로 단건 조회 (SELECT) */
    Ingredient findByName(String name);
} 