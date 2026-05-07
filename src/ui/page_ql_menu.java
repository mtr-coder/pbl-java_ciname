package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class page_ql_menu extends JFrame implements ActionListener {
    public JLabel lb_title;
    public JButton btn_nhanvien, btn_phim, btn_suatchieu, btn_phong, btn_thongke, btn_logout;
    public JPanel pn_title, pn_body, pn_footer, pn_all;

    public page_ql_menu() {
        setTitle("Trang Menu");
        lb_title = new JLabel("CinameJAVA", SwingConstants.CENTER);
        btn_nhanvien = new JButton("Quản lí nhân viên");
        btn_phim = new JButton("Quản lí phim");
        btn_suatchieu = new JButton("Quản lí suất chiếu");
        btn_phong = new JButton("Quản lí phòng chiếu");
        btn_thongke = new JButton("Thống kê");
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
        btn_nhanvien.setFont(buttonFont);
        btn_phim.setFont(buttonFont);
        btn_suatchieu.setFont(buttonFont);
        btn_phong.setFont(buttonFont);
        btn_thongke.setFont(buttonFont);
        btn_logout.setFont(buttonFont);

        btn_nhanvien.setBackground(new Color(255, 140, 0));
        btn_phim.setBackground(new Color(255, 140, 0));
        btn_suatchieu.setBackground(new Color(255, 140, 0));
        btn_phong.setBackground(new Color(255, 140, 0));
        btn_thongke.setBackground(new Color(255, 140, 0));
        btn_logout.setBackground(new Color(255, 140, 0));

        btn_nhanvien.setForeground(Color.WHITE);
        btn_phim.setForeground(Color.WHITE);
        btn_suatchieu.setForeground(Color.WHITE);
        btn_phong.setForeground(Color.WHITE);
        btn_thongke.setForeground(Color.WHITE);
        btn_logout.setForeground(Color.WHITE);

        btn_nhanvien.setFocusPainted(false);
        btn_phim.setFocusPainted(false);
        btn_suatchieu.setFocusPainted(false);
        btn_phong.setFocusPainted(false);
        btn_thongke.setFocusPainted(false);
        btn_logout.setFocusPainted(false);

        btn_nhanvien.setPreferredSize(new Dimension(180, 40));
        btn_phim.setPreferredSize(new Dimension(180, 40));
        btn_suatchieu.setPreferredSize(new Dimension(180, 40));
        btn_phong.setPreferredSize(new Dimension(180, 40));
        btn_thongke.setPreferredSize(new Dimension(180, 40));
        btn_logout.setPreferredSize(new Dimension(180, 40));

        btn_nhanvien.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn_phim.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn_suatchieu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn_phong.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn_thongke.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn_logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn_nhanvien.addActionListener(this);
        btn_phim.addActionListener(this);
        btn_suatchieu.addActionListener(this);
        btn_phong.addActionListener(this);  
        btn_thongke.addActionListener(this);  
        btn_logout.addActionListener(this);

        pn_body.setLayout(new GridLayout(5, 1, 12, 12));
        pn_body.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40));
        pn_body.setBackground(Color.WHITE);
        pn_body.add(btn_nhanvien);
        pn_body.add(btn_phim);
        pn_body.add(btn_suatchieu);
        pn_body.add(btn_phong);
        pn_body.add(btn_thongke);
        pn_body.add(btn_logout);

        pn_all.setLayout(new BorderLayout());
        pn_all.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230), 1));
        pn_all.add(pn_title, BorderLayout.NORTH);
        pn_all.add(pn_body, BorderLayout.CENTER);
        add(pn_all);

        setResizable(false);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_logout) {
            this.dispose();
            page_login login = new page_login();
            login.setVisible(true);
        } else if (e.getSource() == btn_nhanvien) {
            this.dispose();
            page_ql_nhanvien qlnv = new page_ql_nhanvien();
            qlnv.setVisible(true);
        } else if (e.getSource() == btn_phim) {
            this.dispose();
            page_ql_phim qlp = new page_ql_phim();
            qlp.setVisible(true);
        } else if (e.getSource() == btn_suatchieu) {
            this.dispose();
            page_ql_suatchieu qlsct = new page_ql_suatchieu();
            qlsct.setVisible(true);
        } else if (e.getSource() == btn_phong) {
            this.dispose();
            page_ql_phongchieu qlp = new page_ql_phongchieu();
            qlp.setVisible(true);
        } else if (e.getSource() == btn_thongke) {
            this.dispose();
            page_ql_thongke qltt = new page_ql_thongke("Thống kê doanh thu");
            qltt.setVisible(true);
        }
    }

    public static void main(String[] args) {
        page_ql_menu pn = new page_ql_menu();
        pn.setVisible(true);
    }
}
