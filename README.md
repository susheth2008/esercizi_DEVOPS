# DevOps & Containerizzazione · Giorno 1

Oggi costruiamo una piccola applicazione Java, la chiudiamo in un container e la pubblichiamo su un registry.
Domani la stessa immagine girerà su Kubernetes.

```
Repository → App Java → Dockerfile → Immagine → Registry → Kubernetes (domani)
```

Nel pomeriggio l'app la scrive un coding agent nel tuo editor. Il tuo lavoro è il **prompt**: deve essere così chiaro che l'agent costruisca esattamente l'app richiesta.

## Chi ha lavorato su questo repository

<!-- Lab 1: aggiungi qui il tuo nome -->

## Prerequisiti

| Strumento | Verifica |
| --- | --- |
| Git | `git --version` |
| JDK 17 o 21 (Eclipse Temurin) | `java -version` |
| Docker Desktop | `docker run hello-world` |
| VS Code + Extension Pack for Java | apri questa cartella |
| Account GitHub e Docker Hub | `docker login` |

Maven non serve: il progetto contiene il wrapper `./mvnw` (su Windows `mvnw.cmd`), che scarica Maven da solo.

## Cosa c'è nel repository

| File | A cosa serve |
| --- | --- |
| `README.md` | queste istruzioni |
| `AGENTS.md` | le regole che l'agent legge da solo prima di modificare il codice |
| `docs/REQUISITI.md` | i 7 requisiti dell'app, con la verifica di ognuno |
| `docs/PROMPT.md` | come si scrive un buon prompt, con esempi e checklist |
| `prompts/lab3.md` | il modello da compilare con il tuo prompt |
| `pom.xml`, `mvnw`, `.mvn/`, `src/` | lo scheletro del progetto Quarkus, ancora vuoto |
| `.env.example` | le variabili d'ambiente dell'app: copialo in `.env` |
| `.dockerignore` | cosa escludere dalla build dell'immagine |
| `docs/pipeline-demo.yaml` | la pipeline mostrata in aula (non viene eseguita) |

Comandi utili:

```bash
cp .env.example .env   # una volta sola: le variabili per la dev mode e per il container
./mvnw quarkus:dev     # avvia l'app in modalità sviluppo, con ricarica automatica
./mvnw test            # esegue i test
./mvnw package         # crea target/*-runner.jar
```

Con l'app avviata, apri `http://localhost:8080/q/swagger-ui`: vedi tutti gli endpoint e li provi dal browser, anche `POST /crash`.

---

## Lab 1 · Copia, clone, commit (30')

1. Su GitHub, nella pagina di questo repository, premi **Use this template → Create a new repository**, scegli un nome e conferma: ottieni la tua copia sul tuo account.
2. Clona la tua copia:
```bash
   git clone <URL della tua copia>
   cd <nome del tuo repository>
```
3. Aggiungi il tuo nome nella sezione *Chi ha lavorato su questo repository*.
4. Salva il lavoro e pubblicalo:
```bash
   git add README.md
   git commit -m "docs: aggiungo il mio nome"
   git push
```
5. Ricarica la pagina del tuo repository su GitHub: il tuo nome è nel README.

## Lab 2 · Il tuo primo container (30')

```bash
docker run -d --name web -p 8080:80 nginx:1.27-alpine
docker ps
# apri http://localhost:8080
docker logs web
docker exec -it web sh
  cat /etc/os-release
  exit
docker stop web && docker rm web
```

**Sfida:** avvia un secondo nginx. Cosa succede se usi di nuovo la porta 8080? Come risolvi?

**Bonus:** Java senza installarlo: `docker run --rm eclipse-temurin:21-jre java -version`

## Lab 3 · Il prompt e il container (60')

1. **Leggi** `docs/REQUISITI.md` e `docs/PROMPT.md`.
2. **Scrivi il prompt** in `prompts/lab3.md`, seguendo le sei parti: contesto, obiettivo, requisiti, vincoli, verifica, formato della risposta.
3. **Revisione:** un compagno lo controlla con la checklist in fondo a `docs/PROMPT.md`.
4. **Dai il prompt all'agent** e leggi quello che fa. Se il risultato non va, correggi il prompt e annota la modifica in fondo al file.
5. **Verifica i requisiti uno per uno** con l'app in dev mode:
```bash
   ./mvnw quarkus:dev
   curl localhost:8080/info
   curl localhost:8080/health
```

oppure da browser.

6. **Esegui i test:** `./mvnw test`.

### Il Dockerfile

Il Dockerfile, nella cartella principale del progetto:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*-runner.jar app.jar
ENV PORT=8080
EXPOSE 8080
USER 1001
CMD ["java", "-jar", "app.jar"]
```

### Immagine e container

```bash
./mvnw package
docker build -t mia-app:1.0 .
docker run -d --name app -p 8080:8080 --env-file .env mia-app:1.0
curl localhost:8080/info
curl -X POST localhost:8080/crash
docker ps -a      # il container è Exited: nessuno lo riavvia
docker rm app
```

Infine salva il lavoro sul tuo repository, come nel Lab 1:

```bash
git add .
git commit -m "Lab 3: app e Dockerfile"
git push
```

Ferma la dev mode prima di avviare il container: usano la stessa porta.

## Lab 4 · Il tuo registry locale (40')

Un registry è il magazzino delle immagini. Usiamo l'immagine ufficiale `registry`, il registry open source
(progetto CNCF *Distribution*): parla la stessa API di Docker Hub.

```bash
# 1. Avvia il registry: è anch'esso un container
docker run -d -p 5001:5000 --name registry registry:2

# 2. Tagga l'immagine con l'indirizzo del registry e pubblicala
docker tag mia-app:1.0 localhost:5001/mia-app:1.0
docker push localhost:5001/mia-app:1.0
curl localhost:5001/v2/_catalog

# 3. Cancella la copia locale e riscaricala dal registry
docker rmi mia-app:1.0 localhost:5001/mia-app:1.0
docker pull localhost:5001/mia-app:1.0
docker run -d --name app -p 8080:8080 localhost:5001/mia-app:1.0
curl localhost:8080/info
```

**Sfida:** cambia il messaggio di default, ricostruisci il JAR, crea `mia-app:2.0` con `APP_VERSION` a `2.0` e pubblicala.
Alla fine `curl localhost:5001/v2/mia-app/tags/list` deve mostrare `1.0` e `2.0`.

```
Immagine: localhost:5001/mia-app (tag 1.0 e 2.0)
```

## Checklist per domani

- [ ] App e `Dockerfile` pubblicati sul tuo repository GitHub
- [ ] `/health` e `/info` rispondono
- [ ] L'app ascolta su `0.0.0.0:8080`
- [ ] `POST /crash` spegne il container
- [ ] `mia-app:1.0` e `mia-app:2.0` sono su `localhost:5001`
- [ ] Il container `registry` e le immagini restano sul PC

## Problemi frequenti

| Sintomo | Causa e soluzione |
| --- | --- |
| `port is already allocated` | la porta è usata da un altro container o dalla dev mode: `docker ps -a`, `docker rm -f <nome>` |
| il browser non raggiunge l'app nel container | l'app ascolta su `localhost`: serve `quarkus.http.host=0.0.0.0` |
| `COPY failed: no source files were specified` | manca il JAR: esegui `./mvnw package` prima di `docker build` |
| `docker build` chiede un argomento | manca il punto finale: `docker build -t mia-app:1.0 .` |
| `./mvnw: Permission denied` (macOS/Linux) | `chmod +x mvnw` |
| `mvnw` non parte su Windows | usa `mvnw.cmd` |
| con Podman il push fallisce per HTTPS | `podman push --tls-verify=false localhost:5001/mia-app:1.0` |
