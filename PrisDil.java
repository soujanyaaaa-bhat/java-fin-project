/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
/**
 *
 * @author soujanyabhat
 */

//creating an abstract class called player that initially has score 0
//using an arraylist to track the moves of the user for game
abstract class Player {
    protected int score = 0;
    protected List<String> movesHistory = new ArrayList<>(); 

    public Player() {}

    public abstract String makeMove(String lastOpponentMove);
    
    public int getScore() {
        return score;
    }

    public List<String> getMovesHistory() {
        return movesHistory;
    }

    //game logic where score is updated based on the score 
    public void updateScore(String playerMove, String opponentMove) {
        if (playerMove.equals("Cooperate") && opponentMove.equals("Cooperate")) {
            score += 3;
        } else if (playerMove.equals("Defect") && opponentMove.equals("Defect")) {
            score += 1;
        } else if (playerMove.equals("Cooperate") && opponentMove.equals("Defect")) {
            score += 0;
        } else if (playerMove.equals("Defect") && opponentMove.equals("Cooperate")) {
            score += 5;
        }
        movesHistory.add(playerMove); //adds to the playermove
    }

    public abstract void reset();
}
//allwoing human player to make choice using scanner class
class HumanPlayer extends Player {

    public HumanPlayer() {
        super();
    }

    @Override
    //input validation anything other than 1 or 2 is cleared
public String makeMove(String lastOpponentMove) {
    Scanner sc = new Scanner(System.in);
    int choice = 0;
    while (true) {
        System.out.println("Choose your move: (1) Cooperate, (2) Defect");
        if (sc.hasNextInt()) {
            choice = sc.nextInt();
            if (choice == 1 || choice == 2) {
                break;
            }
        } else {
            sc.next(); // Clear the invalid input
        }
        System.out.println("Invalid choice. Please enter 1 or 2.");
    }
    return choice == 1 ? "Cooperate" : "Defect";
}
    @Override
    public void reset() {
        score = 0;
        movesHistory.clear(); //reset
    }
}
//
class AIPlayer extends Player {
    private final String strategy;

    public AIPlayer(String strategy) {
        super();
        this.strategy = strategy;
    }
//ai playing different strategies
    //determines moves of player. uses strategy based on player 
    @Override
    public String makeMove(String lastOpponentMove) {
        //cooperate, defect: same, titfortat: change
        if (null != strategy) switch (strategy) {
            case "AlwaysCooperate" -> {
                return "Cooperate";
            }
            case "AlwaysDefect" -> {
                return "Defect";
            }
            case "TitForTat" -> {
                return lastOpponentMove != null ? lastOpponentMove : "Cooperate";
            
}
            //default-cooperate
            default -> {
            }
        }
        return "Cooperate";
    }

    @Override
    public void reset() {
        score = 0;
        movesHistory.clear(); //resets the moves history
    }
}


//using runnable interface where human, and ai moves are made.
class Game implements Runnable {
    private Player humanPlayer;
    private Player aiPlayer;
    private int rounds;
    private Connection connection;

    public Game(Player human, Player ai, int rounds, Connection connection) {
        this.humanPlayer = human;
        this.aiPlayer = ai;
        this.rounds = rounds;
        this.connection = connection;
    }

    @Override
    public void run() {
        String lastHumanMove = null;
        String lastAIMove = null;

        for (int i = 0; i < rounds; i++) {
            String humanMove = humanPlayer.makeMove(lastAIMove);
            String aiMove = aiPlayer.makeMove(lastHumanMove);

            humanPlayer.updateScore(humanMove, aiMove);
            aiPlayer.updateScore(aiMove, humanMove);

            lastHumanMove = humanMove;
            lastAIMove = aiMove;

            System.out.println("Round " + (i + 1) + " - Human: " + humanMove + ", AI: " + aiMove);
        }

        System.out.println("Game over! Final Scores - Human: " + humanPlayer.getScore() + ", AI: " + aiPlayer.getScore());

        // Store scores in the database
        storeScores(humanPlayer.getScore(), aiPlayer.getScore());
    }


    private void storeScores(int humanScore, int aiScore) {
        try {
            String sql = "INSERT INTO game_scores (human_score, ai_score) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, humanScore);
            pstmt.setInt(2, aiScore);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
//main class that creates players (both human and ai)
class PrisonersDilemmaGame {
    public static void main(String[] args) {
        try {
            //database connection so scores are stored in the system db
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dilemma_db", "root", "ribstyles@67");
            //creating human and ai player
            Player human = new HumanPlayer();
            Player ai = new AIPlayer("TitForTat");

            //game being setup using threads, 5 rounds are played, scores sent to db
            Game game = new Game(human, ai, 5, conn); 
            Thread gameThread = new Thread(game);
            gameThread.start();

            gameThread.join(); //thread waiting to finish
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

