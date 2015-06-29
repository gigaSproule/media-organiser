package com.benjaminsproule.photoorganiser.gui;

import com.benjaminsproule.photoorganiser.domain.DateConstants;
import com.benjaminsproule.photoorganiser.service.PhotoService;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static javax.swing.JOptionPane.showMessageDialog;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel inputDirectoryPathLabel;
    private JLabel outputDirectoryPathLabel;
    private JFileChooser inputDirectoryChooser;
    private JFileChooser outputDirectoryChooser;
    private ButtonGroup buttonGroup;

    public MainFrame() {
        super("Photo Organiser");
        initUi();
    }

    private void initUi() {
        GridLayout gridLayout = new GridLayout(6, 3);
        JPanel jPanel = new JPanel(gridLayout);
        this.getContentPane().add(jPanel);

        JLabel inputDirectoryLabel = new JLabel();
        inputDirectoryLabel.setText("Input Directory");
        jPanel.add(inputDirectoryLabel);

        inputDirectoryPathLabel = new JLabel();
        jPanel.add(inputDirectoryPathLabel);

        inputDirectoryChooser = new JFileChooser();
        inputDirectoryChooser.setFileSelectionMode(DIRECTORIES_ONLY);

        JButton inputFileButton = new JButton("Input Directory");
        jPanel.add(inputFileButton);

        inputFileButton.addActionListener(event -> {
            int returnVal = inputDirectoryChooser.showOpenDialog(MainFrame.this);

            if (returnVal == APPROVE_OPTION) {
                File file = inputDirectoryChooser.getSelectedFile();
                inputDirectoryPathLabel.setText(file.getAbsolutePath());
            } else {
                inputDirectoryPathLabel.setText("");
            }
        });

        JLabel outputDirectoryLabel = new JLabel();
        outputDirectoryLabel.setText("Output Directory");
        jPanel.add(outputDirectoryLabel);

        outputDirectoryPathLabel = new JLabel();
        jPanel.add(outputDirectoryPathLabel);

        outputDirectoryChooser = new JFileChooser();
        outputDirectoryChooser.setFileSelectionMode(DIRECTORIES_ONLY);

        JButton outputDirectoryButton = new JButton("Output Directory");
        jPanel.add(outputDirectoryButton);

        outputDirectoryButton.addActionListener(event -> {
            int returnVal = outputDirectoryChooser
                .showSaveDialog(MainFrame.this);

            if (returnVal == APPROVE_OPTION) {
                File file = outputDirectoryChooser.getSelectedFile();
                outputDirectoryPathLabel.setText(file.getAbsolutePath());
            } else {
                outputDirectoryPathLabel.setText("");
            }
        });

        buttonGroup = new ButtonGroup();
        JRadioButton numberFormat = new JRadioButton(DateConstants.YYYY_MM_DD);
        numberFormat.setActionCommand(DateConstants.YYYY_MM_DD);
        numberFormat.setSelected(true);
        JRadioButton textFormat = new JRadioButton(DateConstants.YYYY_MMMM_DD);
        textFormat.setActionCommand(DateConstants.YYYY_MMMM_DD);
        JRadioButton numberTextFormat = new JRadioButton(DateConstants.YYYY_MM_MMMM_DD);
        numberTextFormat.setActionCommand(DateConstants.YYYY_MM_MMMM_DD);

        buttonGroup.add(numberFormat);
        buttonGroup.add(textFormat);
        buttonGroup.add(numberTextFormat);

        jPanel.add(new JLabel());
        jPanel.add(new JLabel());
        jPanel.add(numberFormat);

        jPanel.add(new JLabel("Formatting"));
        jPanel.add(new JLabel());
        jPanel.add(textFormat);

        jPanel.add(new JLabel());
        jPanel.add(new JLabel());
        jPanel.add(numberTextFormat);

        jPanel.add(new JLabel());
        jPanel.add(new JLabel());
        JButton organise = new JButton("Organise");
        jPanel.add(organise);

        organise.addActionListener(event -> {
            File inputDirectory = inputDirectoryChooser.getSelectedFile();

            if (inputDirectory == null) {
                showMessageDialog(null, "Please select the input directory");
                return;
            }

            File outputDirectory = outputDirectoryChooser.getSelectedFile();

            if (outputDirectory == null) {
                showMessageDialog(null, "Please select an output directory");
                return;
            }

            PhotoService photoService = new PhotoService();
            try {
                photoService.organise(inputDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath(), buttonGroup.getSelection().getActionCommand());
            } catch (Exception e) {
                showMessageDialog(null, e.getLocalizedMessage());
            }

            showMessageDialog(null, "Organised");
        });

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.pack();
    }
}
