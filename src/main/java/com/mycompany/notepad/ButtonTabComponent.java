/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.notepad;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author admin
 */
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

                // Kiểm tra xem tab có thay đổi không được lưu không
                if (hasUnsavedChanges(jta) && notePad.status_save == 0) {
                    // Kiểm tra nội dung trong JTextArea
                    if (jta.getText().trim().isEmpty()) {
                        // Nếu không có nội dung, đóng tab luôn
                        pane.remove(i);
                        return;
                    }

                    // Nếu có nội dung, hiển thị hộp thoại xác nhận
                    int option = JOptionPane.showConfirmDialog(null, "Do you want to save this file?", "Save File", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        // Hiển thị JFileChooser để lưu tệp
                        JFileChooser fileChooser = new JFileChooser();
                        int saveOption = fileChooser.showSaveDialog(null);
                        if (saveOption == JFileChooser.APPROVE_OPTION) {
                            try {
                                // Lưu file
                                File file = fileChooser.getSelectedFile();
                                notePad.SaveFile(file.getAbsolutePath(), jta);
                                
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                return; // Nếu có lỗi xảy ra trong quá trình lưu, không đóng tab
                            }
                        } else {
                            // Nếu người dùng hủy bỏ lưu tệp, không đóng tab
                            return;
                        }
                    } else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                        // Nếu người dùng hủy hoặc đóng hộp thoại, không đóng tab
                        return;
                    }
                }
                
                // Kiểm tra xem có phải là tab cuối cùng không
                if (pane.getTabCount() == 1) {
                    System.exit(0);
                    // Đóng chương trình khi là tab cuối cùng
                }
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
