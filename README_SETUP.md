# LonghornNetwork — One-shot setup & run instructions

This file explains how to get a fresh machine from zero to a working LonghornNetwork dev environment. It includes prerequisites, one-shot scripts, and exact run commands for both Windows (PowerShell) and bash environments.

**Prerequisites (minimum tested):**

- **Java JDK 17** — required to build and run the Maven backend. Install Oracle/OpenJDK 17.
- **Maven** — 3.6+ recommended. Used to build the Java backend.
- **Node.js** — 18.x or later recommended.
- **npm** — bundled with Node, used to install frontend packages.
- **git** — to clone the repo (optional if code already present).

If you don't have package managers on Windows, consider installing Chocolatey (https://chocolatey.org) or Scoop (https://scoop.sh) and use them to install Java, Maven, Node, and Git.

Paths and versions are controlled externally — this repo's `pom.xml` targets Java 17.

Install using package managers (terminal commands)

The commands below install the minimal required tools: Java (OpenJDK 17), Maven, Git, Node.js (and npm). Pick the section for your OS.

- Windows (Chocolatey) — open an Administrator PowerShell and run:

```powershell
choco install -y git openjdk17 maven nodejs-lts
```

- Windows (Scoop) — install Scoop first, then run in PowerShell (not as Admin):

```powershell
iwr -useb get.scoop.sh | iex
scoop install git openjdk17 maven nodejs
```

- WSL / Debian & Ubuntu (apt) — Node via NodeSource to get a current Node version:

```bash
sudo apt update
sudo apt install -y git maven openjdk-17-jdk curl
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs
```

- Red Hat / CentOS / Fedora (dnf/yum):

```bash
sudo dnf install -y git maven java-17-openjdk-devel curl
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo dnf install -y nodejs
```

- macOS (Homebrew):

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"  # if brew missing
brew update
brew install git openjdk@17 maven node
```

Verification (run after install):

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

If you'd like, I can:

- Commit these scripts to your repository branch (I already added them locally).
- Create a small `scripts` README or add a `setup` npm script in `longhornnetwork-web/package.json`.

Tell me if you want me to also commit, run the scripts here (where possible), or add Windows `.bat` wrappers.
