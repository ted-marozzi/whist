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

        public IFilterStrat getFilterStrat() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
//		String className = System.getProperty( "fileteringpricingstrategy.class.name" );
//		IFilteringStrategy strategy = (IFilteringStrategy) Class.forName( className ).newInstance();

            return new NaiveFilterStrat();

        }

        public ISelectStrat getSelectStrat() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
                return new RandSelectStrat();
        }
}

