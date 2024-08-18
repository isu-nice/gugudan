package game.gugudan;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Solution4 {

    private static final List<Character> CLAP_DIGITS = Arrays.asList('3', '6', '9');
    private static final String CLAP_RESPONSE = "clap";

    public int solution(String[] playerNames, int maxGameCount) {
        //주어진 clapCounter 를 사용해주세요.
        ClapCounter clapCounter = ClapCounter.getInstance();

        GameRunner seoulGame = new GameRunner(playerNames, maxGameCount, new SeoulGameRule(), clapCounter);
        GameRunner busanGame = new GameRunner(playerNames, maxGameCount, new BusanGameRule(), clapCounter);

        Thread seoulThread = new Thread(seoulGame);
        Thread busanThread = new Thread(busanGame);

        seoulThread.start();
        busanThread.start();

        try {
            seoulThread.join();
            busanThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return clapCounter.getCount();
    }


    static class GameRunner implements Runnable {
        private String[] playerNames;
        private int maxGameCount;
        private GameRule gameRule;
        private ClapCounter clapCounter;

        public GameRunner(String[] playerNames, int maxGameCount, GameRule gameRule, ClapCounter clapCounter) {
            this.playerNames = playerNames;
            this.maxGameCount = maxGameCount;
            this.gameRule = gameRule;
            this.clapCounter = clapCounter;
        }

        @Override
        public void run() {
            int playerCount = playerNames.length;

            for (int i = 1; i <= maxGameCount; i++) {
                String playerName = playerNames[(i - 1) % playerCount];
                String result = gameRule.do369(i);

                if (result.contains("clap")) {
                    int claps = (int) result.chars()
                            .filter(ch -> ch == 'c').count();

                    clapCounter.increment(claps);
                }

                System.out.println(playerName + ": " + result);
            }
        }
    }

    /**
     인스턴스 생성로직을 제외한 내용을 자유롭게 수정하여 구현해주세요. (메소드 추가/수정 가능)
     이경우 별도로 수동채점이 이루어집니다.
     */
    static class ClapCounter {
        private static ClapCounter clapCounter = new ClapCounter();
        private AtomicInteger count = new AtomicInteger(0);

        private ClapCounter() {}

        public static ClapCounter getInstance() {
            return clapCounter;
        }

        public int getCount() {
            return count.get();
        }

        public synchronized void increment(int amount) {
            count.addAndGet(amount);
        }
    }

    interface GameRule {
        String do369(int number);
    }

    static class SeoulGameRule implements Solution3.GameRule {
        @Override
        public String do369(int number) {
            if (contains369(number)) {
                return CLAP_RESPONSE;
            } else {
                return String.valueOf(number);
            }
        }

        private boolean contains369(int number) {
            return String.valueOf(number)
                    .chars()
                    .mapToObj(ch -> (char) ch)
                    .anyMatch(CLAP_DIGITS::contains);
        }
    }

    static class BusanGameRule implements Solution3.GameRule {
        @Override
        public String do369(int number) {
            int clapCount = computeClapCount(number);

            if (clapCount > 0) {
                return CLAP_RESPONSE.repeat(clapCount);
            } else {
                return String.valueOf(number);
            }
        }

        private static int computeClapCount(int number) {
            long clapCount = String.valueOf(number)
                    .chars()
                    .mapToObj(ch -> (char) ch)
                    .filter(CLAP_DIGITS::contains)
                    .count();
            return (int) clapCount;
        }
    }

    static class Player {
        private String name;

        public Player(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
