package com.example.ui;

import com.example.dao.RecipeDAO;
import com.example.dao.IngredientDAO;
import com.example.dao.impl.RecipeDAOImpl;
import com.example.dao.impl.IngredientDAOImpl;
import com.example.model.Recipe;
import com.example.model.Ingredient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

/**
 * 레시피 관리 패널 (레시피 목록, 추가/수정/삭제/요리/즐겨찾기/상세보기)
 * - 테이블, 버튼, 다이얼로그 등 UI와 DB 연동을 담당
 */
public class RecipePanel extends JPanel {
    // DAO 및 패널 참조
    private final RecipeDAO recipeDAO; // 레시피 DB 접근 객체
    private final IngredientDAO ingredientDAO; // 재료 DB 접근 객체
    private final IngredientPanel ingredientPanel; // 재료 패널 참조
    // UI 컴포넌트
    private JTable recipeTable; // 레시피 테이블
    private DefaultTableModel tableModel; // 테이블 모델
    private JButton addButton, editButton, deleteButton, cookButton, favoriteButton, viewDetailsButton; // 하단 버튼들
    private SimpleDateFormat dateFormat; // 날짜 포맷

    /**
     * 생성자 - 패널 및 DAO 초기화, UI 세팅
     */
    public RecipePanel(IngredientPanel ingredientPanel) {
        this.recipeDAO = new RecipeDAOImpl();
        this.ingredientDAO = new IngredientDAOImpl();
        this.ingredientPanel = ingredientPanel;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        setLayout(new BorderLayout());
        setBackground(new Color(255, 245, 230));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initComponents(); // UI 컴포넌트 초기화
        loadRecipes();    // 레시피 목록 불러오기
    }

    /**
     * UI 컴포넌트(테이블, 버튼 등) 초기화 및 스타일 적용
     */
    private void initComponents() {
        // 테이블 모델 및 테이블 생성
        String[] columnNames = {"ID", "이름", "재료", "평점", "즐겨찾기", "마지막 요리 일자"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        recipeTable = new JTable(tableModel) {
            // 행별 배경색 지정
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? new Color(255, 255, 240) : new Color(255, 240, 220));
                else c.setBackground(new Color(255, 220, 180));
                return c;
            }
        };
        recipeTable.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        recipeTable.setRowHeight(28);
        JTableHeader header = recipeTable.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        header.setBackground(new Color(255, 230, 200));
        header.setForeground(new Color(255, 140, 60));
        // 평점 컬럼에 별 아이콘 표시
        recipeTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                if (value instanceof Number) {
                    int rating = ((Number) value).intValue();
                    StringBuilder stars = new StringBuilder();
                    for (int i = 0; i < rating; i++) stars.append("★");
                    for (int i = rating; i < 5; i++) stars.append("☆");
                    setText(stars.toString());
                } else setText("");
            }
        });
        // 하단 버튼 패널 및 버튼 생성
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("새 레시피");
        editButton = new JButton("수정");
        deleteButton = new JButton("삭제");
        cookButton = new JButton("요리하기");
        favoriteButton = new JButton("즐겨찾기");
        viewDetailsButton = new JButton("상세보기");
        // 버튼 패널에 추가
        buttonPanel.add(addButton); buttonPanel.add(editButton); buttonPanel.add(deleteButton);
        buttonPanel.add(cookButton); buttonPanel.add(favoriteButton); buttonPanel.add(viewDetailsButton);
        // 버튼 이벤트 리스너 등록
        addButton.addActionListener(e -> addRecipe());
        editButton.addActionListener(e -> editRecipe());
        deleteButton.addActionListener(e -> deleteRecipe());
        cookButton.addActionListener(e -> cookRecipe());
        favoriteButton.addActionListener(e -> toggleFavorite());
        viewDetailsButton.addActionListener(e -> viewRecipeDetails());
        // 레이아웃 배치
        add(new JScrollPane(recipeTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        // 버튼 스타일 통일
        Color mainBtnColor = new Color(255, 180, 80);
        Color mainBorderColor = new Color(255, 140, 60);
        Font mainFont = new Font("맑은 고딕", Font.BOLD, 17);
        JButton[] btns = {addButton, editButton, deleteButton, cookButton, favoriteButton, viewDetailsButton};
        for (JButton btn : btns) {
            btn.setBackground(mainBtnColor);
            btn.setForeground(Color.WHITE);
            btn.setFont(mainFont);
            btn.setBorder(BorderFactory.createLineBorder(mainBorderColor, 2, true));
            btn.setFocusPainted(false);
            btn.setOpaque(true);
        }
    }

    /**
     * DB에서 레시피 목록을 불러와 테이블에 표시 (평점 컬럼은 숫자만 넣음)
     */
    private void loadRecipes() {
        tableModel.setRowCount(0);
        List<Recipe> recipes = recipeDAO.findAll();
        for (Recipe recipe : recipes) {
            String ingredientsStr = recipe.getRequiredIngredientNames().entrySet().stream()
                .map(e -> e.getKey() + "(" + e.getValue() + ")")
                .collect(Collectors.joining(", "));
            // 평점 컬럼은 숫자만 넣음
            int rating = recipe.getRating();
            String favoriteStr = recipe.isFavorite() ? "♥" : "";
            String lastCooked = recipe.getLastCookedAt() != null ? dateFormat.format(recipe.getLastCookedAt()) : "아직 요리하지 않음";
            Object[] row = {recipe.getId(), recipe.getName(), ingredientsStr, rating, favoriteStr, lastCooked};
            tableModel.addRow(row);
        }
    }

    /**
     * 새 레시피 추가 다이얼로그 및 DB 저장 (귀엽고 깔끔한 스타일 적용)
     */
    private void addRecipe() {
        // 이름 입력
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 2, true));
        nameField.setBackground(Color.white);
        // 조리 방법 입력
        JTextArea instructionsArea = new JTextArea(5, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        instructionsArea.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 2, true));
        instructionsArea.setBackground(Color.white);
        // 재료 직접 입력 리스트
        java.util.List<String> ingredientNames = new ArrayList<>();
        java.util.List<Integer> ingredientQuantities = new ArrayList<>();
        DefaultListModel<String> ingredientListModel = new DefaultListModel<>();
        JList<String> ingredientList = new JList<>(ingredientListModel);
        ingredientList.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ingredientList.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 1, true));
        // 재료 추가 버튼
        JButton addIngredientButton = new JButton("재료 직접 입력");
        addIngredientButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        addIngredientButton.setBackground(new Color(255, 180, 80));
        addIngredientButton.setForeground(Color.WHITE);
        addIngredientButton.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 60), 2, true));
        addIngredientButton.setFocusPainted(false);
        addIngredientButton.setOpaque(true);
        addIngredientButton.addActionListener(e -> {
            JTextField ingNameField = new JTextField();
            ingNameField.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
            JSpinner ingQtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
            JPanel ingPanel = new JPanel(new GridLayout(2, 2, 8, 8));
            ingPanel.setBackground(new Color(255, 245, 230));
            ingPanel.add(new JLabel("재료명:"));
            ingPanel.add(ingNameField);
            ingPanel.add(new JLabel("수량:"));
            ingPanel.add(ingQtySpinner);
            int result = JOptionPane.showConfirmDialog(this, ingPanel, "재료 추가", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String ingName = ingNameField.getText().trim();
                int qty = (Integer) ingQtySpinner.getValue();
                if (!ingName.isEmpty() && qty > 0) {
                    ingredientNames.add(ingName);
                    ingredientQuantities.add(qty);
                    ingredientListModel.addElement(ingName + "(" + qty + ")");
                }
            }
        });
        // 재료 입력 패널
        JPanel ingredientsPanel = new JPanel(new BorderLayout(8, 8));
        ingredientsPanel.setBackground(new Color(255, 245, 230));
        ingredientsPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        ingredientsPanel.add(new JScrollPane(ingredientList), BorderLayout.CENTER);
        ingredientsPanel.add(addIngredientButton, BorderLayout.SOUTH);
        // 입력 패널 구성
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(255, 245, 230));
        inputPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("🍳 이름:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("📝 조리 방법:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(instructionsArea), gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        inputPanel.add(new JLabel("🧺 재료 목록 및 추가:"), gbc);
        gbc.gridy = 3;
        inputPanel.add(ingredientsPanel, gbc);
        // 다이얼로그 패널(전체)
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 245, 230));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        // 상단 아이콘
        JLabel iconLabel = new JLabel("🍲");
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(inputPanel, BorderLayout.CENTER);
        // 커스텀 버튼 텍스트
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        // 다이얼로그 표시
        int result = JOptionPane.showConfirmDialog(this, panel, "새 레시피 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String instructions = instructionsArea.getText().trim();
            if (!name.isEmpty() && !instructions.isEmpty() && !ingredientNames.isEmpty()) {
                Recipe recipe = new Recipe(name, instructions);
                for (int i = 0; i < ingredientNames.size(); i++) {
                    recipe.addIngredientNameAndQuantity(ingredientNames.get(i), ingredientQuantities.get(i));
                }
                recipeDAO.save(recipe);
                loadRecipes();
            } else {
                JOptionPane.showMessageDialog(this, "레시피 이름, 조리 방법, 그리고 최소 하나의 재료가 필요합니다.");
            }
        }
    }

    /**
     * 레시피 요리(재고 차감, 마지막 요리 일자 갱신, 안내)
     */
    private void cookRecipe() {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            Recipe recipe = recipeDAO.findById(id);
            // 재고 부족 체크
            java.util.List<String> 부족재료 = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : recipe.getRequiredIngredientNames().entrySet()) {
                String ingName = entry.getKey();
                int required = entry.getValue();
                Ingredient myIng = ingredientDAO.findByName(ingName);
                if (myIng == null || myIng.getAvailableQuantity() < required) {
                    int 부족 = required - (myIng == null ? 0 : myIng.getAvailableQuantity());
                    부족재료.add(ingName + "(" + 부족 + "개 부족)");
                }
            }
            if (!부족재료.isEmpty()) {
                showInsufficientStockDialog();
                return;
            }
            // 충분하면 재고 차감 및 안내
            int confirm = JOptionPane.showConfirmDialog(this,
                "이 레시피로 요리하시겠습니까?\n필요한 재료가 자동으로 차감됩니다.",
                "요리 확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (Map.Entry<String, Integer> entry : recipe.getRequiredIngredientNames().entrySet()) {
                    Ingredient myIng = ingredientDAO.findByName(entry.getKey());
                    if (myIng == null || myIng.getAvailableQuantity() < entry.getValue()) {
                        showInsufficientStockDialog();
                        return;
                    }
                    myIng.useQuantity(entry.getValue());
                    ingredientDAO.update(myIng);
                }
                recipe.cookedNow();
                // 마지막 요리 일자만 업데이트
                recipeDAO.updateLastCookedAt(recipe.getId(), recipe.getLastCookedAt());
                loadRecipes();
                ingredientPanel.loadIngredients();
                JOptionPane.showMessageDialog(this, "요리가 완료되었습니다!\n냉장고 속 재료가 차감되었습니다.");
            }
        }
    }

    /**
     * 즐겨찾기 추가/해제 및 평점/메모 입력 (귀엽고 깔끔한 스타일 적용)
     */
    private void toggleFavorite() {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            Recipe recipe = recipeDAO.findById(id);
            if (recipe.isFavorite()) {
                recipeDAO.removeFromFavorites(id);
            } else {
                // 평점, 메모 입력 (리디자인)
                JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
                ratingSpinner.setFont(new Font("맑은 고딕", Font.BOLD, 16));
                JTextArea noteArea = new JTextArea(3, 20);
                noteArea.setLineWrap(true);
                noteArea.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
                noteArea.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 2, true));
                noteArea.setBackground(Color.white);
                JPanel inputPanel = new JPanel(new GridBagLayout());
                inputPanel.setBackground(new Color(255, 245, 230));
                inputPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(8, 8, 8, 8);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0; gbc.gridy = 0;
                inputPanel.add(new JLabel("⭐ 평점 (1-5):"), gbc);
                gbc.gridx = 1;
                inputPanel.add(ratingSpinner, gbc);
                gbc.gridx = 0; gbc.gridy = 1;
                inputPanel.add(new JLabel("📝 메모:"), gbc);
                gbc.gridx = 1;
                inputPanel.add(new JScrollPane(noteArea), gbc);
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(new Color(255, 245, 230));
                panel.setBorder(new EmptyBorder(12, 12, 12, 12));
                JLabel iconLabel = new JLabel("❤");
                iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
                iconLabel.setVerticalAlignment(SwingConstants.CENTER);
                panel.add(iconLabel, BorderLayout.WEST);
                panel.add(inputPanel, BorderLayout.CENTER);
                UIManager.put("OptionPane.okButtonText", "OK");
                UIManager.put("OptionPane.cancelButtonText", "Cancel");
                int result = JOptionPane.showConfirmDialog(this, panel, "즐겨찾기 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    recipe.setRating((Integer) ratingSpinner.getValue());
                    recipe.setNote(noteArea.getText().trim());
                    recipe.setFavorite(true);
                    recipeDAO.addToFavorites(id);
                    recipeDAO.update(recipe);
                }
            }
            loadRecipes();
        }
    }

    /**
     * 레시피 상세 정보 다이얼로그 표시 (귀엽고 깔끔한 스타일 적용)
     */
    private void viewRecipeDetails() {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            // 항상 최신 정보로 다시 불러오기
            Recipe recipe = recipeDAO.findById(id);
            StringBuilder details = new StringBuilder();
            details.append("레시피: ").append(recipe.getName()).append("\n\n");
            details.append("평점: ");
            if (recipe.getRating() > 0) details.append("★".repeat(recipe.getRating())).append(" (").append(recipe.getRating()).append(")");
            details.append("\n즐겨찾기: ").append(recipe.isFavorite() ? "예" : "아니오");
            // 메모가 있으면 즐겨찾기 밑에 바로 표시
            if (recipe.getNote() != null && !recipe.getNote().isEmpty()) {
                details.append("\n메모: ").append(recipe.getNote());
            }
            details.append("\n\n필요한 재료:\n");
            for (Map.Entry<String, Integer> entry : recipe.getRequiredIngredientNames().entrySet()) {
                details.append("- ").append(entry.getKey()).append("(").append(entry.getValue()).append(")\n");
            }
            details.append("\n조리 방법:\n").append(recipe.getInstructions());
            JTextArea detailsArea = new JTextArea(details.toString());
            detailsArea.setEditable(false);
            detailsArea.setLineWrap(true);
            detailsArea.setWrapStyleWord(true);
            detailsArea.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            detailsArea.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 2, true));
            detailsArea.setBackground(Color.white);
            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 2, true));
            // 전체 패널
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(255, 245, 230));
            panel.setBorder(new EmptyBorder(16, 16, 16, 16));
            JLabel iconLabel = new JLabel("🍳");
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.CENTER);
            panel.add(iconLabel, BorderLayout.WEST);
            panel.add(scrollPane, BorderLayout.CENTER);
            UIManager.put("OptionPane.okButtonText", "OK");
            JOptionPane.showMessageDialog(this, panel, "레시피 상세 정보", JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * 레시피 수정 다이얼로그 및 DB 반영 (귀엽고 깔끔한 스타일 적용)
     */
    private void editRecipe() {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            Recipe recipe = recipeDAO.findById(id);
            JTextField nameField = new JTextField(recipe.getName());
            nameField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            nameField.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 2, true));
            nameField.setBackground(Color.white);
            JTextArea instructionsArea = new JTextArea(recipe.getInstructions(), 5, 20);
            instructionsArea.setLineWrap(true);
            instructionsArea.setWrapStyleWord(true);
            instructionsArea.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            instructionsArea.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 2, true));
            instructionsArea.setBackground(Color.white);
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setBackground(new Color(255, 245, 230));
            inputPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 0;
            inputPanel.add(new JLabel("🍳 이름:"), gbc);
            gbc.gridx = 1;
            inputPanel.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            inputPanel.add(new JLabel("📝 조리 방법:"), gbc);
            gbc.gridx = 1;
            inputPanel.add(new JScrollPane(instructionsArea), gbc);
            // 전체 패널
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(255, 245, 230));
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));
            JLabel iconLabel = new JLabel("🍲");
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.CENTER);
            panel.add(iconLabel, BorderLayout.WEST);
            panel.add(inputPanel, BorderLayout.CENTER);
            UIManager.put("OptionPane.okButtonText", "OK");
            UIManager.put("OptionPane.cancelButtonText", "Cancel");
            int result = JOptionPane.showConfirmDialog(this, panel, "레시피 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String newName = nameField.getText().trim();
                String newInstructions = instructionsArea.getText().trim();
                if (!newName.isEmpty() && !newInstructions.isEmpty()) {
                    recipe.setName(newName);
                    recipe.setInstructions(newInstructions);
                    recipeDAO.update(recipe);
                    loadRecipes();
                }
            }
        }
    }

    /**
     * 레시피 삭제 (확인 다이얼로그)
     */
    private void deleteRecipe() {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this, name + " 레시피를 정말로 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                recipeDAO.delete(id);
                loadRecipes();
            }
        }
    }

    /**
     * 재고 부족 안내 다이얼로그
     */
    private void showInsufficientStockDialog() {
        JOptionPane.showMessageDialog(this, "🥲 재고가 부족해요! 재료를 먼저 채워주세요.", "재고 부족", JOptionPane.WARNING_MESSAGE);
    }
} 