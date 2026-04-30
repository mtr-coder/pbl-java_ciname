package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class page_nv_menu extends JFrame implements ActionListener {
    public JLabel lb_title;
    public JButton btn_danhsachphim, btn_banve, btn_suatchieu, btn_logout;
    public JPanel pn_title, pn_body, pn_footer, pn_all;

    public page_nv_menu() {
        setTitle("Trang Menu Nhân Viên");
        lb_title = new JLabel("CinameJAVA", SwingConstants.CENTER);
        btn_danhsachphim = new JButton("Danh sách phim");
        btn_banve = new JButton("Bán vé");
        btn_suatchieu = new JButton("Tạo suất chiếu");
        btn_logout = new JButton("Thoát");

        pn_title = new JPanel();
        pn_body = new JPanel(); 
        pn_footer = new JPanel();
        pn_all = new JPanel();

        lb_title.setFont(new Font("Arial", Font.BOLD, 22));
        lb_title.setForeground(new Color(255, 140, 0));
        lb_title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        pn_title.setBackground(Color.WHITE);
        pn_title.add(lb_title);

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        btn_danhsachphim.setFont(buttonFont);
        btn_banve.setFont(buttonFont);
        btn_suatchieu.setFont(buttonFont);
        btn_logout.setFont(buttonFont);

        btn_danhsachphim.setBackground(new Color(255, 140, 0));
        btn_banve.setBackground(new Color(255, 140, 0));
        btn_suatchieu.setBackground(new Color(255, 140, 0));
        btn_logout.setBackground(new Color(255, 140, 0));

        btn_danhsachphim.setForeground(Color.WHITE);
        btn_banve.setForeground(Color.WHITE);
        btn_suatchieu.setForeground(Color.WHITE);
        btn_logout.setForeground(Color.WHITE);

        btn_danhsachphim.setFocusPainted(false);
        btn_banve.setFocusPainted(false);
        btn_suatchieu.setFocusPainted(false);
        btn_logout.setFocusPainted(false);

        btn_danhsachphim.setPreferredSize(new Dimension(180, 40));
        btn_banve.setPreferredSize(new Dimension(180, 40));
        btn_suatchieu.setPreferredSize(new Dimension(180, 40));
        btn_logout.setPreferredSize(new Dimension(180, 40));

        btn_danhsachphim.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn_banve.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn_suatchieu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn_logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn_danhsachphim.addActionListener(this);
        btn_banve.addActionListener(this);
        btn_suatchieu.addActionListener(this);  
        btn_logout.addActionListener(this);

        pn_body.setLayout(new GridLayout(4, 1, 12, 12));
        pn_body.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40));
        pn_body.setBackground(Color.WHITE);
        pn_body.add(btn_danhsachphim);
        pn_body.add(btn_banve);
        pn_body.add(btn_suatchieu);
        pn_body.add(btn_logout);

        pn_all.setLayout(new BorderLayout());
        pn_all.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230), 1));
        pn_all.add(pn_title, BorderLayout.NORTH);
        pn_all.add(pn_body, BorderLayout.CENTER);
        add(pn_all);

        setResizable(false);
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_logout) {
            this.dispose();
            page_login login = new page_login();
            login.setVisible(true);
        } else if (e.getSource() == btn_danhsachphim) {
            this.dispose();
            page_nv_danhsachphim dsp = new page_nv_danhsachphim();
            dsp.setVisible(true);
        } else if (e.getSource() == btn_banve) {
            this.dispose();
            page_nv_banve bv = new page_nv_banve();
            bv.setVisible(true);
        } else if (e.getSource() == btn_suatchieu) {
            this.dispose();
            page_nv_suatchieu sct = new page_nv_suatchieu();
            sct.setVisible(true);
        }
    }

    public static void main(String[] args) {
        page_nv_menu pn = new page_nv_menu();
        pn.setVisible(true);
    }
}
