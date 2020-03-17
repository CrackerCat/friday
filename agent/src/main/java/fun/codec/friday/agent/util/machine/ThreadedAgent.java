package fun.codec.friday.agent.util.machine;

import org.slf4j.Logger;

/**
 * 进程监控
 *
 * @author echo
 */
public class ThreadedAgent implements Runnable {

    public interface Block {
        /**
         * @return false iff you're ready to stop, true otherwise
         * @throws Exception when things go bump
         */
        boolean run() throws Exception;
    }

    private final Logger logger;
    private final Block block;

    private Thread thread;

    public ThreadedAgent(final Logger logger, Block block) {
        this.logger = logger;
        this.block = block;
    }

    public void start() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        logger.debug("Started");
        try {
            while (!Thread.currentThread().isInterrupted() && block.run())
                ;

            logger.debug(Thread.currentThread().getName() + " Stopped");
        } catch (Throwable throwable) {
            // Deliberately catching throwable since we're at the top of a thread
            logger.error(throwable.getMessage(), throwable);
        }
    }

}
