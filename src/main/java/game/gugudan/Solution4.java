package game.gugudan;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

        // 실행 중인 모든 Task가 수행되면 종료
        executor.shutdown();

        try {
            /*
              awaitTermination() -> 새로운 Task가 실행되는 것을 막고
              일정 시간동안 실행 중인 Task가 완료되기를 기다림
              일정 시간동안 처리되지 않은 Task는 강제 종료시킴
             */
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                /*
                 shutdownNow() -> 실행 중인 Thread들을 즉시 종료시키려고 하지만
                 모든 Thread가 동시에 종료되는 것을 보장하지는 않음, 실행되지 않은 Task 반환
                 */
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
        while (true) { // TODO: while(true) 사용 의문 -> while 안에 복잡한 조건 작성할 건지
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
