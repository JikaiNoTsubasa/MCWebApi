package com.mcwebapi.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcwebapi.web.endpoints.PlayerEndpoint;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class WebServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final MinecraftServer mcServer;
    private final int port;
    private HttpServer httpServer;

    public WebServer(int port, MinecraftServer server) {
        this.port = port;
        this.mcServer = server;
    }

    public void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);

            // Register endpoints
            httpServer.createContext("/", this::handleRoot);
            httpServer.createContext("/api/players", this::handlePlayers);

            // Use a thread pool executor
            httpServer.setExecutor(Executors.newFixedThreadPool(4));
            httpServer.start();

            LOGGER.info("Web server started on port {}", port);
        } catch (IOException e) {
            LOGGER.error("Failed to start web server", e);
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            LOGGER.info("Web server stopped");
        }
    }

    private void handleRoot(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (path.equals("/") || path.equals("")) {
            StatusResponse response = new StatusResponse(
                "MCWebApi",
                "1.0.0",
                "Web API for Minecraft server communication"
            );
            sendResponse(exchange, 200, GSON.toJson(response));
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
        }
    }

    private void handlePlayers(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String path = exchange.getRequestURI().getPath();

        try {
            if (path.equals("/api/players") || path.equals("/api/players/")) {
                String response = PlayerEndpoint.getPlayers(mcServer);
                sendResponse(exchange, 200, response);
            } else if (path.startsWith("/api/players/")) {
                String username = path.substring("/api/players/".length());
                String response = PlayerEndpoint.getPlayer(mcServer, username);

                int statusCode = response.contains("\"error\"") ? 404 : 200;
                sendResponse(exchange, statusCode, response);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Endpoint not found\"}");
            }
        } catch (Exception e) {
            LOGGER.error("Error handling request", e);
            sendResponse(exchange, 500, "{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static class StatusResponse {
        public final String name;
        public final String version;
        public final String description;

        public StatusResponse(String name, String version, String description) {
            this.name = name;
            this.version = version;
            this.description = description;
        }
    }
}
