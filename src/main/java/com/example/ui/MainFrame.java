package com.example.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private RecipePanel recipePanel;
    private IngredientPanel ingredientPanel;

    public MainFrame() {
        setTitle("🍳 나만의 레시피 냉장고");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 전체 배경색(파스텔톤)
        Color bgColor = new Color(255, 245, 230); // 연한 오렌지
        getContentPane().setBackground(bgColor);

        // 상단 타이틀 패널
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 230, 200));
        JLabel titleLabel = new JLabel("🍳 나만의 레시피 냉장고");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 140, 60));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 각 패널 초기화
        ingredientPanel = new IngredientPanel();
        recipePanel = new RecipePanel(ingredientPanel);

        // 탭 패널 생성
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        tabbedPane.setBackground(new Color(255, 245, 230));
        tabbedPane.setForeground(new Color(255, 140, 60));
        tabbedPane.addTab("🍲 레시피 관리", new JScrollPane(recipePanel));
        tabbedPane.addTab("🥕 나의 냉장고 속 재료", new JScrollPane(ingredientPanel));
        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // 전역 폰트 설정 (귀여운 느낌)
        UIManager.put("Button.font", new Font("맑은 고딕", Font.BOLD, 15));
        UIManager.put("Label.font", new Font("맑은 고딕", Font.PLAIN, 15));
        UIManager.put("Table.font", new Font("맑은 고딕", Font.PLAIN, 15));
        UIManager.put("TableHeader.font", new Font("맑은 고딕", Font.BOLD, 15));
        UIManager.put("TextField.font", new Font("맑은 고딕", Font.PLAIN, 15));
        UIManager.put("TextArea.font", new Font("맑은 고딕", Font.PLAIN, 15));
        UIManager.put("TabbedPane.font", new Font("맑은 고딕", Font.BOLD, 15));
        UIManager.put("Panel.background", new Color(255, 245, 230));
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
} 