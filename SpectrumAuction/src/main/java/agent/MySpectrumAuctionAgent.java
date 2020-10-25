package agent;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.ParseException;

import brown.communication.messages.ITradeMessage;
import brown.simulations.OfflineSimulation;
import brown.system.setup.library.Setup;
import brown.user.agent.IAgent;
import brown.user.agent.library.AbsSpectrumAuctionAgent;
import brown.user.agent.library.OnlineAgentBackend;

public class MySpectrumAuctionAgent extends AbsSpectrumAuctionAgent implements IAgent {
	private final static String NAME = "Agent A+"; // TODO: give your agent a name.

	public MySpectrumAuctionAgent(String name) {
		super(name);
	}

	@Override
	protected void onAuctionStart() {}

	@Override
	protected void onAuctionEnd(Map<Integer, Set<String>> allocations, Map<Integer, Double> payments,
			List<List<ITradeMessage>> tradeHistory) {}

	@Override
	protected Map<String, Double> getNationalBids(Map<String, Double> minBids) {

		Map<String, Double> bids = new HashMap<>();
		
		String[] names =  {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
		
		for(String s : names) {
			if (getValuation(s) + 1.25 >= minBids.get(s)) {
				if(s == "E" | s == "F" | s == "G" | s == "H") {
					bids.put(s, getValuation(s) + 2.5);
				}
				else {
					bids.put(s, getValuation(s) + 1.25);
				}
			}
		}	
		
		return bids;
	}
	
	
	protected Map<String, Double> getBestBundle(Map<String, Double> minBids, String[] names) {
		int[][] combos = {{1, 2, 3, 4}, {1, 2, 3, 5}, {1, 2, 3, 6}, {1, 2, 4, 5}, {1, 2, 4, 6}, {1, 2, 5, 6}, 
				{1, 3, 4, 5}, {1, 3, 4, 6}, {1, 3, 6, 6}, {1, 4, 5, 6}, {2, 3, 4, 5}, {2, 3, 4, 6}, {2, 3, 5, 6},
				{2, 4, 5, 6}, {3, 4, 5, 6}};
		
		double max_profit = 0;
		Map<String, Double> best_bids = new HashMap<>();
		
		Set<String> curr_all = getTentativeAllocation();
		for(String name: curr_all) {
			max_profit += (getValuation(name) - minBids.get(name) + 2.7);
			best_bids.put(name, getValuation(name) + 1.25);
		}
		
		for(int[] combo : combos) {
			double cur_profit = 0;
			Map<String, Double> bids = new HashMap<>();
			String[] cur_names = {names[combo[0] - 1], names[combo[1] - 1], names[combo[2] - 1], names[combo[3] - 1]};
			for(String name : cur_names) {
				cur_profit += (getValuation(name) - minBids.get(name));
				bids.put(name, getValuation(name) + 1.25);
			}
			if (cur_profit > max_profit && isValidBidBundle(bids, minBids, false)) {
				best_bids = bids;
				max_profit = cur_profit;
			}
		}
		
		return best_bids;
	}

	
	@Override
	protected Map<String, Double> getRegionalBids(Map<String, Double> minBids) {
		// TODO: fill this in

		Map<String, Double> bids = new HashMap<>();
		
		int my_position = getBidderPosition();
		
		if (my_position == 1) {
			String[] names = {"A", "B", "C", "D", "M", "N"};
			
			if (getCurrentRound() == 0) {
			
			String lowest = names[0];
			double val_lowest = getValuation(lowest);
			String second_lowest = names[1];
			double val_second_lowest = getValuation(second_lowest);
			
			if (val_second_lowest < val_lowest) {
				lowest = names[1];
				second_lowest = names[0];
				val_lowest = getValuation(lowest);
				val_second_lowest = getValuation(second_lowest);
			}
			
			if (1.1*getValuation("C") < val_lowest) {
				lowest = "C";
				val_lowest = 1.1*getValuation("C");
			}
			else if (1.1*getValuation("C") < val_second_lowest) {
				second_lowest = "C";
				val_second_lowest = 1.1*getValuation("C");
			}
			
			if (1.1*getValuation("D") < val_lowest) {
				lowest = "D";
				val_lowest = 1.1*getValuation("D");
			}
			else if (1.1*getValuation("D") < val_second_lowest) {
				second_lowest = "D";
				val_second_lowest = 1.1*getValuation("D");
			}
			
			if (1.2*getValuation("M") < getValuation(lowest)) {
				lowest = "M";
				val_lowest = 1.2*getValuation("M");
			}
			else if (1.2*getValuation("M") < val_second_lowest) {
				second_lowest = "M";
				val_second_lowest = 1.2*getValuation("M");
			}
			
			if (1.3*getValuation("N") < getValuation(lowest)) {
				lowest = "N";
				val_lowest = 1.3*getValuation("N");
			}
			else if (1.3*getValuation("N") < val_second_lowest) {
				second_lowest = "N";
				val_second_lowest = 1.3*getValuation("N");
			}

			String[] to_bid = new String[4];
			int place = 0;
			for (int i = 0; i < 6; i ++) {
				if (names[i] != lowest && names[i] != second_lowest) {
					to_bid[place] = names[i];
					place ++;
				}
			}
			
			for(String s : to_bid) {
				if (getValuation(s) + 1.25 >= minBids.get(s)) {
					bids.put(s, getValuation(s) + 1.25);
				}
			}
			}
			else {
				return getBestBundle(minBids, names);
			}
				
		}
		else if (my_position == 2) {
			String[] names = {"E", "F", "C", "D", "N", "O"};
			
			if (getCurrentRound() == 0) {

			String lowest = names[0];
			double val_lowest = getValuation(lowest);
			String second_lowest = names[1];
			double val_second_lowest = getValuation(second_lowest);
			
			if (val_second_lowest < val_lowest) {
				lowest = names[1];
				second_lowest = names[0];
				val_lowest = getValuation(lowest);
				val_second_lowest = getValuation(second_lowest);
			}
			
			if (1.2*getValuation("C") < val_lowest) {
				lowest = "C";
				val_lowest = 1.2*getValuation("C");
			}
			else if (1.2*getValuation("C") < val_second_lowest) {
				second_lowest = "C";
				val_second_lowest = 1.2*getValuation("C");
			}
			
			if (1.2*getValuation("D") < val_lowest) {
				lowest = "D";
				val_lowest = 1.2*getValuation("D");
			}
			else if (1.2*getValuation("D") < val_second_lowest) {
				second_lowest = "D";
				val_second_lowest = 1.2*getValuation("D");
			}
			
			if (1.5*getValuation("O") < getValuation(lowest)) {
				lowest = "O";
				val_lowest = 1.5*getValuation("O");
			}
			else if (1.5*getValuation("O") < val_second_lowest) {
				second_lowest = "O";
				val_second_lowest = 1.5*getValuation("O");
			}
			
			if (1.3*getValuation("N") < getValuation(lowest)) {
				lowest = "N";
				val_lowest = 1.3*getValuation("N");
			}
			else if (1.3*getValuation("N") < val_second_lowest) {
				second_lowest = "N";
				val_second_lowest = 1.3*getValuation("N");
			}

			String[] to_bid = new String[4];
			int place = 0;
			for (int i = 0; i < 6; i ++) {
				if (names[i] != lowest && names[i] != second_lowest) {
					to_bid[place] = names[i];
					place ++;
				}
			}
			
			for(String s : to_bid) {
				if (getValuation(s) + 1.25 >= minBids.get(s)) {
					bids.put(s, getValuation(s) + 1.25);
				}
			}
			}
			else {
				return getBestBundle(minBids, names);
			}
		}
		else if (my_position == 3) {
			String[] names = {"E", "F", "G", "H", "O", "P"};
			
			if (getCurrentRound() == 0) {
			
			String lowest = names[0];
			double val_lowest = getValuation(lowest);
			String second_lowest = names[1];
			double val_second_lowest = getValuation(second_lowest);
			
			if (val_second_lowest < val_lowest) {
				lowest = names[1];
				second_lowest = names[0];
				val_lowest = getValuation(lowest);
				val_second_lowest = getValuation(second_lowest);
			}
			
			if (getValuation("G") < val_lowest) {
				lowest = "G";
				val_lowest = getValuation("G");
			}
			else if (getValuation("G") < val_second_lowest) {
				second_lowest = "G";
				val_second_lowest = getValuation("G");
			}
			
			if (getValuation("H") < val_lowest) {
				lowest = "H";
				val_lowest = getValuation("H");
			}
			else if (getValuation("H") < val_second_lowest) {
				second_lowest = "H";
				val_second_lowest = getValuation("H");
			}
			
			if (1.5*getValuation("O") < getValuation(lowest)) {
				lowest = "O";
				val_lowest = 1.5*getValuation("O");
			}
			else if (1.5*getValuation("O") < val_second_lowest) {
				second_lowest = "O";
				val_second_lowest = 1.5*getValuation("O");
			}
			
			if (1.5*getValuation("P") < getValuation(lowest)) {
				lowest = "P";
				val_lowest = 1.5*getValuation("P");
			}
			else if (1.5*getValuation("P") < val_second_lowest) {
				second_lowest = "P";
				val_second_lowest = 1.5*getValuation("P");
			}

			String[] to_bid = new String[4];
			int place = 0;
			for (int i = 0; i < 6; i ++) {
				if (names[i] != lowest && names[i] != second_lowest) {
					to_bid[place] = names[i];
					place ++;
				}
			}
			
			for(String s : to_bid) {
				if (getValuation(s) + 1.25 >= minBids.get(s)) {
					bids.put(s, getValuation(s) + 1.25);
				}
			}
			}
			else {
				return getBestBundle(minBids, names);
			}
		}
		else if (my_position == 4) {
			String[] names = {"G", "H", "I", "J", "P", "Q"};
			
			if (getCurrentRound() == 0) {
			
			String lowest = names[0];
			double val_lowest = getValuation(lowest);
			String second_lowest = names[1];
			double val_second_lowest = getValuation(second_lowest);
			
			if (val_second_lowest < val_lowest) {
				lowest = names[1];
				second_lowest = names[0];
				val_lowest = getValuation(lowest);
				val_second_lowest = getValuation(second_lowest);
			}
			
			if (1.2*getValuation("I") < val_lowest) {
				lowest = "I";
				val_lowest = 1.2*getValuation("I");
			}
			else if (1.2*getValuation("I") < val_second_lowest) {
				second_lowest = "I";
				val_second_lowest = 1.2*getValuation("I");
			}
			
			if (1.2*getValuation("J") < val_lowest) {
				lowest = "J";
				val_lowest = 1.2*getValuation("J");
			}
			else if (1.2*getValuation("J") < val_second_lowest) {
				second_lowest = "J";
				val_second_lowest = 1.2*getValuation("J");
			}
			
			if (1.5*getValuation("P") < getValuation(lowest)) {
				lowest = "P";
				val_lowest = 1.5*getValuation("P");
			}
			else if (1.5*getValuation("P") < val_second_lowest) {
				second_lowest = "P";
				val_second_lowest = 1.5*getValuation("P");
			}
			
			if (1.3*getValuation("Q") < getValuation(lowest)) {
				lowest = "Q";
				val_lowest = 1.3*getValuation("Q");
			}
			else if (1.3*getValuation("Q") < val_second_lowest) {
				second_lowest = "Q";
				val_second_lowest = 1.3*getValuation("Q");
			}

			String[] to_bid = new String[4];
			int place = 0;
			for (int i = 0; i < 6; i ++) {
				if (names[i] != lowest && names[i] != second_lowest) {
					to_bid[place] = names[i];
					place ++;
				}
			}
			
			for(String s : to_bid) {
				if (getValuation(s) + 1.25 >= minBids.get(s)) {
					bids.put(s, getValuation(s) + 1.25);
				}
			}
			}
			else {
				return getBestBundle(minBids, names);
			}
		}
		else if (my_position == 5) {
			String[] names = {"K", "L", "I", "J", "Q", "R"};
			
			if (getCurrentRound() == 0) {
			
			String lowest = names[0];
			double val_lowest = getValuation(lowest);
			String second_lowest = names[1];
			double val_second_lowest = getValuation(second_lowest);
			
			if (val_second_lowest < val_lowest) {
				lowest = names[1];
				second_lowest = names[0];
				val_lowest = getValuation(lowest);
				val_second_lowest = getValuation(second_lowest);
			}
			
			if (1.1*getValuation("I") < val_lowest) {
				lowest = "I";
				val_lowest = 1.1*getValuation("I");
			}
			else if (1.1*getValuation("I") < val_second_lowest) {
				second_lowest = "I";
				val_second_lowest = 1.1*getValuation("I");
			}
			
			if (1.1*getValuation("J") < val_lowest) {
				lowest = "J";
				val_lowest = 1.1*getValuation("J");
			}
			else if (1.1*getValuation("J") < val_second_lowest) {
				second_lowest = "J";
				val_second_lowest = 1.1*getValuation("J");
			}
			
			if (1.3*getValuation("Q") < getValuation(lowest)) {
				lowest = "Q";
				val_lowest = 1.3*getValuation("Q");
			}
			else if (1.3*getValuation("Q") < val_second_lowest) {
				second_lowest = "Q";
				val_second_lowest = 1.3*getValuation("Q");
			}
			
			if (1.2*getValuation("R") < getValuation(lowest)) {
				lowest = "R";
				val_lowest = 1.2*getValuation("R");
			}
			else if (1.2*getValuation("R") < val_second_lowest) {
				second_lowest = "R";
				val_second_lowest = 1.2*getValuation("R");
			}

			String[] to_bid = new String[4];
			int place = 0;
			for (int i = 0; i < 6; i ++) {
				if (names[i] != lowest && names[i] != second_lowest) {
					to_bid[place] = names[i];
					place ++;
				}
			}
			
			for(String s : to_bid) {
				if (getValuation(s) + 1.25 >= minBids.get(s)) {
					bids.put(s, getValuation(s) + 1.25);
				}
			}
			}
			else {
				return getBestBundle(minBids, names);
			}
		}
		else {
			String[] names = {"A", "B", "K", "L", "M", "R"};
			
			if (getCurrentRound() == 0) {
			
			String lowest = names[0];
			double val_lowest = getValuation(lowest);
			String second_lowest = names[1];
			double val_second_lowest = getValuation(second_lowest);
			
			if (val_second_lowest < val_lowest) {
				lowest = names[1];
				second_lowest = names[0];
				val_lowest = getValuation(lowest);
				val_second_lowest = getValuation(second_lowest);
			}
			
			if (getValuation("K") < val_lowest) {
				lowest = "K";
				val_lowest = getValuation("K");
			}
			else if (getValuation("K") < val_second_lowest) {
				second_lowest = "K";
				val_second_lowest = getValuation("K");
			}
			
			if (getValuation("L") < val_lowest) {
				lowest = "L";
				val_lowest = getValuation("L");
			}
			else if (getValuation("L") < val_second_lowest) {
				second_lowest = "L";
				val_second_lowest = getValuation("L");
			}
			
			if (1.2*getValuation("M") < getValuation(lowest)) {
				lowest = "M";
				val_lowest = 1.2*getValuation("M");
			}
			else if (1.2*getValuation("M") < val_second_lowest) {
				second_lowest = "M";
				val_second_lowest = 1.2*getValuation("M");
			}
			
			if (1.2*getValuation("R") < getValuation(lowest)) {
				lowest = "R";
				val_lowest = 1.2*getValuation("R");
			}
			else if (1.2*getValuation("R") < val_second_lowest) {
				second_lowest = "R";
				val_second_lowest = 1.2*getValuation("R");
			}

			String[] to_bid = new String[4];
			int place = 0;
			for (int i = 0; i < 6; i ++) {
				if (names[i] != lowest && names[i] != second_lowest) {
					to_bid[place] = names[i];
					place ++;
				}
			}
			
			for(String s : to_bid) {
				if (getValuation(s) + 1.25 >= minBids.get(s)) {
					bids.put(s, getValuation(s) + 1.25);
				}
			}
			}
			else {
				return getBestBundle(minBids, names);
			}
		}

		return bids;
	}

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 0) {
			// Don't change this.
			new OfflineSimulation("offline_test_config.json", "input_configs/gsvm_smra_offline.json", "outfile", false).run();
		} else {
			// Don't change this.
			MySpectrumAuctionAgent agent = new MySpectrumAuctionAgent(NAME);
			agent.addAgentBackend(new OnlineAgentBackend("localhost", Integer.parseInt(args[0]), new Setup(), agent));
			while (true) {
			}
		}
	}

}
