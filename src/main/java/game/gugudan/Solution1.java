package game.gugudan;

import java.util.HashSet;
import java.util.Set;

class Solution1 {

    private static final Set<Character> CLAP_DIGITS = new HashSet<>(Set.of('3', '6', '9'));

    /**
     * do369 함수를 이용하여 playNames의 유저가 돌아가면서,
     * maxGameCount까지 진행하도록 구현
     * 각 유저는 본인의 차례때 '이름: 답변'을 출력한다.
     * 예) playerNames = {"영수", "광수", "영철", "상철"} maxGameCount = 100 이 입력된경우
     * 영수: 1
     * 광수: 2
     * 영철: clap
     * 상철: 4
     * ..중략..
     * 상철: 100
     */
    public String solution(String[] playerNames, int maxGameCount) {
        StringBuilder answer = new StringBuilder();

        for (int index = 1; index <= maxGameCount; index++) {
            String player = getPlayerName(playerNames, index);
            String result = do369(index);
            createResult(answer, player, result);
        }

        return answer.toString();
    }

    /**
     * number 에 3,6,9가 들어 있으면 "clap" 을 리턴
     * 그렇지 않으면 숫자를 string으로 변환하여 리턴
     * 메소드 시그니처를 변경하지 마세요.
     */
    private String do369(int number) {
        if (contains369(number)) {
            return "clap";
        } else {
            return String.valueOf(number);
        }
    }

    private String getPlayerName(String[] playerNames, int i) {
        int playerCount = playerNames.length;

        return playerNames[(i - 1) % playerCount];
    }

    // Stream 사용 -> 유지보수성 향상
    private boolean contains369(int number) {
        return String.valueOf(number)
                .chars()
                .mapToObj(ch -> (char) ch)
                .anyMatch(CLAP_DIGITS::contains);
    }

    // String.format 사용 -> 코드의 명확성 향상
    private void createResult(StringBuilder answer, String player, String gameResult) {
        String formattedResult = String.format("%s: %s\n", player, gameResult);
        answer.append(formattedResult);
    }


    public static void main(String[] args) {
        Solution1 solution = new Solution1();

        String[] playerNames = {"1번참", "2번참", "3번참", "4번참", "5번참"};
        int maxGameCount = 122;

        String result = solution.solution(playerNames, maxGameCount);

        System.out.println(result);
    }
}
