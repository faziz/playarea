package com.faziz.playarea;

/**
 * A particular location on the play area.
 * 
 * @author faziz
 */
public class Cell {

    private Cell leftCell = null;
    private Cell topCell = null;
    private Cell rightCell = null;
    private Cell bottomCell = null;
    
    private Player player = null;
    private int row;
    private int column;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
    
    public boolean isTopLeft(){
        return (row == 0 && column == 0) ? true: false;
    }
    public boolean isTopRight(){
        return (row == 0 && column == PlayArea.MATRIX_SIZE - 1) ? true: false;
    }
    public boolean isBottomLeft(){
        return (PlayArea.MATRIX_SIZE - 1 == 0 && column == 0) ? true: false;
    }
    public boolean isBottomRight(){
        return (PlayArea.MATRIX_SIZE - 1 == 0 && PlayArea.MATRIX_SIZE - 1 == 0) ? true: false;
    }

    /**
     * @return the leftCell
     */
    public Cell getLeftCell() {
        return leftCell;
    }

    /**
     * @return the topCell
     */
    public Cell getTopCell() {
        return topCell;
    }

    /**
     * @return the rightCell
     */
    public Cell getRightCell() {
        return rightCell;
    }

    /**
     * @return the bottomCell
     */
    public Cell getBottomCell() {
        return bottomCell;
    }

    /**
     * @param player the player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @param leftCell the leftCell to set
     */
    public void setLeftCell(Cell leftCell) {
        this.leftCell = leftCell;
    }

    /**
     * @param topCell the topCell to set
     */
    public void setTopCell(Cell topCell) {
        this.topCell = topCell;
    }

    /**
     * @param rightCell the rightCell to set
     */
    public void setRightCell(Cell rightCell) {
        this.rightCell = rightCell;
    }

    /**
     * @param bottomCell the bottomCell to set
     */
    public void setBottomCell(Cell bottomCell) {
        this.bottomCell = bottomCell;
    }
    
    public boolean isOccupied(){
        return (player == null) ? false: true;
    }
    
    public boolean isEmpty(){
        return !isOccupied();
    }
    
    public Cell getCellInDirection(MovementDirection direction){
        Cell cell = null;
        
        if( MovementDirection.LEFT == direction){
            cell = getLeftCell();
        }
        if( MovementDirection.TOP == direction){
            cell = getTopCell();
        }
        if( MovementDirection.RIGHT == direction){
            cell = getRightCell();
        }
        if( MovementDirection.BOTTOM == direction){
            cell = getBottomCell();
        }
        
        return cell;
    }
}
