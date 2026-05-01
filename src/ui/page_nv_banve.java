package ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class page_nv_banve extends JFrame implements ActionListener {
    public JLabel lb_phong, lb_suatchieu, lb_title, lb_dsphim, lb_chonghe, lb_hd, lb_tongtien, lb_giave;
    public JTextField txt_giave;
    public JComboBox<String> cbo_rooms, cbo_showtimes;
    public JList<String> list_ve;
    public DefaultListModel<String> model_ve;
    public JTable tb_movies, tb_seats;
    public DefaultTableModel model_movies, model_seats;
    public JButton btn_muave, btn_trolai;
    public JPanel pn_all, pn_left, pn_right, pn_filters;
    private Connection con;

    public page_nv_banve() {
        try {
            DBContext db = new DBContext();
            con = db.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        lb_title = new JLabel("QUẢN LÝ BÁN VÉ CINEMA");
        lb_dsphim = new JLabel("Danh sách phim:");
        lb_chonghe = new JLabel("Chọn ghế:");
        lb_hd = new JLabel("Hóa đơn:");
        lb_phong = new JLabel("Phòng chiếu:");
        lb_suatchieu = new JLabel("Giờ chiếu:");
        lb_giave = new JLabel("Giá vé:");

        cbo_rooms = new JComboBox<>();
        cbo_showtimes = new JComboBox<>();
        txt_giave = new JTextField("0");
        
        model_movies = new DefaultTableModel(new Object[]{"Tên phim", "Thời lượng", "Thể loại"}, 0);
        tb_movies = new JTable(model_movies);
        
        model_seats = new DefaultTableModel(new Object[]{"Số ghế"}, 0);
        tb_seats = new JTable(model_seats);
        
        model_ve = new DefaultListModel<>();
        list_ve = new JList<>(model_ve);
        
        btn_muave = new JButton("Xác nhận");
        btn_trolai = new JButton("Trở lại");
        
        lb_tongtien = new JLabel("Tổng tiền: 0 VNĐ");
        lb_tongtien.setFont(new Font("Arial", Font.BOLD, 15));
        lb_tongtien.setForeground(Color.RED);
        lb_tongtien.setHorizontalAlignment(SwingConstants.RIGHT);


        lb_title.setFont(new Font("Arial", Font.BOLD, 19));
        lb_title.setForeground(new Color(255, 140, 0));
        lb_title.setHorizontalAlignment(SwingConstants.CENTER);
        
        tb_movies.setRowHeight(22);
        tb_seats.setRowHeight(22);
        pn_all = new JPanel(new BorderLayout(10, 10));
        pn_all.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pn_all.add(lb_title, BorderLayout.NORTH);
        pn_left = new JPanel(new BorderLayout(10, 10));
        pn_left.add(lb_dsphim, BorderLayout.NORTH);
        pn_left.add(new JScrollPane(tb_movies), BorderLayout.CENTER);
        pn_left.setPreferredSize(new Dimension(450, 0));
        pn_right = new JPanel();
        pn_right.setLayout(new BoxLayout(pn_right, BoxLayout.Y_AXIS));

        pn_filters = new JPanel(new GridLayout(3, 2, 10, 10));
        pn_filters.add(lb_phong); pn_filters.add(cbo_rooms);
        pn_filters.add(lb_suatchieu); pn_filters.add(cbo_showtimes);
        pn_filters.add(lb_giave); pn_filters.add(txt_giave);

        JPanel pn_seats_container = new JPanel(new BorderLayout(10, 10));
        pn_seats_container.add(lb_chonghe, BorderLayout.NORTH);
        pn_seats_container.add(new JScrollPane(tb_seats), BorderLayout.CENTER);

        JPanel pn_invoice = new JPanel(new BorderLayout(10, 10));
        pn_invoice.add(lb_hd, BorderLayout.NORTH);
        pn_invoice.add(new JScrollPane(list_ve), BorderLayout.CENTER);
        
        JPanel pn_bottom_invoice = new JPanel(new BorderLayout(0, 10));
        pn_bottom_invoice.add(lb_tongtien, BorderLayout.NORTH);
        
        JPanel pn_buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        pn_buttons.add(btn_trolai);
        pn_buttons.add(btn_muave);
        
        pn_bottom_invoice.add(pn_buttons, BorderLayout.SOUTH);
        pn_invoice.add(pn_bottom_invoice, BorderLayout.SOUTH);

        pn_right.add(pn_filters);
        pn_right.add(Box.createVerticalStrut(15));
        pn_right.add(pn_seats_container);
        pn_right.add(Box.createVerticalStrut(15));
        pn_right.add(pn_invoice);

        pn_all.add(pn_left, BorderLayout.WEST);
        pn_all.add(pn_right, BorderLayout.CENTER);

        add(pn_all);
        setTitle("Quản lý Bán vé");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Sự kiện
        tb_movies.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    cbo_rooms.removeAllItems();
                    String sql = "SELECT * FROM rooms";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        cbo_rooms.addItem(rs.getString("name"));
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
        tb_seats.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int rowMovie = tb_movies.getSelectedRow();
                int rowSeat = tb_seats.getSelectedRow();
                if (rowMovie != -1 && rowSeat != -1) {
                    String movie = tb_movies.getValueAt(rowMovie, 0).toString();
                    String room = cbo_rooms.getSelectedItem().toString();
                    String seat = tb_seats.getValueAt(rowSeat, 0).toString();
                    String info = movie + " | " + room + " | Ghế: " + seat;
                    
                    if (!model_ve.contains(info)) {
                        model_ve.addElement(info);
                    } else {
                        model_ve.removeElement(info);
                    }
                    int price = 50000;
                    try { price = Integer.parseInt(txt_giave.getText()); } catch(Exception ex) {}
                    int total = model_ve.size() * price;
                    lb_tongtien.setText("Tổng tiền: " + String.format("%,d", total).replace(",", ".") + " VNĐ");
                }
            }
        });
        cbo_rooms.addActionListener(this);
        btn_muave.addActionListener(this);
        btn_trolai.addActionListener(this);
        try {
            if (con == null) return;
            String sql = "SELECT * FROM movies";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            model_movies.setRowCount(0);
            while (rs.next()) {
                model_movies.addRow(new Object[]{
                    rs.getString("title"), rs.getInt("duration"), rs.getString("genre")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cbo_rooms) {
            try {
            cbo_showtimes.removeAllItems();
            int selectedRow = tb_movies.getSelectedRow();
            if (selectedRow == -1) return;
            
            String movieTitle = tb_movies.getValueAt(selectedRow, 0).toString();
            String roomName = (String) cbo_rooms.getSelectedItem();
            if (roomName == null) return;

            String sql = "SELECT start_time FROM showtimes st " +
                         "JOIN movies m ON st.movie_id = m.id " +
                         "JOIN rooms r ON st.room_id = r.id " +
                         "WHERE m.title = ? AND r.name = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, movieTitle);
            pst.setString(2, roomName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cbo_showtimes.addItem(rs.getString("start_time"));
            }
            model_seats.setRowCount(0);
            String sql1 = "SELECT s.seat_number FROM seats s " +
                         "JOIN rooms r ON s.room_id = r.id WHERE r.name = ?";
            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setString(1, roomName);
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                model_seats.addRow(new Object[]{rs1.getString("seat_number")});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        }
        if (e.getSource() == cbo_showtimes) {
            try {
                model_seats.setRowCount(0);
                String showtime = (String) cbo_showtimes.getSelectedItem();
                String roomName = (String) cbo_rooms.getSelectedItem();
                if (showtime == null || roomName == null) return;
                
                String sql1 = "SELECT s.seat_number FROM seats s " +
                             "JOIN rooms r ON s.room_id = r.id WHERE r.name = ?";
                PreparedStatement pst1 = con.prepareStatement(sql1);
                pst1.setString(1, roomName);
                ResultSet rs1 = pst1.executeQuery();
                while (rs1.next()) {
                    model_seats.addRow(new Object[]{rs1.getString("seat_number")});
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        if (e.getSource() == btn_muave) {
            if (model_ve.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ghế trước khi thanh toán!");
                return;
            }
            try {
                int selectedRow = tb_movies.getSelectedRow();
                String movieTitle = tb_movies.getValueAt(selectedRow, 0).toString();
                String roomName = cbo_rooms.getSelectedItem().toString();
                String showtime = cbo_showtimes.getSelectedItem().toString();
                
                String sqlST = "SELECT st.id FROM showtimes st " +
                               "JOIN movies m ON st.movie_id = m.id " +
                               "JOIN rooms r ON st.room_id = r.id " +
                               "WHERE m.title = ? AND r.name = ? AND st.start_time = ?";
                PreparedStatement psST = con.prepareStatement(sqlST);
                psST.setString(1, movieTitle);
                psST.setString(2, roomName);
                psST.setString(3, showtime);
                ResultSet rsST = psST.executeQuery();
                
                if (rsST.next()) {
                    int showtimeId = rsST.getInt("id");
                    int price = 50000;
                    try { price = Integer.parseInt(txt_giave.getText()); } catch(Exception ex) {}
                    
                    int userId = 1;
                    try {
                        ResultSet rsU = con.createStatement().executeQuery("SELECT TOP 1 id FROM users");
                        if (rsU.next()) userId = rsU.getInt("id");
                    } catch(Exception ignored) {}
                    
                    String insertSql = "INSERT INTO tickets (showtime_id, seat_id, price, created_by) VALUES (?, ?, ?, ?)";
                    PreparedStatement psIns = con.prepareStatement(insertSql);
                    
                    for (int i = 0; i < model_ve.size(); i++) {
                        String info = model_ve.get(i);
                        String seatNumber = info.substring(info.lastIndexOf("Ghế: ") + 5);
                        
                        String sqlSeat = "SELECT s.id FROM seats s JOIN rooms r ON s.room_id = r.id WHERE r.name = ? AND s.seat_number = ?";
                        PreparedStatement psSeat = con.prepareStatement(sqlSeat);
                        psSeat.setString(1, roomName);
                        psSeat.setString(2, seatNumber);
                        ResultSet rsSeat = psSeat.executeQuery();
                        if (rsSeat.next()) {
                            psIns.setInt(1, showtimeId);
                            psIns.setInt(2, rsSeat.getInt("id"));
                            psIns.setInt(3, price);
                            psIns.setInt(4, userId);
                            psIns.executeUpdate();
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công! (" + model_ve.size() + " vé)");
                    dispose();
                    new page_nv_banve().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy suất chiếu hợp lệ!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
        if (e.getSource() == btn_trolai) {
            dispose();
            new page_nv_menu().setVisible(true);   
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        new page_nv_banve().setVisible(true);
    }
}
