package ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class page_login extends JFrame implements ActionListener {
    public JLabel lb_user, lb_pass, lb_title;
    public JTextField txt_user;
    public JPasswordField txt_pass;
    public JButton btn_login;
    public JPanel pn_title, pn_body, pn_footer, pn_all;

    public page_login() {
        setTitle("Trang Dang Nhap");

        lb_title = new JLabel("CinameJAVA", SwingConstants.CENTER);
        lb_user = new JLabel("Tài khoản:");
        lb_pass = new JLabel("Mật khẩu:");
        txt_user = new JTextField(15);
        txt_pass = new JPasswordField(15);
        btn_login = new JButton("Đăng Nhập");

        pn_title = new JPanel();
        pn_body = new JPanel();
        pn_footer = new JPanel();
        pn_all = new JPanel();

        lb_title.setFont(new Font("Arial", Font.BOLD, 22));
        lb_title.setForeground(new Color(255, 140, 0));
        lb_title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        pn_title.setBackground(Color.WHITE);
        pn_title.add(lb_title);

        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        lb_user.setFont(labelFont);
        lb_pass.setFont(labelFont);

        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        txt_user.setFont(fieldFont);
        txt_user.setPreferredSize(new Dimension(200, 32));
        txt_pass.setFont(fieldFont);
        txt_pass.setPreferredSize(new Dimension(200, 32));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        row1.setBackground(Color.WHITE);
        lb_user.setPreferredSize(new Dimension(80, 32));
        row1.add(lb_user);
        row1.add(txt_user);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        row2.setBackground(Color.WHITE);
        lb_pass.setPreferredSize(new Dimension(80, 32));
        row2.add(lb_pass);
        row2.add(txt_pass);

        pn_body.setLayout(new BoxLayout(pn_body, BoxLayout.Y_AXIS));
        pn_body.setBackground(Color.WHITE);
        pn_body.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pn_body.add(row1);
        pn_body.add(row2);

        btn_login.setFont(new Font("Arial", Font.BOLD, 14));
        btn_login.setBackground(new Color(255, 140, 0));
        btn_login.setForeground(Color.WHITE);
        btn_login.setFocusPainted(false);
        btn_login.setPreferredSize(new Dimension(140, 38));
        btn_login.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn_login.addActionListener(this);
        pn_footer.setBackground(Color.WHITE);
        pn_footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        pn_footer.add(btn_login);

        pn_all.setLayout(new BorderLayout());
        pn_all.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230), 1));
        pn_all.add(pn_title, BorderLayout.NORTH);
        pn_all.add(pn_body, BorderLayout.CENTER);
        pn_all.add(pn_footer, BorderLayout.SOUTH);
        add(pn_all);
        setResizable(false);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_login) {
            String user = txt_user.getText();
            String pass = new String(txt_pass.getPassword());
            try {
                DBContext db = new DBContext();
                Connection con = db.getConnection();
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement st = con.prepareStatement(sql);
                st.setString(1, user);
                st.setString(2, pass);
                
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    String role = rs.getString("role");
                    
                    JOptionPane.showMessageDialog(this, "Chào mừng " + name + "! Đăng nhập thành công.");
                    this.dispose();
                    
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        page_ql_menu menu_ql = new page_ql_menu();
                        menu_ql.setVisible(true);
                    } else {
                        page_nv_menu menu_nv = new page_nv_menu();
                        menu_nv.setVisible(true);
                    }
                } else {
                    txt_user.setText("");
                    txt_pass.setText("");
                    JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                con.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        page_login pn = new page_login();
        pn.setVisible(true);
    }
}