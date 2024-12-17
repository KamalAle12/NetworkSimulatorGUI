package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NetworkSimulatorGUI extends JPanel {
    
    // Array to store the network (maximum jump length for each device)
    private int[] network;
    
    // Variable to store the furthest reachable index
    private int reachable;
    
    // Constructor to initialize the network
    public NetworkSimulatorGUI(int[] network) {
        this.network = network;
        this.reachable = 0; // Initially, we can only reach the first device
        
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.white);
    }
    
    // Function to draw the network and visualize the jumps
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int width = getWidth();
        int height = getHeight();
        
        // Calculate the positions of each device (circle)
        int radius = 30;  // Radius of each device (circle)
        int gap = (width - 2 * radius) / network.length;
        
        // Draw circles (devices)
        for (int i = 0; i < network.length; i++) {
            g.setColor(Color.blue);
            g.fillOval(radius + i * gap, height / 2 - radius, 2 * radius, 2 * radius);
            g.setColor(Color.white);
            g.drawString(Integer.toString(network[i]), radius + i * gap + radius - 5, height / 2);
        }
        
        // Visualize possible jumps by drawing lines
        for (int i = 0; i < network.length; i++) {
            int jump = network[i];
            for (int j = i + 1; j <= i + jump && j < network.length; j++) {
                g.setColor(Color.green);
                g.drawLine(radius + i * gap + radius, height / 2, radius + j * gap + radius, height / 2);
            }
        }
        
        // Draw the reachable area
        g.setColor(Color.red);
        g.fillOval(radius + reachable * gap, height / 2 - radius, 2 * radius, 2 * radius);
        
        // If last device is reachable, show a success message
        if (reachable >= network.length - 1) {
            g.setColor(Color.black);
            g.drawString("Success: Reachable to last device!", 20, height - 50);
        }
    }
    
    // Function to check if data can reach the last device
    public boolean canTransmit() {
        reachable = 0;  // Reset the reachable position
        
        // Simulate the jump and check if we can reach the last device
        for (int i = 0; i < network.length; i++) {
            if (reachable < i) {
                return false;
            }
            reachable = Math.max(reachable, i + network[i]);
            if (reachable >= network.length - 1) {
                return true;
            }
        }
        return false;
    }
    
    // Method to get user input for the network array
    public static int[] getUserInput() {
        String input = JOptionPane.showInputDialog("Enter the network array values separated by commas (e.g. 2,3,1,1,4):");
        
        if (input != null && !input.isEmpty()) {
            String[] inputArray = input.split(",");
            int[] network = new int[inputArray.length];
            
            for (int i = 0; i < inputArray.length; i++) {
                try {
                    network[i] = Integer.parseInt(inputArray[i].trim());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter only integers.");
                    return null;
                }
            }
            return network;
        }
        
        // Return an empty array if input is invalid or cancelled
        return new int[0];
    }

    // Main method to run the application
    public static void main(String[] args) {
        // Get user input for the network configuration
        int[] network = getUserInput();
        
        // If user input is invalid, exit the program
        if (network.length == 0) {
            JOptionPane.showMessageDialog(null, "Invalid network configuration. Exiting.");
            System.exit(0);
        }

        // Create a JFrame for the GUI
        JFrame frame = new JFrame("Network Routing Simulator");
        NetworkSimulatorGUI panel = new NetworkSimulatorGUI(network);
        
        // Create sliders for each device to adjust the jump length dynamically
        JPanel controlsPanel = new JPanel(new GridLayout(network.length, 1));
        for (int i = 0; i < network.length; i++) {
            final int deviceIndex = i;
            JSlider slider = new JSlider(0, 10, network[i]);
            slider.setMajorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.addChangeListener(e -> {
                network[deviceIndex] = slider.getValue();
                panel.repaint(); // Update visualization when slider changes
            });
            controlsPanel.add(new JLabel("Device " + (i + 1) + " Jump Length:"));
            controlsPanel.add(slider);
        }
        
        // Set up the JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(controlsPanel, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
        
        // Check if transmission is possible and update the visualization
        if (panel.canTransmit()) {
            JOptionPane.showMessageDialog(frame, "Yes, data can be transmitted to the last device.");
        } else {
            JOptionPane.showMessageDialog(frame, "No, data cannot be transmitted to the last device.");
        }
    }
}
