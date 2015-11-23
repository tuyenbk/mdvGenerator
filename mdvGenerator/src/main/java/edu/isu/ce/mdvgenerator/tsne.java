package edu.isu.ce.mdvgenerator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.swing.JFrame;

import org.math.plot.FrameView;
import org.math.plot.Plot2DPanel;
import org.math.plot.PlotPanel;
import org.math.plot.plots.ColoredScatterPlot;
import org.math.plot.plots.IconScatterPlot;
import org.math.plot.plots.ScatterPlot;

import com.jujutsu.tsne.FastTSne;
import com.jujutsu.tsne.MatrixOps;
import com.jujutsu.tsne.PrincipalComponentAnalysis;
import com.jujutsu.tsne.TSne;
import com.jujutsu.utils.*;
public class tsne {
	private static int initial_dims = 400;
	static double perplexity = 100;
	public static void main(String [] args) {
	    int initial_dims = 55;
	    double perplexity = 20.0;
	    double [][] X = MatrixUtils.simpleRead2DMatrix(new File("src/main/resources/datasets/iris_X.txt"), ",");
	    System.out.println(MatrixOps.doubleArrayToPrintString(X, ", ", 50,10));
	    TSne tsne = new FastTSne();
	    double [][] Y = tsne.tsne(X, 2, initial_dims, perplexity);   
	    
	    // Plot Y or save Y to file and plot with some other tool such as for instance R
	    plotIris(Y);
	    
	    
	    
	  }
    public static void fast_tsne(String filename, String labelfilename) {
    	TSne tsne = new FastTSne();
    	int iters = 1000;
    	System.out.println("Running " + iters + " iterations of TSne on " + filename);
        double [][] X = MatrixUtils.simpleRead2DMatrix(new File(filename), " ");
    	String [] labels = MatrixUtils.simpleReadLines(new File(labelfilename));
    	for (int i = 0; i < labels.length; i++) {
			labels[i] = labels[i].trim().substring(0, 1);
		}
        System.out.println("Shape is: " + X.length + " x " + X[0].length);
        System.out.println("Starting TSNE: " + new Date());
        double [][] Y = tsne.tsne(X, 2, initial_dims, perplexity, iters);
        System.out.println("Finished TSNE: " + new Date());
        //System.out.println("Result is = " + Y.length + " x " + Y[0].length + " => \n" + MatrixOps.doubleArrayToString(Y));
        System.out.println("Result is = " + Y.length + " x " + Y[0].length);
        saveFile(new File("Java-tsne-result.txt"), MatrixOps.doubleArrayToString(Y));
        Plot2DPanel plot = new Plot2DPanel();
        
        ColoredScatterPlot setosaPlot = new ColoredScatterPlot("setosa", Y, labels);
        plot.plotCanvas.setNotable(true);
        plot.plotCanvas.setNoteCoords(true);
        plot.plotCanvas.addPlot(setosaPlot);
                
        FrameView plotframe = new FrameView(plot);
        plotframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        plotframe.setVisible(true);
    }
	
	public static void pca_mnist() {
		double [][] X = MatrixUtils.simpleRead2DMatrix(new File("src/main/resources/datasets/selected_term_vectors.txt"));
    	String [] labels = MatrixUtils.simpleReadLines(new File("src/main/resources/datasets/selected_term.txt"));
    	for (int i = 0; i < labels.length; i++) {
			labels[i] = labels[i].trim();//.substring(0, 2);
		}
        PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
    	double [][] Y = pca.pca(X,2);
        Plot2DPanel plot = new Plot2DPanel();
        
        ColoredScatterPlot setosaPlot = new ColoredScatterPlot("setosa", Y, labels);
        plot.plotCanvas.setNotable(true);
        plot.plotCanvas.setNoteCoords(true);
        plot.plotCanvas.addPlot(setosaPlot);
                
        FrameView plotframe = new FrameView(plot);
        plotframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        plotframe.setVisible(true);
    }
	
	static void plotIris(double[][] Y) {
		double [][]        setosa = new double[initial_dims][2];
        String []     setosaNames = new String[initial_dims];
        double [][]    versicolor = new double[initial_dims][2];
        String [] versicolorNames = new String[initial_dims];
        double [][]     virginica = new double[initial_dims][2];
        String []  virginicaNames = new String[initial_dims];
        
        int cnt = 0;
        for (int i = 0; i < initial_dims; i++, cnt++) {
        	for (int j = 0; j < 2; j++) {
            	setosa[i][j] = Y[cnt][j];
            	setosaNames[i] = "setosa";
			}
        }
        for (int i = 0; i < initial_dims; i++, cnt++) {
        	for (int j = 0; j < 2; j++) {
        		versicolor[i][j] = Y[cnt][j];
        		versicolorNames[i] = "versicolor";
			}
        }
        for (int i = 0; i < initial_dims; i++, cnt++) {
        	for (int j = 0; j < 2; j++) {
        		virginica[i][j] = Y[cnt][j];
        		virginicaNames[i] = "virginica";
			}
        }
        
        Plot2DPanel plot = new Plot2DPanel();
        
        ScatterPlot setosaPlot = new ScatterPlot("setosa", PlotPanel.COLORLIST[0], setosa);
        setosaPlot.setTags(setosaNames);
        
        ScatterPlot versicolorPlot = new ScatterPlot("versicolor", PlotPanel.COLORLIST[1], versicolor);
        versicolorPlot.setTags(versicolorNames);
        ScatterPlot virginicaPlot = new ScatterPlot("versicolor", PlotPanel.COLORLIST[2], virginica);
        virginicaPlot.setTags(virginicaNames);
        
        plot.plotCanvas.setNotable(true);
        plot.plotCanvas.setNoteCoords(true);
        plot.plotCanvas.addPlot(setosaPlot);
        plot.plotCanvas.addPlot(versicolorPlot);
        plot.plotCanvas.addPlot(virginicaPlot);
        
        //int setosaId = plot.addScatterPlot("setosa", setosa);
        //int versicolorId = plot.addScatterPlot("versicolor", versicolor);
        //int virginicaId = plot.addScatterPlot("virginica", virginica);
        
        FrameView plotframe = new FrameView(plot);
        plotframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        plotframe.setVisible(true);
	}
	public static void saveFile(File file, String text) {
		saveFile(file,text,false);
	}
	
	public static void saveFile(File file, String text, boolean append) {
        try (FileWriter fw = new FileWriter(file, append);
            BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(text);
            bw.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
