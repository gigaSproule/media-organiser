package com.benjaminsproule.mediaorganiser.gui;

import com.benjaminsproule.mediaorganiser.dao.MediaDao;
import com.benjaminsproule.mediaorganiser.domain.DateConstants;
import com.benjaminsproule.mediaorganiser.domain.Progress;
import com.benjaminsproule.mediaorganiser.service.MediaService;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
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
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    private JLabel inputDirectoryPathLabel;
    private JLabel outputDirectoryPathLabel;
    private JFileChooser inputDirectoryChooser;
    private JFileChooser outputDirectoryChooser;
    private ButtonGroup buttonGroup;
    private final ExecutorService executorService;
    private final MediaService mediaService;
    private Future<?> organiser;

    public MainFrame() {
        super("Media Organiser");
        executorService = Executors.newFixedThreadPool(2);
        mediaService = new MediaService(new MediaDao());
        initUi();
    }

    private void initUi() {
        GridLayout gridLayout = new GridLayout(7, 3);
        JPanel jPanel = new JPanel(gridLayout);
        this.getContentPane().add(jPanel);

        JLabel inputDirectoryLabel = new JLabel();
        inputDirectoryLabel.setText("Input Directory");
        jPanel.add(inputDirectoryLabel);

        inputDirectoryPathLabel = new JLabel();
        jPanel.add(inputDirectoryPathLabel);

        inputDirectoryChooser = new JFileChooser();
        inputDirectoryChooser.setFileSelectionMode(DIRECTORIES_ONLY);

        JButton inputDirectoryButton = new JButton("Input Directory");
        jPanel.add(inputDirectoryButton);

        inputDirectoryButton.addActionListener(event -> {
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
            int returnVal = outputDirectoryChooser.showSaveDialog(MainFrame.this);

            if (returnVal == APPROVE_OPTION) {
                File file = outputDirectoryChooser.getSelectedFile();
                outputDirectoryPathLabel.setText(file.getAbsolutePath());
            } else {
                outputDirectoryPathLabel.setText("");
            }
        });

        buttonGroup = new ButtonGroup();
        JRadioButton yearMonthFormat = new JRadioButton(DateConstants.YYYY_MM);
        yearMonthFormat.setActionCommand(DateConstants.YYYY_MM);
        yearMonthFormat.setSelected(true);
        JRadioButton numberYearMonthDayFormat = new JRadioButton(DateConstants.YYYY_MM_DD);
        numberYearMonthDayFormat.setActionCommand(DateConstants.YYYY_MM_DD);
        JRadioButton textYearMonthDateFormat = new JRadioButton(DateConstants.YYYY_MMMM_DD);
        textYearMonthDateFormat.setActionCommand(DateConstants.YYYY_MMMM_DD);
        JRadioButton numberTextYearMonthDayFormat = new JRadioButton(DateConstants.YYYY_MM_MMMM_DD);
        numberTextYearMonthDayFormat.setActionCommand(DateConstants.YYYY_MM_MMMM_DD);

        buttonGroup.add(yearMonthFormat);
        buttonGroup.add(numberYearMonthDayFormat);
        buttonGroup.add(textYearMonthDateFormat);
        buttonGroup.add(numberTextYearMonthDayFormat);

        jPanel.add(new JLabel("Formatting"));
        jPanel.add(new JLabel());
        jPanel.add(yearMonthFormat);

        jPanel.add(new JLabel());
        jPanel.add(new JLabel());
        jPanel.add(numberYearMonthDayFormat);

        jPanel.add(new JLabel());
        jPanel.add(new JLabel());
        jPanel.add(textYearMonthDateFormat);

        jPanel.add(new JLabel());
        jPanel.add(new JLabel());
        jPanel.add(numberTextYearMonthDayFormat);

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
                    inputDirectoryButton.setEnabled(false);
                    outputDirectoryButton.setEnabled(false);
                    yearMonthFormat.setEnabled(false);
                    numberYearMonthDayFormat.setEnabled(false);
                    textYearMonthDateFormat.setEnabled(false);
                    numberTextYearMonthDayFormat.setEnabled(false);
                    organise.setEnabled(false);
                    String format = buttonGroup.getSelection().getActionCommand();

                    List<String> errorMessages = mediaService.organise(inputDirectory.getAbsolutePath(),
                        outputDirectory.getAbsolutePath(), format);
                    errors.addAll(errorMessages);
                } catch (Exception e) {
                    showMessageDialog(null, e.getLocalizedMessage());
                }

                inputDirectoryButton.setEnabled(true);
                outputDirectoryButton.setEnabled(true);
                yearMonthFormat.setEnabled(true);
                numberYearMonthDayFormat.setEnabled(true);
                textYearMonthDateFormat.setEnabled(true);
                numberTextYearMonthDayFormat.setEnabled(true);
                organise.setEnabled(true);
                if (errors.isEmpty()) {
                    showMessageDialog(null, "Organised");
                } else {
                    StringBuilder errorString = new StringBuilder();
                    for (String error : errors) {
                        errorString.append("\n").append(error);
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
                    text += " (" + (decimalFormat.format(decimalTotal * 100)) + "%)";
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
