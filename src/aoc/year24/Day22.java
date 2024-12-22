package aoc.year24;

import aoc.util.PuzzleApp;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day22 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 22: Monkey Market");
        PuzzleApp app = new Day22();
        app.run();
    }

    public String filename() {
        return "data/year24/day22";
    }

    private final List<Long> secretNumbersPartOne = new ArrayList<>(); // The set of values we'll iterate over 2000 times...
    private final ConcurrentMap<Integer,Long> secretNumbersPartTwo = new ConcurrentHashMap<>();

    public void parseLine(String line) {
        secretNumbersPartOne.add(Long.parseLong(line));
        secretNumbersPartTwo.put(secretNumbersPartTwo.size(), Long.parseLong(line));
    }

    private long nextSecretNumber(long secretNumber) {
        secretNumber = ((secretNumber * 64) ^ secretNumber) % 16777216;
        secretNumber = ((secretNumber / 32) ^ secretNumber) % 16777216;
        secretNumber = ((secretNumber * 2048) ^ secretNumber) % 16777216;
        return secretNumber;
    }

    private int price(long secretNumber) {
        return (int) (secretNumber % 10);
    }

    public void process() {
        // System.out.println("Initial secret numbers: " + initialSecretNumbers);

        // This is a neat way to process the whole list of secret numbers 2000 times (though it doesn't use parallel threads):
        IntStream.range(0, 2000).forEach(_ -> secretNumbersPartOne.replaceAll(this::nextSecretNumber));
    }

    public void results() { // 14119253575
        // System.out.println("Secret numbers: " + secretNumbers);
        System.out.println("Day 22 Part 1 result: " + secretNumbersPartOne.stream().mapToLong(Long::longValue).sum());
    }

    // Track the prices for each secret sequence, keyed by the set of 4 preceding prices changes.
    // We could do something fancy to convert the 4 consecutive price changes into an integer, which would
    // let us key our maps by a simple number rather than by a list, but this works just fine, just takes
    // more space.
    private final List<Map<List<Integer>,Integer>> prices = new ArrayList<>();

    private void initializePriceList() {
        secretNumbersPartTwo.forEach((_,_) -> prices.add(new HashMap<>()));
    }

    private int trackPriceChanges(int index, long currentSecretNumber, long nextSecretNumber, Deque<Integer> priceChanges) {
        int oldPrice = price(currentSecretNumber);
        int newPrice = price(nextSecretNumber);
        int priceChange = newPrice - oldPrice;
        priceChanges.addLast(priceChange);
        if (priceChanges.size() > 4) priceChanges.removeFirst(); // Keep only the past 4 price changes
        if (priceChanges.size() == 4) {
            // We want to keep track of the first occurrence of the set of four price changes,
            // so if this isn't the first occurrence, we just ignore it.
            List<Integer> key = new ArrayList<>(priceChanges); // Make a copy since we'll be changing the Deque over time
            prices.get(index).putIfAbsent(key, newPrice);
        }
        return priceChange; // we don't really need to return this, useful for debugging.
    }

    public void processPartTwo() {
        initializePriceList(); // Create enough maps to store all our prices

        secretNumbersPartTwo.keySet().parallelStream().forEach(i -> {
            final Deque<Integer> priceChanges = new LinkedList<>(); // This tracks prices changes for secretNumbers[i]
            // System.out.println(secretNumbers[i] + "\t" + price(secretNumbers[i]));

            IntStream.range(0, 2000).forEach(_ -> {
                long prev = secretNumbersPartTwo.get(i);
                long next = nextSecretNumber(prev);
                trackPriceChanges(i, prev, next, priceChanges);
                // System.out.println(next + "\t" + price(next) + "\t" + priceChange + "\t" + priceChanges);
                secretNumbersPartTwo.put(i, next);
            });
        });
    }

    public void resultsPartTwo() {
        // Now that we've collected all the prices and the ranges which "predict" them, we just need to find
        // the range which produces the maximum total price across all secret numbers.

        // System.out.println( "Total number of map entries: " + prices.stream().mapToInt(Map::size).sum());

        /*
        Set<List<Integer>> allKeys = prices.stream().flatMap(m -> m.keySet().stream()).collect(Collectors.toSet());

        final AtomicInteger bestPrice = new AtomicInteger();

        allKeys.forEach( k -> {
            final AtomicInteger totalPrice = new AtomicInteger();
            prices.forEach(p -> {
                totalPrice.accumulateAndGet(p.getOrDefault(k, 0), Integer::sum);
            });

            if (totalPrice.get() > bestPrice.get()) bestPrice.set(totalPrice.get());
        });
        */

        /*
         * This streams function below is equivalent to the commented-out code above.
         *
         * It iterates over the prices list, grabs all the maps and creates a stream
         * over all of them. We then collect all entries into a single map, keyed by
         * the same keys, summing up all the values, giving us the total price across
         * all of our input secret sequences, for each key (the key is the list of 4
         * preceding price changes). Lastly, we select the maximum value from this map
         * and return it (for our puzzle answer, we only need this value, not the key
         * which maps to it).
         *
         * If we wanted to get the key (one of the keys, since there could be more than
         * one) which produced this maximum value, we could change the stream after the
         * collect() call to do:
         *
         * .entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null)
         *
         * and then get both the key and value out of that Map.Entry.
         */

        int bestPrice = prices.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)))
                .values().stream()
                .mapToInt(Integer::intValue)
                .max().orElse(0);

        System.out.println("Day 22 part 2 results: " + bestPrice);
    }
}

