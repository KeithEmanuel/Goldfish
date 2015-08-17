public class Main {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        // Add new cards to CardCatalog.allCards

        /***** Pick one! *****/
        testAllCards();
        //testBoringCards();
        //playTestGame();
        //test();

        System.out.println("Finished in " + (float) (System.currentTimeMillis() - startTime) / 60000.0 + " minutes.");
    }

    /**
     * Runs all cards for 100 generations, then reruns the final generation decks 10000 times each.
     * Prints the best deck of the final generation.
     *
     * With the included cards, the best deck should only contain a mix of Mountains, Lighting Bolts, Goblin Guides,
     * and possibly a few Flame Rifts.
     */
    public static void testAllCards(){
        Genetics genetics = new Genetics();
        genetics.runForGenerations(100);

        System.out.println("Testing the final generation...");
        genetics.evaluateGeneration(10000);

        System.out.println("The best is:\n");
        System.out.println(genetics.getGeneration().get(0));
    }

    /**
     * Runs decks containing standard-common power cards for 1 minutes at the default deck run count for one minute,
     * then ups the deck run count to 1500 for two minutes, then runs the final generation once at 10000 deck runs.
     * Prints the entire final generation
     */
    public static void testBoringCards(){
        CardCatalog catalog = new CardCatalog(new String[]{
                "Mon's Goblin Raiders",
                "Grizzly Bears",
                "Great Stable Stag",
                "Shock",
                "Incinerate",
                "Mountain",
        });

        Genetics genetics = new Genetics(catalog);

        genetics.runForMinutes(1);
        genetics.setDeckRunCount(1500);
        genetics.runForMinutes(2);

        System.out.println("Testing the final generation...");
        genetics.evaluateGeneration(10000);

        System.out.println("The best decks are:\n");
        genetics.getGeneration().stream()
                .forEachOrdered((d) -> System.out.println(d.toString()));

    }

    /**
     * Run this with Game.DEBUG = true and the debug calls uncommented to see a game output.
     */
    public static void playTestGame(){
        CardCatalog catalog = new CardCatalog(new String[]{
                "Mountain",
                "Lightning Bolt",
        //        "Goblin Guide",
                "Jackal Pup",
        });

        Deck deck = new Deck(Deck.Option.Random, catalog);

        new Game(deck).playGame();
    }

    public static void test(){
        CardCatalog catalog = new CardCatalog(new String[]{
                "Mountain",
                "Lightning Bolt",
                "Goblin Guide",
                "Jackal Pup",
        });

        Genetics genetics = new Genetics(catalog);

        genetics.runForGenerations(100);
    }
}