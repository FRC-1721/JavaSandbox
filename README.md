<h1 style="color:lightblue;">Reefscape Java Test</h1>

## git
- Type the following to clone the repository
  ```bash
  git clone https://github.com/Smittysports/ReefscapeJavaTest.git
- Type the following to create your own branch
  ```bash
  git checkout -b YourBranchName
- Type the following to rename your own branch (useful if you came up with new ideas since creation)
  ```bash
  git branch -m YourBranchName YourNewBranchName

## Code Formatting
- Type the following to perform a code formatting check:
  ```bash
  ./gradlew spotlessCheck
- Type the following to apply code formatting:
  ```bash
  ./gradlew spotlessApply