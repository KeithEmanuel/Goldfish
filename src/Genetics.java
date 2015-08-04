import java.io.UncheckedIOException;
import java.lang.Math;
import java.util.*;


/**
 * Genetics contains the bulk of genetic algorithm used to improve decks.
 *
 * Usage:
 * The Genetics constructor accepts a CardCatalog parameter. Only the cards in the CardCatalog will
 * be used to create the random decks in the initial generation. The default constructor creates a
 * CardCatalog with all available cards.
 * After construction, runForGenerations, runForSeconds, or runForMinutes should likely be called to evaluate the
 * decks in the generation. Alternatively, evalutateGeneration can be called for a more customized approach.
 *
 * Note that a lower win turn is better. This is opposite of a more traditional fitness score.
 */
public class Genetics {

    // The chance the two deck being bred will swap their cards.
    private final float DEFAULT_CARD_SWAP_RATE = 0.7f;
    private float cardSwapRate;
    // The chance that a card will become another random card.
    private final float DEFAULT_MUTATION_CHANCE = 0.02f;
    private float mutationChance;
    // The total population of each generation
    private final int DEFAULT_GENERATION_SIZE = 64;
    private int generationSize;
    // The number of games played to evaluate a deck
    private final int DEFAULT_DECK_RUN_COUNT = 500;
    private int deckRunCount;

    private int generationCount;

    private final CardCatalog cardCatalog;
    private List<RankedDeck> generation;
    private Random rand;

    /**
     * Creates a new Genetics object with the entire card catalog.
     */
    public Genetics(){
        this(null);
    }

    /**
     * Creates a new Genetics object with a specified card catalog.
     * @param cardCatalog The cards to use in the generated decks.
     */
    public Genetics(CardCatalog cardCatalog){
        generationCount = 1;
        cardSwapRate = DEFAULT_CARD_SWAP_RATE;
        mutationChance = DEFAULT_MUTATION_CHANCE;
        generationSize = DEFAULT_GENERATION_SIZE;
        deckRunCount = DEFAULT_DECK_RUN_COUNT;

        if(cardCatalog == null){
            cardCatalog = new CardCatalog();
        }

        this.cardCatalog = cardCatalog;
        generation = new ArrayList<>();
        rand = new Random();

        while(generation.size() < generationSize){
            generation.add(new RankedDeck(cardCatalog));
        }
    }

    /**
     * Evaluates a single generation using the default run count and sorts it from best to worst.
     */
    public void evaluateGeneration(){
        evaluateGeneration(deckRunCount);
    }

    /**
     * Evaluates a generation a sorts it from best to worst.
     * @param runCount The number of times to run each deck.
     */
    public void evaluateGeneration(int runCount){

        for(RankedDeck deck : generation){
            deck.playGames(runCount);
        }

        sortGeneration();

    }

    /**
     * Creates the next generation and replaces the current generation with the new one.
     */
    private void breedNewGeneration(){
        sortGeneration();

        List<RankedDeck> newGeneration = new ArrayList<>();

        while(newGeneration.size() < generationSize){
            newGeneration.addAll(
                    breed(
                            generation.get(getSkewedInt(generation.size())),
                            generation.get(getSkewedInt(generation.size()))
                    )
            );
        }

        generation = newGeneration;
    }

    /**
     * runForGenerations runs the genetic algorithm a specified number of times, rather than stopping on
     *      a terminating condition.
     * @param generationCount The number of times to evaluate the current generation and create a new generation.
     * @return The best from the final generation.
     */
    public RankedDeck runForGenerations(int generationCount){
        System.out.println("-- Initial Population Sample --");
        System.out.println(generation.get(0).toString());

        for(int i = 0; i < generationCount; i++) {
            evaluateGeneration();

            System.out.println("-- Gen " + generationCount + " --");
            System.out.println("-- Best deck --");
            System.out.println(generation.get(0).toString());

            breedNewGeneration();
            ++generationCount;
        }

        return generation.get(0);
    }

    /**
     * runForSeconds breeds new generation for the specified number of seconds, then evaluates the final generation.
     * @param seconds The number of seconds to breed new generations. The total time run will exceed the this number.
     * @return The best deck from the final generation.
     */
    public RankedDeck runForSeconds(int seconds){
        Calendar end = Calendar.getInstance();
        end.add(Calendar.SECOND, seconds);

        System.out.println("-- Initial Population Sample --");
        System.out.println(generation.get(0).toString());

        while(Calendar.getInstance().before(end)){
            evaluateGeneration();

            System.out.println("-- Gen " + generationCount + " --");
            System.out.println("-- Best deck --");
            System.out.println(generation.get(0).toString());

            breedNewGeneration();
            ++generationCount;
        }

        return generation.get(0);
    }

    /**
     * runForMinutes breeds new generation for the specified number of minutes, then evaluates the final generation.
     * @param minutes The number of seconds to breed new generations. The total time run will exceed the this number.
     * @return The best deck from the final generation.
     */
    public RankedDeck runForMinutes(int minutes){
        return runForSeconds(minutes * 60);
    }

    /**
     * breed takes two RankedDeck objects and combines and mutates them into a new RankedDeck.
     * @param deck1 The first RankedDeck to breed.
     * @param deck2 The second RankedDeck to breed.
     * @return A new RankedDeck breed by genetic rules.
     */
    public List<RankedDeck> breed(RankedDeck deck1, RankedDeck deck2){
        List<RankedDeck> children = new ArrayList<>(2);
        int splitPos = rand.nextInt(Math.min(deck1.size(), deck2.size()));

        deck1.sort();
        deck2.sort();

        if(rand.nextFloat() < cardSwapRate) {
            RankedDeck bredDeck1 = new RankedDeck(new ArrayList<>(deck1.subList(0, splitPos)));
            bredDeck1.addAll(new ArrayList<>(deck2.subList(splitPos, deck2.size())));
            mutate(bredDeck1);

            RankedDeck bredDeck2 = new RankedDeck(new ArrayList<>(deck2.subList(0, splitPos)));
            bredDeck2.addAll(new ArrayList<>(deck1.subList(splitPos, deck1.size())));
            mutate(bredDeck2);

            children.add(bredDeck1);
            children.add(bredDeck2);
        }
        else{
            children.add(deck1.copy());
            children.add(deck2.copy());
        }

        return children;
    }

    /**
     * mutate iterates over a deck of cards and may mutate each card over the MUTATION_RATE
     * @param deck The deck to be mutated.
     * @return The mutated deck.
     */
    public RankedDeck mutate(RankedDeck deck){
        for(int i = 0; i < deck.size(); i++){
            if(rand.nextFloat() < mutationChance){
                deck.replace(i, cardCatalog.getRandomCard());
            }
        }

        return deck;
    }

    /**
     * Sorts the current generation by average win turn, descending.
     */
    private void sortGeneration(){
        generation.sort((o1, o2) -> Double.compare(o1.getAverageWinTurn(), o2.getAverageWinTurn()));
    }

    /**
     * Gets a random integer in the range of 0 - max, skewed left towards 0.
     *
     * @param max The maximum integer that the function will return.
     * @return A random int from 0 to max.
     */
    private int getSkewedInt(int max){

        int ret = (int)(rand.nextGaussian() * max / 7.0);

        if(ret < 0){
            return (-ret - 1) % max;
        }

        return ret % max;
    }

    public void testSkewedInt(){
        HashMap<Integer, Integer> map = new HashMap<>();

        for(int i = 0; i < 100000; i++){
            int randInt = getSkewedInt(100);

            map.put(randInt, map.getOrDefault(randInt , 0) + 1);

        }

        System.out.println(map.toString());
    }

    public List<RankedDeck> getGeneration(){
        return generation;
    }

    /**
     * The cardSwapRate is the chance that two decks will swap their cards when bred.
     * @return The current cardSwapRate
     */
    public float getCardSwapRate() {
        return cardSwapRate;
    }

    /**
     * The cardSwapRate is the chance that two decks will swap their cards when bred.
     * @param cardSwapRate The new card swap rate
     */
    public void setCardSwapRate(float cardSwapRate) {
        this.cardSwapRate = cardSwapRate;
    }

    /**
     * The mutation chance is the chance that a card in a deck will become another random card.
     * @return The current mutation chance
     */
    public float getMutationChance() {
        return mutationChance;
    }

    /**
     * The mutationChance is the chance that a card in a deck will become another random card.
     * @param mutationChance The new mutation chance
     */
    public void setMutationChance(float mutationChance) {
        this.mutationChance = mutationChance;
    }

    /**
     * The generation size is the number of deck in a single generation.
     * @return The current generation size
     */
    public int getGenerationSize() {
        return generationSize;
    }

    /**
     * The generation size is the number of deck in a single generation.
     * @param generationSize the new generation size
     */
    public void setGenerationSize(int generationSize) {
        this.generationSize = generationSize;
    }

    /**
     * The deck run count is the number of games each deck in a generation plays to find an average win turn.
     * @return The current deck run count
     */
    public int getDeckRunCount() {
        return deckRunCount;
    }

    /**
     * The deck run count is the number of games each deck in a generation plays to find an average win turn.
     * @param deckRunCount The new deck run count
     */
    public void setDeckRunCount(int deckRunCount) {
        this.deckRunCount = deckRunCount;
    }

    /**
     * The generation count is the number of generations that have been generated. The initial, randomly generated
     * generation is generation one.
     * @return The number of generations that have been generated
     */
    public int getGenerationCount() {
        return generationCount;
    }
}