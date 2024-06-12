package com.mycompany.notepad;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class NotePad extends JFrame implements ActionListener, WindowListener {

    JTabbedPane tabbedPane = new JTabbedPane();
    private int fontSize = 15;
    private int searchIndex = 0;

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

        if (e.getActionCommand().equals("New Tab")) {
            addNewTab("Untitled.txt", new Font("Arial", Font.PLAIN, fontSize));
        } else if (e.getActionCommand().equals("Open")) {
            int ret = jfc.showDialog(null, "Open");
            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    File fyl = jfc.getSelectedFile();
                    OpenFile(fyl.getAbsolutePath(), jta);
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), fyl.getName());
                    setTitle(fyl.getName() + " - NotePad");
                    searchIndex = 0;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getActionCommand().equals("Save")) {
            saveTab();
        } else if (e.getActionCommand().equals("Save As")) {
            int ret = jfc.showDialog(null, "Save As");
            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    File fyl = jfc.getSelectedFile();
                    SaveFile(fyl.getAbsolutePath(), jta);
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), fyl.getName());
                    setTitle(fyl.getName() + " - NotePad");
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
        String title = tabbedPane.getTitleAt(selectedIndex);
        if (!title.equals("Untitled.txt")) {
            try {
                // Lưu dữ liệu vào tệp
                SaveFile(title, jta);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            // Nếu tab chưa được lưu, hiển thị hộp thoại lưu
            JFileChooser jfc = new JFileChooser();
            int ret = jfc.showDialog(null, "Save");
            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    File fyl = jfc.getSelectedFile();
                    SaveFile(fyl.getAbsolutePath(), jta);
                    tabbedPane.setTitleAt(selectedIndex, fyl.getName());
                    setTitle(fyl.getName() + " - NotePad");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}


    public void OpenFile(String fname, JTextArea jta) throws IOException {
        BufferedReader d = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));
        String l;
        jta.setText("");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        while ((l = d.readLine()) != null) {
            jta.append(l + "\n");
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        d.close();
        searchIndex = 0;
    }

    public boolean SaveFile(String fname, JTextArea jta) throws IOException {
        System.out.println("Saving file: " + fname);
        BufferedWriter out = new BufferedWriter(new FileWriter(fname));
        out.write(jta.getText());
        out.close();
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

    public static void main(String[] args) {
        new NotePad();
    }
}

class ButtonTabComponent extends JPanel {
    private final JTabbedPane pane;
    private final NotePad notePad;

    public ButtonTabComponent(final JTabbedPane pane, NotePad notePad) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        this.notePad = notePad;
        setOpaque(false);

        JLabel label = new JLabel() {
            public String getText() {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };

        add(label);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JButton button = new TabButton();
        add(button);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            setUI(new BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBorderPainted(true);
                }

                public void mouseExited(MouseEvent e) {
                    setBorderPainted(false);
                }
            });
            setRolloverEnabled(true);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
    int i = pane.indexOfTabComponent(ButtonTabComponent.this);
    if (i != -1) {
        JScrollPane currentScrollPane = (JScrollPane) pane.getComponentAt(i);
        JTextArea jta = (JTextArea) currentScrollPane.getViewport().getView();

        // Check if the tab has unsaved changes
        if (hasUnsavedChanges(jta)) {
            // Kiểm tra nội dung trong JTextArea
            if (jta.getText().trim().isEmpty()) {
                // Nếu không có nội dung, đóng tab luôn
                pane.remove(i);
                return;
            }

            // Nếu có nội dung, hiển thị hộp thoại xác nhận
            int option = JOptionPane.showConfirmDialog(null, "Do you want to save this file?", "Save File", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    // Lưu file
                    notePad.SaveFile(pane.getTitleAt(i), jta);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return; // Nếu có lỗi xảy ra trong quá trình lưu, không đóng tab
                }
            } else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                // Nếu người dùng hủy hoặc đóng hộp thoại, không đóng tab
                return;
            }
        }

        // Kiểm tra xem có phải là tab cuối cùng không
        if (pane.getTabCount() == 1) {
            JOptionPane.showMessageDialog(null, "Cannot close the last tab.", "NotePad", JOptionPane.ERROR_MESSAGE);
            return; // Không đóng tab nếu là tab cuối cùng
        }

        // Xoá tab
        pane.remove(i);
    }
}

        public boolean hasUnsavedChanges(JTextArea jta) {
            return !jta.getText().isEmpty();
        }

        public void updateUI() {
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }
}
