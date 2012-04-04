package com.faziz.playarea;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author faziz
 */
public class Player {

    /** Delay between each request in player's movement.*/
    private static final long MOVEMENT_REQUEST_DELAY = 1000l;
    /** Delay in the call to the referee for reintroduction.*/
    private static final long REINTRODUCTION_REQUEST_DELAY = 1000l * 10;
    /** 
     * Foul flag count for the player. If the count is reaches 2, player will be
     * permanently removed from the play.
     */
    protected int flagCount = 0;
    /** Current cell of the player. */
    private Cell cell = null;
    /** Playarea used to me movement request.*/
    private PlayArea playArea = null;
    /** Referee instance used to allow the user to return. */
    private Referee referee = null;

    /**
     * Constructor to initialize the player.
     * @param playArea
     * @param referee
     * @param cell 
     */
    public Player(PlayArea playArea, Referee referee, Cell cell) {
        this.cell = cell;
        this.playArea = playArea;
        this.referee = referee;
    }
    /** Timer used to move the player in the player area.*/
    private Timer movementTimer = null;
    /** Timer used to request the referee to allow player's return.*/
    private Timer requestReturnTimer = null;

    /** Called by the referee. Increases the number fouls committed by the player.*/
    public void flag() {
        flagCount++;

        if (flagCount == 2) {
            requestRefereeForReintroduction();
        }
    }

    /**
     * 
     */
    private final void requestRefereeForReintroduction() {
        requestReturnTimer = new Timer();
        requestReturnTimer.schedule(new RequestReintoductionTimer(this, referee),
                REINTRODUCTION_REQUEST_DELAY);
    }

    /** Checks whether player can be permanently removed from the play.*/
    public boolean isPlayerPermanatlyDisallowed() {
        if (flagCount == 2) {
            return true;
        } else {
            return false;
        }
    }

    /** Initializes the movement requests. Called by the playarea and referee. */
    private void getSet() {
        MovementDirection movementDirection = getMovementDirection();
        movementTimer = new Timer();
        movementTimer.schedule(
                new MovementRequestTimer(this, movementDirection),
                MOVEMENT_REQUEST_DELAY);
    }
    
    public void ready(){
        getSet();
    }
    
    /**
     * Called by the playarea to notify that the move has been made.
     */
    public void moveAccepted(){
        getSet();
    }
    
    /**
     * Called by the playarea to notify that move request has been rejected.
     * @param direction 
     */
    public void rejectMoveRequest(MovementDirection direction){
        getSet();
    }

    /**
     * @param cell the cell to set
     */
    public void setCell(Cell cell) {
        this.cell = cell;
    }

    /**
     * @return the cell
     */
    public Cell getCell() {
        return cell;
    }

    class MovementRequestTimer extends TimerTask {

        MovementDirection direction = null;
        Player player = null;

        public MovementRequestTimer(Player player, MovementDirection direction) {
            this.player = player;
            this.direction = direction;
        }

        @Override
        public void run() {
            playArea.requestMove(player, direction);
        }
    }

    class RequestReintoductionTimer extends TimerTask {

        Referee referee = null;
        Player player = null;

        public RequestReintoductionTimer(Player player, Referee referee) {
            this.player = player;
            this.referee = referee;
        }

        @Override
        public void run() {
            referee.requestReturnToPlay(player);
        }
    }

    /** Determines player's direction, randomly, relative to its current location.*/
    private final MovementDirection getMovementDirection() {
        //TODO: Implement me.
        return MovementDirection.LEFT;
    }
}
