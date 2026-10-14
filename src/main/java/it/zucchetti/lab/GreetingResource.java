package it.zucchetti.lab;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Risorsa REST principale dell'applicazione.
 * Espone gli endpoint richiesti: /, /info, /health, /crash.
 */
@Path("/")
public class GreetingResource {

    /** Messaggio configurabile tramite la variabile d'ambiente MESSAGE. */
    @ConfigProperty(name = "app.message")
    String message;

    /** Versione configurabile tramite la variabile d'ambiente APP_VERSION. */
    @ConfigProperty(name = "app.version")
    String version;

    /**
     * Se true, /crash esegue System.exit(0).
     * Nel profilo di test è false, così i test non spengono la JVM.
     */
    @ConfigProperty(name = "app.crash.exit")
    boolean crashExit;

    /** Ora di avvio dell'applicazione, registrata una sola volta al caricamento della classe. */
    private static final Instant START_TIME = Instant.now();

    // ---------- GET / ----------

    /**
     * Pagina principale: restituisce un testo con messaggio, hostname e versione.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return message + "\nHostname: " + getHostname() + "\nVersion: " + version;
    }

    // ---------- GET /info ----------

    /**
     * Restituisce un JSON con hostname, versione e ora di avvio.
     */
    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> info() {
        return Map.of(
                "hostname", getHostname(),
                "version", version,
                "startedAt", START_TIME.toString()
        );
    }

    // ---------- GET /health ----------

    /**
     * Health-check: restituisce 200 con {"status": "ok"}.
     */
    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    // ---------- POST /crash ----------

    /**
     * Spegne l'applicazione con System.exit(0).
     * Durante i test (app.crash.exit=false) restituisce 200 senza spegnere.
     */
    @POST
    @Path("/crash")
    @Produces(MediaType.APPLICATION_JSON)
    public Response crash() {
        if (crashExit) {
            // Esegui System.exit in un thread separato per dare tempo alla risposta
            new Thread(() -> System.exit(0)).start();
        }
        return Response.ok(Map.of("message", "Shutting down...")).build();
    }

    // ---------- Utility ----------

    /**
     * Restituisce l'hostname della macchina, o "unknown" in caso di errore.
     */
    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}

