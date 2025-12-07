# LonghornNetwork — One-shot setup & run instructions

This file explains how to get a fresh machine from zero to a working LonghornNetwork dev environment. It includes prerequisites, one-shot scripts, and exact run commands for both Windows (PowerShell) and bash environments.

**Prerequisites (minimum tested):**

- **Java JDK 17** — required to build and run the Maven backend. Install Oracle/OpenJDK 17.
- **Maven** — 3.6+ recommended. Used to build the Java backend.
- **Node.js** — 18.x or later recommended.
- **npm** — bundled with Node, used to install frontend packages.
- **git** — to clone the repo (optional if code already present).

If you don't have package managers on Windows, install Chocolatey (https://chocolatey.org). Make sure after this is install RESTART your computer

Paths and versions are controlled externally — this repo's `pom.xml` targets Java 17.

Install using package managers (terminal commands)

The commands below install the minimal required tools: Java (OpenJDK 17), Maven, Git, Node.js (and npm). Pick the section for your OS.

- Windows (Chocolatey) — open an Administrator PowerShell and run:

```powershell
choco install -y git openjdk17 maven nodejs-lts
```

Verification (run after install): (Close your powershell terminal restart your computer than continue)

```bash
git --version
mvn -v
java -version
node -v
npm -v
```

Files added to help automation:

- `ONE_SHOT_SETUP.sh` — POSIX bash script to check prerequisites, run `mvn clean package -DskipTests`, and run `npm ci` in `longhornnetwork-web`.
- `ONE_SHOT_SETUP.ps1` — PowerShell equivalent. Use `-Dev` to start the frontend dev server after install.

Quick flow (recommended):

1. Clone the repo (if you haven't already):

```bash
git clone https://github.com/aaravlakdawala/LonghornNetwork.git
cd LonghornNetwork
```

2. Run the one-shot script for your shell.

- On Git Bash / WSL / other bash shells:

```bash
./ONE_SHOT_SETUP.sh
```

- On Windows PowerShell (run as Administrator if installing system packages):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\ONE_SHOT_SETUP.ps1
# or start in dev mode (installs, then launches frontend dev server):
.\ONE_SHOT_SETUP.ps1 -Dev
```

What the scripts do:

- Verify `git`, `java`, `mvn`, `node`, and `npm` are available — if missing, scripts stop and point you to this README.
- Run `mvn clean package -DskipTests` to build the backend and copy dependencies to `target/lib`.
- If `longhornnetwork-web` exists, run `npm ci` there to install exact dependency versions from `package-lock.json` (or `package.json` if lockfile missing).

**Port Configuration:**

- **Backend (Java)**: Runs on port **8080** (configured in `Main.java` via `port(8080)`)
- **Frontend (React/Vite)**: Runs on port **5173** (configured in `vite.config.js`)

Run commands (after setup):

- Start the Java backend (option A: using Maven exec plugin):

```bash
mvn -q exec:java -Dexec.mainClass="Main"
```

- Or run the packaged jar (option B: after `mvn package`):

```bash
cd target
java -cp "longhornnetwork-server-0.1.0.jar;lib/*" Main    # PowerShell/Windows syntax
# or on bash (Linux/WSL/macOS):
java -cp "longhornnetwork-server-0.1.0.jar:lib/*" Main
```

Note: the artifact name `longhornnetwork-server-0.1.0.jar` is taken from `pom.xml` <artifactId> and <version>. Adjust if you changed the version.

- Start the frontend dev server (in a separate terminal):

```bash
cd longhornnetwork-web
npm run dev
# Server will be available at http://localhost:5173
```

If you prefer to serve a built frontend (production-like):

```bash
cd longhornnetwork-web
npm run build
# Then serve the `dist`/`build` folder with a static server (e.g. `npx serve dist` or any static file server)
```

Troubleshooting

- If the script exits with code 2 and says a command is missing, install the missing tool and re-run the one-shot.
- If Maven fails due to JDK version mismatch, confirm `java -version` prints a Java 17 JDK (not only a JRE). On Windows, ensure JAVA_HOME points to the JDK installation.
- If `npm ci` fails, check `node -v` and `npm -v`. Consider deleting `node_modules` and `package-lock.json` then re-running `npm install`.
- On Windows with `bash.exe` (Git Bash), some commands that rely on Windows paths may behave differently. When in doubt use the PowerShell script.

Optional: commit the added scripts to your repo and share them with contributors so they can run the same one-shot setup.

Answer the following questions in your README:

a. Did you use AI to code the UI? If so, what were the sources that the AI used, what was the AI good at and what was it not so good at? What did you do to fill in the gaps.

AI's Strengths: The AI was exceptionally good at generating the initial structural skeleton of the React components and suggesting different design options and patterns for the UI architecture. It provided a quick way to draft the boilerplate code, saving significant time.

AI's Weaknesses: The AI struggled with creating a truly polished and visually appealing design (beautify/styling) and generating a fully functional, complex algorithm (e.g., the WebSocket logic) that worked seamlessly with the backend. I had to manually fix numerous bugs and logical errors within the generated code.

Filling the Gaps: To address the AI's shortcomings, I relied heavily on external resources for debugging, styling, and implementing complex logic:

YouTube Tutorials: For visual demonstrations of best practices and complex component implementation.

Stack Overflow: For specific error troubleshooting and efficient code snippets.

GeeksforGeeks / Official Documentation: For deep dives into language features, React hooks, and library specifications.

b. If you did not use AI, what sources did you use to learn React, and what were the hardest things to learn?

The hardest thing to learn and implement in this project was definitely the WebSocketServer integration. Establishing and maintaining a reliable, real-time connection between the frontend and the backend was challenging. I had to ensure that both the client-side (frontend) and the server-side (backend) logic handled state updates and connection failures correctly.

Aside from the WebSockets, while the creation of the .jsx component files was generally enjoyable and straightforward, it became quite tedious and repetitive. This is where I let the AI handle the bulk of the component generation, and I subsequently went back to implement the custom styling, logic refinement, and aesthetic beautification.

c. We are planning to cover React next semester for this class, in what unit do you think this would be appropriate to teach?

I strongly recommend integrating React into the curriculum during the unit dedicated to Client-Server Architecture and DNS (Domain Name System).

React, being a framework for building dynamic single-page applications, perfectly encapsulates and implements all the core concepts covered in that unit:

Client-Server Interaction: React components make asynchronous calls (like fetching data via fetch or Axios) to a server/API, demonstrating how the client requests resources.

State Management: It clearly shows how the client (the browser) manages its own state and renders the UI based on server responses.

Real-World Implementation: Introducing it here provides students with a tangible, modern example of how all the theoretical networking and architectural concepts they learned are fully implemented in a functional web application.
