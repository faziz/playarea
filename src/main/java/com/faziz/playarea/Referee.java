package com.faziz.playarea;

/**
 *
 * @author faziz
 */
public class Referee {

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
        boolean isFouled = false;
        Cell cellInDirection = player.getCell().getCellInDirection(direction);
        if (cellInDirection == null) {
            isFouled = true;
        } else if (areAdjacentCellsOccupied(cellInDirection)) {
            isFouled = true;
        }

        return isFouled;
    }

    public boolean areAdjacentCellsOccupied(Cell cell) {
        return isAdjacentCellsOccupied(cell.getLeftCell())
                && isAdjacentCellsOccupied(cell.getTopCell())
                && isAdjacentCellsOccupied(cell.getRightCell())
                && isAdjacentCellsOccupied(cell.getBottomCell());

    }

    public boolean isAdjacentCellsOccupied(Cell cell) {
        return (cell == null || null != cell.getPlayer()) ? true : false;
    }

    /**
     * Returns true if the cell in the direction is empty, else false.
     * @param player
     * @param direction
     * @return 
     */
    public boolean moveIsAllowed(Player player, MovementDirection direction) {
        boolean isAllowed = false;
        Cell playerCell = player.getCell();

        Cell cellInDirection = playerCell.getCellInDirection(direction);
        if (cellInDirection == null) {
            isAllowed = false;
        } else if (cellInDirection.isEmpty()) {
            isAllowed = true;
        }

        return isAllowed;
    }

    public void flagPlayer(Player player) {
        playArea.evictPlayer(player);
        player.flag();
    }

    public void requestReturnToPlay(Player player) {
        if (player.isPlayerPermanatlyDisallowed() == false) {
            player.ready();
        }
    }
}
