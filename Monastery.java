package Lista11;

import java.util.Random;
import java.util.concurrent.Semaphore;

public final class Monastery
{
    private class Chopstick
    {
        private Semaphore availability = new Semaphore(1);

        public void take() throws InterruptedException
        {
            availability.acquire();
        }
        public void putDown()
        {
            availability.release();
        }
    }

    private class Philosopher extends Thread
    {
        private Chopstick left, right;

        public Philosopher(int number)
        {
            super("Philosopher #" + (number + 1));
            right = chopsticks[number];
            left = chopsticks[(number + chopsticks.length - 1) % chopsticks.length];
        }

        private void meditate() throws InterruptedException
        {
            System.out.println(getName() + " is" + Main.ANSI_GREEN + " meditating." + Main.ANSI_RESET);
            sleep(new Random().nextInt(5)*1000);
            System.out.println(getName() + " has finished meditating.");
        }

        private void eat() throws InterruptedException
        {
            System.out.println(getName() + " is going to eat.");
            try
            {
                doorkeeper.acquire();
            }
            catch(InterruptedException ignored){}

            right.take();
            left.take();
            System.out.println(getName() + " is"+ Main.ANSI_RED + " eating." + Main.ANSI_RESET);
            sleep(new Random().nextInt(5)*1000+5000);
            System.out.println(getName() + " has finished eating.");

            right.putDown();
            left.putDown();
            doorkeeper.release();
        }

        public void run()
        {
//            for (int i = 0; i < 5; i++)
            while(true)
            {
                try {
                    meditate();
                    eat();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private final Semaphore doorkeeper;
    private final Philosopher [] philosophers;
    private final Chopstick [] chopsticks;


    public Monastery()
    {
        chopsticks = createChopsticks(5);
        philosophers = createPhilosophers(5);
        doorkeeper = new Semaphore( philosophers.length - 1, true);
    }

    public Monastery(int allPhilosophers)
    {
        if(allPhilosophers < 0)
            allPhilosophers = 2;

        chopsticks = createChopsticks(allPhilosophers);
        philosophers = createPhilosophers(allPhilosophers);
        doorkeeper = new Semaphore(allPhilosophers - 1, true);
    }

    private Philosopher[] createPhilosophers(int allPhilosophers)
    {
        Philosopher [] philosophersTab = new Philosopher[allPhilosophers];
        for (int i = 0; i < allPhilosophers; i++)
        {
            philosophersTab[i] = new Philosopher(i);
        }
        return philosophersTab;
    }

    private Chopstick [] createChopsticks(int allPhilosophers)
    {
        Chopstick [] chopsticksTab = new Chopstick[allPhilosophers];
        for (int i = 0; i < allPhilosophers; i++)
        {
            chopsticksTab[i] = new Chopstick();
        }
        return chopsticksTab;
    }

    public void start() throws InterruptedException {
//        for (int i = 0; i < 2*philosophers.length; i++)
        for (int i = 0; i < philosophers.length; i++)
        {
//            if(i < philosophers.length)
                philosophers[i].start();

//            else
//                {
//                    try
//                    {
//                        philosophers[i% philosophers.length].join();
//                    }
//                    catch (InterruptedException ignored){}
//                }

        }
    }
}

class Main
{
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) throws InterruptedException {
        Monastery monastery = new Monastery();
        monastery.start();
        System.out.println("hej");
    }
}