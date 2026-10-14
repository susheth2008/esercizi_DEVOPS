# Requisiti dell'app

L'app è piccola di proposito. Ogni requisito serve a una dimostrazione del Giorno 2 su Kubernetes.
Il prompt per l'agent deve coprirli tutti e 7. Un requisito è soddisfatto quando passa la sua verifica.

| # | Requisito | Verifica | Perché serve domani |
| --- | --- | --- | --- |
| 1 | `GET /` restituisce una pagina HTML con il messaggio, l'hostname e la versione | apri `http://localhost:8080` | con tre copie dell'app si vede quale risponde |
| 2 | `GET /info` restituisce JSON con `hostname`, `version` e `startedAt` (data e ora di avvio) | `curl localhost:8080/info` | come sopra, da riga di comando |
| 3 | `GET /health` restituisce `200` con `{"status":"ok"}` | `curl -i localhost:8080/health` | Kubernetes controlla se l'app è viva |
| 4 | `POST /crash` risponde `202` con `{"status":"crashing"}` e subito dopo spegne l'app | `curl -i -X POST localhost:8080/crash` | Kubernetes la riaccende da solo |
| 5 | Messaggio e versione arrivano dalle variabili `MESSAGE` e `APP_VERSION`, con un valore di default | cambia `MESSAGE` nel file `.env`, riavvia `./mvnw quarkus:dev` e ricarica la pagina | stessa immagine, configurazioni diverse |
| 6 | Porta letta da `PORT` (default `8080`), in ascolto su `0.0.0.0` | l'app risponde dal container con `-p 8080:8080` | su `localhost` il container non è raggiungibile |
| 7 | Un solo JAR (uber-jar, già configurato), nessun database, nessun file scritto su disco | `./mvnw package` crea `target/*-runner.jar` | il container si può buttare e ricreare |
