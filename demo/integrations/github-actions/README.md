# 🔄 GitHub Actions CI/CD Integration

<div align="center">

**Automate Fuzzing in Your GitHub Workflow**

[🏠 Demo Hub](../../README.md) | [🔧 All Integrations](../)

</div>

---

## 🎯 What's Included

Complete GitHub Actions workflows for:

- ✅ Automated fuzzing on every PR
- ✅ Scheduled fuzzing runs
- ✅ Regression testing
- ✅ Artifact collection
- ✅ Security reporting

---

## 🚀 Quick Setup

### 1. Create Workflow File

Create `.github/workflows/fuzzing.yml` in your repository:

```yaml
name: Fuzzing Tests

on:
  # Run on every push and pull request
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  
  # Run on schedule (nightly)
  schedule:
    - cron: '0 2 * * *'  # 2 AM UTC daily
  
  # Allow manual triggering
  workflow_dispatch:

jobs:
  fuzz-tests:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Run regression tests
        run: mvn test
      
      - name: Run fuzz tests (5 minutes)
        run: |
          timeout 300 mvn test || true
        env:
          JAZZER_FUZZ: 1
      
      - name: Upload crash artifacts
        if: failure()
        uses: actions/upload-artifact@v3
        with:
          name: crash-inputs
          path: |
            **/src/test/resources/**/*Inputs/
            .cifuzz-corpus/
      
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: target/surefire-reports/
```

### 2. Commit and Push

```bash
git add .github/workflows/fuzzing.yml
git commit -m "Add fuzzing workflow"
git push
```

### 3. Watch It Run

- Go to your repository on GitHub
- Click "Actions" tab
- See your fuzzing workflow running!

---

## 📋 Workflow Templates

### Basic Fuzzing Workflow

Perfect for getting started:

```yaml
name: Basic Fuzzing

on: [push, pull_request]

jobs:
  fuzz:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      
      - name: Fuzz Test
        run: mvn test
        env:
          JAZZER_FUZZ: 1
```

---

### Advanced Fuzzing Workflow

With matrix testing and extended fuzzing:

```yaml
name: Advanced Fuzzing

on:
  push:
    branches: [ main ]
  pull_request:
  schedule:
    - cron: '0 2 * * *'

jobs:
  fuzz-matrix:
    strategy:
      matrix:
        java: ['11', '17', '21']
        os: [ubuntu-latest, macos-latest, windows-latest]
    
    runs-on: ${{ matrix.os }}
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Java ${{ matrix.java }}
        uses: actions/setup-java@v3
        with:
          java-version: ${{ matrix.java }}
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Build project
        run: mvn clean compile
      
      - name: Regression tests
        run: mvn test
      
      - name: Fuzz tests (10 minutes)
        run: |
          timeout 600 mvn test -Dtest="*FuzzTest" || true
        env:
          JAZZER_FUZZ: 1
          JAZZER_VALUEPROFILE: 1
        if: matrix.os == 'ubuntu-latest'
      
      - name: Upload findings
        uses: actions/upload-artifact@v3
        if: failure()
        with:
          name: crash-${{ matrix.os }}-java${{ matrix.java }}
          path: |
            **/src/test/resources/**/*Inputs/
            target/surefire-reports/
```

---

### Scheduled Deep Fuzzing

Run intensive fuzzing overnight:

```yaml
name: Deep Fuzzing

on:
  schedule:
    # Run every night at 2 AM UTC
    - cron: '0 2 * * *'
  workflow_dispatch:

jobs:
  deep-fuzz:
    runs-on: ubuntu-latest
    timeout-minutes: 480  # 8 hours max
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Restore corpus
        uses: actions/cache@v3
        with:
          path: .cifuzz-corpus
          key: corpus-${{ github.sha }}
          restore-keys: |
            corpus-
      
      - name: Extended fuzz testing (4 hours)
        run: |
          timeout 14400 mvn test || true
        env:
          JAZZER_FUZZ: 1
          JAZZER_VALUEPROFILE: 1
          JAZZER_COVERAGE: 1
      
      - name: Save corpus
        uses: actions/cache@v3
        with:
          path: .cifuzz-corpus
          key: corpus-${{ github.sha }}
      
      - name: Generate report
        if: always()
        run: |
          echo "# Fuzzing Report" > report.md
          echo "Date: $(date)" >> report.md
          echo "Corpus size: $(du -sh .cifuzz-corpus)" >> report.md
          echo "Crashes found: $(find **/src/test/resources -name 'crash-*' | wc -l)" >> report.md
      
      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: fuzzing-results
          path: |
            report.md
            **/src/test/resources/**/*Inputs/
            .cifuzz-corpus/
      
      - name: Create issue if crashes found
        if: failure()
        uses: actions/github-script@v6
        with:
          script: |
            github.rest.issues.create({
              owner: context.repo.owner,
              repo: context.repo.repo,
              title: 'Fuzzing found crashes',
              body: 'Automated fuzzing discovered new crashes. Check the workflow artifacts.',
              labels: ['bug', 'security', 'fuzzing']
            })
```

---

### Gradle Workflow

For Gradle projects:

```yaml
name: Gradle Fuzzing

on: [push, pull_request]

jobs:
  fuzz:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: 'gradle'
      
      - name: Grant execute permission
        run: chmod +x gradlew
      
      - name: Regression tests
        run: ./gradlew test
      
      - name: Fuzz tests
        run: ./gradlew fuzzTest
        env:
          JAZZER_FUZZ: 1
      
      - name: Upload results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: build/reports/tests/
```

---

## 🎯 Best Practices

### 1. Time-Boxed Fuzzing

```yaml
- name: Fuzz tests (limited time)
  run: |
    # Run for max 5 minutes
    timeout 300 mvn test || true
  env:
    JAZZER_FUZZ: 1
```

### 2. Corpus Management

```yaml
- name: Restore corpus cache
  uses: actions/cache@v3
  with:
    path: .cifuzz-corpus
    key: corpus-${{ hashFiles('**/pom.xml') }}
    restore-keys: corpus-

- name: Run fuzzing
  run: mvn test
  env:
    JAZZER_FUZZ: 1

- name: Save corpus
  uses: actions/cache@v3
  with:
    path: .cifuzz-corpus
    key: corpus-${{ hashFiles('**/pom.xml') }}
```

### 3. Security Scanning

```yaml
- name: Run fuzzing with sanitizers
  run: mvn test
  env:
    JAZZER_FUZZ: 1

- name: Check for security issues
  run: |
    if find . -name 'crash-*' | grep -q .; then
      echo "Security issues found!"
      exit 1
    fi
```

### 4. Parallel Fuzzing

```yaml
jobs:
  fuzz:
    strategy:
      matrix:
        test: [FuzzTest1, FuzzTest2, FuzzTest3]
    
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
      
      - name: Fuzz ${{ matrix.test }}
        run: mvn test -Dtest=${{ matrix.test }}
        env:
          JAZZER_FUZZ: 1
```

---

## 🔒 Security Considerations

### 1. Limit Fuzzing Time

```yaml
jobs:
  fuzz:
    timeout-minutes: 30  # Prevent runaway jobs
```

### 2. Secure Artifacts

```yaml
- name: Upload crash inputs (private)
  uses: actions/upload-artifact@v3
  with:
    name: crashes
    path: '**/crash-*'
    retention-days: 7  # Auto-delete after 7 days
```

### 3. Notify on Failures

```yaml
- name: Notify on crash
  if: failure()
  uses: actions/github-script@v6
  with:
    script: |
      github.rest.issues.create({
        owner: context.repo.owner,
        repo: context.repo.repo,
        title: '🚨 Fuzzing found issues',
        body: 'Check artifacts for details',
        labels: ['security']
      })
```

---

## 📊 Monitoring and Reporting

### Generate Coverage Reports

```yaml
- name: Generate coverage
  run: mvn test jacoco:report
  env:
    JAZZER_COVERAGE: 1

- name: Upload coverage
  uses: codecov/codecov-action@v3
  with:
    files: ./target/site/jacoco/jacoco.xml
```

### Create Summary

```yaml
- name: Create summary
  if: always()
  run: |
    echo "## Fuzzing Results" >> $GITHUB_STEP_SUMMARY
    echo "- Tests run: $(grep -c 'testcase' target/surefire-reports/*.xml || echo 0)" >> $GITHUB_STEP_SUMMARY
    echo "- Crashes: $(find . -name 'crash-*' | wc -l)" >> $GITHUB_STEP_SUMMARY
    echo "- Corpus size: $(du -sh .cifuzz-corpus | cut -f1)" >> $GITHUB_STEP_SUMMARY
```

---

## 🚀 Next Steps

1. [Set Up Maven Project](../maven-template/)
2. [Set Up Gradle Project](../gradle-template/)
3. [Write Fuzz Tests](../../tutorials/first-fuzz-test.md)
4. [View Examples](../../examples/)

---

## 📚 Resources

- 📖 [GitHub Actions Documentation](https://docs.github.com/en/actions)
- 🎯 [Jazzer Configuration](../../../docs/arguments-and-configuration-options.md)
- 💡 [CI/CD Best Practices](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)

---

<div align="center">

**Automate Your Security Testing! 🔄**

[🏠 Demo Hub](../../README.md)

</div>
