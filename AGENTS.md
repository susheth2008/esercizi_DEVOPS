# AGENTS.md

Istruzioni per i coding agent che lavorano su questo repository.
Rispondi in italiano. Chi usa questo repository sta imparando: spiega ogni modifica in modo semplice.

## Il progetto

- Applicazione web Java con Quarkus 3 (estensioni Quarkus REST, REST Jackson e SmallRye OpenAPI per Swagger UI). Gira con Java 17 e con Java 21.
- Il codice deve compilare con Java 17 (`maven.compiler.release` è `17` nel `pom.xml`): non usare funzionalità introdotte dopo Java 17.
- Build con il Maven wrapper: `./mvnw` (su Windows `mvnw.cmd`). Maven non è installato a parte.
- Il pacchetto è un uber-jar: `./mvnw package` produce `target/*-runner.jar`. Non cambiare `quarkus.package.jar.type`.
- Il package Java dell'applicazione è `it.zucchetti.lab`.
- L'applicazione verrà eseguita in un container e poi su Kubernetes.

## Comandi

```bash
./mvnw quarkus:dev     # sviluppo con ricarica automatica, su http://localhost:8080
./mvnw test            # test
./mvnw package         # JAR in target/
```

## Regole

1. **Non creare né modificare** `Dockerfile`, `.dockerignore`, `docs/` e `prompts/`: li scrive lo studente.
2. **Niente database, niente file scritti su disco, niente stato in memoria tra le richieste** oltre all'ora di avvio.
3. **Configurazione da variabili d'ambiente**, sempre con un default, in `src/main/resources/application.properties`
   con la sintassi `${VARIABILE:default}`. Il file `.env.example` mostra le variabili previste.
4. **Dipendenze:** usa solo le estensioni già presenti. Se ne serve un'altra, chiedi prima.
5. **Codice piccolo e leggibile:** una classe di risorse REST è sufficiente.
6. **Test:** per ogni endpoint scrivi un test in `src/test/java` con `@QuarkusTest` e RestAssured.
   Un test non deve mai spegnere la JVM: `/crash` esegue `System.exit` solo se la proprietà `app.crash.exit` è `true`.
   È già configurata in `application.properties` (`true` di default, `false` nel profilo di test): non cambiarla.
   Prima di dichiarare finito il lavoro, esegui `./mvnw test` e riporta l'esito.
7. **Segreti:** non scrivere mai password, token o chiavi nel codice o nella configurazione.
8. **Spiegazione:** alla fine elenca i file creati o modificati e spiega in due o tre frasi cosa fa ciascuno.
