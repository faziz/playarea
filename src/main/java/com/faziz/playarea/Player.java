package com.faziz.playarea;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.RandomUtils;

/**
 * 
 * @author faziz
 */
public class Player {

    private static final Logger logger = Logger.getLogger(Player.class.getName());
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
     */
    public Player(PlayArea playArea, Referee referee) {
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

    public void ready() {
        logger.log(Level.INFO, "Initializing player.");
        getSet();
    }

    /**
     * Called by the playarea to notify that the move has been made.
     */
    public void moveAccepted() {
        logger.log(Level.INFO, "Player:{0} move accepted.", new Object[]{this});
        getSet();
    }

    /**
     * Called by the playarea to notify that move request has been rejected.
     * @param direction 
     */
    public void rejectMoveRequest(MovementDirection direction) {
        logger.log(Level.INFO, "Player:{0} move in direction: {1} rejected.", new Object[]{this, direction});
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

    /** 
     * Determines player's direction, randomly, 
     * relative to its current location.
     */
    private final MovementDirection getMovementDirection() {
        MovementDirection direction = MovementDirection.LEFT;

        int numberOfDirections = MovementDirection.values().length;
        int randomDirection;

        if (cell.isTopLeft()) {
            randomDirection = generateRandomeExcluding(numberOfDirections, 0, 1);
        } else if (cell.isTopRight()) {
            randomDirection = generateRandomeExcluding(numberOfDirections, 1, 2);
        } else if (cell.isBottomLeft()) {
            randomDirection = generateRandomeExcluding(numberOfDirections, 0, 3);
        } else if (cell.isBottomRight()) {
            randomDirection = generateRandomeExcluding(numberOfDirections, 2, 3);
        } else if (cell.isLeftEdge()) {
            randomDirection = generateRandomeExcluding(numberOfDirections, 0);
        } else if (cell.isTopEdge()) {
            randomDirection = generateRandomeExcluding(numberOfDirections, 1);
        } else if (cell.isRightEdge()) {
            randomDirection = generateRandomeExcluding(numberOfDirections, 2);
        } else if (cell.isBottomEdge()) {
            randomDirection = generateRandomeExcluding(numberOfDirections, 3);
        } else {
            randomDirection = generateRandomeExcluding(numberOfDirections);
        }

        direction = MovementDirection.values()[randomDirection];
        return direction;
    }

    /**
     * WARNING: NOT VERY PRETTY BUT EFFECTIVE FOR NOW.
     * @param max
     * @param exclude
     * @return 
     */
    private final int generateRandomeExcluding(int max, int... exclude) {
        int n = RandomUtils.nextInt(max);
        for (int i : exclude) {
            if (i == n) {
                return generateRandomeExcluding(max, exclude);
            }
        }

        return n;
    }
}
