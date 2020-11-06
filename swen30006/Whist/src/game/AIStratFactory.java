package game;

public class AIStratFactory {
        private static AIStratFactory instance;

        private AIStratFactory() {
            // TODO Auto-generated constructor stub
        }

        public static AIStratFactory getInstance() {
            if (instance == null) {
                instance = new AIStratFactory();
            }
            return instance;
        }

        public IFilterStrat getFilterStrat(String filterProperty) {
            IFilterStrat product = null;
            switch (filterProperty) {
                case "no":
                    product = new NoFilterStrat();
                    break;
                case "naive":
                    product = new NaiveFilterStrat();
                    break;
                case "trump":
                    product = new TrumpFilterStrat();
                    break;
            }
            return product;
        }

        public ISelectStrat getSelectStrat(String selectProperty) {
            ISelectStrat product = null;
            switch (selectProperty) {
                case "random":
                    product = new RandSelectStrat();
                    break;
                case "highestRank":
                    product = new HighestRankSelectStrat();
                    break;
                case "smart":
                    product = new RandSelectStrat(); // TO REPLACE
                    break;
            }
            return product;
        }
}

