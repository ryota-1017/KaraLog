@echo off
cd /d "%~dp0"
echo ==============================================
echo  KaraLog GitHub Push
echo ==============================================
echo.
git push -u origin main --force
echo.
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] Push completed successfully!
) else (
    echo [ERROR] Push failed.
)
pause
