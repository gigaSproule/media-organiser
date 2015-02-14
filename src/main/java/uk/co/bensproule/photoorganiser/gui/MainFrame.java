package uk.co.bensproule.photoorganiser.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel inputFileLabel;
    private JLabel inputFilePathLabel;
    private JLabel outputFileLabel;
    private JLabel outputFilePathLabel;
    private JFileChooser inputFileChooser;
    private JFileChooser outputDirectoryChooser;
    private JButton organise;

    public MainFrame() {
        super("JSON converter");
        initUi();
    }

    private void initUi() {
        GridLayout gridLayout = new GridLayout(5, 3);
        JPanel jPanel = new JPanel(gridLayout);
        this.getContentPane().add(jPanel);

        inputFileLabel = new JLabel();
        inputFileLabel.setText("Input File");
        jPanel.add(inputFileLabel);

        inputFilePathLabel = new JLabel();
        jPanel.add(inputFilePathLabel);

        inputFileChooser = new JFileChooser();

        JButton inputFileButton = new JButton("Input File");
        inputFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = inputFileChooser.showOpenDialog(MainFrame.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = inputFileChooser.getSelectedFile();
                    inputFilePathLabel.setText(file.getName());
                } else {
                    inputFilePathLabel.setText("");
                }
            }
        });
        jPanel.add(inputFileButton);

        outputFileLabel = new JLabel();
        outputFileLabel.setText("Output Directory");
        jPanel.add(outputFileLabel);

        outputFilePathLabel = new JLabel();
        jPanel.add(outputFilePathLabel);

        outputDirectoryChooser = new JFileChooser();
        JButton outputDirectoryButton = new JButton("Output File");
        outputDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = outputDirectoryChooser
                        .showSaveDialog(MainFrame.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = outputDirectoryChooser.getSelectedFile();
                    outputFilePathLabel.setText(file.getName());
                } else {
                    outputFilePathLabel.setText("");
                }
            }
        });
        jPanel.add(outputDirectoryButton);

        jPanel.add(new JLabel("From"));
        jPanel.add(new JLabel());
        JPanel fromJPanel = new JPanel(new GridLayout());
        jPanel.add(fromJPanel);

        jPanel.add(new JLabel("To"));
        jPanel.add(new JLabel());

        JPanel toJPanel = new JPanel(new GridLayout());
        jPanel.add(toJPanel);

        jPanel.add(new JLabel());
        jPanel.add(new JLabel());

        organise = new JButton("Convert");
        organise.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File inputFile = inputFileChooser.getSelectedFile();

                if (inputFile == null) {
                    JOptionPane.showMessageDialog(null,
                            "Please select the input file");
                    return;
                }

                File outputFile = outputDirectoryChooser.getSelectedFile();

                if (outputFile == null) {
                    JOptionPane.showMessageDialog(null,
                            "Please select an output file");
                    return;
                }

                JOptionPane.showMessageDialog(null, "Organised");
            }
        });

        jPanel.add(organise);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.pack();
    }
}
