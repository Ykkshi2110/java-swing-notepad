package com.mycompany.notepad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
//import javax.swing.filechooser.FileNameExtensionFilter;

public final class NotePad extends JFrame implements ActionListener, WindowListener {

    JTextArea jta = new JTextArea();
    File fnameContainer;
    private int fontSize = 15;
    private int searchIndex = 0; // Thêm biến để theo dõi vị trí tìm kiếm hiện tại

    public NotePad() {
        Font fnt = new Font("Arial", Font.PLAIN, fontSize);
        Container con = getContentPane();
        JMenuBar jmb = new JMenuBar();
        JMenu jmfile = new JMenu("File");
        JMenu jmedit = new JMenu("Edit");
        JMenu jmview = new JMenu("View");
        JMenu jmhelp = new JMenu("Help");

        con.setLayout(new BorderLayout());
        JScrollPane sbrText = new JScrollPane(jta);
        sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sbrText.setVisible(true);

        jta.setFont(fnt);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);

        con.add(sbrText);

        createMenuItem(jmfile, "New");
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

    public void createMenuItem(JMenu jm, String txt) {
        JMenuItem jmi = new JMenuItem(txt);
        jmi.addActionListener(this);
        jm.add(jmi);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        if (e.getActionCommand().equals("New")) {
            this.setTitle("Untitled.txt - NotePad");
            jta.setText("");
            fnameContainer = null;
            searchIndex = 0; // Reset vị trí tìm kiếm khi tạo tài liệu mới
        } else if (e.getActionCommand().equals("Open")) {
            int ret = jfc.showDialog(null, "Open");
            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    File fyl = jfc.getSelectedFile();
                    OpenFile(fyl.getAbsolutePath());
                    this.setTitle(fyl.getName() + " - NotePad");
                    fnameContainer = fyl;
                    searchIndex = 0; // Reset vị trí tìm kiếm khi mở tài liệu mới
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getActionCommand().equals("Save")) {
            if (fnameContainer != null) {
                try {
                    SaveFile(fnameContainer.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                int ret = jfc.showDialog(null, "Save");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        File fyl = jfc.getSelectedFile();
                        SaveFile(fyl.getAbsolutePath());
                        this.setTitle(fyl.getName() + " - NotePad");
                        fnameContainer = fyl;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else if (e.getActionCommand().equals("Save As")) {
            int ret = jfc.showDialog(null, "Save As");
            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    File fyl = jfc.getSelectedFile();
                    SaveFile(fyl.getAbsolutePath());
                    this.setTitle(fyl.getName() + " - NotePad");
                    fnameContainer = fyl;
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
                    searchIndex += find.length(); // Cập nhật vị trí tìm kiếm để tìm tiếp
                    // Thêm nút Find Next
                    int option = JOptionPane.showConfirmDialog(this, "Find Next?", "Find", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        searchIndex = text.indexOf(find, searchIndex);
                        while (searchIndex != -1) {
                            jta.setCaretPosition(searchIndex);
                            jta.select(searchIndex, searchIndex + find.length());
                            jta.grabFocus();
                            searchIndex += find.length(); // Cập nhật vị trí tìm kiếm để tìm tiếp
                            option = JOptionPane.showConfirmDialog(this, "Find Next?", "Find", JOptionPane.YES_NO_OPTION);
                            if (option != JOptionPane.YES_OPTION) {
                                break;
                            }
                            searchIndex = text.indexOf(find, searchIndex);
                        }
                        if (searchIndex == -1) {
                            JOptionPane.showMessageDialog(this, "Text not found!", "Find", JOptionPane.INFORMATION_MESSAGE);
                            searchIndex = 0; // Reset vị trí tìm kiếm nếu không tìm thấy
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Text not found!", "Find", JOptionPane.INFORMATION_MESSAGE);
                    searchIndex = 0; // Reset vị trí tìm kiếm nếu không tìm thấy
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
            JOptionPane.showMessageDialog(this, "Created by: Bao Thien", "Notepad", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void OpenFile(String fname) throws IOException {
        BufferedReader d = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));
        String l;
        jta.setText("");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        while ((l = d.readLine()) != null) {
            jta.append(l + "\n");
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        d.close();
        searchIndex = 0; // Reset vị trí tìm kiếm khi mở file
    }

    public void SaveFile(String fname) throws IOException {
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        BufferedWriter out = new BufferedWriter(new FileWriter(fname));
        out.write(jta.getText());
        out.close();
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
