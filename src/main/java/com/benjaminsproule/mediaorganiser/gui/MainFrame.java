package com.benjaminsproule.mediaorganiser.gui;

import com.benjaminsproule.mediaorganiser.domain.DateConstants;
import com.benjaminsproule.mediaorganiser.service.MediaService;
import com.benjaminsproule.mediaorganiser.service.Progress;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static javax.swing.JOptionPane.showMessageDialog;

@Slf4j
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel inputDirectoryPathLabel;
    private JLabel outputDirectoryPathLabel;
    private JFileChooser inputDirectoryChooser;
    private JFileChooser outputDirectoryChooser;
    private ButtonGroup buttonGroup;
    private ExecutorService executorService;
    private MediaService mediaService;
    private Future<?> organiser;

    public MainFrame() {
        super("Media Organiser");
        executorService = Executors.newFixedThreadPool(2);
        mediaService = new MediaService();
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

        jPanel.add(new JLabel("Files processed"));
        JLabel progress = new JLabel();
        jPanel.add(progress);
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

            List<String> errors = new ArrayList<>();
            organiser = executorService.submit(() -> {
                try {
                    organise.setEnabled(false);
                    errors.addAll(mediaService.organise(inputDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath(), buttonGroup.getSelection().getActionCommand()));
                } catch (Exception e) {
                    showMessageDialog(null, e.getLocalizedMessage());
                }

                organise.setEnabled(true);
                if (errors.isEmpty()) {
                    showMessageDialog(null, "Organised");
                } else {
                    String errorString = "";
                    for (String error : errors) {
                        errorString += "\n" + error;
                    }
                    showMessageDialog(null, "Organised with the following errors:" + errorString);
                }
            });
        });

        executorService.submit(() -> {
            while (true) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    log.error(e.getLocalizedMessage(), e);
                }
                String text = Progress.getNumberOfFilesProcessed() + "/" + Progress.getTotalNumberOfFiles();
                if (Progress.getTotalNumberOfFiles() > 0) {
                    double decimalTotal = (double) Progress.getNumberOfFilesProcessed() / Progress.getTotalNumberOfFiles();
                    text += " (" + (decimalTotal * 100) + "%)";
                } else {
                    text += " (0%)";
                }
                progress.setText(text);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (organiser != null && !organiser.isDone()) {
                    showMessageDialog(null, "Still processing");
                    return;
                }

                executorService.shutdown();
                System.exit(0);
            }
        });

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.pack();
    }
}
