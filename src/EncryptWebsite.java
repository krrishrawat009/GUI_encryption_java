import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.security.Key;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EncryptWebsite extends JFrame {

    private static final String CHAR_SET = " " + "~!@#$%^&*()-_=+[{]}|;:'\",<.>/?"
            + "0123456789"
            + "abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final List<Character> CHARS = new ArrayList<>();
    private static final List<Character> KEY = new ArrayList<>();
    private static final String ALGORITHM = "AES"; // AES Algorithm for file encryption

    static {
        for (char c : CHAR_SET.toCharArray()) {
            CHARS.add(c);
            KEY.add(c);
        }
        Collections.shuffle(KEY);
    }

    // UI Components
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JLabel charCountLabel;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton clearButton;
    private JButton resetKeyButton;
    private JButton viewLogsButton;
    private JButton uploadButton;
    private JButton decryptFileButton;
    private JCheckBox confidentialCheckBox;
    private File uploadedFile;

    private JPanel loginPanel;
    private JPanel encryptionPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private JPanel mainPanel; // Main panel to hold navigation and other panels
    private CardLayout cardLayout; // CardLayout for switching panels

    private SecretKey secretKey; // Secret key for AES encryption

    public EncryptWebsite() {
        setTitle("Encrypt | Secure Messaging for Defense Personnel");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            // Generate AES key
            secretKey = generateSecretKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Background setup
        JLabel background = new JLabel(new ImageIcon("C:\\Users\\Asus\\Desktop\\defence.jpg"));
        background.setLayout(new BorderLayout());

        // Navigation Bar
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setOpaque(false);
        JButton homeButton = new JButton("Home");
        JButton aboutButton = new JButton("About");
        JButton contactButton = new JButton("Contact");
        navPanel.add(homeButton);
        navPanel.add(aboutButton);
        navPanel.add(contactButton);

        contactButton.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "Email: ht8791@srmist.edu.in\nPhone: +918000962722",
                "Contact Information", JOptionPane.INFORMATION_MESSAGE));

        aboutButton.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "EncryptWebsite is a secure tool that allows defense personnel to encrypt and decrypt messages.",
                "About EncryptWebsite", JOptionPane.INFORMATION_MESSAGE));

        // Main Panel with CardLayout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.setOpaque(false);

        // Login and encryption panels
        loginPanel = createLoginPanel();
        encryptionPanel = createEncryptionPanel();
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(encryptionPanel, "Encryption");

        // Add components to background
        background.add(navPanel, BorderLayout.NORTH);
        background.add(mainPanel, BorderLayout.CENTER);
        setContentPane(background);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE, 2), "Login"));
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.WHITE);
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (authenticateUser(username, password)) {
                cardLayout.show(mainPanel, "Encryption");
            } else {
                JOptionPane.showMessageDialog(null, "Access Denied", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createEncryptionPanel() {
        encryptionPanel = new JPanel(new GridBagLayout());
        encryptionPanel.setBackground(new Color(255, 255, 255, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Input Text Area
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputTextArea = new JTextArea(5, 40);
        inputTextArea.setBorder(BorderFactory.createTitledBorder("Input Text"));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        encryptionPanel.add(new JScrollPane(inputTextArea), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        charCountLabel = new JLabel("Character Count: 0");
        encryptionPanel.add(charCountLabel, gbc);

        inputTextArea.addCaretListener(e -> charCountLabel.setText("Character Count: " + inputTextArea.getText().length()));

        // Confidentiality Checkbox
        gbc.gridx = 1;
        gbc.gridy = 1;
        confidentialCheckBox = new JCheckBox("Mark as Top Confidential");
        encryptionPanel.add(confidentialCheckBox, gbc);

        // Output Text Area
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        outputTextArea = new JTextArea(5, 40);
        outputTextArea.setEditable(false);
        outputTextArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GREEN, 2), "Message Output"));
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        encryptionPanel.add(new JScrollPane(outputTextArea), gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(5, 5, 5, 5);
        btnGbc.gridx = 0;
        btnGbc.gridy = 0;

        encryptButton = new JButton("Encrypt");
        encryptButton.setBackground(Color.GREEN);
        buttonPanel.add(encryptButton, btnGbc);

        btnGbc.gridx++;
        decryptButton = new JButton("Decrypt");
        decryptButton.setBackground(Color.ORANGE);
        buttonPanel.add(decryptButton, btnGbc);

        btnGbc.gridx++;
        clearButton = new JButton("Clear");
        clearButton.setBackground(Color.RED);
        buttonPanel.add(clearButton, btnGbc);

        btnGbc.gridx = 0;
        btnGbc.gridy++;
        resetKeyButton = new JButton("Reset Key");
        resetKeyButton.setBackground(Color.PINK);
        buttonPanel.add(resetKeyButton, btnGbc);

        btnGbc.gridx++;
        viewLogsButton = new JButton("View Logs");
        viewLogsButton.setBackground(Color.CYAN);
        buttonPanel.add(viewLogsButton, btnGbc);

        // Upload and Decrypt File Buttons
        btnGbc.gridx++;
        uploadButton = new JButton("Upload File");
        uploadButton.setBackground(Color.YELLOW);
        buttonPanel.add(uploadButton, btnGbc);

        btnGbc.gridx++;
        decryptFileButton = new JButton("Decrypt File");
        decryptFileButton.setBackground(Color.LIGHT_GRAY);
        buttonPanel.add(decryptFileButton, btnGbc);

        // Add button panel to encryptionPanel
        gbc.gridy = 3;
        encryptionPanel.add(buttonPanel, gbc);

        // Action Listeners
        encryptButton.addActionListener(e -> {
            String plainText = inputTextArea.getText();
            String cipherText = encrypt(plainText);
            outputTextArea.setText(cipherText);
            logMessage("Text Encrypted", "Text Encryption");
        });

        decryptButton.addActionListener(e -> {
            String cipherText = inputTextArea.getText();
            String plainText = decrypt(cipherText);
            outputTextArea.setText(plainText);
            logMessage("Text Decrypted", "Text Decryption");
        });

        clearButton.addActionListener(e -> {
            inputTextArea.setText("");
            outputTextArea.setText("");
        });

        resetKeyButton.addActionListener(e -> {
            Collections.shuffle(KEY);
            JOptionPane.showMessageDialog(null, "Key has been reset and reshuffled.");
        });

        viewLogsButton.addActionListener(e -> viewLogs());

        // Upload Button Action
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            // Set file filter for supported types
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files (JPEG)", "jpg", "jpeg"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PPT Files", "ppt", "pptx"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Word Files", "doc", "docx"));

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                uploadedFile = fileChooser.getSelectedFile();
                String fileName = uploadedFile.getName();

                // Show file name in output text area or log it
                outputTextArea.setText("Selected File: " + fileName);

                // Encrypt file
                encryptFile(uploadedFile);

                logMessage("File Uploaded: " + fileName, "File Upload");
            }
        });

        decryptFileButton.addActionListener(e -> {
            if (uploadedFile != null) {
                // Decrypt the uploaded file
                File decryptedFile = decryptFile(uploadedFile);
                if (decryptedFile != null) {
                    JOptionPane.showMessageDialog(null, "File decrypted successfully! Opening it now...");
                    openFile(decryptedFile); // Open decrypted file
                }
            } else {
                JOptionPane.showMessageDialog(null, "No file selected for decryption.");
            }
        });

        return encryptionPanel;
    }

    private boolean authenticateUser(String username, String password) {
        // Simple check for demonstration (replace with actual authentication logic)
        return username.equals("user") && password.equals("password");
    }

    private void logMessage(String message, String messageType) {
        String confidentiality = confidentialCheckBox.isSelected() ? "Top Confidential" : "Non-Confidential";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/encryptdb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO logs (message, messageType, confidentiality) VALUES (?, ?, ?)")) {

            stmt.setString(1, message);
            stmt.setString(2, messageType);
            stmt.setString(3, confidentiality);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // AES File Encryption
    private void encryptFile(File file) {
        try {
            File encryptedFile = new File(file.getParent(), "encrypted_" + file.getName());
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(encryptedFile);
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            byte[] block = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(block)) != -1) {
                cos.write(block, 0, bytesRead);
            }

            cos.close();
            fis.close();

            JOptionPane.showMessageDialog(null, "File encrypted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during file encryption: " + e.getMessage());
        }
    }

    // AES File Decryption
    private File decryptFile(File encryptedFile) {
        try {
            File decryptedFile = new File(encryptedFile.getParent(), "decrypted_" + encryptedFile.getName());
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            FileInputStream fis = new FileInputStream(encryptedFile);
            FileOutputStream fos = new FileOutputStream(decryptedFile);
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            byte[] block = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(block)) != -1) {
                cos.write(block, 0, bytesRead);
            }

            cos.close();
            fis.close();

            return decryptedFile;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during file decryption: " + e.getMessage());
        }
        return null;
    }

    private String encrypt(String plainText) {
        StringBuilder cipherText = new StringBuilder();
        for (char c : plainText.toCharArray()) {
            int index = CHARS.indexOf(c);
            if (index != -1) {
                cipherText.append(KEY.get(index));
            } else {
                cipherText.append(c);
            }
        }
        return cipherText.toString();
    }

    private String decrypt(String cipherText) {
        StringBuilder plainText = new StringBuilder();
        for (char c : cipherText.toCharArray()) {
            int index = KEY.indexOf(c);
            if (index != -1) {
                plainText.append(CHARS.get(index));
            } else {
                plainText.append(c);
            }
        }
        return plainText.toString();
    }

    private void viewLogs() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/encryptdb", "root", "password");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM logs")) {

            StringBuilder logs = new StringBuilder();
            while (rs.next()) {
                logs.append("Message: ").append(rs.getString("message"))
                        .append("\nType: ").append(rs.getString("messageType"))
                        .append("\nConfidentiality: ").append(rs.getString("confidentiality"))
                        .append("\n\n");
            }

            JOptionPane.showMessageDialog(null, new JTextArea(logs.toString()), "Logs", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(null, "Opening files is not supported on this system.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while opening file: " + e.getMessage());
        }
    }

    private SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // AES key size of 128 bits
        return keyGen.generateKey();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EncryptWebsite app = new EncryptWebsite();
            app.setVisible(true);
        });
    }
}