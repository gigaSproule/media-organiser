package uk.co.bensproule.photoorganiser.gui;

import org.apache.commons.imaging.ImageReadException;
import uk.co.bensproule.photoorganiser.service.PhotoService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static javax.swing.JOptionPane.showMessageDialog;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel inputDirectoryLabel;
    private JLabel inputDirectoryPathLabel;
    private JLabel outputDirectoryLabel;
    private JLabel outputDirectoryPathLabel;
    private JFileChooser inputDirectoryChooser;
    private JFileChooser outputDirectoryChooser;
    private JButton organise;

    public MainFrame() {
        super("Photo Organiser");
        initUi();
    }

    private void initUi() {
        GridLayout gridLayout = new GridLayout(5, 3);
        JPanel jPanel = new JPanel(gridLayout);
        this.getContentPane().add(jPanel);

        inputDirectoryLabel = new JLabel();
        inputDirectoryLabel.setText("Input Directory");
        jPanel.add(inputDirectoryLabel);

        inputDirectoryPathLabel = new JLabel();
        jPanel.add(inputDirectoryPathLabel);

        inputDirectoryChooser = new JFileChooser();
        inputDirectoryChooser.setFileSelectionMode(DIRECTORIES_ONLY);

        JButton inputFileButton = new JButton("Input Directory");
        inputFileButton.addActionListener(e -> {
            int returnVal = inputDirectoryChooser.showOpenDialog(MainFrame.this);

            if (returnVal == APPROVE_OPTION) {
                File file = inputDirectoryChooser.getSelectedFile();
                inputDirectoryPathLabel.setText(file.getName());
            } else {
                inputDirectoryPathLabel.setText("");
            }
        });
        jPanel.add(inputFileButton);

        outputDirectoryLabel = new JLabel();
        outputDirectoryLabel.setText("Output Directory");
        jPanel.add(outputDirectoryLabel);

        outputDirectoryPathLabel = new JLabel();
        jPanel.add(outputDirectoryPathLabel);

        outputDirectoryChooser = new JFileChooser();
        outputDirectoryChooser.setFileSelectionMode(DIRECTORIES_ONLY);

        JButton outputDirectoryButton = new JButton("Output Directory");
        outputDirectoryButton.addActionListener(e -> {
            int returnVal = outputDirectoryChooser
                    .showSaveDialog(MainFrame.this);

            if (returnVal == APPROVE_OPTION) {
                File file = outputDirectoryChooser.getSelectedFile();
                outputDirectoryPathLabel.setText(file.getName());
            } else {
                outputDirectoryPathLabel.setText("");
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
        organise.addActionListener(e -> {
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
                photoService.organise(inputDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath());
            } catch (IOException | ImageReadException ex) {
                ex.printStackTrace();
            }

            showMessageDialog(null, "Organised");
        });

        jPanel.add(organise);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.pack();
    }
}
