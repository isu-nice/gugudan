package game.gugudan;

public class Solution3 {

    public String solution(String region, String[] playerNames, int maxGameCount) {
        GameRule gameRule;

        if (region.equals("서울")) {
            gameRule = new SeoulGameRule();
        } else if (region.equals("부산")) {
            gameRule = new BusanGameRule();
        } else {
            throw new IllegalArgumentException("지원하지 않는 지역");
        }

        Player[] players = new Player[playerNames.length];
        for (int i = 0; i < playerNames.length; i++) {
            players[i] = new Player(playerNames[i]);
        }

        return playGame(players, maxGameCount, gameRule);
    }

    private String playGame(Player[] players, int maxGameCount, GameRule gameRule) {
        StringBuilder answer = new StringBuilder();
        int playerCount = players.length;

        for (int i = 1; i <= maxGameCount; i++) {
            Player currentPlayer = players[(i - 1) % playerCount];
            String result = gameRule.processNumber(i);
            createResult(answer, currentPlayer, result);
        }

        return answer.toString();
    }

    private void createResult(StringBuilder answer, Player currentPlayer, String result) {
        answer.append(currentPlayer.getName()).append(": ")
                .append(result).append("\n");
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
}
