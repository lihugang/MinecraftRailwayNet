package top.lihugang.mc.mod.minecraftrailwaynet.utils;

import net.minecraft.util.WorldSavePath;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.LRU;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Pair;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.lihugang.mc.mod.minecraftrailwaynet.MinecraftRailwayNet.MOD_ID;
import static top.lihugang.mc.mod.minecraftrailwaynet.MinecraftRailwayNet.logger;

class RailwayNetChunkStructure {
    int x;
    int z;
    Map<Coord,
            Pair<Integer, // direction
                    List<Coord // connected rails
                            >
                    >> data;
    int size;
    static final String worldSavePath = WorldSavePath.ROOT.getRelativePath();

    static String getStoragePath(String storageId, int x, int z) {
        return Path.of(worldSavePath, MOD_ID, storageId +
                "." +
                x +
                "." +
                z + ".dat").toString();
    }

    public RailwayNetChunkStructure(int x, int z, Map<Coord, Pair<Integer, List<Coord>>> data, int size) {
        this.x = x;
        this.z = z;
        this.data = data;
        this.size = size;
    }

    public void writeToFile(String storageId) {
        try {
            String path = getStoragePath(storageId, x, z);
            FileOutputStream fileStream = new FileOutputStream(path);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(this.data);
            objectStream.close();
            fileStream.close();
            logger.info("Saving chunk ({}, {}) data to {}", x, z, path);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
    }

    public static RailwayNetChunkStructure readFromFile(String storageId, int x, int z) {
        try {
            logger.info("Read chunk data from {}: {} {}", storageId, x, z);
            FileInputStream fileStream = new FileInputStream(getStoragePath(storageId, x, z));
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);

            Map<Coord, Pair<Integer, List<Coord>>> data = (Map<Coord, Pair<Integer, List<Coord>>>) objectStream.readObject();

            objectStream.close();
            fileStream.close();

            int size = 3 * 4; // key (x, y, z) * sizeof(int)
            for (Pair<Integer, List<Coord>> values : data.values()) {
                size += values.second.size()
                        * 3 // (x, y, z)
                        * 4 // sizeof(int)
                        + 4; // direction
            }

            return new RailwayNetChunkStructure(x, z, data, size);
        } catch (Exception ignored) {
            return new RailwayNetChunkStructure(x, z, new HashMap<>(), 0);
        }
    }

    public void addNode(Coord position, int direction) {
        if (this.data.containsKey(position)) return;
        this.data.put(
                position,
                new Pair<>(direction, new ArrayList<>())
        );
    }

    public void removeNode(Coord position) {
        this.data.remove(position);
    }

    public void connect(Coord from, Coord to) {
        List<Coord> startRailNode = this.data.get(from).second;

        if (startRailNode.contains(to)) return;

        startRailNode.add(to);
        logger.info("Connect {} with {}", from, to);
    }

    public void remove(Coord from, Coord to) {
        if (!this.data.containsKey(from)) return;
        List<Coord> connectedRails = this.data.get(from).second;
        connectedRails.remove(to);
        logger.info("Remove {} to {}", from, to);
    }

    public List<Coord> getConnectedRails(Coord nodePos) {
        if (this.data.containsKey(nodePos)) return this.data.get(nodePos).second;
        return new ArrayList<>();
    }
}

public class RailwayNetStorage {
    static Map<String, RailwayNetStorage> instances = new HashMap<>();
    static int memoryUsage = 0;
    static int memoryUsageLimit = 1024 * 128; // 128k
    // global limit

    public static RailwayNetStorage getInstance(String key) {
        if (!instances.containsKey(key)) {
            instances.put(key, new RailwayNetStorage(key));
            logger.info("Create Railway Net Storage Instance for {}", key);
        }

        return instances.get(key);
    }

    public static List<RailwayNetStorage> getInstances() {
        return instances.values().stream().toList();
    }

    public static Pair<Integer, Integer> getChunkId(int x, int z) {
        return new Pair<>(x / 256, z / 256);
    }

    String name;
    LRU<Pair<Integer, Integer>, RailwayNetChunkStructure> cachedData = new LRU<>();

    public RailwayNetStorage(String name) {
        this.name = name;
    }

    private void loadChunk(Pair<Integer, Integer> pos) {
        if (cachedData.has(pos)) return;

        RailwayNetChunkStructure chunk = RailwayNetChunkStructure.readFromFile(name, pos.first, pos.second);
        cachedData.put(pos, chunk);
        memoryUsage += chunk.size;

        logger.info("Load chunk {}, currentMemoryUsage: {}", pos, chunk.size);
    }

    private void LRUCleanup() {
        while (memoryUsage > memoryUsageLimit && !cachedData.empty()) {
            RailwayNetChunkStructure removedData = cachedData.remove().second;
            removedData.writeToFile(name);
            memoryUsage -= removedData.size;
            logger.info("Unload chunk {}, {}", removedData.x, removedData.z);
        }
    }

    public void addNode(Coord nodePosition, int railDirection) {
        Pair<Integer, Integer> chunkId = getChunkId(nodePosition.first, nodePosition.third);
        loadChunk(chunkId);

        RailwayNetChunkStructure chunk = cachedData.get(chunkId);

        chunk.addNode(nodePosition, railDirection);

        LRUCleanup();
    }

    public void connect(Coord from, Coord to) {
        Pair<Integer, Integer> firstChunkId, secondChunkId;
        firstChunkId = getChunkId(from.first, from.third);
        secondChunkId = getChunkId(to.first, to.third);

        loadChunk(firstChunkId);
        loadChunk(secondChunkId);

        RailwayNetChunkStructure firstChunk, secondChunk;
        firstChunk = cachedData.get(firstChunkId);
        secondChunk = cachedData.get(secondChunkId);

        firstChunk.connect(from, to);
        secondChunk.connect(to, from);

        LRUCleanup();
    }

    public void remove(Coord from, Coord to) {
        remove(from, to, false);
    }

    public void remove(Coord from, Coord to, boolean forbidCleanup) {
        Pair<Integer, Integer> firstChunkId, secondChunkId;
        firstChunkId = getChunkId(from.first, from.third);
        secondChunkId = getChunkId(to.first, to.third);

        loadChunk(firstChunkId);
        loadChunk(secondChunkId);

        RailwayNetChunkStructure firstChunk, secondChunk;
        firstChunk = cachedData.get(firstChunkId);
        secondChunk = cachedData.get(secondChunkId);

        firstChunk.remove(from, to);
        secondChunk.remove(to, from);

        if (!forbidCleanup)
            LRUCleanup();
    }

    public void destroyNode(Coord railPos) {
        Pair<Integer, Integer> chunkId = getChunkId(railPos.first, railPos.third);
        loadChunk(chunkId);

        RailwayNetChunkStructure chunk = cachedData.get(chunkId);
        List<Coord> connectedRails = new ArrayList<>(chunk.getConnectedRails(railPos)); // deep copy

        for (Coord connectedRail : connectedRails) {
            this.remove(railPos, connectedRail);
        }

        chunk.removeNode(railPos);

        LRUCleanup();
    }

    public void save() {
        for (Pair<Pair<Integer, Integer>, RailwayNetChunkStructure> chunk : cachedData.getAllData()) {
            chunk.second.writeToFile(name);
        }
    }
}