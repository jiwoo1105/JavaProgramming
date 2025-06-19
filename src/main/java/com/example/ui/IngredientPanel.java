package com.example.ui;

import com.example.dao.IngredientDAO;
import com.example.dao.impl.IngredientDAOImpl;
import com.example.model.Ingredient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * 재료 관리 패널 (재료 목록, 추가/수정/삭제/수량 추가)
 * - 테이블, 버튼, 다이얼로그 등 UI와 DB 연동을 담당
 */
public class IngredientPanel extends JPanel {
    // DAO 참조
    private final IngredientDAO ingredientDAO; // 재료 DB 접근 객체
    // UI 컴포넌트
    private JTable ingredientTable; // 재료 테이블
    private DefaultTableModel tableModel; // 테이블 모델
    private JButton addButton, editButton, deleteButton, addQuantityButton; // 하단 버튼들

    /**
     * 생성자 - 패널 및 DAO 초기화, UI 세팅
     */
    public IngredientPanel() {
        ingredientDAO = new IngredientDAOImpl();
        setLayout(new BorderLayout());
        setBackground(new Color(255, 245, 230));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initComponents(); // UI 컴포넌트 초기화
        loadIngredients(); // 재료 목록 불러오기
    }

    /**
     * UI 컴포넌트(테이블, 버튼 등) 초기화 및 스타일 적용
     */
    private void initComponents() {
        // 테이블 모델 및 테이블 생성
        String[] columnNames = {"ID", "이름", "보유 수량"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ingredientTable = new JTable(tableModel) {
            // 행별 배경색 지정
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? new Color(255, 255, 240) : new Color(255, 240, 220));
                else c.setBackground(new Color(255, 220, 180));
                return c;
            }
        };
        ingredientTable.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        ingredientTable.setRowHeight(28);
        JTableHeader header = ingredientTable.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        header.setBackground(new Color(255, 230, 200));
        header.setForeground(new Color(255, 140, 60));
        // 하단 버튼 패널 및 버튼 생성
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("새 재료 추가");
        editButton = new JButton("이름 수정");
        deleteButton = new JButton("삭제");
        addQuantityButton = new JButton("수량 추가");
        // 버튼 패널에 추가
        buttonPanel.add(addButton); buttonPanel.add(editButton); buttonPanel.add(deleteButton); buttonPanel.add(addQuantityButton);
        // 버튼 이벤트 리스너 등록
        addButton.addActionListener(e -> addIngredient());
        editButton.addActionListener(e -> editIngredient());
        deleteButton.addActionListener(e -> deleteIngredient());
        addQuantityButton.addActionListener(e -> addQuantity());
        // 레이아웃 배치
        add(new JScrollPane(ingredientTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        // 버튼 스타일 통일
        Color mainBtnColor = new Color(255, 180, 80);
        Color mainBorderColor = new Color(255, 140, 60);
        Font mainFont = new Font("맑은 고딕", Font.BOLD, 17);
        JButton[] btns = {addButton, editButton, deleteButton, addQuantityButton};
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
     * DB에서 재료 목록을 불러와 테이블에 표시
     */
    public void loadIngredients() {
        tableModel.setRowCount(0);
        List<Ingredient> ingredients = ingredientDAO.findAll();
        for (Ingredient ingredient : ingredients) {
            Object[] row = {ingredient.getId(), ingredient.getName(), ingredient.getAvailableQuantity()};
            tableModel.addRow(row);
        }
    }

    /**
     * 새 재료 추가 다이얼로그 및 DB 저장 (귀엽고 깔끔한 스타일 적용)
     */
    private void addIngredient() {
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 2, true));
        nameField.setBackground(Color.white);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        quantitySpinner.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(255, 245, 230));
        inputPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("🥕 이름:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("📦 초기 수량:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(quantitySpinner, gbc);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 245, 230));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel iconLabel = new JLabel("🧺");
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(inputPanel, BorderLayout.CENTER);
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        int result = JOptionPane.showConfirmDialog(this, panel, "새 재료 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            int quantity = (Integer) quantitySpinner.getValue();
            if (!name.isEmpty()) {
                Ingredient ingredient = new Ingredient(name, quantity);
                ingredientDAO.save(ingredient);
                loadIngredients();
            }
        }
    }

    /**
     * 재료 이름 수정 다이얼로그 및 DB 반영 (귀엽고 깔끔한 스타일 적용)
     */
    private void editIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            Ingredient ingredient = ingredientDAO.findById(id);
            JTextField nameField = new JTextField(ingredient.getName());
            nameField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            nameField.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 120), 2, true));
            nameField.setBackground(Color.white);
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setBackground(new Color(255, 245, 230));
            inputPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 0;
            inputPanel.add(new JLabel("🥕 이름 수정:"), gbc);
            gbc.gridx = 1;
            inputPanel.add(nameField, gbc);
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(255, 245, 230));
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));
            JLabel iconLabel = new JLabel("✏");
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.CENTER);
            panel.add(iconLabel, BorderLayout.WEST);
            panel.add(inputPanel, BorderLayout.CENTER);
            UIManager.put("OptionPane.okButtonText", "OK");
            UIManager.put("OptionPane.cancelButtonText", "Cancel");
            int result = JOptionPane.showConfirmDialog(this, panel, "재료 이름 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty()) {
                    ingredient.setName(newName);
                    ingredientDAO.update(ingredient);
                    loadIngredients();
                }
            }
        }
    }

    /**
     * 재료 수량 추가 다이얼로그 및 DB 반영 (귀엽고 깔끔한 스타일 적용)
     */
    private void addQuantity() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            Ingredient ingredient = ingredientDAO.findById(id);
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
            quantitySpinner.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setBackground(new Color(255, 245, 230));
            inputPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 0;
            inputPanel.add(new JLabel("📦 추가 수량:"), gbc);
            gbc.gridx = 1;
            inputPanel.add(quantitySpinner, gbc);
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(255, 245, 230));
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));
            JLabel iconLabel = new JLabel("➕");
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.CENTER);
            panel.add(iconLabel, BorderLayout.WEST);
            panel.add(inputPanel, BorderLayout.CENTER);
            UIManager.put("OptionPane.okButtonText", "OK");
            UIManager.put("OptionPane.cancelButtonText", "Cancel");
            int result = JOptionPane.showConfirmDialog(this, panel, ingredient.getName() + " - 수량 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                int addQuantity = (Integer) quantitySpinner.getValue();
                ingredient.addQuantity(addQuantity);
                ingredientDAO.update(ingredient);
                loadIngredients();
            }
        }
    }

    /**
     * 재료 삭제 (확인 다이얼로그)
     */
    private void deleteIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (Integer) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this, name + " 재료를 정말로 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ingredientDAO.delete(id);
                loadIngredients();
            }
        }
    }

    /**
     * 재료 추가 안내 다이얼로그
     */
    private void showAddIngredientDialog() {
        JOptionPane.showMessageDialog(this, "🧺 재료를 추가해보세요!", "재료 추가", JOptionPane.INFORMATION_MESSAGE);
    }
} 