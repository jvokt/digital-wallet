package antifraud;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Antifraud {

	/**
	 * The main method first reads the batch input file line by line and
	 * adds the transaction to the initial state. Then for each line of the
	 * stream input file it evaluates the features, writes the results to the
	 * appropriate output files, and adds the new transaction to the current
	 * state.
	 * @param args
	 * args[0] is the name of the batch input file,
	 * args[1] is the name of the stream input file,
	 * args[2] is the name of the output file for feature 1
	 * args[3] is the name of the output file for feature 2
	 * args[4] is the name of the output file for feature 3
	 */
	public static void main(String[] args) {
		if (args.length != 5) {
			System.out.println("Need exactly 5 arguments");
		}
		BufferedReader br = null;
		BufferedWriter bw1 = null, bw2 = null, bw3 = null;
		String input_batch = args[0];
		String input_stream = args[1];
		String output1 = args[2];
		String output2 = args[3];
		String output3 = args[4];
		Graph graph = new Graph();
		try {
			String line;
			br = new BufferedReader(new FileReader(input_batch));
			line = br.readLine();
			System.out.println("Reading batch input file");
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(",");
				if (tokens.length >= 3)
					graph.addEdge(tokens[1], tokens[2]);
			}
			br.close();
			graph.processNeighbors();
			br = new BufferedReader(new FileReader(input_stream));
			bw1 = new BufferedWriter(new FileWriter(output1));
			bw2 = new BufferedWriter(new FileWriter(output2));
			bw3 = new BufferedWriter(new FileWriter(output3));
			line = br.readLine();
			System.out.println("Reading stream input file");
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(",");
				if (tokens.length >= 3) {
					bw1.write(graph.getFeature1Output(tokens[1], tokens[2]));
					bw1.newLine();
					bw2.write(graph.getFeature2Output(tokens[1], tokens[2]));
					bw2.newLine();
					bw3.write(graph.getFeature3Output(tokens[1], tokens[2]));
					bw3.newLine();
					graph.addEdge(tokens[1], tokens[2]);
					graph.updateNeighbors(tokens[1], tokens[2]);
				}
			}
			br.close();
			bw1.close();
			bw2.close();
			bw3.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
