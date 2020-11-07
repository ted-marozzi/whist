package game;

/**
 * The singleton factory that produced the strategies for the AI
 */
public class AIStratFactory {
        private static AIStratFactory instance;

        private AIStratFactory() {}

    /**
     * @return The factory
     */
        public static AIStratFactory getInstance() {
            if (instance == null) {
                instance = new AIStratFactory();
            }
            return instance;
        }

    /**
     * @param filterProperty The properties string related to filtering
     * @return The coresponding filter strategy
     */
        public IFilterStrat getFilterStrat(String filterProperty) {
            IFilterStrat product = null;
            if ("no".equals(filterProperty)) {
                product = new NoFilterStrat();
            } else if ("naive".equals(filterProperty)) {
                product = new NaiveFilterStrat();
            } else if ("trump".equals(filterProperty)) {
                product = new TrumpFilterStrat();
            }
            return product;
        }

    /**
     * @param selectProperty The properties string related to selection
     * @return The selection strategy
     */
        public ISelectStrat getSelectStrat(String selectProperty) {
            ISelectStrat product = null;
            if ("random".equals(selectProperty)) {
                product = new RandSelectStrat();
            } else if ("highestRank".equals(selectProperty)) {
                product = new HighestRankSelectStrat();
            } else if ("smart".equals(selectProperty)) {
                product = new SmartSelectStrat();
            }
            return product;
        }
}

