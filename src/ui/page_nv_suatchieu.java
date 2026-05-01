package ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class page_nv_suatchieu extends JFrame implements ActionListener {
    public JLabel lb_title;
    public JButton btn_trolai, btn_luu;
    public JTable tb_movies;
    public JComboBox<String> cbo_phong, cbo_gio;
    public JLabel lb_phim, lb_phong, lb_gio;
    public JPanel pn_all, pn_tille, pn_trai, pn_phai;
    private Connection con;
    public page_nv_suatchieu() {
        try {
            DBContext db = new DBContext();
            con = db.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTitle("Tạo suất chiếu");

        tb_movies = new JTable();
        tb_movies.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = tb_movies.getSelectedRow();
                if (row >= 0) {
                    cbo_phong.removeAllItems();
                    cbo_gio.removeAllItems();
                    try {
                        String sqlRoom = "SELECT name FROM rooms";
                        PreparedStatement ps = con.prepareStatement(sqlRoom);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            cbo_phong.addItem(rs.getString("name"));
                        }
                        String movieTitle = tb_movies.getValueAt(row, 0).toString();
                        String sqlMovie = "SELECT duration FROM movies WHERE title = ?";
                        PreparedStatement psM = con.prepareStatement(sqlMovie);
                        psM.setString(1, movieTitle);
                        ResultSet rsM = psM.executeQuery();
                        
                        int duration = 0;
                        if (rsM.next()) {
                            duration = rsM.getInt("duration");
                        }

                        if (duration > 0) {
                            int startMinutes = 9 * 60;
                            int endMinutes = 23 * 60;
                            int currentMinutes = startMinutes;
                            
                            while (currentMinutes + duration <= endMinutes) {
                                int h = currentMinutes / 60;
                                int m = currentMinutes % 60;
                                String timeStr = String.format("%02d:%02d", h, m);
                                cbo_gio.addItem(timeStr);
                                currentMinutes += duration;
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        lb_phim = new JLabel("Danh sách phim:");
        lb_phong = new JLabel("Phòng chiếu:");
        lb_gio = new JLabel("Giờ chiếu:");
        cbo_phong = new JComboBox<>();
        cbo_gio = new JComboBox<>();
        btn_trolai = new JButton("Trở lại");
        btn_trolai.addActionListener(this);
        btn_luu = new JButton("Lưu");
        btn_luu.addActionListener(this);

        lb_title = new JLabel("TẠO SUẤT CHIẾU");
        lb_title.setFont(new Font("Arial", Font.BOLD, 19));
        lb_title.setForeground(new Color(255, 140, 0));
        lb_title.setHorizontalAlignment(SwingConstants.CENTER);

        String[] cols = {"Tên phim", "Thể loại", "Thời lượng"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        tb_movies.setModel(model);
        pn_all = new JPanel(new BorderLayout(10, 10));
        pn_all.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pn_all.add(lb_title, BorderLayout.NORTH);
        
        pn_trai = new JPanel();
        pn_trai.setLayout(new BorderLayout(0, 5));
        pn_trai.add(lb_phim, BorderLayout.NORTH); 
        pn_trai.add(new JScrollPane(tb_movies), BorderLayout.CENTER);

        pn_phai = new JPanel(new GridLayout(3, 2, 10, 15));
        pn_phai.add(lb_phong); pn_phai.add(cbo_phong);
        pn_phai.add(lb_gio); pn_phai.add(cbo_gio);
        
        JPanel pn_buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        pn_buttons.add(btn_trolai);
        pn_buttons.add(btn_luu);

        pn_phai.add(btn_trolai);
        pn_phai.add(btn_luu);
        
        JPanel pn_right_wrapper = new JPanel(new BorderLayout());
        pn_right_wrapper.setBorder(BorderFactory.createEmptyBorder(25, 15, 0, 0));
        pn_right_wrapper.add(pn_phai, BorderLayout.NORTH);

        pn_all.add(pn_trai, BorderLayout.CENTER);
        pn_all.add(pn_right_wrapper, BorderLayout.EAST);
        add(pn_all);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        loadData();
    }

    public void loadData() {
        try {
            if (con == null) return;
            DefaultTableModel model = (DefaultTableModel) tb_movies.getModel();
            model.setRowCount(0);
            
            String sql = "SELECT title, genre, duration FROM movies";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getInt("duration") + " phút"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_trolai) {
            dispose();
            new page_nv_menu().setVisible(true);
        }
        if (e.getSource() == btn_luu) {
            try {
                if (con == null) return;
                
                int selectedRow = tb_movies.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 phim từ danh sách!");
                    return;
                }
                if (cbo_phong.getSelectedItem() == null || cbo_gio.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng và giờ chiếu!");
                    return;
                }
                
                String movieTitle = tb_movies.getValueAt(selectedRow, 0).toString();
                String roomName = cbo_phong.getSelectedItem().toString();
                String timeStr = cbo_gio.getSelectedItem().toString();
                String fullTime = java.time.LocalDate.now().toString() + " " + timeStr + ":00";
                
                int movieId = -1;
                PreparedStatement psM = con.prepareStatement("SELECT id FROM movies WHERE title = ?");
                psM.setString(1, movieTitle);
                ResultSet rsM = psM.executeQuery();
                if (rsM.next()) movieId = rsM.getInt("id");

                int roomId = -1;
                PreparedStatement psR = con.prepareStatement("SELECT id FROM rooms WHERE name = ?");
                psR.setString(1, roomName);
                ResultSet rsR = psR.executeQuery();
                if (rsR.next()) roomId = rsR.getInt("id");

                if (movieId != -1 && roomId != -1) {
                    String sql = "INSERT INTO showtimes (movie_id, room_id, start_time) VALUES (?, ?, ?)";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, movieId);
                    ps.setInt(2, roomId);
                    ps.setString(3, fullTime);
                    ps.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Lưu suất chiếu thành công!");
                    dispose();
                    new page_nv_suatchieu().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phim hoặc phòng hợp lệ!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        new page_nv_suatchieu().setVisible(true);
    }
}