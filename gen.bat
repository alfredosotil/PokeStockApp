@echo off
setlocal EnableExtensions EnableDelayedExpansion

REM === Comprobaciones básicas ===
where docker >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Docker Desktop no esta instalado o no esta en PATH.
  exit /b 1
)

docker compose version >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Docker Compose v2 no disponible. Actualiza Docker Desktop.
  exit /b 1
)

if not exist "docker-compose.yml" (
  echo [ERROR] No se encontro docker-compose.yml en este directorio.
  exit /b 1
)

if not exist "domain.jdl" (
  echo [ERROR] domain.jdl no existe, por favor crearlo...
  exit /b 1
)

echo [STEP] Levantando contenedor del generador...
docker compose up -d
if errorlevel 1 (
  echo [ERROR] No se pudo levantar el contenedor. Revisa Docker Desktop.
  exit /b 1
)

echo [STEP] Generando la app con JHipster (dentro del contenedor)...
docker compose exec -T jhipster bash -lc "ls -la && jhipster jdl domain.jdl --no-insight --force"
if errorlevel 1 (
  echo [ERROR] Fallo la generacion con JHipster.
  exit /b 1
)
docker compose exec -u root -T jhipster sh -lc "mkdir -p /home/jhipster/.m2 /home/jhipster/.npm /home/jhipster/.gradle && chown -R jhipster:jhipster /home/jhipster/.m2 /home/jhipster/.npm /home/jhipster/.gradle"
echo [STEP] Compilando backend y preparando frontend...
docker compose exec -T jhipster bash -lc "./mvnw -ntp -DskipTests compile && cd src/main/webapp && npm ci"
if errorlevel 1 (
  echo [WARN] Compilacion/instalacion de deps tuvo advertencias o errores. Revisa logs.
)

echo.
echo ✅ Proyecto generado en tu carpeta local.
echo.
echo ▶ Para ejecutar en DEV:
echo    1) Backend:    .\mvnw
echo    2) Frontend:   cd src\main\webapp ^&^& npm start
echo    3) PostgreSQL: docker compose -f src/main/docker/postgresql.yml up -d
echo.
echo.
endlocal
