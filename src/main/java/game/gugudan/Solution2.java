package game.gugudan;

import java.util.ArrayList;
import java.util.List;

public class Solution2 {

    public String solution(String[] playerNames, int[] errorRates, int maxGameCount, int[] randomValues) {
        List<Player> players = createPlayers(playerNames, errorRates);
        Random random = new Random(maxGameCount, randomValues);

        return playGame(players, maxGameCount, random);
    }

    private List<Player> createPlayers(String[] playerNames, int[] errorRates) {
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < playerNames.length; i++) {
            players.add(new Player(playerNames[i], errorRates[i]));
        }

        return players;
    }

    private String playGame(List<Player> players, int maxGameCount, Random random) {
        StringBuilder answer = new StringBuilder();
        int playerCount = players.size();

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
        String numberStr = String.valueOf(number);
        if (numberStr.contains("3") || numberStr.contains("6") || numberStr.contains("9")) {
            return "clap";
        } else {
            return numberStr;
        }
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

        public int getNextInt() {
            return randomValues[currentCount++];
        }
    }
}