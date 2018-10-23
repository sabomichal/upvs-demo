# UPVS DEMO

Featuring:
* UPVS SAML WEB SSO
* UPVS STS service
* UPVS IAM service

## Spustenie demo aplikacie
Spustenie je mozne priamo v embedded jetty kontaineri:

`
gradlew appStart
`

Demo aplikacia pocuva na porte 8088.

## Demo sluzby
Demo sluzby su dostupne vo forme nasledovnych REST sluzieb:

`/login` - prihlasenie cez federovanu identitu UPVS

`/edesk/status/{ico}` - zistenie dostupnosti schranky UPVS pre dane {ico}
