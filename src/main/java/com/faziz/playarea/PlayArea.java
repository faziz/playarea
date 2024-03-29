package com.faziz.playarea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.RandomUtils;

/**
 * This class acts a coordinator. It coordinates the players movements, as well as
 * foul play.
 * 
 * It uses <code>ArrayBlockingQueue</code> to process players movement requests.
 * 
 * @author faziz
 */
public class PlayArea implements Runnable {

    /** Logger for this class. */
    private static final Logger logger = Logger.getLogger(PlayArea.class.getName());
    private Referee referee = null;
    /** Matrix size of the play area. */
    public static final int MATRIX_SIZE = 100;
    /** Play area matrix. */
    private Cell[][] playAreaCells = new Cell[MATRIX_SIZE][MATRIX_SIZE];

    private PlayArea() {
        initializePlayAreaCells();
    }
    private static PlayArea PLAYAREA = new PlayArea();

    public static PlayArea getInstance() {
        return PLAYAREA;
    }
    
    /** 
     * Currently registered players. Only registered players will be able to make
     * movement requests.
     */
    private Set<Player> registeredPlayers = new HashSet<Player>();
    
    /** 
     * Blocking queue to hold on to the movement requests. Serve the request in 
     * FIFO manner.
     */
    private BlockingQueue<MoveRequest> queue = new ArrayBlockingQueue<MoveRequest>(100, true);

    /**
     * Called by the player to the playarea to make a move request.
     * @param player
     * @param direction 
     */
    public void requestMove(Player player, MovementDirection direction) {
        try {
            if(registeredPlayers.contains(player))
                queue.put(new MoveRequest(player, direction));
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Could not put new request, queue exhausted.", ex);
            throw new IllegalStateException("Could not put new request, queue exhausted.", ex);
        }
    }
    
    public void run() {
        logger.log(Level.INFO, "Handling requests....");
        while (true) {
            try {
                MoveRequest moveRequest = queue.take();                
                Player player = moveRequest.getPlayer();
                MovementDirection direction = moveRequest.getDirection();
                logger.log(Level.INFO, "Move request for player:{0} in direction:{1}", 
                    new Object[]{player, direction});
                
                if (referee.moveIsAllowed(player, direction)) {
                    logger.info("move is allowed.");
                    boolean moveIsFouled = referee.moveIsFouled(player, direction);
                    if (moveIsFouled) {
                        logger.info("move is fouled.");
                        player.flag();
                        
                        if(player.isPlayerToBeRemoved()){
                            referee.flagPlayer(player);
                        }
                    }
                    
                    movePlayer(player, direction);
                    if (registeredPlayers.contains(player) == false && moveIsFouled == false) {
                        registeredPlayers.add(player);
                    }
                } else {
                    player.rejectMoveRequest(direction);                    
                }

                logger.log(Level.INFO, "Players in the system: {0}.", 
                        registeredPlayers.size());
                if (registeredPlayers.size() == 1) {
                    cleanupAndShutdown();                    
                    break;
                }
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, "Could not process requests.", ex);
                throw new IllegalStateException("Could not process requests.", ex);
            }
        }
    }    

    /**
     * Moves the player to the new cell.
     * @param player
     * @param direction 
     */
    private final void movePlayer(Player player, MovementDirection direction) {
        Cell newCell = player.getCell().getCellInDirection(direction);
        newCell.setPlayer(player);

        player.getCell().setPlayer(null);
        player.setCell(newCell);
        player.moveAccepted();
    }

    /**
     * Initializing the matrix with cells.
     */
    private final void initializePlayAreaCells() {
        logger.log(Level.INFO, "Initializing the grid");
        for (int row = 0; row < MATRIX_SIZE; row++) {
            for (int column = 0; column < MATRIX_SIZE; column++) {
                Cell cell = new Cell(row, column, this);
                playAreaCells[row][column] = cell;
            }
        }
    }

    /**
     * Returns cell matching the coordinates. If coordinates are not found, it 
     * returns null.
     * 
     * @param row
     * @param column
     * @return cell if found in the play area matrix or null.
     */
    public final Cell getCell(int row, int column) {
        try {
            return playAreaCells[row][column];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    /**
     * @param referee the referee to set
     */
    public void setReferee(Referee referee) {
        this.referee = referee;
    }

    private void cleanupAndShutdown() {
        ArrayList<MoveRequest> processess = new ArrayList<MoveRequest>();
        queue.drainTo(processess);
        for (MoveRequest moveRequest : processess) {
            Player player = moveRequest.getPlayer();
            player.cleanup();
        }
        Player winningPlayer = registeredPlayers.iterator().next();
        logger.log(Level.INFO, "{0} won!", winningPlayer);
        
        //TODOL: Ugly hack.
        System.exit(0);
    }

    /**
     * Internal wrapper class.
     */
    class MoveRequest {

        private Player player = null;
        private MovementDirection direction = null;

        public MoveRequest(Player player, MovementDirection direction) {
            this.player = player;
            this.direction = direction;
        }

        /**
         * @return the player
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * @return the direction
         */
        public MovementDirection getDirection() {
            return direction;
        }
    }

    /**
     * Tries to fit a player to a randomly selected cell.
     * Recursively calls itself until it finds an empty cell.
     * @param player 
     */
    public void registerPlayer(Player player) {
        int row = RandomUtils.nextInt(MATRIX_SIZE);
        int column = RandomUtils.nextInt(MATRIX_SIZE);

        Cell cell = playAreaCells[row][column];
        if (cell.isOccupied()) {
            registerPlayer(player);
        }

        logger.log(Level.INFO, "Registering the player: {0} with cell: {1}", 
            new Object[]{player, cell});
        cell.setPlayer(player);
        player.setCell(cell);
        registeredPlayers.add(player);
    }
    
    /**
     * Initializes internal infrastructure to accept the player's request.
     */
    public void activate(){
        logger.log(Level.INFO, "Activating playing area.");
        Thread t = new Thread(this);
        t.start();
        logger.log(Level.INFO, "Paying area activated.");
    }

    /**
     * Removes the player from the playing area.
     * @param player 
     */
    public void evictPlayer(Player player) {
        registeredPlayers.remove(player);
        player.getCell().setPlayer(null);
    }
    
    /**
     * Make the registered players to start making movement requests.
     */
    public void go(){
        logger.log(Level.INFO, "Initializing players.");
        for (Player player : registeredPlayers) {
            player.ready();
        }
    }
}
