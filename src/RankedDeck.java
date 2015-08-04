import java.util.ArrayList;
import java.util.List;

/**
 * RankedDeck is a subclass of deck which adds some functionality that helps rank decks.
 */
public class RankedDeck extends Deck{
    private int runCount;
    private double winTurnSum;

    // generates a random deck
    public RankedDeck(Option option){
        super(option);
        runCount= 0;
        winTurnSum = 0;
    }

    public RankedDeck(CardCatalog catalog){
        super(Deck.Option.Random, catalog);
        runCount= 0;
        winTurnSum = 0;
    }

    public RankedDeck(Deck deck){
        super(deck);
        runCount = 0;
        winTurnSum = 0;
    }

    public RankedDeck(List<Card> deck){
        this.deck = new ArrayList<>(deck);
        runCount = 0;
        winTurnSum = 0;
    }

    public double playGames(int gameCount){
        for(int i = 0; i < gameCount; i++){
            addWinTurn(new Game(copy()).playGame());
        }

        return getAverageWinTurn();
    }

    public void addWinTurn(double winTurn) {
        runCount++;
        winTurnSum += winTurn;
    }

    @Override
    public RankedDeck copy(){
        RankedDeck copy = new RankedDeck(new Deck(new ArrayList<>(deck)));
        copy.save();

        return copy;
    }

    public double getAverageWinTurn(){
        if(runCount == 0){
            return -1.0;
        }

        return winTurnSum / runCount;
    }

    @Override
    public String toString(){
        if(runCount > 0){
            return "Deck runs: " + runCount +
                    "\nAverage win turn: " + getAverageWinTurn()
                    + "\n" + super.toString() + "\n";
        }

        return "Average win turn: unknown\n" + super.toString() + "\n";
    }
}
