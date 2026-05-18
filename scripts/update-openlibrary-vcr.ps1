$ErrorActionPreference = 'Stop'

$isbn = '9780132350884'
$root = Split-Path -Parent $PSScriptRoot
$target = Join-Path $root 'src/test/resources/wiremock/openlibrary/__files/open-library-clean-code-response.json'
$url = "https://openlibrary.org/api/books?bibkeys=ISBN:$isbn&format=json&jscmd=data"
$bookKey = "ISBN:$isbn"

$response = Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 30
$book = $response.PSObject.Properties[$bookKey].Value

if ($null -eq $book) {
  throw 'Resposta da Open Library nao contem livro para o ISBN configurado.'
}

if ($book.title -ne 'Clean Code') {
  throw 'Resposta da Open Library nao contem o livro esperado para o ISBN configurado.'
}

if ($null -eq $book.authors -or @($book.authors).Count -eq 0 -or [string]::IsNullOrWhiteSpace($book.authors[0].name)) {
  throw 'Resposta da Open Library nao contem autor para o livro esperado.'
}

if ($null -eq $book.number_of_pages) {
  throw 'Resposta da Open Library nao contem quantidade de paginas para o livro esperado.'
}

if ($null -eq $book.publishers -or @($book.publishers).Count -eq 0 -or [string]::IsNullOrWhiteSpace($book.publishers[0].name)) {
  throw 'Resposta da Open Library nao contem editora para o livro esperado.'
}

if ([string]::IsNullOrWhiteSpace($book.publish_date)) {
  throw 'Resposta da Open Library nao contem data de publicacao para o livro esperado.'
}

if ($null -eq $book.cover -or [string]::IsNullOrWhiteSpace($book.cover.small)) {
  throw 'Resposta da Open Library nao contem capa pequena para o livro esperado.'
}

$normalized = [ordered]@{
  "ISBN:$isbn" = [ordered]@{
    url = $book.url
    title = $book.title
    authors = @($book.authors | ForEach-Object { [ordered]@{ name = $_.name } })
    number_of_pages = $book.number_of_pages
    publishers = @([ordered]@{ name = $book.publishers[0].name })
    publish_date = $book.publish_date
    cover = [ordered]@{ small = $book.cover.small }
  }
}

$json = ($normalized | ConvertTo-Json -Depth 10) -replace "`r`n?", "`n"
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($target, $json + "`n", $utf8NoBom)
Write-Output "VCR atualizado em $target"
