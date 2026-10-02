$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing
$root = Split-Path -Parent $PSScriptRoot
$assetDirectory = Join-Path $root "web\assets"
$desktopDirectory = Join-Path $root "assets"
New-Item -ItemType Directory -Force -Path $assetDirectory, $desktopDirectory | Out-Null

function New-AuroraIcon([int]$size, [string]$path) {
    $bitmap = New-Object Drawing.Bitmap $size, $size
    $graphics = [Drawing.Graphics]::FromImage($bitmap)
    $graphics.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.Clear([Drawing.Color]::FromArgb(10, 32, 41))
    $scale = $size / 128.0
    $mint = [Drawing.Color]::FromArgb(91, 240, 196)
    $amber = [Drawing.Color]::FromArgb(255, 190, 92)
    $white = [Drawing.Color]::FromArgb(223, 253, 242)
    $pen = New-Object Drawing.Pen $mint, (7 * $scale)
    $pen.StartCap = $pen.EndCap = [Drawing.Drawing2D.LineCap]::Round
    $graphics.DrawArc($pen, 21*$scale, 21*$scale, 86*$scale, 86*$scale, -25, 300)
    $shield = [Drawing.PointF[]]@(
        [Drawing.PointF]::new(41*$scale, 62*$scale), [Drawing.PointF]::new(48*$scale, 45*$scale),
        [Drawing.PointF]::new(64*$scale, 39*$scale), [Drawing.PointF]::new(80*$scale, 45*$scale),
        [Drawing.PointF]::new(88*$scale, 62*$scale), [Drawing.PointF]::new(88*$scale, 74*$scale),
        [Drawing.PointF]::new(81*$scale, 94*$scale), [Drawing.PointF]::new(64*$scale, 106*$scale),
        [Drawing.PointF]::new(47*$scale, 94*$scale), [Drawing.PointF]::new(40*$scale, 74*$scale)
    )
    $graphics.FillPolygon((New-Object Drawing.SolidBrush ([Drawing.Color]::FromArgb(15, 48, 56))), $shield)
    $graphics.DrawPolygon((New-Object Drawing.Pen $white, (4*$scale)), $shield)
    $graphics.FillEllipse((New-Object Drawing.SolidBrush $amber), 57*$scale, 66*$scale, 14*$scale, 14*$scale)
    $graphics.FillRectangle((New-Object Drawing.SolidBrush $amber), 61*$scale, 77*$scale, 6*$scale, 15*$scale)
    $bitmap.Save($path, [Drawing.Imaging.ImageFormat]::Png)
    $graphics.Dispose(); $bitmap.Dispose(); $pen.Dispose()
}

New-AuroraIcon 192 (Join-Path $assetDirectory "aurora-vault-192.png")
New-AuroraIcon 512 (Join-Path $assetDirectory "aurora-vault-512.png")
Copy-Item (Join-Path $assetDirectory "aurora-vault-512.png") (Join-Path $desktopDirectory "aurora-vault.png") -Force

# ICO can contain a PNG payload; modern Windows reads this compact form directly.
$png = [IO.File]::ReadAllBytes((Join-Path $assetDirectory "aurora-vault-192.png"))
$icoPath = Join-Path $desktopDirectory "aurora-vault.ico"
$stream = [IO.File]::Create($icoPath)
$writer = New-Object IO.BinaryWriter $stream
$writer.Write([uint16]0); $writer.Write([uint16]1); $writer.Write([uint16]1)
$writer.Write([byte]0); $writer.Write([byte]0); $writer.Write([byte]0); $writer.Write([byte]0)
$writer.Write([uint16]1); $writer.Write([uint16]32); $writer.Write([uint32]$png.Length); $writer.Write([uint32]22)
$writer.Write($png); $writer.Dispose(); $stream.Dispose()
Write-Output "Aurora Vault icons generated."
