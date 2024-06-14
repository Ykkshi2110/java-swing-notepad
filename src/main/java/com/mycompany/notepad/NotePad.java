package com.mycompany.notepad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class NotePad extends JFrame implements ActionListener, WindowListener {
    
    JTabbedPane tabbedPane = new JTabbedPane();
    private int fontSize = 15;
    private int searchIndex = 0;
    public int status_save = 0;
    
    // Map to store file paths for each tab
    public Map<Integer, String> tabFilePaths = new HashMap<>();

    public NotePad() {
        Font fnt = new Font("Arial", Font.PLAIN, fontSize);
        Container con = getContentPane();
        JMenuBar jmb = new JMenuBar();
        JMenu jmfile = new JMenu("File");
        JMenu jmedit = new JMenu("Edit");
        JMenu jmview = new JMenu("View");
        JMenu jmhelp = new JMenu("Help");

        con.setLayout(new BorderLayout());

        addNewTab("Untitled.txt", fnt);

        con.add(tabbedPane);

        createMenuItem(jmfile, "New Tab");
        createMenuItem(jmfile, "Open");
        createMenuItem(jmfile, "Save");
        createMenuItem(jmfile, "Save As");
        jmfile.addSeparator();
        createMenuItem(jmfile, "Exit");

        createMenuItem(jmedit, "Cut");
        createMenuItem(jmedit, "Copy");
        createMenuItem(jmedit, "Paste");
        createMenuItem(jmedit, "Find");
        createMenuItem(jmedit, "Replace");

        createMenuItem(jmview, "Zoom In");
        createMenuItem(jmview, "Zoom Out");

        createMenuItem(jmhelp, "About Notepad");

        jmb.add(jmfile);
        jmb.add(jmedit);
        jmb.add(jmview);
        jmb.add(jmhelp);

        setJMenuBar(jmb);

        setIconImage(Toolkit.getDefaultToolkit().getImage("notepad.gif"));
        addWindowListener(this);
        setSize(500, 500);
        setTitle("Untitled.txt - NotePad");
        setVisible(true);
    }

    void addNewTab(String title, Font font) {
        JScrollPane newTab = createNewTab(font);
        tabbedPane.addTab(title, newTab);
        int index = tabbedPane.indexOfComponent(newTab);
        tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane, this));
        tabbedPane.setSelectedIndex(index);
    }

    private JScrollPane createNewTab(Font font) {
        JTextArea jta = new JTextArea();
        jta.setFont(font);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        JScrollPane sbrText = new JScrollPane(jta);
        sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sbrText.setVisible(true);
        return sbrText;
    }

    public void createMenuItem(JMenu jm, String txt) {
        JMenuItem jmi = new JMenuItem(txt);
        jmi.addActionListener(this);
        jm.add(jmi);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        JScrollPane currentScrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        JTextArea jta = (JTextArea) currentScrollPane.getViewport().getView();
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (e.getActionCommand().equals("New Tab")) {
            addNewTab("Untitled.txt", new Font("Arial", Font.PLAIN, fontSize));
        } else if (e.getActionCommand().equals("Open")) {
            int ret = jfc.showDialog(null, "Open");
            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    File fyl = jfc.getSelectedFile();
                    OpenFile(fyl.getAbsolutePath(), jta);
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), fyl.getName());
                    tabFilePaths.put(selectedIndex, fyl.getAbsolutePath());
                    setTitle(fyl.getName() + " - NotePad");
                    searchIndex = 0;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getActionCommand().equals("Save")) {
            saveTab();
            status_save = 1;
        } else if (e.getActionCommand().equals("Save As")) {
            int ret = jfc.showDialog(null, "Save As");
            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    File fyl = jfc.getSelectedFile();
                    SaveFile(fyl.getAbsolutePath(), jta);
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), fyl.getName());
                    tabFilePaths.put(selectedIndex, fyl.getAbsolutePath());
                    setTitle(fyl.getName() + " - NotePad");
                    status_save = 1;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getActionCommand().equals("Exit")) {
            Exiting();
        } else if (e.getActionCommand().equals("Cut")) {
            jta.cut();
        } else if (e.getActionCommand().equals("Copy")) {
            jta.copy();
        } else if (e.getActionCommand().equals("Paste")) {
            jta.paste();
        } else if (e.getActionCommand().equals("Find")) {
            String find = JOptionPane.showInputDialog(this, "Find:");
            if (find != null && !find.isEmpty()) {
                String text = jta.getText();
                searchIndex = text.indexOf(find, searchIndex);
                if (searchIndex != -1) {
                    jta.setCaretPosition(searchIndex);
                    jta.select(searchIndex, searchIndex + find.length());
                    jta.grabFocus();
                    searchIndex += find.length();
                    int option = JOptionPane.showConfirmDialog(this, "Find Next?", "Find", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        searchIndex = text.indexOf(find, searchIndex);
                        while (searchIndex != -1) {
                            jta.setCaretPosition(searchIndex);
                            jta.select(searchIndex, searchIndex + find.length());
                            jta.grabFocus();
                            searchIndex += find.length();
                            option = JOptionPane.showConfirmDialog(this, "Find Next?", "Find", JOptionPane.YES_NO_OPTION);
                            if (option != JOptionPane.YES_OPTION) {
                                break;
                            }
                            searchIndex = text.indexOf(find, searchIndex);
                        }
                        if (searchIndex == -1) {
                            JOptionPane.showMessageDialog(this, "Text not found!", "Find", JOptionPane.INFORMATION_MESSAGE);
                            searchIndex = 0;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Text not found!", "Find", JOptionPane.INFORMATION_MESSAGE);
                    searchIndex = 0;
                }
            }
        } else if (e.getActionCommand().equals("Replace")) {
            JPanel panel = new JPanel(new GridLayout(2, 2));
            JTextField findField = new JTextField(10);
            JTextField replaceField = new JTextField(10);
            panel.add(new JLabel("Find:"));
            panel.add(findField);
            panel.add(new JLabel("Replace with:"));
            panel.add(replaceField);
            int option = JOptionPane.showConfirmDialog(this, panel, "Replace", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                String find = findField.getText();
                String replace = replaceField.getText();
                if (!find.isEmpty()) {
                    String text = jta.getText();
                    jta.setText(text.replace(find, replace));
                }
            }
        } else if (e.getActionCommand().equals("Zoom In")) {
            fontSize += 2;
            jta.setFont(new Font("Arial", Font.PLAIN, fontSize));
        } else if (e.getActionCommand().equals("Zoom Out")) {
            if (fontSize > 8) {
                fontSize -= 2;
                jta.setFont(new Font("Arial", Font.PLAIN, fontSize));
            }
        } else if (e.getActionCommand().equals("About Notepad")) {
            JOptionPane.showMessageDialog(this, "created by 'Ba chàng lính ngự lâm'", "Notepad", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveTab() {
        JScrollPane currentScrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        JTextArea jta = (JTextArea) currentScrollPane.getViewport().getView();
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0) {
            String filePath = tabFilePaths.get(selectedIndex);
            if (filePath != null && !filePath.isEmpty()) {
                try {
                    SaveFile(filePath, jta);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                // If the tab has not been saved before, show the save dialog
                JFileChooser jfc = new JFileChooser();
                int ret = jfc.showDialog(null, "Save");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        File fyl = jfc.getSelectedFile();
                        SaveFile(fyl.getAbsolutePath(), jta);
                        tabbedPane.setTitleAt(selectedIndex, fyl.getName());
                        tabFilePaths.put(selectedIndex, fyl.getAbsolutePath());
                        setTitle(fyl.getName() + " - NotePad");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void OpenFile(String fname, JTextArea jta) throws IOException {
        try (BufferedReader d = new BufferedReader(new InputStreamReader(new FileInputStream(fname)))) {
            String l;
            jta.setText("");
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            while ((l = d.readLine()) != null) {
                jta.append(l + "\n");
            }
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        searchIndex = 0;
    }

    public boolean SaveFile(String fname, JTextArea jta) throws IOException {
        System.out.println("Saving file: " + fname);
        try (BufferedWriter out = new BufferedWriter(new FileWriter(fname))) {
            out.write(jta.getText());
        }
        System.out.println("File saved successfully.");
        return true;
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        Exiting();
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    public void Exiting() {
        System.exit(0);
    }
}
