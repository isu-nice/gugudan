package game.gugudan;

import java.util.concurrent.atomic.AtomicInteger;

public class Solution4 {

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

    interface GameRule {
        String processNumber(int number);
    }

    class SeoulGameRule implements GameRule {
        @Override
        public String processNumber(int number) {
            String numberStr = String.valueOf(number);
            if (numberStr.contains("3") || numberStr.contains("6") || numberStr.contains("9")) {
                return "clap";
            } else {
                return numberStr;
            }
        }
    }

    class BusanGameRule implements GameRule {
        @Override
        public String processNumber(int number) {
            String numberStr = String.valueOf(number);
            int clapCount = 0;

            for (char ch : numberStr.toCharArray()) {
                if (ch == '3' || ch == '6' || ch == '9') {
                    clapCount++;
                }
            }

            if (clapCount > 0) {
                return "clap".repeat(clapCount);
            } else {
                return numberStr;
            }
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
                String result = gameRule.processNumber(i);

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
}
