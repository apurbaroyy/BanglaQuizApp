import java.sql.*;
import java.util.Scanner;

public class QuizApplication {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/quizdb";
    private static final String USER = "apurba31";
    private static final String PASS = "apurbaict";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== 🎓 বাংলা কুইজ অ্যাপ ===");
            System.out.println("১. কুইজ খেলুন");
            System.out.println("২. স্কোর দেখুন");
            System.out.println("৩. প্রস্থান করুন");
            System.out.print("আপনার পছন্দ (১-৩): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "১":
                case "1":
                    playQuiz(scanner);
                    break;
                case "২":
                case "2":
                    viewScores();
                    break;
                case "৩":
                case "3":
                    System.out.println("ধন্যবাদ! আবার দেখা হবে।");
                    scanner.close();
                    return;
                default:
                    System.out.println("❌ ভুল ইনপুট, আবার চেষ্টা করুন।");
            }
        }
    }

    private static void playQuiz(Scanner scanner) {
        System.out.println("\nলগইন করুন:");
        System.out.print("নাম: ");
        String name = scanner.nextLine();
        System.out.print("ইমেইল: ");
        String email = scanner.nextLine();

        String[] questions = {
                "প্রশ্ন ১: 2 + 2 কত?",
                "প্রশ্ন ২: 5 - 3 কত?",
                "প্রশ্ন ৩: 3 * 3 কত?",
                "প্রশ্ন ৪: 10 / 2 কত?",
                "প্রশ্ন ৫: 7 + 5 কত?"
        };

        String[] answers = {"4", "2", "9", "5", "12"};

        int[] scores = new int[5];
        int totalScore = 0;

        for (int i = 0; i < questions.length; i++) {
            System.out.println(questions[i]);
            System.out.print("আপনার উত্তর: ");
            String userAnswer = scanner.nextLine();

            if (userAnswer.equals(answers[i])) {
                System.out.println("✅ সঠিক উত্তর!");
                scores[i] = 1;
                totalScore++;
            } else {
                System.out.println("❌ ভুল উত্তর! সঠিক উত্তর ছিল: " + answers[i]);
                scores[i] = 0;
            }
            System.out.println();
        }

        System.out.println("আপনার মোট স্কোর: " + totalScore + " / 5");
        saveScore(name, email, scores, totalScore);
    }

    private static void saveScore(String name, String email, int[] scores, int total) {
        String sql = "INSERT INTO scores (name, email, q1, q2, q3, q4, q5, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            for (int i = 0; i < 5; i++) {
                pstmt.setInt(3 + i, scores[i]);
            }
            pstmt.setInt(8, total);
            pstmt.executeUpdate();

            System.out.println("🎯 স্কোর সফলভাবে ডাটাবেসে সংরক্ষিত হয়েছে।");

        } catch (SQLException e) {
            System.out.println("⚠️ ডাটাবেস ত্রুটি: " + e.getMessage());
        }
    }

    private static void viewScores() {
        String sql = "SELECT * FROM scores";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== 🧾 সংরক্ষিত স্কোর তালিকা ===");
            while (rs.next()) {
                System.out.println("নাম: " + rs.getString("name"));
                System.out.println("ইমেইল: " + rs.getString("email"));
                System.out.println(
                        "Q1: " + rs.getInt("q1") +
                        " | Q2: " + rs.getInt("q2") +
                        " | Q3: " + rs.getInt("q3") +
                        " | Q4: " + rs.getInt("q4") +
                        " | Q5: " + rs.getInt("q5")
                );
                System.out.println("মোট স্কোর: " + rs.getInt("total"));
                System.out.println("----------------------------");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ ডাটাবেস ত্রুটি: " + e.getMessage());
        }
    }
}
