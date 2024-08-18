package game.gugudan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Solution2 {

    private static final List<Character> CLAP_DIGITS = Arrays.asList('3', '6', '9');
    private static final String CLAP_RESPONSE = "clap";

    public String solution(String[] playerNames, int[] errorRates, int maxGameCount, int[] randomValues) {
        //playerNames, errorRates 를 사용하여 players 만드는 코드를 작성해주세요.
        Player[] players = createPlayers(playerNames, errorRates);

        Random random = new Random(maxGameCount, randomValues);
        return playGame(players, maxGameCount, random);
    }

    // Stream API 사용
    private Player[] createPlayers(String[] playerNames, int[] errorRates) {
        return IntStream.range(0, playerNames.length)
                .mapToObj(index -> new Player(playerNames[index], errorRates[index]))
                .toArray(Player[]::new);
    }

    private String playGame(Player[] players, int maxGameCount, Random random) {
        StringBuilder answer = new StringBuilder();
        //players 가 돌아가면서 자신의 오답율에 따라 응답하고
        //(오답율 계산 시에는 제공되는 random 함수를 사용해주세요.)
        //모두가 정답을 얘기하는경우 maxGameCount까지 게임을 진행
        int playerCount = players.length;

        for (int index = 1; index <= maxGameCount; index++) {
            Player currentPlayer = getCurrentPlayer(players, index, playerCount);
            String expectedAnswer = do369(index);
            String playerAnswer = processPlayerTurn(currentPlayer, index, expectedAnswer, random);

            createResult(answer, currentPlayer, playerAnswer);

            // 오답 시 게임 종료
            if (!expectedAnswer.equals(playerAnswer)) {
                break;
            }
        }

        return answer.toString();
    }

    private Player getCurrentPlayer(Player[] players, int turnIndex, int playerCount) {
        return players[(turnIndex - 1) % playerCount];
    }

    private String do369(int number) {
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

    private String processPlayerTurn(Player player, int number, String expectedAnswer, Random random) {
        return player.respond(number, expectedAnswer, random);
    }

    private void createResult(StringBuilder answer, Player player, String result) {
        String formattedResult = String.format("%s: %s\n", player.getName(), result);
        answer.append(formattedResult);
    }

    static class Player {
        private String name;
        private int errorRate;

        public Player(String name, int errorRate) {
            this.name = name;
            this.errorRate = errorRate;
        }

        public String getName() {
            return name;
        }

        public String respond(int number, String expectedAnswer, Random random) {
            int randomChance = random.getNextInt();
            boolean isIncorrect = randomChance < errorRate;

            if (isIncorrect) {
                return expectedAnswer.equals(CLAP_RESPONSE) ? String.valueOf(number) : CLAP_RESPONSE;
            }

            return expectedAnswer;
        }
    }

    /**
     * 이 클래스는 수정하지 마세요.
     */
    static private class Random {
        private int currentCount;
        private int maxCount;
        private int[] randomValues;

        public Random(int maxCount, int[] randomValues) {
            if (randomValues.length != maxCount) {
                throw new IllegalArgumentException("Random 클래스 초기화 실패");
            }
            this.maxCount = maxCount;
            this.randomValues = randomValues;
        }

        /**
         * 0~99까지의 값을 리턴하는 함수 각 숫자는 모두 나올 확율이 같다고 가정하면 된다.
         */
        public int getNextInt() {
            return randomValues[currentCount++];
        }
    }




    // main
    public static void main(String[] args) {
        // 테스트
        String[] playerNames = {"가", "나나", "다다다", "라라라", "마마마마마"};
        int[] errorRates = {0, 8, 19, 0, 0};
        int maxGameCount = 100;
        int[] randomValues = generateRandomValues();

        Solution2 solution = new Solution2();
        String result = solution.solution(playerNames, errorRates, maxGameCount, randomValues);
        System.out.println(result);
    }

    private static int[] generateRandomValues() {
        List<Integer> allValues =
                new ArrayList<>(IntStream.range(0, 100).boxed().toList());

        Collections.shuffle(allValues);

        return allValues.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }
}