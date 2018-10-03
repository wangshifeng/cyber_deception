package MainPackage;

import Classes.AccessCall;
import Classes.Criteria;
import Classes.ResourceItem;
import Classes.ResourceType;
import Classes.SysdigRecordObject;
import ControlClasses.GraphObjectHelper;
import ControlClasses.RecordInterpretorFactory;
import DataBaseStuff.DataBaseLayer;
import DataBaseStuff.GraphDBDal;
import DataBaseStuff.SysdigObjectDAL;
import Helpers.BaseMemory;
import Helpers.ColorHelpers;
import Helpers.GraphQueryModel;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import exceptions.HighFieldNumberException;
import exceptions.LowFieldNumberException;

import java.awt.Color;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jfree.ui.RefineryUtilities;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCodeHelper;

public class MainClass {
	private static final boolean IsVerbose = false;

	public static void main(String args[]) throws Exception {
		Graph<ResourceItem, AccessCall> theGraph = new DirectedOrderedSparseMultigraph<ResourceItem, AccessCall>();
		boolean ReadFromFile = false;
		String pid = "";

		boolean SaveToDB = false, SaveToGraph = false, ShowVerbose = false, ShowGraph = false, Neo4JVerbose = false,
				InShortFormat = false, SaveFormated = false;
		String fileAdr = "", output_file = "";
		for (String pick : args) {
			if (pick.equals("file"))
				ReadFromFile = true;
			if (pick.startsWith("\"path") || pick.startsWith("path")) {
				fileAdr = pick.split("=")[1].replace("\"", "");
			}
			if (pick.startsWith("\"outpath") || pick.startsWith("outpath")) {
				output_file = pick.split("=")[1].replace("\"", "");
			}
			if (pick.startsWith("pid")) {
				pid = pick.split("=")[1];
				System.out.println("pid = " + pid);
			}

			if (pick.equals("gv")) {
				ShowVerbose = true;
				// ShowGraph = true;
			}
			if (pick.equals("g"))
				ShowGraph = true;
			if (pick.equals("ssql"))
				SaveToDB = true;
			if (pick.equals("sneo4j"))
				SaveToGraph = true;
			if (pick.equals("neo4jv"))
				Neo4JVerbose = true;
			if (pick.equals("short"))
				InShortFormat = true;
			if (pick.equals("sf"))
				SaveFormated = true;
			if (pick.equals("-h")) {
				System.out.println(" gv: Show Graph in verbose mode \r\n " + " g : show graph in minimized mode \r\n"
						+ "smsql: save to my sql \r\n" + "sneo4j: save to neo4 j data base"
						+ "neo4jv : save neo4j data in verbose  \r\nsv: save in a key value pair format");
				return;
			}
		}

		if (SaveFormated && output_file.isEmpty()) {
			ColorHelpers.PrintRed(
					"to save formated output the outoutfuile has to be supplied! use outpath= key to set the path");
			return;
		}

		ArrayList<SysdigRecordObject> items = new ArrayList<SysdigRecordObject>();
		SysdigObjectDAL temp = new SysdigObjectDAL(InShortFormat, false);
		InputStreamReader isReader = new InputStreamReader(System.in);
		BufferedReader bufReader = new BufferedReader(isReader);

		GraphDBDal GraphActionFactory = new GraphDBDal();

		GraphObjectHelper VerboseHelper = new GraphObjectHelper(true, pid);
		GraphObjectHelper ClearHelper = new GraphObjectHelper(false, pid);

		int counter = 0;
		int inError = 0;
		long counterr = 0;
		int skipped = 0;

		FileWriter output_file_writer = null;
		if (SaveFormated) {
			output_file_writer = new FileWriter(new File(output_file));
		}

		while (true) {
			if (ReadFromFile)
				break;
			try {
				String inputStr = null;
				if ((inputStr = bufReader.readLine()) != null) {
					// System.out.print("+");
					counterr++;
					if (counterr == 1 || counterr == 100000)
						System.out.println(inputStr);
					// if(true) continue;
					SysdigRecordObject tempObj = temp.GetObjectFromTextLine(inputStr);

					/// if desired write the formated file
					if (SaveFormated)
						output_file_writer.write(tempObj.toString() + "\n");

					// items.add(tempObj);
					if (Thread.currentThread().getId() == Long.parseLong(tempObj.thread_tid)) {
						skipped++;
						System.out.println(".");
						continue;
					}

					if (SaveToDB)
						temp.Insert(tempObj);

					if (SaveToGraph)
						GraphActionFactory.Save(tempObj, Neo4JVerbose);

					if (ShowVerbose) {
						VerboseHelper.AddRowToGraph(theGraph, tempObj);
						// VerboseGraphWindow.vv.repaint();
					}
					if (ShowGraph) {
						ClearHelper.AddRowToGraph(theGraph, tempObj);
						// ClearGraphWindow.vv.repaint();
					}

				} else {

					break;

				}
				counter++;
				System.out.print("\033[H\033[2J");
				System.out.print(String.format("%c[%d;%df Total : %d", 0x1B, 15, 25, counter));
				System.out.print(String.format("%c[%d;%df With Error : %d %d", 0x1B, 16, 25, inError,
						Thread.currentThread().getId()));
				System.out.flush();

			} catch (NumberFormatException ex) {
				inError++;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Error");
			}
		}
		Instant start2 = Instant.now();
		if (ReadFromFile) {
			try {
				System.out.println("in read File");
				Scanner test = new Scanner(new File(fileAdr));
				// int counter = 0;
				GraphDBDal db = new GraphDBDal();
				String multipleRecords = "";
				String currentRecord="";
				while (test.hasNextLine()) {
					try {
						SysdigRecordObject tempObj;
						try {
							int theL = multipleRecords.length();
							
							multipleRecords += test.nextLine();
							tempObj = temp.GetObjectFromTextLine(multipleRecords);

							currentRecord = "";
							
							if (SaveFormated)
								output_file_writer.write(tempObj.toString() + "\n");
							if (theL > 1)
								System.out.println("---------------------------------------------");
						} catch (LowFieldNumberException ex) {
							System.out.println(multipleRecords);
							currentRecord = "";
							continue;
						} catch (HighFieldNumberException ex) {
							multipleRecords = "";
							continue;
						}

						multipleRecords = "";
						// SysdigRecordObject tempObj =
						// temp.GetObjectFromTextLine(test.nextLine());
						items.add(tempObj);
						counter++;
						// System.out.println ( "show graph : " + ShowGraph);
						// db.Save(tempObj, true);
						if (SaveToDB)
							temp.Insert(tempObj);

						if (SaveToGraph)
							GraphActionFactory.Save(tempObj, Neo4JVerbose);

						if (ShowVerbose) {
							VerboseHelper.AddRowToGraph(theGraph, tempObj);
							// VerboseHelper.AddRowToGraph(VerboseGraphWindow.graph,
							// tempObj);
							// VerboseGraphWindow.vv.repaint();
						}
						if (ShowGraph) {
							ClearHelper.AddRowToGraph(theGraph, tempObj);
							// ClearGraphWindow.vv.repaint();
						}
						// GraphObjectHelper tempHelper = new
						// GraphObjectHelper(false);
						// tempHelper.AddRowToGraph(VerboseGraphWindow.graph,
						// tempObj);
						// VerboseGraphWindow.vv.repaint();

						if (counter % 1000 == 0) {
							System.out.println(counter);
							// break;
						}

					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}

				}
				db.closeConnections();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		// ClearHelper.release_maps();
		// VerboseHelper.release_maps();

		Instant end2 = Instant.now();

		ColorHelpers.PrintBlue("in : " + Duration.between(start2, end2).toMillis() + "  Milli Seconds \n");
		/// clsoe the output file
		if (output_file_writer != null) {
			output_file_writer.flush();
			output_file_writer.close();
		}
		JFrame frame2 = new JFrame();
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// frame2.setSize(800, 600); // Container content =

		frame2.getContentPane();
		// EdgeLabelDemo VerboseGraphWindow = new EdgeLabelDemo(new
		// ArrayList<ResourceItem>(),
		// new ArrayList<AccessCall>());

		BaseMemory mem = BaseMemory.getSignleton();
		for (ResourceItem pick : theGraph.getVertices()) {
			mem.addResourceItem(pick);
		}
		for (AccessCall pick : theGraph.getEdges()) {
			mem.addAccessCall(pick);
		}

		int num_edges = theGraph.getEdgeCount();
		int num_vertex = theGraph.getVertexCount();

		theGraph = null;

		GraphQueryModel qt = new GraphQueryModel();

		EdgeLabelDemo theGraphWindow = null;
		JFrame frame1 = new JFrame();

		Scanner reader = new Scanner(System.in);
		while (true) {
			try {
				ColorHelpers.PrintBlue("$$>>");
				String command = reader.nextLine();
				if (command.equals("exit()"))
					break;

				else if (command.trim().equalsIgnoreCase("info")) {
					ColorHelpers.PrintGreen(
							String.format("Total Edges : %d \n Total Vertices : %d \r\n", num_edges, num_vertex));
					continue;
				}
				Instant start = Instant.now();

				try {
					theGraph = qt.RunQuety(command, theGraph);
				} catch (Exception ex) {
					ColorHelpers.PrintRed("Error evaluating the query! please check the query and run again.");
					continue;
				}

				Instant end = Instant.now();

				theGraphWindow = new EdgeLabelDemo(theGraph);

				ColorHelpers.PrintBlue("in : " + Duration.between(start, end).toMillis() + "  Milli Seconds \n");

				if (frame1.isVisible()) {
					frame1.setVisible(false);
					frame1.dispose();
				}

				frame1 = new JFrame();
				frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame1.setSize(400, 400);
				if (ShowGraph || ShowVerbose) {
					theGraphWindow.vv.repaint();
					frame1.add(theGraphWindow);
					frame1.setVisible(true);
					frame1.setExtendedState(JFrame.MAXIMIZED_BOTH);
					frame1.setTitle(fileAdr + ": " + pid);
				}

				// System.out.print("\033[H\033[2J >");

				System.out.flush();
			} catch (Exception ex) {
				throw (ex);
				// ColorHelpers.PrintRed("query Problem!please try agin...
				// \r\n");
			}
		}
		GraphActionFactory.closeConnections();
		// System.out.print("\033[H\033[2J");
		ColorHelpers.PrintGreen("\nGood Luck from DroidForensics Team!\r\n");

	}
}
