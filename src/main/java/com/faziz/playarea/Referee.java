package com.faziz.playarea;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author faziz
 */
public class Referee {
    
    private static final Logger logger = Logger.getLogger(Referee.class.getName());

    private PlayArea playArea = null;
    /** Singleton instance of the referee.*/
    private static Referee REFEREE = null;

    private Referee(PlayArea playArea) {
        this.playArea = playArea;
    }

    public synchronized static Referee getInstance(PlayArea playArea) {
        if (REFEREE == null) {
            REFEREE = new Referee(playArea);
        }
        return REFEREE;
    }

    /** 
     * Returns true, if there is another player(s) occupies adjacent cells in 
     * any direction.
     */
    public boolean moveIsFouled(Player player, MovementDirection direction) {
        logger.log(Level.INFO, "Testing if move is fouled for player: "
                + "{0} to direction: {1}.", new Object[]{player, direction});
        boolean isFouled = false;
        
        Cell cellInDirection = player.getCell().getCellInDirection(direction);
        logger.log(Level.INFO, "Player cell: {0}", player.getCell());
        logger.log(Level.INFO, "Player cellInDirection: {0}", cellInDirection);
        
        if (areAdjacentCellsOccupied(cellInDirection)) {
            isFouled = true;
        }

        logger.log(Level.INFO, "isFouled: {0}", isFouled);
        return isFouled;
    }

    public boolean areAdjacentCellsOccupied(Cell cell) {
        return (isAdjacentCellsOccupied(cell.getLeftCell())
                || isAdjacentCellsOccupied(cell.getTopCell())
                || isAdjacentCellsOccupied(cell.getRightCell())
                || isAdjacentCellsOccupied(cell.getBottomCell()))? true: false;

    }

    public boolean isAdjacentCellsOccupied(Cell cell) {
        logger.log(Level.INFO, "isAdjacentCellsOccupied: {0}", cell);
        boolean isAdjacent = false;
        if( cell != null){
            if( null == cell.getPlayer()){
                isAdjacent = true;
            }else{
                isAdjacent = false;
            }
        }
        return isAdjacent;
    }

    /**
     * Returns true if the cell in the direction is empty, else false.
     * @param player
     * @param direction
     * @return 
     */
    public boolean moveIsAllowed(Player player, MovementDirection direction) {
        logger.info("Testing if the move is allowed.");
        
        boolean isAllowed = false;
        Cell playerCell = player.getCell();
        logger.log(Level.INFO, "Player cell: {0}", playerCell);

        Cell cellInDirection = playerCell.getCellInDirection(direction);
        logger.log(Level.INFO, "Player cellInDirection: {0}", cellInDirection);
        
        if (cellInDirection == null) {
            isAllowed = false;
        } else if (cellInDirection.isEmpty()) {
            isAllowed = true;
        }

        logger.log(Level.INFO, "cell is allowed: {0}", isAllowed);
        return isAllowed;
    }

    public void flagPlayer(Player player) {
        playArea.evictPlayer(player);
    }

    public void requestReturnToPlay(Player player) {
        playArea.registerPlayer(player);
        player.ready();
    }
}
