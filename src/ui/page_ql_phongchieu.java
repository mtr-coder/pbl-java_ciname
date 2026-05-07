package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class page_ql_phongchieu extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtSearch, txtTotalSeats;
    private JTable tableSeats;
    private DefaultTableModel tableModelSeats;
    private JButton btnAdd, btnEdit, btnDelete, btnSearch, btnRefresh;

    public page_ql_phongchieu() {
        setTitle("Quản lý phòng chiếu");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null); 
        UI();
    }

    public void UI() {
        JLabel label = new JLabel("QUẢN LÝ PHÒNG CHIẾU");
        label.setBounds(280, 10, 250, 40);
        label.setFont(label.getFont().deriveFont(20f));
        add(label);

        // Bảng dữ liệu
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Tên phòng");
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 70, 450, 400);
        add(scrollPane);

        // Form nhập liệu
        JLabel lblId = new JLabel("ID Phòng:");
        lblId.setBounds(500, 70, 80, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(580, 70, 170, 25);
        txtId.setEditable(false); // ID tự tăng, không cho sửa tay
        add(txtId);

        JLabel lblName = new JLabel("Tên phòng:");
        lblName.setBounds(500, 110, 80, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(580, 110, 170, 25);
        add(txtName);

        JLabel lblTotalSeats = new JLabel("Tổng số ghế:");
        lblTotalSeats.setBounds(500, 150, 80, 25);
        add(lblTotalSeats);

        txtTotalSeats = new JTextField();
        txtTotalSeats.setBounds(580, 150, 170, 25);
        txtTotalSeats.setEditable(false);
        add(txtTotalSeats);

        JLabel lblSeats = new JLabel("Danh sách ghế:");
        lblSeats.setBounds(500, 190, 100, 25);
        add(lblSeats);

        tableModelSeats = new DefaultTableModel();
        tableModelSeats.addColumn("STT");
        tableModelSeats.addColumn("Tên ghế");
        tableSeats = new JTable(tableModelSeats);
        JScrollPane scrollSeats = new JScrollPane(tableSeats);
        scrollSeats.setBounds(500, 220, 260, 100);
        add(scrollSeats);

        // Các nút chức năng
        btnAdd = new JButton("Thêm");
        btnAdd.setBounds(500, 330, 80, 30);
        add(btnAdd);

        btnEdit = new JButton("Sửa");
        btnEdit.setBounds(590, 330, 80, 30);
        add(btnEdit);

        btnDelete = new JButton("Xóa");
        btnDelete.setBounds(680, 330, 80, 30);
        add(btnDelete);

        // Tìm kiếm
        txtSearch = new JTextField();
        txtSearch.setBounds(500, 380, 170, 30);
        add(txtSearch);

        btnSearch = new JButton("Tìm");
        btnSearch.setBounds(680, 380, 80, 30);
        add(btnSearch);

        btnRefresh = new JButton("Làm mới / Tải lại");
        btnRefresh.setBounds(500, 430, 260, 30);
        add(btnRefresh);

        addEvents();


        loadData("");
    }

    private void addEvents() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String roomIdStr = tableModel.getValueAt(row, 0).toString();
                    txtId.setText(roomIdStr);
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    
                    // Tải thông tin ghế cho phòng này
                    loadSeatInfo(Integer.parseInt(roomIdStr));
                }
            }
        });


        btnAdd.addActionListener(e -> {
            String name = txtName.getText();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = new DBContext().getConnection()) {
                // Giả sử bảng tên là rooms và có các cột id, name
                String sql = "INSERT INTO rooms (name) VALUES (?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
                loadData("");
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage());
            }
        });

        btnEdit.addActionListener(e -> {
            String idStr = txtId.getText();
            String name = txtName.getText();
            if (idStr.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng để sửa và nhập tên mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = new DBContext().getConnection()) {
                String sql = "UPDATE rooms SET name=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setInt(2, Integer.parseInt(idStr));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Cập nhật phòng thành công!");
                loadData("");
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi sửa: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            String idStr = txtId.getText();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phòng này không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = new DBContext().getConnection()) {
                    String sql = "DELETE FROM rooms WHERE id=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, Integer.parseInt(idStr));
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Xóa phòng thành công!");
                    loadData("");
                    clearForm();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
                }
            }
        });

        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            clearForm();
            loadData("");
        });
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0); 
        try (Connection conn = new DBContext().getConnection()) {
            String sql;
            PreparedStatement ps;
            if (keyword.isEmpty()) {
                sql = "SELECT id, name FROM rooms"; 
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT id, name FROM rooms WHERE name LIKE ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi Cơ Sở Dữ Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSeatInfo(int roomId) {
        tableModelSeats.setRowCount(0);
        try (Connection conn = new DBContext().getConnection()) {
            String sql = "SELECT seat_number FROM seats WHERE room_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roomId);
            
            ResultSet rs = ps.executeQuery();
            java.util.List<String> seatList = new java.util.ArrayList<>();
            
            while (rs.next()) {
                seatList.add(rs.getString("seat_number"));
            }
            
            seatList.sort((s1, s2) -> {
                String letter1 = s1.replaceAll("[0-9]", "");
                String letter2 = s2.replaceAll("[0-9]", "");
                int num1 = 0, num2 = 0;
                try { num1 = Integer.parseInt(s1.replaceAll("[^0-9]", "")); } catch(Exception ignored){}
                try { num2 = Integer.parseInt(s2.replaceAll("[^0-9]", "")); } catch(Exception ignored){}
                
                int cmp = letter1.compareTo(letter2);
                if (cmp == 0) return Integer.compare(num1, num2);
                return cmp;
            });
            
            int count = 0;
            for (String seat : seatList) {
                count++;
                Vector<Object> row = new Vector<>();
                row.add(count);
                row.add(seat);
                tableModelSeats.addRow(row);
            }
            
            txtTotalSeats.setText(String.valueOf(count));
            
        } catch (Exception e) {
            e.printStackTrace();
            txtTotalSeats.setText("Lỗi");
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtTotalSeats.setText("");
        if (tableModelSeats != null) {
            tableModelSeats.setRowCount(0);
        }
    }
    
    public static void main(String[] args) {
        // Tạo 1 đối tượng duy nhất
        page_ql_phongchieu frame = new page_ql_phongchieu();
        frame.UI(); // Gọi UI để add các nhãn vào
        frame.setVisible(true); // Hiển thị giao diện lên
    }
}

