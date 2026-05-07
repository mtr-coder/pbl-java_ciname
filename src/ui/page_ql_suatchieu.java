package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import java.text.SimpleDateFormat;


public class page_ql_suatchieu extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId;
    private JSpinner spDate, spTime;
    private JComboBox<ComboItem> cbRoom, cbMovie;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnBack;

    // Lớp hỗ trợ lưu ID và Tên cho ComboBox
    class ComboItem {
        private int id;
        private String name;

        public ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public int getId() { return id; }
        @Override
        public String toString() { return name; }
    }

    public page_ql_suatchieu() {
        setTitle("Quản lý suất chiếu");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        
        UI();
        loadComboboxData();
        loadData();
    }

    private void UI() {
        JLabel label = new JLabel("QUẢN LÝ SUẤT CHIẾU");
        label.setBounds(350, 10, 250, 40);
        label.setFont(label.getFont().deriveFont(20f));
        add(label);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID Suất");
        tableModel.addColumn("Phòng");
        tableModel.addColumn("Phim");
        tableModel.addColumn("Ngày");
        tableModel.addColumn("Giờ Chiếu");
        
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 70, 550, 450);
        add(scrollPane);

        int xForm = 600;
        int yForm = 70;

        JLabel lblId = new JLabel("ID Suất:");
        lblId.setBounds(xForm, yForm, 80, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(xForm + 80, yForm, 200, 25);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblMovie = new JLabel("Phim:");
        lblMovie.setBounds(xForm, yForm + 40, 80, 25);
        add(lblMovie);

        cbMovie = new JComboBox<>();
        cbMovie.setBounds(xForm + 80, yForm + 40, 200, 25);
        add(cbMovie);

        JLabel lblRoom = new JLabel("Phòng:");
        lblRoom.setBounds(xForm, yForm + 80, 80, 25);
        add(lblRoom);

        cbRoom = new JComboBox<>();
        cbRoom.setBounds(xForm + 80, yForm + 80, 200, 25);
        add(cbRoom);

        JLabel lblDate = new JLabel("Ngày:");
        lblDate.setBounds(xForm, yForm + 120, 80, 25);
        add(lblDate);

        spDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spDate, "yyyy-MM-dd");
        spDate.setEditor(dateEditor);
        spDate.setBounds(xForm + 80, yForm + 120, 200, 25);
        add(spDate);

        JLabel lblTime = new JLabel("Giờ:");
        lblTime.setBounds(xForm, yForm + 170, 80, 25);
        add(lblTime);

        spTime = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spTime, "HH:mm:ss");
        spTime.setEditor(timeEditor);
        spTime.setBounds(xForm + 80, yForm + 170, 200, 25);
        add(spTime);

        // Nút chức năng
        btnAdd = new JButton("Thêm");
        btnAdd.setBounds(xForm, yForm + 240, 80, 30);
        add(btnAdd);

        btnEdit = new JButton("Sửa");
        btnEdit.setBounds(xForm + 100, yForm + 240, 80, 30);
        add(btnEdit);

        btnDelete = new JButton("Xóa");
        btnDelete.setBounds(xForm + 200, yForm + 240, 80, 30);
        add(btnDelete);

        btnRefresh = new JButton("Làm mới form");
        btnRefresh.setBounds(xForm, yForm + 290, 280, 30);
        add(btnRefresh);

        btnBack = new JButton("Quay lại");
        btnBack.setBounds(xForm, yForm + 330, 280, 30);
        add(btnBack);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row, 0).toString());
                    String roomName = tableModel.getValueAt(row, 1).toString();
                    String movieName = tableModel.getValueAt(row, 2).toString();
                    
                    // Convert date back to obj
                    String vnDate = tableModel.getValueAt(row, 3).toString();
                    try {
                        java.util.Date parsedDate = new SimpleDateFormat("dd-MM-yyyy").parse(vnDate);
                        spDate.setValue(parsedDate);
                    } catch (Exception ex) {}
                    
                    String vnTime = tableModel.getValueAt(row, 4).toString();
                    try {
                        java.util.Date parsedTime = new SimpleDateFormat("HH:mm:ss").parse(vnTime);
                        spTime.setValue(parsedTime);
                    } catch (Exception ex) {}

                    // Đặt lại combobox
                    for (int i = 0; i < cbRoom.getItemCount(); i++) {
                        if (cbRoom.getItemAt(i).toString().equals(roomName)) {
                            cbRoom.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < cbMovie.getItemCount(); i++) {
                        if (cbMovie.getItemAt(i).toString().equals(movieName)) {
                            cbMovie.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        // Xử lý sự kiện nút
        btnAdd.addActionListener(e -> addShowtime());
        btnEdit.addActionListener(e -> editShowtime());
        btnDelete.addActionListener(e -> deleteShowtime());
        btnRefresh.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> {
            this.dispose();
            new page_ql_menu().setVisible(true);
        });
    }

    private void loadComboboxData() {
        cbRoom.removeAllItems();
        cbMovie.removeAllItems();
        try (Connection conn = new DBContext().getConnection()) {
            // Load Rooms
            PreparedStatement psRoom = conn.prepareStatement("SELECT id, name FROM rooms");
            ResultSet rsRoom = psRoom.executeQuery();
            while (rsRoom.next()) {
                cbRoom.addItem(new ComboItem(rsRoom.getInt("id"), rsRoom.getString("name")));
            }
            
            // Load Movies
            PreparedStatement psMovie = conn.prepareStatement("SELECT id, title FROM movies");
            ResultSet rsMovie = psMovie.executeQuery();
            while (rsMovie.next()) {
                cbMovie.addItem(new ComboItem(rsMovie.getInt("id"), rsMovie.getString("title")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addShowtime() {
        ComboItem selectedMovie = (ComboItem) cbMovie.getSelectedItem();
        ComboItem selectedRoom = (ComboItem) cbRoom.getSelectedItem();
        
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(spDate.getValue());
        String timeStr = new SimpleDateFormat("HH:mm:ss").format(spTime.getValue());

        if (selectedMovie == null || selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String start_time = dateStr + " " + timeStr;
        java.sql.Timestamp timestamp;
        try {
            java.util.Date parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch (java.text.ParseException px) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày/giờ sai!\nXin hãy nhập Ngày: yyyy-MM-dd và Giờ: HH:mm:ss", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = new DBContext().getConnection()) {
            String sql = "INSERT INTO showtimes (movie_id, room_id, start_time) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, selectedMovie.getId());
            ps.setInt(2, selectedRoom.getId());
            ps.setTimestamp(3, timestamp);
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm suất chiếu thành công!");
            loadData();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm suất chiếu (Kiểm tra lại định dạng ngày/giờ): " + ex.getMessage());
        }
    }

    private void editShowtime() {
        String idStr = txtId.getText();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu để sửa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ComboItem selectedMovie = (ComboItem) cbMovie.getSelectedItem();
        ComboItem selectedRoom = (ComboItem) cbRoom.getSelectedItem();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(spDate.getValue());
        String timeStr = new SimpleDateFormat("HH:mm:ss").format(spTime.getValue());
        String start_time = dateStr + " " + timeStr;

        java.sql.Timestamp timestamp;
        try {
            java.util.Date parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch (java.text.ParseException px) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày/giờ sai!\nXin hãy nhập Ngày: yyyy-MM-dd và Giờ: HH:mm:ss", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = new DBContext().getConnection()) {
            String sql = "UPDATE showtimes SET movie_id=?, room_id=?, start_time=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, selectedMovie.getId());
            ps.setInt(2, selectedRoom.getId());
            ps.setTimestamp(3, timestamp);
            ps.setInt(4, Integer.parseInt(idStr));
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sửa suất chiếu thành công!");
            loadData();
            clearForm();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi sửa suất chiếu: " + ex.getMessage());
        }
    }

    private void deleteShowtime() {
        String idStr = txtId.getText();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu để xóa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa suất chiếu này?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = new DBContext().getConnection()) {
                String sql = "DELETE FROM showtimes WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idStr));
                
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa suất chiếu thành công!");
                loadData();
                clearForm();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage());
            }
        }
    }

    private void clearForm() {
        txtId.setText("");
        if (cbMovie.getItemCount() > 0) cbMovie.setSelectedIndex(0);
        if (cbRoom.getItemCount() > 0) cbRoom.setSelectedIndex(0);
        spDate.setValue(new java.util.Date());
        spTime.setValue(new java.util.Date());
    }

    private void loadData() {
        tableModel.setRowCount(0); 
        try (Connection conn = new DBContext().getConnection()) {
            String sql = "SELECT s.id, r.name AS room_name, m.title AS movie_title, CAST(s.start_time AS DATE) AS show_date, CAST(s.start_time AS TIME) AS show_time " +
                         "FROM showtimes s " +
                         "JOIN rooms r ON s.room_id = r.id " +
                         "JOIN movies m ON s.movie_id = m.id " +
                         "ORDER BY s.id ASC, s.start_time ASC";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:00");
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("room_name"));
                row.add(rs.getString("movie_title"));
                
                // Format Date and Time properly
                Date sqlDate = rs.getDate("show_date");
                Time sqlTime = rs.getTime("show_time");
                
                row.add(dateFormat.format(sqlDate));
                row.add(timeFormat.format(sqlTime));
                
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new page_ql_suatchieu().setVisible(true);
    }
}
