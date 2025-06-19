# 🍳 나만의 레시피 냉장고

[![시연 영상 바로가기](https://img.shields.io/badge/YouTube-시연영상-red?logo=youtube)](https://www.youtube.com/watch?v=fgTBMG-kUqk)


---

## 📌 프로젝트 소개

"나만의 레시피 냉장고"는
내가 가진 재료로 만들 수 있는 요리 레시피를 관리하고,
냉장고 속 재료와 요리 기록, 평점, 메모, 즐겨찾기 등
다양한 기능을 통해 요리 생활을 더 편리하게 만들어주는
**Java 기반 데스크탑 애플리케이션**입니다.

---

## ✨ 주요 기능

### 🥕 냉장고 속 재료 관리
- 새 재료 추가, 이름 수정, 삭제, 수량 추가
- 장을 보고 온 뒤 재고를 바로 반영 가능
- 냉장고에 어떤 재료가 얼마나 남았는지 한눈에 확인

### 🍲 레시피 관리
- 나만의 요리 레시피 추가, 수정, 삭제
- 각 레시피별로 필요한 재료와 수량, 조리 방법 입력
- 레시피별로 평점(별점), 메모, 즐겨찾기, 마지막 요리 일자 관리

### 🍳 요리하기
- 레시피를 선택해 "요리하기" 버튼 클릭 시
  - 필요한 재료가 충분한지 자동 체크
  - 부족하면 안내, 충분하면 재고 자동 차감 및 마지막 요리 일자 기록

### ❤️ 즐겨찾기/평점/메모
- 자주 해먹는 요리는 즐겨찾기로 등록, 하트(♥)로 표시
- 평점(별점)과 메모를 남겨 나만의 요리 노하우 축적
- 상세보기에서 모든 정보 한눈에 확인

### 🖼️ 직관적이고 귀여운 UI
- 연한 오렌지 파스텔톤, 넉넉한 여백, 귀여운 이모지 아이콘
- "맑은 고딕" 폰트, 라운드 버튼 등 깔끔한 디자인

---

## 🎬 시연 영상

- [YouTube 시연 영상 바로가기](https://www.youtube.com/watch?v=fgTBMG-kUqk)

---

## 🛠️ 프로젝트 구조 및 기술

- **언어/플랫폼**: Java 11, Swing(GUI), MySQL
- **구성**:  
  - UI: MainFrame, RecipePanel, IngredientPanel 등  
  - DAO/DTO: 데이터베이스 연동  
  - DB: recipes, ingredients, recipe_ingredients, favorite_recipes 등 테이블

---

## 🚀 처음 실행하는 방법

1. **프로젝트 클론**
   ```bash
   git clone https://github.com/jiwoo1105/JavaProgramming.git
   cd JavaProgramming
   ```

2. **DB(MySQL) 준비**
   - MySQL이 설치되어 있어야 합니다.
   - `src/main/java/com/example/db/setup_database.sql` 파일을 이용해 DB를 생성하세요.
     ```bash
     mysql -u [사용자명] -p < src/main/java/com/example/db/setup_database.sql
     ```
   - DB 접속 정보는 코드 내에서 필요시 수정하세요.

3. **프로젝트 빌드**
   ```bash
   mvn clean package
   ```

4. **프로그램 실행**
   ```bash
   java -jar target/recipe-manager-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

5. **실행 후**
   - GUI가 뜨면, 재료와 레시피를 자유롭게 추가/관리할 수 있습니다!

---

## 💡 프로젝트의 특징 & 활용 예시

- **실제 냉장고 재고와 연동된 요리 경험**  
  → 요리할 때마다 재고가 자동 차감, 부족한 재료는 장보기 전에 미리 파악!
- **나만의 요리 노트**  
  → 평점, 메모, 즐겨찾기로 내가 좋아하는 요리와 노하우를 기록
- **가족/친구와의 공유**  
  → 상세보기에서 모든 정보를 한눈에 볼 수 있어 추천도 쉽다!

---

## 🙌 개발 후기 & 개선점

- Java와 Swing, MySQL을 활용해 실생활에 바로 쓸 수 있는 프로그램을 만들었습니다.
- DAO 패턴, DB 정규화 등으로 확장성과 유지보수성을 높였습니다.
- 추후 모바일/웹 버전, 사용자별 계정 기능 등도 확장 가능성이 있습니다.

---

## 📎 기타

- **프로젝트 GitHub**: [https://github.com/jiwoo1105/JavaProgramming.git](https://github.com/jiwoo1105/JavaProgramming.git)
- **시연 영상**: [https://www.youtube.com/watch?v=fgTBMG-kUqk](https://www.youtube.com/watch?v=fgTBMG-kUqk)

---

> **"나만의 레시피 냉장고"로 더 똑똑하고 즐거운 요리 생활을 시작해보세요!** 