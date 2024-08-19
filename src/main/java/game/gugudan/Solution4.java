package game.gugudan;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Solution4 {

    private static final List<Character> CLAP_DIGITS = Arrays.asList('3', '6', '9');
    private static final String CLAP_RESPONSE = "clap";

    public int solution(String[] playerNames, int maxGameCount) {
        //주어진 clapCounter 를 사용해주세요.
        ClapCounter clapCounter = ClapCounter.getInstance();

        // ExecutorService 활용 -> 두 개의 게임 동시에 실행
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 서울, 부산 게임 실행
        executor.submit(() ->
                playGame("서울", maxGameCount, clapCounter)
        );
        executor.submit(() ->
                playGame("부산", maxGameCount, clapCounter));

        // ExecutorService 종료 대기
        executor.shutdown();

        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return clapCounter.getCount();
    }

    private void playGame(String region, int maxGameCount, ClapCounter clapCounter) {
        GameRule gameRule = selectGameRule(region);

        for (int index = 1; index <= maxGameCount; index++) {
            String playerAnswer = gameRule.do369(index);
            increaseClapCount(playerAnswer, clapCounter);
        }
    }

    private void increaseClapCount(String result, ClapCounter clapCounter) {
        int index = 0;
        while (true) {
            index = result.indexOf(CLAP_RESPONSE, index);

            if (index == -1) {
                break;
            }

            clapCounter.increaseCount();

            index += CLAP_RESPONSE.length();
        }
    }

    private GameRule selectGameRule(String region) {
        return switch (region) {
            case "서울" -> new SeoulGameRule();
            case "부산" -> new BusanGameRule();
            default -> throw new IllegalArgumentException("지원하지 않는 지역입니다.");
        };
    }

/*    private Player[] createPlayers(String[] playerNames) {
        return Arrays.stream(playerNames)
                .map(Player::new)
                .toArray(Player[]::new);
    }

    private Player getCurrentPlayer(Player[] players, int turnIndex) {
        int playerCount = players.length;
        return players[(turnIndex - 1) % playerCount];
    }*/

    interface GameRule {
        String do369(int number);
    }

    static class SeoulGameRule implements GameRule {
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

    static class BusanGameRule implements GameRule {
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

/**
 * 인스턴스 생성로직을 제외한 내용을 자유롭게 수정하여 구현해주세요. (메소드 추가/수정 가능)
 * 이경우 별도로 수동채점이 이루어집니다.
 */
class ClapCounter {
    private static ClapCounter clapCounter = new ClapCounter();
    private int count = 0;

    private ClapCounter() {
    }

    public static ClapCounter getInstance() {
        return clapCounter;
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized void increaseCount() {
        count++;
    }
}


// main
class Main {
    public static void main(String[] args) {
        // 테스트용 데이터 설정
        String[] playerNames = {"aaa", "bbb", "ccc", "ddd"};
        int maxGameCount = 33;

        // Solution4 인스턴스화
        Solution4 solution = new Solution4();

        // solution 메서드 호출 및 결과 출력
        int clapCount = solution.solution(playerNames, maxGameCount);
        System.out.println("clap count: " + clapCount);
    }
}
