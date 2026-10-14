# Come scrivere un buon prompt

L'agent nell'editor legge i file, scrive codice e lancia comandi. È un collega molto veloce che non sa niente del progetto,
tranne quello che gli scrivi tu. Il risultato dipende quasi tutto da come chiedi.

## Le sei parti di un buon prompt

| Parte | Domanda a cui risponde | Esempio |
| --- | --- | --- |
| **Contesto** | Su cosa sta lavorando? | Progetto Quarkus 3, Java 17, package `it.zucchetti.lab` |
| **Obiettivo** | Cosa deve esistere alla fine? | Una web app con 4 endpoint pronta per girare in un container |
| **Requisiti** | Cosa, esattamente? | Indirizzi, metodi HTTP, formato delle risposte, variabili: uno per riga |
| **Vincoli** | Cosa non deve fare? | Niente database, niente nuove dipendenze, non toccare il Dockerfile |
| **Verifica** | Come si capisce che ha finito? | Quali test scrivere, `./mvnw test` deve essere verde |
| **Formato della risposta** | Cosa vuoi indietro? | Elenco dei file modificati e due frasi di spiegazione per ognuno |

Le regole che valgono sempre sono già in `AGENTS.md`, che l'agent legge da solo. Nel prompt metti solo il compito di oggi.

## Un prompt da migliorare

```text
Fammi un'app Java con degli endpoint per il corso, deve funzionare con Docker.
```

Cosa manca: quali endpoint, che risposte, su che porta, da dove arriva la configurazione, come si verifica.
L'agent deve indovinare, e ogni volta indovina in modo diverso.

## Un prompt ben fatto, su un altro compito

Questo esempio non è l'esercizio di oggi: serve a vedere la struttura.

```text
Contesto: progetto Quarkus 3 con Java 17, package it.zucchetti.lab, estensioni REST e REST Jackson.

Obiettivo: aggiungere un endpoint che restituisca l'ora del server.

Requisiti:
- GET /ora restituisce JSON {"ora": "<ora corrente in formato ISO-8601>", "zona": "<fuso orario>"}
- il fuso orario arriva dalla variabile d'ambiente TZ, con default Europe/Rome

Vincoli:
- una sola nuova classe di risorse, nessuna nuova dipendenza
- non modificare le altre classi

Verifica:
- scrivi un test @QuarkusTest con RestAssured che controlli stato 200 e presenza dei due campi
- esegui ./mvnw test e riporta l'esito

Risposta: elenca i file creati o modificati e spiega in due frasi cosa fa ognuno.
```

## Tre abitudini

1. **Un passo alla volta.** Prima `/health` e `/info`, poi il resto. Un compito grande dà errori grandi.
2. **Leggi prima di accettare.** Il codice lo committi tu: se non capisci una riga, chiedi all'agent di spiegarla.
3. **Correggi, non ricominciare.** Se qualcosa non va, scrivi cosa non va e incolla l'errore completo.

## Checklist

- [ ] Il prompt copre tutti e 7 i requisiti di `docs/REQUISITI.md`?
- [ ] Ogni requisito dice indirizzo, metodo HTTP e formato della risposta?
- [ ] Ci sono i vincoli (niente database, niente dipendenze, Dockerfile escluso)?
- [ ] Dice come verificare il risultato?
- [ ] Dice cosa rispondere alla fine?
- [ ] Un compagno che non conosce l'esercizio capirebbe cosa costruire?
