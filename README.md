# 🚀 Code Brahma

Code Brahma is a beautiful, community-driven Android app built using **Jetpack Compose**.  
It serves as a learning hub for developers to:

- 📚 Learn from curated coding resources  
- 🏆 Track learning progress and climb the leaderboard  
- 🔐 Showcase GitHub profiles with OAuth login  

Whether you're a beginner learning to code or an experienced dev sharing knowledge, **Code Brahma** is your developer playground.

---

## ✨ Features

### ✅ Phase 1 – Core UI
- 🔐 **GitHub OAuth Login** – Secure sign-in with GitHub profile fetch  
- 📚 **Courses Screen** – Browse hand-picked learning resources  
- 🏆 **Leaderboard** – See top coders based on activity *(mock for now)*  
- 🏠 **Dashboard** – Central hub with quick access to all features  
- 🙍‍♂️ **Profile Screen** *(New)* – View your GitHub avatar, name, username, and bio

### ⚙️ Phase 2 – Data Integration
- ☁️ Fetch GitHub contribution stats via API  
- 📂 Local progress tracking using Room DB  
- 📊 Live leaderboard based on GitHub metrics  

### 🤝 Phase 3 – Community
- 👥 Add friends via GitHub  
- 🔔 Daily reminders for coding streaks  
- 🌎 Global leaderboard from real GitHub data  

---

## 🛠️ Tech Stack

| Layer           | Tech |
|----------------|------|
| Language        | Kotlin |
| UI Framework    | Jetpack Compose |
| Design System   | Material 3 |
| Navigation      | Navigation Compose |
| Data Layer      | Room Database *(planned)* |
| Auth & API      | GitHub OAuth + GitHub REST API |
| Build System    | Gradle Kotlin DSL |

---

## 📖 Getting Started

Set up the project in under 5 minutes.

### 🔧 Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 33+
- Kotlin 1.9+
- GitHub developer account

### 🚀 Setup Steps

1. **Clone the repo**
   ```bash
   git clone https://github.com/<your-username>/CodeBrahma-An-app-for-Developers.git
   cd CodeBrahma-An-app-for-Developers
Open in Android Studio

Open Android Studio

Click "Open Project" and select this folder

Configure GitHub OAuth

Create a GitHub OAuth App

Add these to your local.properties:

properties
Copy code
GITHUB_CLIENT_ID=your_client_id
GITHUB_CLIENT_SECRET=your_client_secret
Build & Run

Hit the ▶️ Run button or use a connected emulator/device

💡 Need help? Check the CONTRIBUTING.md or open an issue!

🛠️ How to Contribute
We welcome PRs of all sizes! Here’s how you can contribute:

🧪 1. Fork & Clone
bash
Copy code
git clone https://github.com/<your-username>/CodeBrahma-An-app-for-Developers.git
cd CodeBrahma-An-app-for-Developers
🌱 2. Create a Branch
bash
Copy code
git checkout -b feature/your-feature-name
✏️ 3. Make Changes Locally
Use Android Studio or your favorite editor.

✅ 4. Stage & Commit
bash
Copy code
git add .
git commit -m "feat: describe your change here"
📤 5. Push to GitHub
bash
Copy code
git push origin feature/your-feature-name
📬 6. Create Pull Request
Go to your forked repo

Click “Compare & pull request”

Add a descriptive title: docs: improve README

Submit!

🔁 Maintainers may request changes — just push commits to the same branch.

🔮 Future Scope
🔥 Coding Streak Tracker – Motivate users with daily coding streaks

📊 Live Leaderboard – Rank users based on real GitHub activity

💬 In-app Discussions – Allow users to ask questions and collaborate

🎯 Daily Coding Challenges – Bite-sized tasks to stay consistent

📱 Home Screen Widgets – Quick access to streaks or goals

---

## 🧭 Architecture Diagram

The following flowchart shows the high-level screens and navigation for the CodeBrahma app. This was added to satisfy issue #15 (flowchart or KANBAN board).

![Flowchart](docs/flowchart.svg)

You can also find a lightweight Kanban template in `docs/kanban-board.md`.