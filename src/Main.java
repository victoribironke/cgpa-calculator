import ui.CGPACalculatorFrame;
import javax.swing.*;

/**
 * Entry point for the CGPA Calculator application.
 */
public class Main {
    public static void main(String[] args) {
        // Set Nimbus Look and Feel for a modern look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fall back to system L&F
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            CGPACalculatorFrame frame = new CGPACalculatorFrame();
            frame.setVisible(true);
        });
    }
}
