package ui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class page_ql_nhanvien extends JFrame implements ActionListener {
    public JLabel lb_qlnv, lb_username, lb_password, lb_role, lb_name, lb_phone, lb_search;
    public JTextField txt_username, txt_password, txt_role, txt_name, txt_phone;
    public JRadioButton rb_all, rb_username, rb_role, rb_name, rb_phone;
    public ButtonGroup searchGroup;
    public JComboBox<String> list_users;
    public JButton btn_newUser, btn_updateUser, btn_deleteUser, btn_refresh, btn_undo;
    DefaultComboBoxModel<String> userListModel;
    public JTable tb_users;
    private final DefaultTableModel tableModel;
    public JPanel pn_title, pn_body1, pn_body2, pn_body3, pn_footer, pn_all;

    public page_ql_nhanvien() {
        setTitle("Quản lý nhân viên");

        lb_qlnv = new JLabel("Quản lý nhân viên", SwingConstants.CENTER);
        lb_qlnv.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));
        lb_qlnv.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        lb_username = new JLabel("Tên đăng nhập:");
        lb_password = new JLabel("Mật khẩu:");
        lb_role = new JLabel("Chức vụ:");
        lb_name = new JLabel("Tên nhân viên:");
        lb_phone = new JLabel("Số điện thoại:");
        lb_search = new JLabel("Tìm kiếm theo:");
        txt_username = new JTextField(5);
        txt_password = new JTextField(5);
        txt_role = new JTextField(5);
        txt_name = new JTextField(5);
        txt_phone = new JTextField(5);
        rb_all = new JRadioButton("Tất cả");
        rb_username = new JRadioButton("Tên đăng nhập");
        rb_role = new JRadioButton("Chức vụ");
        rb_name = new JRadioButton("Tên nhân viên");
        rb_phone = new JRadioButton("Số điện thoại");
        searchGroup = new ButtonGroup();
        searchGroup.add(rb_all);
        searchGroup.add(rb_username);
        searchGroup.add(rb_role);
        searchGroup.add(rb_name);
        searchGroup.add(rb_phone);
        userListModel = new DefaultComboBoxModel<>();
        list_users = new JComboBox<>(userListModel);
        list_users.setPreferredSize(new Dimension(100, 20));
        btn_newUser = new JButton("Thêm");
        btn_updateUser = new JButton("Sửa");
        btn_deleteUser = new JButton("Xóa");
        btn_refresh = new JButton("Làm mới");
        btn_undo = new JButton("Quay lại");
        tableModel = new DefaultTableModel(new String[] {"ID", "Tên đăng nhập", "Mật khẩu", "Chức vụ", "Tên", "Số điện thoại"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tb_users = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tb_users);

        pn_title = new JPanel();
        pn_body1 = new JPanel();
        pn_body2 = new JPanel();
        pn_body3 = new JPanel();
        pn_footer = new JPanel();
        pn_all = new JPanel();

        pn_title.setLayout(new FlowLayout());
        pn_body1.setLayout(new FlowLayout());
        pn_body2.setLayout(new FlowLayout());
        pn_body3.setLayout(new FlowLayout());
        pn_footer.setLayout(new BorderLayout());
        pn_footer.setPreferredSize(new Dimension(800, 400));
        pn_footer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pn_all.setLayout(new BoxLayout(pn_all, BoxLayout.Y_AXIS));

        pn_title.add(lb_qlnv);
        pn_body1.add(lb_username);
        pn_body1.add(txt_username);
        pn_body1.add(lb_password);
        pn_body1.add(txt_password);
        pn_body1.add(lb_role);
        pn_body1.add(txt_role);
        pn_body1.add(lb_name);
        pn_body1.add(txt_name);
        pn_body1.add(lb_phone);
        pn_body1.add(txt_phone);
        pn_body2.add(lb_search);
        pn_body2.add(rb_all);
        pn_body2.add(rb_username);
        pn_body2.add(rb_role);
        pn_body2.add(rb_name);
        pn_body2.add(rb_phone);
        pn_body2.add(list_users);
        pn_body3.add(btn_newUser);
        pn_body3.add(btn_updateUser);
        pn_body3.add(btn_deleteUser);
        pn_body3.add(btn_refresh);
        pn_body3.add(btn_undo);
        pn_footer.add(tableScrollPane, BorderLayout.CENTER);
        pn_all.add(pn_title);
        pn_all.add(pn_body1);
        pn_all.add(pn_body2);
        pn_all.add(pn_body3);
        pn_all.add(pn_footer);
        add(pn_all);

        rb_all.addActionListener(this);
        rb_username.addActionListener(this);
        rb_role.addActionListener(this);
        rb_name.addActionListener(this);
        rb_phone.addActionListener(this);
        btn_refresh.addActionListener(this);
        btn_newUser.addActionListener(this);
        btn_updateUser.addActionListener(this);
        btn_deleteUser.addActionListener(this);
        btn_undo.addActionListener(this);

        list_users.addActionListener(e -> {
            handleListSelection();
        });

        tb_users.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFieldsFromTable();
            }
        });

        rb_all.setSelected(true);
        loadAllUsers();

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == rb_all) {
            userListModel.removeAllElements();
            loadAllUsers();
        } else if (src == rb_username) {
            loadListByColumn("username");
        } else if (src == rb_role) {
            loadListByColumn("role");
        } else if (src == rb_name) {
            loadListByColumn("name");
        } else if (src == rb_phone) {
            loadListByColumn("phone");
        } else if (src == btn_refresh) {
            txt_username.setText("");
            txt_password.setText("");
            txt_role.setText("");
            txt_name.setText("");
            txt_phone.setText("");
            rb_all.setSelected(true);
            userListModel.removeAllElements();
            loadAllUsers();
        } else if (src == btn_newUser) {
            handleAdd();
        } else if (src == btn_updateUser) {
            handleUpdate();
        } else if (src == btn_deleteUser) {
            handleDelete();
        } else if (src == btn_undo) {
            dispose();
            page_ql_menu menu = new page_ql_menu();
            menu.setVisible(true);
        }
    }

    private void handleListSelection() {
        String selected = (String) list_users.getSelectedItem();
        if (selected == null) {
            return;
        }
        if (rb_username.isSelected()) {
            loadUsersByFilter("username", selected);
        } else if (rb_role.isSelected()) {
            loadUsersByFilter("role", selected);
        } else if (rb_name.isSelected()) {
            loadUsersByFilter("name", selected);
        } else if (rb_phone.isSelected()) {
            loadUsersByFilter("phone", selected);
        } else {
            loadAllUsers();
        }
    }

    private void fillFieldsFromTable() {
        int row = tb_users.getSelectedRow();
        if (row < 0) {
            return;
        }
        txt_username.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txt_password.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txt_role.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txt_name.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txt_phone.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void loadAllUsers() {
        String sql = "SELECT id, username, password, role, name, phone FROM users";
        loadTableBySql(sql, null);
    }

    private void loadUsersByFilter(String column, String value) {
        String sql = "SELECT id, username, password, role, name, phone FROM users WHERE " + column + " = ?";
        loadTableBySql(sql, value);
    }

    private void loadListByColumn(String column) {
        userListModel.removeAllElements();
        String sql = "SELECT DISTINCT " + column + " FROM users ORDER BY " + column;
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Set<String> seen = new HashSet<>();
            while (rs.next()) {
                String value = rs.getString(1);
                if (value != null) {
                    String normalized = value.trim();
                    if (!normalized.isEmpty() && seen.add(normalized.toLowerCase())) {
                        userListModel.addElement(normalized);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTableBySql(String sql, String param) {
        clearTable();
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("name"),
                        rs.getString("phone")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải bảng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    private void handleAdd() {
        String username = txt_username.getText().trim();
        String password = txt_password.getText().trim();
        String role = txt_role.getText().trim();
        String name = txt_name.getText().trim();
        String phone = txt_phone.getText().trim();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (phone.length() != 10 || !phone.matches("\\d{10}") || !phone.startsWith("0")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO users (username, password, role, name, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setString(4, name);
            ps.setString(5, phone);
            ps.executeUpdate();
            refreshAfterCrud();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        int row = tb_users.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một nhân viên để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = txt_username.getText().trim();
        String password = txt_password.getText().trim();
        String role = txt_role.getText().trim();
        String name = txt_name.getText().trim();
        String phone = txt_phone.getText().trim();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (phone.length() != 10 || !phone.matches("\\d{10}") || !phone.startsWith("0")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, name = ?, phone = ? WHERE id = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setString(4, name);
            ps.setString(5, phone);
            ps.setInt(6, id);
            ps.executeUpdate();
            refreshAfterCrud();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int row = tb_users.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một nhân viên để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            refreshAfterCrud();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAfterCrud() {
        txt_username.setText("");
        txt_password.setText("");
        txt_role.setText("");
        txt_name.setText("");
        txt_phone.setText("");
        rb_all.setSelected(true);
        userListModel.removeAllElements();
        loadAllUsers();
    }

    public static void main(String[] args) {
        page_ql_nhanvien pn = new page_ql_nhanvien();
        pn.setVisible(true);
    }
}