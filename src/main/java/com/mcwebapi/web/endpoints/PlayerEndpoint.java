package com.mcwebapi.web.endpoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PlayerEndpoint {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String getPlayers(MinecraftServer server) {
        List<PlayerInfo> players = new ArrayList<>();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            BlockPos pos = player.blockPosition();
            String dimension = player.level().dimension().location().toString();

            PlayerInfo info = new PlayerInfo(
                player.getName().getString(),
                player.getUUID().toString(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                dimension,
                player.getHealth(),
                player.getFoodData().getFoodLevel(),
                player.experienceLevel
            );

            players.add(info);
        }

        PlayersResponse response = new PlayersResponse(
            players.size(),
            server.getPlayerList().getMaxPlayers(),
            players
        );

        return GSON.toJson(response);
    }

    public static String getPlayer(MinecraftServer server, String username) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(username);

        if (player == null) {
            return GSON.toJson(new ErrorResponse("Player not found: " + username));
        }

        BlockPos pos = player.blockPosition();
        String dimension = player.level().dimension().location().toString();

        PlayerInfo info = new PlayerInfo(
            player.getName().getString(),
            player.getUUID().toString(),
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            dimension,
            player.getHealth(),
            player.getFoodData().getFoodLevel(),
            player.experienceLevel
        );

        return GSON.toJson(info);
    }

    private static class PlayersResponse {
        public final int online;
        public final int max;
        public final List<PlayerInfo> players;

        public PlayersResponse(int online, int max, List<PlayerInfo> players) {
            this.online = online;
            this.max = max;
            this.players = players;
        }
    }

    private static class PlayerInfo {
        public final String name;
        public final String uuid;
        public final int x;
        public final int y;
        public final int z;
        public final String dimension;
        public final float health;
        public final int foodLevel;
        public final int level;

        public PlayerInfo(String name, String uuid, int x, int y, int z, String dimension,
                         float health, int foodLevel, int level) {
            this.name = name;
            this.uuid = uuid;
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
            this.health = health;
            this.foodLevel = foodLevel;
            this.level = level;
        }
    }

    private static class ErrorResponse {
        public final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
