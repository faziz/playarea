package com.faziz.playarea;

/**
 *
 * @author faziz
 */
public class Referee {
    
    /** Singleton instance of the referee.*/
    private static Referee REFEREE = new Referee();
    
    private Referee(){
    }
    
    public static Referee getInstance(){
        return REFEREE;
    }
    
    /** 
     * Returns true, if there is another player(s) occupies adjacent cells in 
     * any direction.
     */
    public boolean moveIsFouled(Player player, MovementDirection direction){
        boolean isFouled = false;
        Cell playerCell = player.getCell();
        
        Cell cellInDirection = playerCell.getCellInDirection(direction);
        if( cellInDirection == null)
            isFouled = false;
        else if( cellInDirection.isEmpty()){
            isFouled = true;
        }
        
        return isFouled;
    }
    
    /**
     * Returns true if the cell in the direction is empty, else false.
     * @param player
     * @param direction
     * @return 
     */
    public boolean moveIsAllowed(Player player, MovementDirection direction){
        boolean isAllowed = false;
        Cell playerCell = player.getCell();
        
        Cell cellInDirection = playerCell.getCellInDirection(direction);
        if(cellInDirection == null)
            isAllowed = false;
        else if(cellInDirection.isEmpty()){
            isAllowed = true;
        }
        
        return isAllowed;
    }
    
    public void flagPlayer(Player player){
        player.flag();
    }
    
    public void requestReturnToPlay(Player player){
        if(player.isPlayerPermanatlyDisallowed() == false){
            player.ready();
        }
    }
}
