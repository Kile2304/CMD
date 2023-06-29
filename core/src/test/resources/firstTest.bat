@echo off

>nul chcp 65001

rem Inizio dello script principale

rem Imposta i caratteri di escape
set "esc="
set "reset=%esc%[0m"

rem Funzione per scrivere il testo colorato e ripristinare lo stile
:writeText
echo %esc%[31mTesto di colore 1%reset%
echo %esc%[32mTesto di colore 2%reset%
echo %esc%[33mTesto di colore 3%reset%
echo %esc%[34mTesto di colore 4%reset%
echo %esc%[35mTesto di colore 5%reset%

rem Aggiungi una riga vuota
echo .

rem Aspetta n secondi e aggiorna la progress bar
set /a "timerSeconds=5"
set "_spc=          "
set "_bar=■■■■■■■■■■"

setlocal enabledelayedexpansion

for /f %%a in ('copy /Z "%~dpf0" nul')do for /f skip^=4 %%b in ('echo;prompt;$H^|cmd')do set "BS=%%~b" & set "CR=%%a"
for /l %%L in (1 1 10)do <con: set /p "'= !CR!!BS!!CR![!_bar:~0,%%~L!!BS!!_spc:~%%~L!] " <nul & >nul timeout.exe /t 1
echo .

echo Riga da cancellare
echo Riga da cancellare
echo Riga da cancellare

rem Cancella la riga precedente
echo %esc%[1A%esc%[2K

rem Aggiungi altro testo
echo Testo dopo la cancellazione

rem Aspetta n secondi
timeout /t 2 >nul

rem Muovi il cursore a sinistra
echo Spostamento del cursore%esc%[100D Rimossa la parte a sinistra

rem Aggiungi ancora altro testo
echo Testo dopo lo spostamento del cursore

echo cancello tutto %esc%[J

rem Fine dello script
exit /b
