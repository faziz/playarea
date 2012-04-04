package com.faziz.playarea.util;

import com.faziz.playarea.PlayArea;
import com.faziz.playarea.Player;
import com.faziz.playarea.Referee;

/**
 *
 * @author faziz
 */
public class Main {

    private static final int NUMBER_OF_PLAYERS = 10;

    public static void main(String[] args) {
        PlayArea playArea = PlayArea.getInstance();
        Referee referee = Referee.getInstance(playArea);
        playArea.setReferee(referee);

        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            Player player = new Player(playArea, referee, "Jhon" + i);
            playArea.registerPlayer(player);
        }

        playArea.activate();
        playArea.initializePlayers();
    }
}
