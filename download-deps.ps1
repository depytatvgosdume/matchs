# Скачивает Android Gradle Plugin и Kotlin в local-repo проекта.
# Запуск: правый клик -> "Выполнить с помощью PowerShell"
# или в терминале: powershell -ExecutionPolicy Bypass -File download-deps.ps1

$ErrorActionPreference = "Stop"
$repoRoot = Join-Path $PSScriptRoot "local-repo"

function Get-MavenPath {
    param([string]$Group, [string]$Artifact, [string]$Version, [string]$Ext)
    $groupPath = ($Group -replace '\.', '/')
    $fileName = "$Artifact-$Version.$Ext"
    $dir = Join-Path $repoRoot "$groupPath/$Artifact/$Version"
    $file = Join-Path $dir $fileName
    return @{ Dir = $dir; File = $file; FileName = $fileName; GroupPath = $groupPath; Artifact = $Artifact; Version = $Version; Ext = $Ext }
}

function Download-Artifact {
    param([string]$Group, [string]$Artifact, [string]$Version, [string[]]$Extensions = @("pom", "jar"))

    foreach ($ext in $Extensions) {
        $m = Get-MavenPath -Group $Group -Artifact $Artifact -Version $Version -Ext $ext
        if (Test-Path $m.File) {
            Write-Host "[skip] $($m.FileName)" -ForegroundColor DarkGray
            continue
        }

        New-Item -ItemType Directory -Force -Path $m.Dir | Out-Null

        $urls = @(
            "https://maven.aliyun.com/repository/google/$($m.GroupPath)/$($m.Artifact)/$($m.Version)/$($m.FileName)",
            "https://maven.aliyun.com/repository/public/$($m.GroupPath)/$($m.Artifact)/$($m.Version)/$($m.FileName)",
            "https://maven.aliyun.com/repository/central/$($m.GroupPath)/$($m.Artifact)/$($m.Version)/$($m.FileName)",
            "https://dl.google.com/dl/android/maven2/$($m.GroupPath)/$($m.Artifact)/$($m.Version)/$($m.FileName)",
            "https://repo1.maven.org/maven2/$($m.GroupPath)/$($m.Artifact)/$($m.Version)/$($m.FileName)"
        )

        $ok = $false
        foreach ($url in $urls) {
            try {
                Write-Host "[try] $url"
                Invoke-WebRequest -Uri $url -OutFile $m.File -TimeoutSec 120 -UseBasicParsing
                if ((Get-Item $m.File).Length -gt 0) {
                    Write-Host "[ ok ] $($m.FileName)" -ForegroundColor Green
                    $ok = $true
                    break
                }
            } catch {
                Write-Host "       failed: $($_.Exception.Message)" -ForegroundColor Yellow
            }
        }

        if (-not $ok) {
            Write-Host "[FAIL] $($m.FileName) - скачайте вручную в:`n       $($m.Dir)" -ForegroundColor Red
        }
    }
}

Write-Host "`n=== Android deps -> $repoRoot ===`n" -ForegroundColor Cyan

# Buildscript
Download-Artifact "com.android.tools.build" "gradle" "8.2.0"
Download-Artifact "org.jetbrains.kotlin" "kotlin-gradle-plugin" "1.9.22"

# App dependencies
Download-Artifact "androidx.core" "core-ktx" "1.12.0" @("pom", "aar")
Download-Artifact "androidx.appcompat" "appcompat" "1.6.1" @("pom", "aar")
Download-Artifact "com.google.android.material" "material" "1.11.0" @("pom", "aar")
Download-Artifact "androidx.constraintlayout" "constraintlayout" "2.1.4" @("pom", "aar")

Write-Host "`nГотово. Если были [FAIL] — скачайте файлы в браузере и положите в указанные папки." -ForegroundColor Cyan
Write-Host "Затем в Android Studio: File -> Sync Project with Gradle Files`n"
