package org.runaway.menu;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.util.function.Consumer;
import org.runaway.menu.design.MenuDesigner;

import java.util.HashMap;
import java.util.Map;

public class MenuAnimation {

    private Map<Integer, MenuDesigner> frames = new HashMap<>();
    private boolean loop = false;
    private BukkitTask task;

    private int playEvery;
    private int loopTimes = -1;
    private int delayBetweenLoops = -1;

    /**
     * Gets called when animation ends, useful for crates.
     */
    private Consumer<IMenu> animationEndConsumer;

    /**
     * Runs every frame!
     */

    private Consumer<IMenu> taskToDoEveryFrame;

    public MenuAnimation() {}

    public static MenuAnimation create() {
        return new MenuAnimation();
    }

    /**
     * Stops animation if present
     */

    public void stop() {
        if (task != null)
            task.cancel();
    }

    /**
     * Gets frame from {@link #frames}
     *
     * @param frame
     *            is a frame position
     * @return a {@link MenuDesigner}
     */

    public MenuDesigner getFrame(int frame) {
        if (frames.containsKey(frame))
            return null;
        return frames.get(frame);
    }

    /**
     *
     * @param frame
     *            frame position
     * @param designer
     *            is a design that will be used on that frame.
     * @return a {@link MenuAnimation}
     */
    public MenuAnimation setFrame(int frame, MenuDesigner designer) {
        if (frames.containsKey(frame))
            frames.remove(frame);
        frames.put(frame, designer);
        return this;
    }

    /**
     * Sets if animation should loop!
     *
     * @param loop
     *            is a boolean
     */

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * Sets playEvery
     *
     * @param playEvery
     *            is an Integer
     */

    public void setPlayEvery(int playEvery) {
        this.playEvery = playEvery;
    }

    /**
     * Starts the animation
     *
     * @param plugin
     *            is a plugin that will be used to register task.
     * @param menu
     *            is a menu that will be affected by the Animation.
     */

    public void start(JavaPlugin plugin, IMenu menu) {
        task = new BukkitRunnable() {

            int currentFrame = 1;
            int looped = 0;
            int currentDelay = -1;

            @Override
            public void run() {

                if (menu.getInventory().getViewers().isEmpty())
                    return;

                if (currentDelay != -1) {

                    currentDelay--;
                    if (currentDelay == 0) {
                        currentDelay = -1;
                    } else
                        return;

                }

                if (frames.size() + 1 == currentFrame) {
                    if (!loop) {

                        if (animationEndConsumer != null)
                            animationEndConsumer.accept(menu);
                        cancel();

                    } else {

                        currentFrame = 1;
                        looped++;

                        if (loopTimes != -1 && loopTimes == looped) {

                            if (animationEndConsumer != null)
                                animationEndConsumer.accept(menu);
                            cancel();

                        }

                        if (delayBetweenLoops != -1)
                            currentDelay = delayBetweenLoops;

                    }
                }

                if (taskToDoEveryFrame != null)
                    taskToDoEveryFrame.accept(menu);
                MenuDesigner design = frames.get(currentFrame);
                design.applyAsButtons(menu);

                currentFrame++;

            }
        }.runTaskTimer(plugin, 0, playEvery);
    }

    /**
     * Sets animation end event
     *
     * @param animationEndConsumer
     *            is an event
     */
    public void setAnimationEndEvent(Consumer<IMenu> animationEndConsumer) {
        this.animationEndConsumer = animationEndConsumer;
    }

    /**
     * Sets delay between each loop!
     *
     * @param delayBetweenLoops
     *            is ant integer in ticks
     */

    public void setDelayBetweenLoops(int delayBetweenLoops) {
        this.delayBetweenLoops = delayBetweenLoops;
    }

    /**
     * Sets loop times, ex. Loops 5 times only!
     *
     * @param loopTimes
     *            is an integer
     */

    public void setLoopTimes(int loopTimes) {
        this.loopTimes = loopTimes;
    }

    /**
     * Sets task that will be ran every frame
     *
     * @param taskToDoEveryFrame
     *            is an event.
     */

    public void setTaskToDoEveryFrame(Consumer<IMenu> taskToDoEveryFrame) {
        this.taskToDoEveryFrame = taskToDoEveryFrame;
    }

}
