package io.github.steaf23.bingoreloaded.chunky;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.teleportspiral.TeleportSpiralData;
import io.github.steaf23.bingoreloaded.data.teleportspiral.TeleportationSpiral;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.action.ActionResult;
import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandlePaper;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.popcraft.chunky.api.ChunkyAPI;

public class BingoReloadedChunkyBridge {

    private final BingoReloaded bingo;
    private final ChunkyAPI chunky;
    private final Queue<GenerationTask> taskQueue = new ArrayDeque<>();
    private boolean isProcessing = false;
    private ActionUser activeUser;
    private int totalTasks = 0;
    private int completedTasks = 0;
    private GenerationTask currentTask;

    // Helper data record to hold individual task parameters.
    private record GenerationTask(
        String worldName,
        double x,
        double z,
        double radiusX,
        double radiusZ
    ) {}

    public BingoReloadedChunkyBridge(BingoReloaded bingo) {
        this.bingo = bingo;

        // Retrieve the Chunky API class.
        this.chunky = Bukkit.getServer()
            .getServicesManager()
            .load(ChunkyAPI.class);

        // Register the generation complete callback
        // Once Chunky finishes its current task, it processes the next task.
        // This is necessary because Chunky does not have its own queue system.
        if (this.chunky != null && this.chunky.version() == 0) {
            this.chunky.onGenerationComplete(event -> {
                userNotifyTaskComplete(event.world(), currentTask);
                processcurrentTask();
            });
        }
    }

    // Commands the Chunky API to pre-generate a list of locations.
    public ActionResult preGenerate(
        TeleportSpiralData data,
        int rounds,
        int radius,
        ActionUser user
    ) {
        if (chunky == null || chunky.version() != 0) {
            user.sendMessage(
                Component.text(
                    "Incompatible or missing Chunky API version: " +
                        (chunky != null ? chunky.version() : "N/A")
                )
            );
            return ActionResult.IGNORED;
        }

        // Retrieve the world group for the active bingo session.
        Optional<String> firstSessionName = this.bingo
            .getGameManager()
            .getSessionNames()
            .stream()
            .findFirst();

        if (firstSessionName.isEmpty()) {
            return ActionResult.IGNORED;
        }

        BingoSession session = this.bingo
            .getGameManager()
            .getSession(firstSessionName.get());

        if (session == null) {
            return ActionResult.IGNORED;
        }

        WorldGroup worldGroup = session.getWorldGroup();
        if (worldGroup == null || worldGroup.getOverworld() == null) {
            return ActionResult.IGNORED;
        }

        var overworld = worldGroup.getOverworld();
        var nether = worldGroup.getNetherWorld();

        // Get dimension names for the overworld and nether, to give to Chunky.
        String overworldName = ((WorldHandlePaper) overworld)
            .handle()
            .getName();
        String netherWorldName =
            nether != null
                ? ((WorldHandlePaper) nether).handle().getName()
                : null;

        int round = 0;
        int firstStep = data.getStep();

        // Build the queue of generation tasks, making sure to skip any ocean spawns if the config indicates that the spawn picker will do so too.
        while (round < rounds) {
            TeleportationSpiral.Point nextStart = data.peekSpiralPosition(
                firstStep
            );

            GlobalPosition pos = worldGroup
                .getOverworld()
                .highestBlockAt(nextStart.x(), nextStart.z());

            if (
                !data.getSpiralOptions().skipOceanBiomes() ||
                !BingoGame.isOceanBiome(worldGroup.getOverworld(), pos)
            ) {
                // Queue Overworld task
                taskQueue.add(
                    new GenerationTask(
                        overworldName,
                        nextStart.x(),
                        nextStart.z(),
                        radius,
                        radius
                    )
                );

                // Queue Nether task if nether exists
                if (netherWorldName != null) {
                    int netherFactor = 4;
                    int netherRadius = Math.max(radius / netherFactor, 16); // Make sure the radius is at least 1 chunk
                    taskQueue.add(
                        new GenerationTask(
                            netherWorldName,
                            nextStart.x() / netherFactor,
                            nextStart.z() / netherFactor,
                            netherRadius,
                            netherRadius
                        )
                    );
                }

                round++;
            }

            firstStep++;
        }

        user.sendMessage(
            Component.text(
                "Queued " +
                    taskQueue.size() +
                    " generation tasks with Chunky. Starting pre-generation..."
            )
        );

        // Reset progress counters and save current user
        this.activeUser = user;
        this.totalTasks = taskQueue.size();
        this.completedTasks = 0;

        // Trigger the processing loop
        processcurrentTask();

        return ActionResult.SUCCESS;
    }

    private synchronized void processcurrentTask() {
        currentTask = taskQueue.poll();

        if (currentTask == null) {
            isProcessing = false;
            return;
        }

        isProcessing = true;

        // Start the task in chunky.
        chunky.startTask(
            currentTask.worldName(),
            "square",
            currentTask.x(),
            currentTask.z(),
            currentTask.radiusX(),
            currentTask.radiusZ(),
            "concentric"
        );
    }

    // Send a message to the user with a progress update.
    private void userNotifyTaskComplete(String worldName, GenerationTask task) {
        if (activeUser == null || totalTasks == 0) {
            return;
        }

        completedTasks++;
        int percentage = (int) (((double) completedTasks / totalTasks) * 100);

        String locationDetails =
            task != null
                ? String.format(
                      "at (X: %.0f, Z: %.0f) with radius %.0f",
                      task.x(),
                      task.z(),
                      task.radiusX()
                  )
                : "";

        activeUser.sendMessage(
            Component.text(
                String.format(
                    "[%d/%d - %d%%] Completed pre-generation in '%s' %s",
                    completedTasks,
                    totalTasks,
                    percentage,
                    worldName,
                    locationDetails
                )
            )
        );

        // Reset state when all queued tasks finish
        if (completedTasks >= totalTasks) {
            activeUser.sendMessage(
                Component.text(
                    "All Chunky pre-generation tasks finished successfully!"
                )
            );
            this.activeUser = null;
            this.totalTasks = 0;
            this.completedTasks = 0;
            this.currentTask = null;
        }
    }
}
