# StudyFlow — JavaFX Study & Task Manager

A minimal dark-mode desktop app for study planning and task management.

## Included requirements
- JavaFX desktop GUI with a full-screen, resizable layout
- Login + create-account flow
- SQLite relational database for users, tasks, and study sessions
- Java multithreading/concurrency with executor services for database/API work and the study timer
- JSON parsing with Jackson from a live HTTP API (`dummyjson.com/quotes/random`)
- Clean Apple-inspired dark UI without external image assets

## Run in IntelliJ IDEA
1. Install JDK 17 or newer.
2. Open this folder as a Maven project in IntelliJ IDEA.
3. Let Maven download dependencies.
4. Run `com.studyflow.Main` or use the Maven goal `javafx:run`.

The project detects Windows/Linux/macOS and selects the correct JavaFX native classifier.

## Data location
The SQLite file is created automatically at:
`~/.studyflow/studyflow.db`

## Notes
- The API panel still works if the internet is temporarily unavailable; it falls back to a local message.
- Passwords are stored as salted SHA-256 hashes rather than plain text.
