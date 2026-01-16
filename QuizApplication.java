import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ApurbaBanglaQuize extends JFrame {

    // ✅ Database Connection Info
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quizdb";
    private static final String USER = "apurba31";
    private static final String PASS = "apurbaict";

    // ✅ Quiz Data
    private final String[] questions = {
            "প্রশ্ন ১: 2 + 2 কত?",
            "প্রশ্ন ২: 5 - 3 কত?",
            "প্রশ্ন ৩: 20 * 10 কত?",
            "প্রশ্ন ৪: 10 / 2 কত?",
            "প্রশ্ন ৫: 7 + 5 কত?",
             "প্রশ্ন ৬: বাংলাদেশের সবচেয়ে বড় শহর কোনটি?"
    };
    private final String[] answers = {"4", "2", "200 ", "5", "12","ঢাকা"};

    private int[] scores = new int[5];
    private int currentQuestion = 0;
    private int totalScore = 0;
    private String name, email;

    // ✅ UI Components
    private CardLayout cardLayout;
    private JPanel mainPanel, homePanel, quizPanel, scorePanel;
    private JLabel questionLabel, scoreLabel;
    private JTextField answerField;
    private JButton nextButton;

    public ApurbaBanglaQuize() {
        setTitle("🎓 বাংলা কুইজ অ্যাপ");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🌸 বাংলা ফন্ট সেট করা
        setBanglaFont();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 🏠 হোম প্যানেল
        homePanel = new JPanel(new GridLayout(4, 1, 15, 15));
        homePanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel titleLabel = new JLabel("🎓 বাংলা কুইজ অ্যাপ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Noto Sans Bengali", Font.BOLD, 22));
        homePanel.add(titleLabel);

        JButton startButton = new JButton("কুইজ শুরু করুন");
        JButton viewScoresButton = new JButton("স্কোর দেখুন");
        JButton exitButton = new JButton("প্রস্থান করুন");

        homePanel.add(startButton);
        homePanel.add(viewScoresButton);
        homePanel.add(exitButton);

        // 🧩 কুইজ প্যানেল
        quizPanel = new JPanel(new BorderLayout(15, 15));
        quizPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Noto Sans Bengali", Font.PLAIN, 18));

        answerField = new JTextField();
        nextButton = new JButton("পরবর্তী প্রশ্ন ▶");

        quizPanel.add(questionLabel, BorderLayout.NORTH);
        quizPanel.add(answerField, BorderLayout.CENTER);
        quizPanel.add(nextButton, BorderLayout.SOUTH);

        // 🏁 স্কোর প্যানেল
        scorePanel = new JPanel(new BorderLayout(15, 15));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        scoreLabel = new JLabel("আপনার স্কোর:", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Noto Sans Bengali", Font.BOLD, 20));

        JButton backButton = new JButton("🏠 হোমে ফিরুন");

        scorePanel.add(scoreLabel, BorderLayout.CENTER);
        scorePanel.add(backButton, BorderLayout.SOUTH);

        // Add panels to main panel
        mainPanel.add(homePanel, "Home");
        mainPanel.add(quizPanel, "Quiz");
        mainPanel.add(scorePanel, "Score");

        add(mainPanel);

        // 🔘 Button Actions
        startButton.addActionListener(e -> startQuiz());
        nextButton.addActionListener(e -> nextQuestion());
        viewScoresButton.addActionListener(e -> viewScores());
        exitButton.addActionListener(e -> System.exit(0));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        cardLayout.show(mainPanel, "Home");
    }

    // 🌸 বাংলা ফন্ট কনফিগারেশন
    private void setBanglaFont() {
        try {
            Font banglaFont = new Font("Noto Sans Bengali", Font.PLAIN, 16);
            UIManager.put("Label.font", banglaFont);
            UIManager.put("Button.font", banglaFont);
            UIManager.put("TextField.font", banglaFont);
            UIManager.put("OptionPane.messageFont", banglaFont);
            UIManager.put("OptionPane.buttonFont", banglaFont);
        } catch (Exception e) {
            // যদি Noto Sans Bengali না থাকে, fallback দাও
            UIManager.put("Label.font", new Font("Vrinda", Font.PLAIN, 16));
            UIManager.put("Button.font", new Font("Vrinda", Font.PLAIN, 16));
        }
    }

    // ▶️ কুইজ শুরু করা
    private void startQuiz() {
        name = JOptionPane.showInputDialog(this, "নাম লিখুন:");
        if (name == null || name.isEmpty()) return;

        email = JOptionPane.showInputDialog(this, "ইমেইল লিখুন:");
        if (email == null || email.isEmpty()) return;

        currentQuestion = 0;
        totalScore = 0;
        scores = new int[5];

        showQuestion();
        cardLayout.show(mainPanel, "Quiz");
    }

    // 📄 প্রশ্ন দেখানো
    private void showQuestion() {
        if (currentQuestion < questions.length) {
            questionLabel.setText(questions[currentQuestion]);
            answerField.setText("");
        }
    }

    // ⏭️ পরবর্তী প্রশ্ন
    private void nextQuestion() {
        String userAnswer = answerField.getText().trim();
        if (userAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "অনুগ্রহ করে উত্তর লিখুন।");
            return;
        }

        if (userAnswer.equals(answers[currentQuestion])) {
            JOptionPane.showMessageDialog(this, "✅ সঠিক উত্তর!");
            scores[currentQuestion] = 1;
            totalScore++;
        } else {
            JOptionPane.showMessageDialog(this, "❌ ভুল উত্তর! সঠিক উত্তর ছিল: " + answers[currentQuestion]);
            scores[currentQuestion] = 0;
        }

        currentQuestion++;

        if (currentQuestion < questions.length) {
            showQuestion();
        } else {
            saveScore();
            scoreLabel.setText("আপনার মোট স্কোর: " + totalScore + " / 5");
            cardLayout.show(mainPanel, "Score");
        }
    }

    // 💾 স্কোর ডাটাবেজে সংরক্ষণ
    private void saveScore() {
        String sql = "INSERT INTO scores (name, email, q1, q2, q3, q4, q5, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            for (int i = 0; i < 5; i++) {
                pstmt.setInt(3 + i, scores[i]);
            }
            pstmt.setInt(8, totalScore);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "🎯 স্কোর সফলভাবে ডাটাবেসে সংরক্ষিত হয়েছে।");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ ডাটাবেস ত্রুটি: " + e.getMessage());
        }
    }

    // 🧾 স্কোর দেখা
    private void viewScores() {
        StringBuilder results = new StringBuilder();
        String sql = "SELECT * FROM scores";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                results.append("নাম: ").append(rs.getString("name")).append("\n");
                results.append("ইমেইল: ").append(rs.getString("email")).append("\n");
                results.append("Q1: ").append(rs.getInt("q1"))
                        .append(" | Q2: ").append(rs.getInt("q2"))
                        .append(" | Q3: ").append(rs.getInt("q3"))
                        .append(" | Q4: ").append(rs.getInt("q4"))
                        .append(" | Q5: ").append(rs.getInt("q5")).append("\n");
                results.append("মোট স্কোর: ").append(rs.getInt("total")).append("\n");
                results.append("-----------------------------\n");
            }

            JTextArea textArea = new JTextArea(results.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "🧾 সংরক্ষিত স্কোর তালিকা", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ ডাটাবেস ত্রুটি: " + e.getMessage());
        }
    }

    // 🏁 Main Method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApurbaBanglaQuize().setVisible(true));
    }
}
