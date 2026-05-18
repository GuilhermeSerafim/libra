$ErrorActionPreference = 'Stop'

$isbn = '9780132350884'
$root = Split-Path -Parent $PSScriptRoot
$target = Join-Path $root 'src/test/resources/wiremock/openlibrary/__files/open-library-clean-code-response.json'
$url = "https://openlibrary.org/api/books?bibkeys=ISBN:$isbn&format=json&jscmd=data"

$response = Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 30
$json = $response | ConvertTo-Json -Depth 20

if (-not $json.Contains('Clean Code')) {
  throw 'Resposta da Open Library nao contem o livro esperado para o ISBN configurado.'
}

Set-Content -LiteralPath $target -Value $json -Encoding UTF8
Write-Output "VCR atualizado em $target"
