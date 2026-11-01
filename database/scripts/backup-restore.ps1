<#
.SYNOPSIS
    Automated Backup & Restore Utility for Smart Water Management System (Windows)
.DESCRIPTION
    Performs directory, custom archive, and plain-SQL backups/restores for PostgreSQL.
.PARAMETER Action
    'backup' or 'restore'
.PARAMETER Format
    'custom' (-Fc, recommended) or 'sql' (plain text)
.PARAMETER BackupFile
    Target path for backup or source path for restore
#>

param (
    [Parameter(Mandatory=$true)]
    [ValidateSet('backup', 'restore', 'verify')]
    [string]$Action,

    [ValidateSet('custom', 'sql')]
    [string]$Format = 'custom',

    [string]$Database = 'smart_water_management',
    [string]$DbHost = 'localhost',
    [int]$Port = 5432,
    [string]$Username = 'postgres',
    [string]$BackupFile = ''
)

$PsqlBin = "C:\Program Files\PostgreSQL\18\bin"
if (-not (Test-Path "$PsqlBin\pg_dump.exe")) {
    $found = Get-Command pg_dump -ErrorAction SilentlyContinue
    if ($found) {
        $PsqlBin = Split-Path $found.Source
    } else {
        Write-Error "Could not locate PostgreSQL binaries. Ensure PostgreSQL is installed."
        exit 1
    }
}

$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$BackupDir = Join-Path $PSScriptRoot "..\backups"
if (-not (Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
}

if (-not $env:PGPASSWORD) {
    $env:PGPASSWORD = "postgres"
}

switch ($Action) {
    'backup' {
        if (-not $BackupFile) {
            $ext = if ($Format -eq 'custom') { 'dump' } else { 'sql' }
            $BackupFile = Join-Path $BackupDir "${Database}_${Timestamp}.$ext"
        }
        Write-Host ">>> Backing up database '$Database' to: $BackupFile" -ForegroundColor Cyan
        
        if ($Format -eq 'custom') {
            & "$PsqlBin\pg_dump.exe" -h $DbHost -p $Port -U $Username -F c -b -v -f $BackupFile $Database
        } else {
            & "$PsqlBin\pg_dump.exe" -h $DbHost -p $Port -U $Username -F p -v -f $BackupFile $Database
        }

        if ($LASTEXITCODE -eq 0) {
            Write-Host ">>> Backup completed successfully: $BackupFile" -ForegroundColor Green
        } else {
            Write-Error "Backup failed with exit code $LASTEXITCODE."
        }
    }

    'restore' {
        if (-not $BackupFile -or -not (Test-Path $BackupFile)) {
            Write-Error "Please provide a valid BackupFile path for restore."
            exit 1
        }
        Write-Host ">>> Restoring database '$Database' from: $BackupFile" -ForegroundColor Yellow

        if ($BackupFile.EndsWith('.dump') -or $Format -eq 'custom') {
            & "$PsqlBin\pg_restore.exe" -h $DbHost -p $Port -U $Username -d $Database --clean --if-exists -v $BackupFile
        } else {
            & "$PsqlBin\psql.exe" -h $DbHost -p $Port -U $Username -d $Database -f $BackupFile
        }

        if ($LASTEXITCODE -eq 0) {
            Write-Host ">>> Restore completed successfully." -ForegroundColor Green
        } else {
            Write-Error "Restore finished with warnings or exit code $LASTEXITCODE."
        }
    }

    'verify' {
        Write-Host ">>> Verifying database connectivity and table inventory for '$Database'..." -ForegroundColor Cyan
        & "$PsqlBin\psql.exe" -h $DbHost -p $Port -U $Username -d $Database -c "\dt"
        & "$PsqlBin\psql.exe" -h $DbHost -p $Port -U $Username -d $Database -c "\dv"
    }
}
