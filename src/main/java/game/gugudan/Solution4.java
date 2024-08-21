package game.gugudan;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class Solution4 {

    private static final Set<Character> CLAP_DIGITS = new HashSet<>(Set.of('3', '6', '9'));
    private static final String CLAP_RESPONSE = "clap";

    public int solution(String[] playerNames, int maxGameCount) {
        //주어진 clapCounter 를 사용해주세요.
        ClapCounter clapCounter = ClapCounter.getInstance();

        // 두 게임 동시에 실행
        executeGames(maxGameCount, clapCounter);

        return clapCounter.getCount();
    }

    private void executeGames(int maxGameCount, ClapCounter counter) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 각 지역 게임 실행
        executor.submit(() -> playGame("서울", maxGameCount, counter));
        executor.submit(() -> playGame("부산", maxGameCount, counter));

        // 모든 스레드가 완료될 때까지 대기
        awaitTermination(executor);
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
        while ((index = result.indexOf(CLAP_RESPONSE, index)) != -1) {
            clapCounter.increaseCount();
            index += CLAP_RESPONSE.length();
        }
    }

    private void awaitTermination(ExecutorService executor) {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private GameRule selectGameRule(String region) {
        return switch (region) {
            case "서울" -> new SeoulGameRule();
            case "부산" -> new BusanGameRule();
            default -> throw new IllegalArgumentException("지원하지 않는 지역입니다.");
        };
    }

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
}

/**
 * 인스턴스 생성로직을 제외한 내용을 자유롭게 수정하여 구현해주세요. (메소드 추가/수정 가능)
 * 이경우 별도로 수동채점이 이루어집니다.
 */
class ClapCounter {
    private static ClapCounter clapCounter = new ClapCounter();
    private AtomicInteger count = new AtomicInteger(0);

    private ClapCounter() {
    }

    public static ClapCounter getInstance() {
        return clapCounter;
    }

    public int getCount() {
        return count.get();
    }

    public void increaseCount() {
        count.incrementAndGet();
    }
}


// main
class Main {
    public static void main(String[] args) {
        // 테스트용 데이터 설정
        String[] playerNames = {"aaa", "bbb", "ccc", "ddd"};
        int maxGameCount = 33;

        Solution4 solution = new Solution4();
        int clapCount = solution.solution(playerNames, maxGameCount);

        System.out.println("clap count: " + clapCount);
    }
}
