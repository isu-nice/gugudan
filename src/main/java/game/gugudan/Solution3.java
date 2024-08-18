package game.gugudan;

import java.util.Arrays;
import java.util.List;

public class Solution3 {

    private static final List<Character> CLAP_DIGITS = Arrays.asList('3', '6', '9');
    private static final String CLAP_RESPONSE = "clap";

    public String solution(String region, String[] playerNames, int maxGameCount) {
        GameRule gameRule = selectGameRule(region);
        Player[] players = createPlayers(playerNames);

        return playGame(players, maxGameCount, gameRule);
    }

    // switch 활용
    private GameRule selectGameRule(String region) {
        return switch (region) {
            case "서울" -> new SeoulGameRule();
            case "부산" -> new BusanGameRule();
            default -> throw new IllegalArgumentException("지원하지 않는 지역입니다.");
        };
    }

    private Player[] createPlayers(String[] playerNames) {
        return Arrays.stream(playerNames)
                .map(Player::new)
                .toArray(Player[]::new);
    }

    private String playGame(Player[] players, int maxGameCount, GameRule gameRule) {
        StringBuilder answer = new StringBuilder();

        for (int index = 1; index <= maxGameCount; index++) {
            Player currentPlayer = getCurrentPlayer(players, index);
            String playerAnswer = gameRule.do369(index);
            createResult(answer, currentPlayer, playerAnswer);
        }

        return answer.toString();
    }

    private Player getCurrentPlayer(Player[] players, int turnIndex) {
        int playerCount = players.length;
        return players[(turnIndex - 1) % playerCount];
    }

    private void createResult(StringBuilder answer, Player player, String result) {
        String formattedResult = String.format("%s: %s\n", player.getName(), result);
        answer.append(formattedResult);
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
