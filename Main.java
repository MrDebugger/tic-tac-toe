import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Math;
import java.util.Scanner;

class History{
	String board_str;
	int player,move;
	public History(String board_str, int player, int move){
		this.board_str = board_str;
		this.player = player;
		this.move = move;
	}
}
class Board{
	ArrayList board;
	ArrayList<History> moveHistory;
	public Board(ArrayList boardValues){
		if (boardValues==null){
			board = new ArrayList();
			for (int i=0;i<9;i++)
				board.add(0);				
		}
		else
			board = (ArrayList) boardValues.clone();
		clearMoveHistory();
	}


	public void clearBoard(){
		for (int i=0;i<9;i++) 
			board.set(i,0);		
		clearMoveHistory();
	}	

	public String display(){
		ArrayList values = new ArrayList();
		values.add("0");
		values.add(" ");
		values.add("X");
		String boardText = "";
		String space = " ";
		String wall = " | ";
		String floor = "-----------\n";
		ArrayList bp1 = new ArrayList();
		for (int i=0; i < board.size();i++ ) 
			bp1.add((int)board.get(i)+1);
		boardText += space + values.get((int)bp1.get(0)) + wall + values.get((int)bp1.get(1)) + wall + values.get((int)bp1.get(2)) + "\n";
		boardText += floor;
		boardText += space + values.get((int)bp1.get(3)) + wall + values.get((int)bp1.get(4)) + wall + values.get((int)bp1.get(5)) + "\n";
		boardText += floor;
		boardText += space + values.get((int)bp1.get(6)) + wall + values.get((int)bp1.get(7)) + wall + values.get((int)bp1.get(8)) + "\n";
		return boardText;
	}

	public ArrayList getMoves(){
		ArrayList moves = new ArrayList();
		for (int i=0; i<9 ;i++ ) 
			if ((int)board.get(i) == 0)
				moves.add(i);
		return moves;
	}

	public int randomMove() {
		ArrayList moves = getMoves();
	    int rnd = new Random().nextInt(moves.size());
	    return (int)moves.get(rnd);
	}

	public void makeMove(int player, int move){
		board.set(move,player);
	}

	public int getWinner(){
		List<int[]> locs = new ArrayList<int[]>();
		locs.add(new int[]{0,1,2});
		locs.add(new int[]{3,4,5});
		locs.add(new int[]{6,7,8});
		locs.add(new int[]{0,3,6});
		locs.add(new int[]{1,4,7});
		locs.add(new int[]{2,5,8});
		locs.add(new int[]{0,4,8});
		locs.add(new int[]{2,4,6});
		for (int[] list: locs ) 
			if (Math.abs((int)board.get(list[0]) + (int)board.get(list[1]) + (int)board.get(list[2]))==3)
				return 	(int)board.get(list[0]);		
		if(board.contains(0))
			return -2;
		else
			return 0;
	}

	public String simpleStr(){
		ArrayList s = new ArrayList();
		s.add("0");
		s.add(" ");
		s.add("X");
		String output = "";
		for(Object space: board)
			output += (String)s.get((int)space+1);
		return output;
	}

	public void appendMoveHistory(String board_str, int player, int move){
		moveHistory.add(new History(board_str,player,move));
	}

	public void clearMoveHistory(){
		moveHistory = new ArrayList<History>();
	}
}
public class Main{

	public static int getPlayer(){
		int[] players = new int[]{-1,1};
		return players[new Random().nextInt(2)];
	}
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		ArrayList boardValues = null;
		boolean humanWins = false;
		Board realboard = new Board(boardValues);
		int player = 1;
		int humanPlayer = getPlayer();
		while(!humanWins){
			realboard.clearBoard();
			for (int i=0; i< 9 ; i++ ) {
				System.out.println(realboard.display());
				int move = -1;
				if (humanPlayer == player){
					ArrayList moves = realboard.getMoves();
					while(!moves.contains(move)){
						System.out.print("Available moves for Stupid Human: ");
						System.out.println(moves);
						try{
							move = in.nextInt();
						}catch(Exception ex){
							continue;
						}
					}
				}
				else
					move = realboard.randomMove();
				realboard.makeMove(player,move);
				int winner = realboard.getWinner();
				if (winner!=-2){
					System.out.println(realboard.display());
					if (winner == humanPlayer){
						System.out.println("You only won because of your poor coding skills. Isolent Human");
						humanWins = true;
					}
					else if(winner == -humanPlayer)
						System.out.println("You lose, pathetic Human");
					else
						System.out.println("You are only Prolonging the inevitible!");
					break;
				}
				player = -player;
			}
			break;
		}
	}
}