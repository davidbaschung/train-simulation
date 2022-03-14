/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jean
 */
public class TrainLogPanel extends javax.swing.JPanel {

    // Variables declaration - do not modify
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;

    /**
     * Creates new form TrainLogPanel
     */
    public TrainLogPanel() {
        initComponents();
    }

    private void initComponents() {

        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();

        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        logTextArea.setFont(new Font("Tahoma", Font.PLAIN, 11));

        logScrollPane.setViewportView(logTextArea);

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        this.setBorder(BorderFactory.createLineBorder(Color.blue));
        GridBagConstraints c = new GridBagConstraints();
        this.setMinimumSize(new Dimension(150, 200));

        c.insets = new Insets(0, 3, 0, 3);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 1;
        c.weighty = 1;

        logScrollPane.setBorder(BorderFactory.createLineBorder(Color.red));
        this.add(logScrollPane, c);
    }
    // End of variables declaration

    /**
     * This method appends the data to the text area.
     *
     * @param data the Logging information data
     */
    public void showInfo(String data) {

        logTextArea.append("> ");
        logTextArea.append(data);
        logTextArea.append("\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());

    }
}
