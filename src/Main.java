
public class Main {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        CardCatalog catalog = new CardCatalog(new String[]{
                "Mon's Goblin Raiders",
                "Grizzly Bears",
                "Great Stable Stag",
                "Shock",
                "Incinerate",
                //"Lightning Bolt",
                "Mountain",
                //"Goblin Guide",
                //"Flame Rift",
        });

        Genetics genetics = new Genetics(catalog);

        //genetics.runForGenerations(100);
        //genetics.runForSeconds(60);
        genetics.runForMinutes(5);

        System.out.println("Testing the final generation...");
        genetics.evaluateGeneration(10000);

        System.out.println("The best decks are:\n");
        genetics.getGeneration().stream()
                .forEachOrdered((d) -> System.out.println(d.toString()));


        System.out.println("Finished in " + (float) (System.currentTimeMillis() - startTime) / 60000.0 + " minutes.");
    }
}