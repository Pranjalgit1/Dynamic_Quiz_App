# Quiz Master 🏆

A premium, modern desktop JavaFX application that provides a dynamic quiz experience. Features a sleek, futuristic dark UI with glassmorphism, glowing neon accents, and comprehensive performance analytics.

## ✨ Features

- **Futuristic UI**: Beautiful dark theme with smooth glassmorphism panels, neon accents, and interactive hover effects.
- **Dynamic Questions**: Loads questions from a local SQLite database (`quiz_data.db`), categorized by difficulty (Easy, Medium, Hard).
- **Result Dashboard**: A split-screen dashboard providing a summary of your performance, grade, and actionable insights based on the difficulty of questions you got wrong.
- **Detailed Analytics**: View an interactive bar chart of your performance breaking down questions by Answered/Unanswered/Correct and Difficulty levels.
- **Local Storage**: All quiz sessions, scores, and questions are stored completely offline using an embedded SQLite database.

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 21 or higher.
- **Maven**: To build and manage dependencies.
- **JavaFX**: Included via Maven dependencies.

### Installation & Running

1. Clone the repository:
   ```bash
   git clone https://github.com/Pranjalgit1/Dynamic_Quiz_App.git
   cd Dynamic_Quiz_App
   ```

2. **Run the Application (Windows)**:
   You can easily launch the app using the provided batch file:
   ```bash
   run.bat
   ```

   Alternatively, use Maven to run it directly:
   ```bash
   mvn clean javafx:run
   ```

## 📸 Screenshots

*(Replace the paths below with the actual paths to your screenshots once you capture them)*

### 1. Start Screen
![Start Screen](screenshot-start.png)
*The landing page to enter your name and start the quiz session.*

### 2. Quiz Interface
![Quiz Interface](screenshot-quiz.png)
*The active quiz session featuring a progress bar, timer, and glassmorphic option cards.*

### 3. Result Dashboard
![Result Dashboard](quiz/view/trophy.png)
*The comprehensive split-screen results panel, showing the shiny 3D trophy, detailed stats, and performance chart.*

## 🛠️ Technology Stack

- **Java 21**: Core application logic.
- **JavaFX 21**: UI framework for rendering the modern interface and graphics.
- **SQLite**: Local relational database for managing questions and saving user statistics.
- **Maven**: Build tool and dependency manager.

## 📂 Project Structure

```text
JAVA_PBL/
├── pom.xml                # Maven project configuration
├── run.bat                # Windows executable to launch the app
├── quiz_data.db           # SQLite database storing questions and results
└── quiz/
    ├── Main.java          # Application entry point
    ├── SceneManager.java  # Handles switching between FXML views
    ├── controller/        # JavaFX Controllers (Start, Quiz, Result, Analysis)
    ├── model/             # Data models (Question, QuizSession, QuizResult)
    ├── manager/           # Database and API Managers
    └── view/              # FXML layouts, CSS styles, and Image assets
```

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](../../issues).

## 📝 License

This project is licensed under the MIT License.
