import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Math;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.parser.JSONParser;

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
		for (int[] loc: locs ) 
			if (Math.abs((int)board.get(loc[0]) + (int)board.get(loc[1]) + (int)board.get(loc[2]))==3)
				return 	(int)board.get(loc[0]);		
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
			output += (String) s.get((int)space+1);
		return output;
	}

	public void appendMoveHistory(String board_str, int player, int move){
		moveHistory.add(new History(board_str,player,move));
	}

	public void clearMoveHistory(){
		moveHistory = new ArrayList<History>();
	}
}

class TicTacToeSearch{
	JSONObject ttt_memo;
	JSONObject str_moves;
	public TicTacToeSearch(){
		ttt_memo = new JSONObject();
		str_moves = new JSONObject();
		str_moves.put(1,"X");
		str_moves.put(0,"Nobody");
		str_moves.put(-1,"O");
	}
	public void saveMemo() throws IOException{
		try (FileWriter file = new FileWriter("ttt_tree.json")) {
			file.write(ttt_memo.toJSONString());
		}
	}

	public void loadMemo(){
		try{
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("ttt_tree.json"));
			ttt_memo = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public int sum(ArrayList list){
		int s = 0;
		for(Object i : list)
		    s += (int)i;
		return s;
	}
	public int count(String str, char c){
		int co = 0;
		for (int i = 0; i < str.length(); i++) 
		    if (str.charAt(i) == c) 
		        co++;
		return co;
	}
	public void buildMemo(){
		ArrayList values = null;
		Board board = new Board(values);
		ArrayList<Board> stack = new ArrayList<Board>();
		stack.add(board);
		while(!stack.isEmpty()){
			Board currentBoard = stack.get(stack.size() - 1);
			stack.remove(stack.size() - 1);
			if(!ttt_memo.keySet().contains(currentBoard.simpleStr())){
				int winner = currentBoard.getWinner();
				JSONObject obj1 = new JSONObject();
				obj1.put("winner",winner);
				obj1.put("best_moves",new JSONArray());
				ttt_memo.put(currentBoard.simpleStr(),obj1);
				if (winner!=-2) 
					continue;
				for (int i=0;i<9 ;i++ ) 
					if ((int)currentBoard.board.get(i)==0) {
						if(sum(currentBoard.board) == 0){
							Board newBoard = new Board(currentBoard.board);
							newBoard.board.set(i,1);
							stack.add(newBoard);
						}
						else{
							Board newBoard = new Board(currentBoard.board);
							newBoard.board.set(i,-1);
							stack.add(newBoard);
						}
				}
			}
		}
		for (int turn=9;turn>=0 ;turn-- ) {
			int player;
			if (turn%2==0) 
				player = 1;
			else
				player = -1;
			for (Iterator iterator = ttt_memo.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next(); 
				JSONObject value = (JSONObject) ttt_memo.get(key);
				int checkTurn = 9 - count(key,' ');
				if(checkTurn != turn || (int)value.get("winner")!=-2)
					continue;
				JSONArray moves2win = new JSONArray();
				JSONArray moves2draw = new JSONArray();
				JSONArray moves2lose = new JSONArray();
				for(int i=0;i<9;i++){
					int nextWinner;
					if (key.charAt(i) != ' ') 
						continue;
					else{
						String nextKey = key.substring(0,i) + str_moves.get(player) + key.substring(i+1);
						nextWinner = (int)((JSONObject)ttt_memo.get(nextKey)).get("winner");
					}
					if(nextWinner == player)
						moves2win.add(i);
					else if(nextWinner == 0)
						moves2draw.add(i);
					else
						moves2lose.add(i);
				}
				if (moves2win.size()>0) {
					((JSONObject)ttt_memo.get(key)).put("winner",player);
					((JSONObject)ttt_memo.get(key)).put("best_moves",moves2win);
				}
				else if (moves2draw.size()>0) {
					((JSONObject)ttt_memo.get(key)).put("winner",0);
					((JSONObject)ttt_memo.get(key)).put("best_moves",moves2draw);
				}
				else{
					((JSONObject)ttt_memo.get(key)).put("winner",-player);
					((JSONObject)ttt_memo.get(key)).put("best_moves",moves2lose);
				}
			}
		}
	}

	public JSONArray getBestMoves(String board_str){
		return (JSONArray)((JSONObject)ttt_memo.get(board_str)).get("best_moves");
	}
	public int getBestMove(String board_str){
		JSONArray moves = getBestMoves(board_str);
	    int rnd = new Random().nextInt(moves.size());
	    return (int) moves.get(rnd);
	}
}

public class AI{

	public static int getPlayer(){
		int[] players = new int[]{-1,1};
		return players[new Random().nextInt(2)];
	}
	public static void main(String[] args) throws IOException {
		TicTacToeSearch s = new TicTacToeSearch();
		s.buildMemo();
		s.saveMemo();
		s.loadMemo();
	}
}