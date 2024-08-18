package game.gugudan;

import java.util.List;
import java.util.stream.IntStream;

public class Solution2 {

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

        for (int i = 1; i <= maxGameCount; i++) {
            Player currentPlayer = getCurrentPlayer(players, i, playerCount);
            String expectedAnswer = do369(i);
            String actualAnswer = processPlayerTurn(currentPlayer, i, expectedAnswer, random);

            buildResult(answer, currentPlayer, actualAnswer);

            // 오답 시 게임 종료
            if (!expectedAnswer.equals(actualAnswer)) {
                break;
            }
        }

        return answer.toString();
    }

    private Player getCurrentPlayer(List<Player> players, int turnIndex, int playerCount) {
        return players.get((turnIndex - 1) % playerCount);
    }

    private String processPlayerTurn(Player player, int number, String expectedAnswer, Random random) {
        return player.respond(number, expectedAnswer, random);
    }

    private void buildResult(StringBuilder answer, Player player, String result) {
        answer.append(player.getName()).append(": ").append(result).append("\n");
    }

    private String do369(int number) {
        if (contains369(number)) {
            return "clap";
        } else {
            return String.valueOf(number);
        }
    }

    private boolean contains369(int number) {
        return String.valueOf(number)
                .chars()
                .mapToObj(c -> (char) c)
                .anyMatch(ch -> ch == '3' || ch == '6' || ch == '9');
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
            int chance = random.getNextInt();
            boolean isError = chance < errorRate;

            if (isError) {
                return expectedAnswer.equals("clap") ? String.valueOf(number) : "clap";
            } else {
                return expectedAnswer;
            }
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
}