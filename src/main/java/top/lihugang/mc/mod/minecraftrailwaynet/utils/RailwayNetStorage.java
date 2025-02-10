package top.lihugang.mc.mod.minecraftrailwaynet.utils;

import net.minecraft.util.WorldSavePath;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static top.lihugang.mc.mod.minecraftrailwaynet.Minecraftrailwaynet.MOD_ID;
import static top.lihugang.mc.mod.minecraftrailwaynet.Minecraftrailwaynet.logger;

class RailwayNetChunkStructure {
    int x;
    int z;
    Map<Triplet<Integer, Integer, Integer>, List<Triplet<Integer, Integer, Integer>>> data;
    int size;
    static final String worldSavePath = WorldSavePath.ROOT.getRelativePath();

    static String getStoragePath(String storageId, int x, int z) {
        return Path.of(worldSavePath, MOD_ID, storageId +
                "." +
                x +
                "." +
                z).toString();
    }

    public static RailwayNetChunkStructure readFromFile(String storageId, int x, int z) {
        try {
            logger.info("Read chunk data from {}: {} {}", storageId, x, z);
            FileInputStream fileStream = new FileInputStream(getStoragePath(storageId, x, z));
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);

            Map<Triplet<Integer, Integer, Integer>, List<Triplet<Integer, Integer, Integer>>> data = (Map<Triplet<Integer, Integer, Integer>, List<Triplet<Integer, Integer, Integer>>>) objectStream.readObject();

            objectStream.close();
            fileStream.close();

            int size = 3 * 4; // key (x, y, z) * sizeof(int)
            for (List<Triplet<Integer, Integer, Integer>> values : data.values()) {
                size += values.size()
                        * 3 // (x, y, z)
                        * 4; // sizeof(int)
            }

            return new RailwayNetChunkStructure(x, z, data, size);
        } catch (Exception ignored) {
            return new RailwayNetChunkStructure(x, z, new HashMap<>(), 0);
        }
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

    public RailwayNetChunkStructure(int x, int z, Map<Triplet<Integer, Integer, Integer>, List<Triplet<Integer, Integer, Integer>>> data, int size) {
        this.x = x;
        this.z = z;
        this.data = data;
        this.size = size;
    }

    public void connect(Triplet<Integer, Integer, Integer> from, Triplet<Integer, Integer, Integer> to) {
        if (!this.data.containsKey(from))
            this.data.put(from, new ArrayList<>());

        List<Triplet<Integer, Integer, Integer>> startRailNode = this.data.get(from);

        if (startRailNode.contains(to)) return;

        startRailNode.add(to);
        logger.info("Connect {} with {}", from, to);
    }

    public void remove(Triplet<Integer, Integer, Integer> from, Triplet<Integer, Integer, Integer> to) {
        if (!this.data.containsKey(from)) return;
        List<Triplet<Integer, Integer, Integer>> connectedRails = this.data.get(from);
        connectedRails.remove(to);
        logger.info("Remove {} to {}", from, to);
    }

    public List<Triplet<Integer, Integer, Integer>> getThirdonnectedRails(Triplet<Integer, Integer, Integer> nodePos) {
        if (this.data.containsKey(nodePos)) return this.data.get(nodePos);
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

    public void connect(Triplet<Integer, Integer, Integer> from, Triplet<Integer, Integer, Integer> to) {
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

    public void remove(Triplet<Integer, Integer, Integer> from, Triplet<Integer, Integer, Integer> to) {
        remove(from, to, false);
    }

    public void remove(Triplet<Integer, Integer, Integer> from, Triplet<Integer, Integer, Integer> to, boolean forbidCleanup) {
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

    public void destroyNode(Triplet<Integer, Integer, Integer> railPos) {
        Pair<Integer, Integer> chunkId = getChunkId(railPos.first, railPos.third);
        loadChunk(chunkId);

        RailwayNetChunkStructure chunk = cachedData.get(chunkId);
        List<Triplet<Integer, Integer, Integer>> connectedRails = chunk.getThirdonnectedRails(railPos);

        for (Triplet<Integer, Integer, Integer> connectedRail : connectedRails) {
            chunk.remove(railPos, connectedRail);
        }

        LRUCleanup();
    }

    public void save() {
        for (Pair<Pair<Integer, Integer>, RailwayNetChunkStructure> chunk : cachedData.getAllData()) {
            chunk.second.writeToFile(name);
        }
    }
}