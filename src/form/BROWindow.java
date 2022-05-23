/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package form;

import base.Helper;
import base.WebSQL;
import static base.WebSQL.finish;
import errors.BrowserException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import struct.Lang;

public class BROWindow extends javax.swing.JFrame {

    public WebSQL websql = new WebSQL();
    public static boolean db_finish_load = false;
    public static HashMap<String, ArrayList<String>> db_table = new HashMap();
    public static HashMap<String, ArrayList<String>> db_table_fields = new HashMap();
    public String db_selected;
    public static int getFieldsIndex = 0;
    /////
    public String table_clicked = "";

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    /**
     * Creates new form BROWindow
     */
    public BROWindow() {
        initComponents();
        BufferedImage imgStart = null, imgCancel = null, imgGetDB = null, imgGetTables = null, imgGetFields = null, imgData = null, imgDataSave = null;
        try {
            imgStart = ImageIO.read(getClass().getResource("run.png"));
            imgStart = resize(imgStart, 30, 30);
            imgCancel = ImageIO.read(getClass().getResource("cancel.png"));
            imgCancel = resize(imgCancel, 30, 30);
            imgGetDB = ImageIO.read(getClass().getResource("db.png"));
            imgGetDB = resize(imgGetDB, 30, 30);
            imgGetTables = ImageIO.read(getClass().getResource("tables.png"));
            imgGetTables = resize(imgGetTables, 30, 30);
            imgData = ImageIO.read(getClass().getResource("data.png"));
            imgData = resize(imgData, 30, 30);
            imgGetFields = ImageIO.read(getClass().getResource("columns.png"));
            imgGetFields = resize(imgGetFields, 30, 30);
            imgDataSave = ImageIO.read(getClass().getResource("download_db_data.png"));
            imgDataSave = resize(imgDataSave, 30, 30);
        } catch (Exception ex) {
            //Logger.getLogger(BROWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        //startIcon.getIconWidth(30);

        startButton.setIcon(new ImageIcon(imgStart));
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setPreferredSize(new Dimension(10, 10));
        startButton.setSize(new Dimension(10, 10));

        cancelButton.setIcon(new ImageIcon(imgCancel));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setPreferredSize(new Dimension(10, 10));
        cancelButton.setSize(new Dimension(10, 10));
        //***********//
        areaComment.setBackground(Color.white);

        // areaComment.setWrapStyleWord(true);
        areaComment.setColumns(10);
        ////**//
        getDB.setIcon(new ImageIcon(imgGetDB));
        getDB.setBorderPainted(false);
        getDB.setFocusPainted(false);
        getDB.setContentAreaFilled(false);
        getDB.setPreferredSize(new Dimension(10, 10));
        getDB.setSize(new Dimension(10, 10));

        getTables.setIcon(new ImageIcon(imgGetTables));
        getTables.setBorderPainted(false);
        getTables.setFocusPainted(false);
        getTables.setContentAreaFilled(false);
        getTables.setPreferredSize(new Dimension(10, 10));
        getTables.setSize(new Dimension(10, 10));

        getColumns.setIcon(new ImageIcon(imgGetFields));
        getColumns.setBorderPainted(false);
        getColumns.setFocusPainted(false);
        getColumns.setContentAreaFilled(false);
        getColumns.setPreferredSize(new Dimension(10, 10));
        getColumns.setSize(new Dimension(10, 10));

        getData.setIcon(new ImageIcon(imgData));
        getData.setBorderPainted(false);
        getData.setFocusPainted(false);
        getData.setContentAreaFilled(false);
        getData.setPreferredSize(new Dimension(10, 10));
        getData.setSize(new Dimension(10, 10));

        saveData.setIcon(new ImageIcon(imgDataSave));
        saveData.setBorderPainted(false);
        saveData.setFocusPainted(false);
        saveData.setContentAreaFilled(false);
        saveData.setPreferredSize(new Dimension(10, 10));
        saveData.setSize(new Dimension(10, 10));

        clearLog.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                areaComment.setText("");
                areaComment.append("");
            }
        });

        selectType.setLightWeightPopupEnabled(false);
        this.type_label.setText(Lang.get("type_label"));
        this.quote_label.setText(Lang.get("quote_label"));
        this.columns_label.setText(Lang.get("columns_label"));
        this.parameter_label.setText(Lang.get("parameter_label"));
        this.post_label.setText(Lang.get("post_label"));
        this.longAnalyze.setText(Lang.get("full_analyze_checkbox"));
        getDB.setText(Lang.get("db_button"));
        getTables.setText(Lang.get("table_button"));
        getColumns.setText(Lang.get("column_button"));
        getData.setText(Lang.get("data_button"));
        saveData.setText(Lang.get("save_button"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        urlForm = new javax.swing.JTextField();
        startButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        areaComment = new java.awt.TextArea();
        clearLog = new java.awt.Label();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        cookieInput = new javax.swing.JTextField();
        quote_label = new javax.swing.JLabel();
        selectQuote = new javax.swing.JComboBox<>();
        parameter_label = new javax.swing.JLabel();
        post_input = new javax.swing.JTextField();
        type_label = new javax.swing.JLabel();
        selectType = new javax.swing.JComboBox<>();
        columns_label = new javax.swing.JLabel();
        inputColumns = new javax.swing.JTextField();
        dbPanel = new javax.swing.JPanel();
        tables_list = new java.awt.List();
        db_list = new java.awt.List();
        jPanel1 = new javax.swing.JPanel();
        getDB = new javax.swing.JButton();
        getTables = new javax.swing.JButton();
        getData = new javax.swing.JButton();
        saveData = new javax.swing.JButton();
        getColumns = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        longAnalyze = new javax.swing.JCheckBox();
        post_label = new javax.swing.JLabel();
        paramInput = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bro Sql Injection Tool =)");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel1.setText("URL:");

        urlForm.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        urlForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                urlFormActionPerformed(evt);
            }
        });

        startButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        cancelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        areaComment.setEditable(false);
        areaComment.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        areaComment.setForeground(new java.awt.Color(51, 51, 51));

        clearLog.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clearLog.setForeground(new java.awt.Color(51, 0, 204));
        clearLog.setText("Clear Log");

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel2.setText("Cookie:");

        quote_label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        quote_label.setText("Кавычка:");

        selectQuote.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "autodetect", " ", "'", "\"" }));

        parameter_label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        parameter_label.setText("Параметр:");

        type_label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        type_label.setText("Тип:");

        selectType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MySql Unknown ver.", "MySql Error Based" }));
        selectType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectTypeActionPerformed(evt);
            }
        });

        columns_label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        columns_label.setText("Колонки:");

        inputColumns.setText("1-32");

        tables_list.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        tables_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tables_listActionPerformed(evt);
            }
        });

        db_list.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        db_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                db_listActionPerformed(evt);
            }
        });

        getDB.setText("БД");
        getDB.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        getDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getDBActionPerformed(evt);
            }
        });

        getTables.setText("Таблицы");
        getTables.setToolTipText("");
        getTables.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        getTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getTablesActionPerformed(evt);
            }
        });

        getData.setText("Данные");
        getData.setToolTipText("");
        getData.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        getData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getDataActionPerformed(evt);
            }
        });

        saveData.setText("Сохранить");
        saveData.setToolTipText("");
        saveData.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        saveData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDataActionPerformed(evt);
            }
        });

        getColumns.setText("Колонки");
        getColumns.setToolTipText("");
        getColumns.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        getColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getColumnsActionPerformed(evt);
            }
        });

        dataTable.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Table Data"
            }
        ));
        jScrollPane1.setViewportView(dataTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(getDB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getTables, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getColumns)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getData)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveData))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 701, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getDB)
                    .addComponent(getTables)
                    .addComponent(getData)
                    .addComponent(saveData)
                    .addComponent(getColumns))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dbPanelLayout = new javax.swing.GroupLayout(dbPanel);
        dbPanel.setLayout(dbPanelLayout);
        dbPanelLayout.setHorizontalGroup(
            dbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dbPanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(dbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(db_list, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tables_list, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        dbPanelLayout.setVerticalGroup(
            dbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dbPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dbPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dbPanelLayout.createSequentialGroup()
                        .addComponent(db_list, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tables_list, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        longAnalyze.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        longAnalyze.setText("Долго анализировать");

        post_label.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        post_label.setText("Пост:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(urlForm, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addGap(24, 24, 24))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(type_label))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cookieInput, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                                    .addComponent(selectType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(quote_label)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(selectQuote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(columns_label)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(inputColumns)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(parameter_label)
                                    .addComponent(post_label))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(paramInput, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                    .addComponent(post_input))
                                .addGap(18, 18, 18)
                                .addComponent(longAnalyze, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dbPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(areaComment, javax.swing.GroupLayout.PREFERRED_SIZE, 869, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(clearLog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cancelButton)
                            .addComponent(startButton)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(urlForm, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cookieInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(quote_label)
                        .addComponent(selectQuote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(parameter_label)
                        .addComponent(paramInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(longAnalyze)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(type_label)
                    .addComponent(selectType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(columns_label)
                    .addComponent(inputColumns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(post_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(post_label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dbPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(clearLog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(areaComment, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void urlFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_urlFormActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_urlFormActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        db_list.removeAll();
        tables_list.removeAll();
        db_finish_load = false;
        this.table_clicked = "";
        int indQuote = selectQuote.getSelectedIndex();
        String quote = indQuote > 0 ? selectQuote.getSelectedItem().toString() : null;
        String url = urlForm.getText();
        int type = selectType.getSelectedIndex();
        String[] columns = inputColumns.getText().trim().split("-");
        if (columns.length > 2) {
            JOptionPane.showMessageDialog(null, Lang.get("invalid_columns_value"));
            return;
        }

        int col1 = Helper.getInt(columns[0], -1);
        int col2 = columns.length > 1 ? Helper.getInt(columns[1], -1) : col1;

        if ((col1 < 0) || (col2 < 0)) {
            JOptionPane.showMessageDialog(null, Lang.get("invalid_columns_value"));
            return;
        }

        String param = paramInput.getText();

        websql = new WebSQL();
        String cookie = cookieInput.getText();
        if (!cookie.isEmpty()) {
            String[] cc = cookie.split("=");
            if (cc.length != 2) {
                JOptionPane.showMessageDialog(null, Lang.get("invalid_cookie_value"));
                return;
            }
            websql.cookie.name = cc[0].trim();
            if ((cc[1].trim().equals("%Inject_Here%")) && (param.isEmpty())) {
                JOptionPane.showMessageDialog(null, Lang.get("param_is_empty"));
                return;
            }

            websql.cookie.val = cc[1].trim().isEmpty() ? param : cc[1].trim();
        }

        if ((websql.cookie.val != null) && (websql.cookie.val.equals("%Inject_Here%"))) {
            websql.cookie.val = param;
        }

        String post = post_input.getText();
        if (!post.isEmpty()) {
            HashMap<String, String> post_map = Helper.parse_query_string(post);
            if (post_map.isEmpty()) {
                JOptionPane.showMessageDialog(null, Lang.get("invalid_post_value"));
                return;
            }

            if ((post.indexOf("%Inject_Here%") > 0) && (param.isEmpty())) {
                JOptionPane.showMessageDialog(null, Lang.get("param_is_empty"));
                return;
            }

            websql.post = post_map;
        }

        db_table.clear();
        db_table_fields.clear();

        Helper.log(Lang.get("start"));
        websql.url = url;
        websql.quote = quote;
        websql.col1 = col1;
        websql.col2 = col2;
        websql.param = param;
        websql.type = type;
        websql.start();

        cancelButton.setEnabled(true);
        startButton.setEnabled(false);
    }//GEN-LAST:event_startButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        Helper.log(Lang.get("start"));
        WebSQL.finish = true;
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void getDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getDBActionPerformed
        if (websql.isAlive()) {
            Helper.log(Lang.get("main_thread_running"));
            return;
        }
        if (this.db_finish_load) {
            Helper.log(Lang.get("db_list_ready"));
            return;
        }

        if (websql.sqlVal.columns == 0) {
            Helper.log(Lang.get("not_analyzed_yet"));
            return;
        }

        db_list.removeAll();
        db_list.add(websql.sqlVal.db);
        Helper.log(Lang.get("fetching_db_list"));
        cancelButton.setEnabled(true);
        startButton.setEnabled(false);

        websql = websql.refresh();
        websql.action = 1;
        websql.start();

        //Helper.log("Список баз данных успешно получен");

    }//GEN-LAST:event_getDBActionPerformed

    private void db_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_db_listActionPerformed
        String db = db_list.getSelectedItem();
        ArrayList<String> tables = db_table.get(db);
        if (tables == null) {
            tables = new ArrayList();
            db_table.put(db, tables);
        }
        tables_list.removeAll();
        for (int i = 0; i < tables.size(); i++) {
            String tbl = tables.get(i);
            tables_list.add(tbl);
            ArrayList<String> fields = db_table_fields.get(db + "__" + tbl);
            if (fields != null) {
                for (int j = 0; j < fields.size(); j++) {
                    String field = fields.get(j);
                    tables_list.add("  " + field);
                }
            }

        }

        this.table_clicked = "";
        //Helper.log(db_list.getSelectedItem());
    }//GEN-LAST:event_db_listActionPerformed

    private void getTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getTablesActionPerformed
        if (websql.isAlive()) {
            Helper.log(Lang.get("main_thread_running"));
            return;
        }
        String db = db_list.getSelectedItem();
        if (db == null) {
            Helper.log(Lang.get("db_not_choosed"));
            return;
        }
        ArrayList<String> tables = db_table.get(db);
        if ((tables != null) && (!tables.isEmpty())) {
            Helper.log(Lang.get("table_list_ready"));
            return;
        }

        if (websql.sqlVal.columns == 0) {
            Helper.log(Lang.get("not_analyzed_yet"));
            return;
        }

        Helper.log(Lang.get("fetching_table_list"));
        tables_list.removeAll();
        cancelButton.setEnabled(true);
        startButton.setEnabled(false);

        websql = websql.refresh();
        websql.action = 2;
        websql.db_select = db;
        websql.start();
    }//GEN-LAST:event_getTablesActionPerformed

    private void getColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getColumnsActionPerformed
        if (websql.isAlive()) {
            Helper.log(Lang.get("main_thread_running"));
            return;
        }
        String db = db_list.getSelectedItem();
        if (db == null) {
            Helper.log(Lang.get("db_not_choosed"));
            return;
        }
        String table = tables_list.getSelectedItem();
        ArrayList<String> columns = db_table_fields.get(db + "__" + table);
        if (columns != null) {
            Helper.log(Lang.get("table_columns_is_listed", table));
            return;
        }

        if (websql.sqlVal.columns == 0) {
            Helper.log(Lang.get("not_analyzed_yet"));
            return;
        }

        getFieldsIndex = tables_list.getSelectedIndex();
        for (int i = getFieldsIndex + 1; i < tables_list.getItemCount(); i++) {
            String elem = tables_list.getItem(i);
            if (elem.indexOf(" ") >= 0) {
                tables_list.remove(i);
                i--;
            } else {
                break;
            }
        }

        Helper.log(Lang.get("table_fetching_columns", table));
        cancelButton.setEnabled(true);
        startButton.setEnabled(false);

        websql = websql.refresh();
        websql.action = 3;
        websql.db_select = db;
        websql.table_select = table;
        websql.start();
    }//GEN-LAST:event_getColumnsActionPerformed

    private void tables_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tables_listActionPerformed
        String elem = tables_list.getSelectedItem();
        if (elem.trim().length() - elem.length() == 0) {
            return;
        }

        String column = elem.trim();
        int index = tables_list.getSelectedIndex();
        String tClicked = "";

        for (int i = index; i >= 0; i--) {
            elem = tables_list.getItem(i);
            if (elem.trim().length() - elem.length() != 0) {
                continue;
            } else {
                tClicked = elem;
                break;
            }
        }

        if (!tClicked.equals(table_clicked)) {
            try {
                while (Math.abs(3) > 1) {
                    dataTable.removeColumn(dataTable.getColumnModel().getColumn(0));
                }
            } catch (Exception e) {
            }
            this.table_clicked = tClicked;
        }

        DefaultTableModel dtm = (DefaultTableModel) dataTable.getModel();
        dtm.setRowCount(0);
        TableColumn tc = null;
        try {
            tc = dataTable.getColumn(column);
            dataTable.removeColumn(tc);
        } catch (Exception e) {
            tc = new TableColumn();
            tc.setHeaderValue(column.trim());
            dataTable.addColumn(tc);
        }
        /*
         if (tc != null) {
         dataTable.removeColumn(tc);
         } else {
         tc = new TableColumn();
         tc.setHeaderValue(column);
         dataTable.addColumn(tc);
         }*/
    }//GEN-LAST:event_tables_listActionPerformed

    private void getDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getDataActionPerformed
        if (websql.isAlive()) {
            Helper.log(Lang.get("main_thread_running"));
            return;
        }
        int cols = dataTable.getColumnCount();
        if (cols == 0) {
            Helper.log(Lang.get("column_not_choosed"));
            return;
        }

        if (this.table_clicked.isEmpty()) {
            Helper.log(Lang.get("column_choose"));
            return;
        }

        ArrayList<String> columns = new ArrayList();
        for (int i = 0; i < cols; i++) {
            columns.add(dataTable.getColumnModel().getColumn(i).getHeaderValue().toString().trim());
        }

        String table = this.table_clicked;
        String db = db_list.getSelectedItem();

        Helper.log(Lang.get("table_fetching_data", table));
        cancelButton.setEnabled(true);
        startButton.setEnabled(false);

        websql = websql.refresh();
        websql.action = 4;
        websql.db_select = db;
        websql.table_select = table;
        websql.table_select_columns = columns;
        websql.start();

    }//GEN-LAST:event_getDataActionPerformed

    private void saveDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDataActionPerformed
        if (dataTable.getModel().getRowCount() == 0) {
            Helper.log(Lang.get("empty_table"));
            return;
        }

        JFrame parentFrame = new JFrame();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Lang.get("save_path"));

        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();
            if (path.indexOf(".csv") == -1) {
                path += ".csv";
            }
            Helper.exportToCSV(path);
            JOptionPane.showMessageDialog(null, Lang.get("saved_ok"));
        }
    }//GEN-LAST:event_saveDataActionPerformed

    private void selectTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectTypeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BROWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BROWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BROWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BROWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BROWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static java.awt.TextArea areaComment;
    public static javax.swing.JButton cancelButton;
    public static java.awt.Label clearLog;
    private javax.swing.JLabel columns_label;
    public static javax.swing.JTextField cookieInput;
    public static javax.swing.JTable dataTable;
    public static javax.swing.JPanel dbPanel;
    public static java.awt.List db_list;
    public static javax.swing.JButton getColumns;
    public static javax.swing.JButton getDB;
    public static javax.swing.JButton getData;
    public static javax.swing.JButton getTables;
    public static javax.swing.JTextField inputColumns;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public static javax.swing.JCheckBox longAnalyze;
    public static javax.swing.JTextField paramInput;
    private javax.swing.JLabel parameter_label;
    public static javax.swing.JTextField post_input;
    private javax.swing.JLabel post_label;
    private javax.swing.JLabel quote_label;
    public static javax.swing.JButton saveData;
    private javax.swing.JComboBox<String> selectQuote;
    private javax.swing.JComboBox<String> selectType;
    public static javax.swing.JButton startButton;
    public static java.awt.List tables_list;
    private javax.swing.JLabel type_label;
    public static javax.swing.JTextField urlForm;
    // End of variables declaration//GEN-END:variables
}
