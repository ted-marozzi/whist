package game;

public class AIStratFactory {
        private static AIStratFactory AIStratFactory;

        private AIStratFactory() {
            // TODO Auto-generated constructor stub
        }

        public static AIStratFactory getInstance() {
            if (AIStratFactory == null) {
                AIStratFactory = new AIStratFactory();
            }
            return AIStratFactory;
        }

        public IFilterStrat getFilterStrat(String filterProperty) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
//		String className = System.getProperty( "fileteringpricingstrategy.class.name" );
//		IFilteringStrategy strategy = (IFilteringStrategy) Class.forName( className ).newInstance();
            IFilterStrat product = null;
            if (filterProperty.equals("no")) {
                product = new NoFilterStrat();
            } else if (filterProperty.equals("naive")) {
                product = new NaiveFilterStrat();
            } else if (filterProperty.equals("trump")) {
                product = new TrumpFilterStrat();
            }
            return product;
        }

        public ISelectStrat getSelectStrat(String selectProperty) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            ISelectStrat product = null;
            if (selectProperty.equals("random")) {
                product = new RandSelectStrat();
            } else if (selectProperty.equals("highestRank")) {
                product = new HighestRankSelectStrat();
            }
            return product;
        }
}

