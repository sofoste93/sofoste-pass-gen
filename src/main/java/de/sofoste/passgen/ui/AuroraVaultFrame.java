package de.sofoste.passgen.ui;

import de.sofoste.passgen.PasswordGenerator;
import de.sofoste.passgen.PasswordPolicy;
import de.sofoste.passgen.PasswordStrength;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/** Desktop shell. The security logic lives in separate classes and is UI-independent. */
public final class AuroraVaultFrame extends JFrame {
    private static final Color MINT = new Color(91, 240, 196);
    private static final Color AMBER = new Color(255, 190, 92);
    private static final Color NIGHT = new Color(5, 18, 27);
    private final PasswordGenerator generator = new PasswordGenerator();
    private final Preferences preferences = Preferences.userNodeForPackage(AuroraVaultFrame.class);

    private String language = preferences.get("language", "en");
    private String theme = preferences.get("theme", "dark");
    private JSlider lengthSlider;
    private JLabel lengthValue;
    private JTextField passwordField;
    private JCheckBox uppercase;
    private JCheckBox lowercase;
    private JCheckBox numbers;
    private JCheckBox symbols;
    private JCheckBox ambiguous;
    private JLabel strengthLabel;
    private JProgressBar strengthBar;

    public AuroraVaultFrame() {
        super("Aurora Vault");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setSize(1080, 700);
        setLocationRelativeTo(null);
        rebuild();
    }

    private void rebuild() {
        boolean light = theme.equals("light");
        Color text = light ? new Color(18, 40, 48) : new Color(235, 250, 246);
        Color muted = light ? new Color(76, 102, 108) : new Color(145, 176, 176);
        Font body = new Font("SansSerif", Font.PLAIN, 14);
        UIManager.put("Label.font", body);
        UIManager.put("CheckBox.font", body);
        UIManager.put("Button.font", body.deriveFont(Font.BOLD));
        UIManager.put("Label.foreground", text);
        UIManager.put("CheckBox.foreground", text);
        UIManager.put("CheckBox.background", new Color(0, 0, 0, 0));

        GradientRoot root = new GradientRoot(light);
        root.setLayout(new BorderLayout(0, 28));
        root.setBorder(new EmptyBorder(28, 38, 34, 38));
        root.add(buildHeader(text, muted), BorderLayout.NORTH);

        JPanel content = transparent(new GridLayout(1, 2, 28, 0));
        content.add(buildMissionPanel(text, muted));
        content.add(buildGeneratorPanel(text, muted, light));
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
        SwingUtilities.updateComponentTreeUI(this);
        generatePassword();
    }

    private JPanel buildHeader(Color text, Color muted) {
        JPanel header = transparent(new BorderLayout());
        JPanel identity = transparent(new FlowLayout(FlowLayout.LEFT, 12, 0));
        identity.add(new BrandMark());
        JPanel names = transparent(new GridLayout(2, 1));
        JLabel name = new JLabel("AURORA VAULT");
        name.setForeground(text);
        name.setFont(name.getFont().deriveFont(Font.BOLD, 17f));
        JLabel edition = new JLabel(t("edition"));
        edition.setForeground(muted);
        edition.setFont(edition.getFont().deriveFont(11f));
        names.add(name);
        names.add(edition);
        identity.add(names);
        header.add(identity, BorderLayout.WEST);

        JButton settings = secondaryButton("⚙  " + t("settings"), text);
        settings.addActionListener(event -> showSettings());
        header.add(settings, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMissionPanel(Color text, Color muted) {
        JPanel panel = transparent(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;

        JLabel eyebrow = new JLabel(t("eyebrow"));
        eyebrow.setForeground(MINT);
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 11f));
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, 20, 0);
        panel.add(eyebrow, constraints);

        JLabel title = new JLabel(html(t("title"), 390));
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 42f));
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 14, 0);
        panel.add(title, constraints);

        JLabel intro = new JLabel(html(t("intro"), 390));
        intro.setForeground(muted);
        intro.setFont(intro.getFont().deriveFont(15f));
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 28, 0);
        panel.add(intro, constraints);

        JPanel facts = transparent(new GridLayout(1, 3, 10, 0));
        facts.add(fact("LOCAL", t("local"), text, muted));
        facts.add(fact("CSPRNG", t("secure"), text, muted));
        facts.add(fact("0 BYTE", t("tracking"), text, muted));
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 0, 20);
        panel.add(facts, constraints);
        return panel;
    }

    private JPanel buildGeneratorPanel(Color text, Color muted, boolean light) {
        RoundedPanel card = new RoundedPanel(light ? new Color(255, 255, 255, 224) : new Color(11, 33, 42, 225));
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(28, 28, 28, 28));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JLabel cardLabel = new JLabel(t("password"));
        cardLabel.setForeground(muted);
        cardLabel.setFont(cardLabel.getFont().deriveFont(Font.BOLD, 11f));
        c.gridy = 0;
        c.insets = new Insets(0, 0, 8, 0);
        card.add(cardLabel, c);

        JPanel passwordRow = transparent(new BorderLayout(8, 0));
        passwordField = new JTextField();
        passwordField.setEditable(false);
        passwordField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
        passwordField.setForeground(text);
        passwordField.setBackground(light ? new Color(237, 247, 244) : new Color(4, 21, 28));
        passwordField.setBorder(new EmptyBorder(14, 14, 14, 14));
        passwordRow.add(passwordField, BorderLayout.CENTER);
        JButton copy = secondaryButton(t("copy"), text);
        copy.addActionListener(event -> copyPassword(copy));
        passwordRow.add(copy, BorderLayout.EAST);
        c.gridy++;
        c.insets = new Insets(0, 0, 20, 0);
        card.add(passwordRow, c);

        JPanel lengthHeader = transparent(new BorderLayout());
        JLabel lengthLabel = new JLabel(t("length"));
        lengthLabel.setForeground(text);
        lengthValue = new JLabel("20");
        lengthValue.setForeground(MINT);
        lengthValue.setFont(lengthValue.getFont().deriveFont(Font.BOLD));
        lengthHeader.add(lengthLabel, BorderLayout.WEST);
        lengthHeader.add(lengthValue, BorderLayout.EAST);
        c.gridy++;
        c.insets = new Insets(0, 0, 4, 0);
        card.add(lengthHeader, c);

        lengthSlider = new JSlider(8, 64, 20);
        lengthSlider.setOpaque(false);
        lengthSlider.setForeground(MINT);
        lengthSlider.addChangeListener(event -> {
            lengthValue.setText(String.valueOf(lengthSlider.getValue()));
            if (!lengthSlider.getValueIsAdjusting()) generatePassword();
        });
        c.gridy++;
        c.insets = new Insets(0, 0, 16, 0);
        card.add(lengthSlider, c);

        JPanel options = transparent(new GridLayout(3, 2, 10, 8));
        uppercase = option(t("uppercase"), true);
        lowercase = option(t("lowercase"), true);
        numbers = option(t("numbers"), true);
        symbols = option(t("symbols"), true);
        ambiguous = option(t("ambiguous"), true);
        options.add(uppercase); options.add(lowercase); options.add(numbers);
        options.add(symbols); options.add(ambiguous);
        c.gridy++;
        c.insets = new Insets(0, 0, 20, 0);
        card.add(options, c);

        JPanel strengthHeader = transparent(new BorderLayout());
        JLabel strengthTitle = new JLabel(t("strength"));
        strengthTitle.setForeground(muted);
        strengthLabel = new JLabel();
        strengthLabel.setForeground(MINT);
        strengthLabel.setFont(strengthLabel.getFont().deriveFont(Font.BOLD));
        strengthHeader.add(strengthTitle, BorderLayout.WEST);
        strengthHeader.add(strengthLabel, BorderLayout.EAST);
        c.gridy++;
        c.insets = new Insets(0, 0, 6, 0);
        card.add(strengthHeader, c);

        strengthBar = new JProgressBar(0, 128);
        strengthBar.setForeground(MINT);
        strengthBar.setBackground(light ? new Color(218, 235, 230) : new Color(26, 54, 60));
        strengthBar.setBorderPainted(false);
        c.gridy++;
        c.insets = new Insets(0, 0, 22, 0);
        card.add(strengthBar, c);

        JButton generate = new JButton(t("generate") + "  ↻");
        generate.setForeground(NIGHT);
        generate.setBackground(MINT);
        generate.setFocusPainted(false);
        generate.setBorder(new EmptyBorder(14, 18, 14, 18));
        generate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        generate.addActionListener(event -> generatePassword());
        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(generate, c);
        return card;
    }

    private JCheckBox option(String label, boolean selected) {
        JCheckBox box = new JCheckBox(label, selected);
        box.setOpaque(false);
        box.setFocusPainted(false);
        box.addActionListener(event -> {
            if (!uppercase.isSelected() && !lowercase.isSelected() && !numbers.isSelected() && !symbols.isSelected()) {
                ((JCheckBox) event.getSource()).setSelected(true);
                Toolkit.getDefaultToolkit().beep();
            }
            generatePassword();
        });
        return box;
    }

    private void generatePassword() {
        if (passwordField == null) return;
        PasswordPolicy policy = currentPolicy();
        passwordField.setText(generator.generate(policy));
        passwordField.setCaretPosition(0);
        PasswordStrength.Result strength = PasswordStrength.estimate(policy);
        strengthBar.setValue(Math.min(128, (int) strength.bits()));
        strengthLabel.setText(t(strength.level().name().toLowerCase()) + " · " + Math.round(strength.bits()) + " bits");
    }

    private PasswordPolicy currentPolicy() {
        return new PasswordPolicy(lengthSlider.getValue(), uppercase.isSelected(), lowercase.isSelected(),
                numbers.isSelected(), symbols.isSelected(), ambiguous.isSelected());
    }

    private void copyPassword(JButton button) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(passwordField.getText()), null);
        String previous = button.getText();
        button.setText(t("copied"));
        Timer timer = new Timer(1400, event -> button.setText(previous));
        timer.setRepeats(false);
        timer.start();
    }

    private void showSettings() {
        JComboBox<String> languageChoice = new JComboBox<>(new String[]{"English", "Deutsch"});
        languageChoice.setSelectedIndex(language.equals("de") ? 1 : 0);
        JComboBox<String> themeChoice = new JComboBox<>(new String[]{t("dark"), t("light")});
        themeChoice.setSelectedIndex(theme.equals("light") ? 1 : 0);
        JPanel settings = new JPanel(new GridLayout(0, 1, 6, 8));
        settings.add(new JLabel(t("language")));
        settings.add(languageChoice);
        settings.add(new JLabel(t("appearance")));
        settings.add(themeChoice);
        int result = JOptionPane.showConfirmDialog(this, settings, t("settings"), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            language = languageChoice.getSelectedIndex() == 1 ? "de" : "en";
            theme = themeChoice.getSelectedIndex() == 1 ? "light" : "dark";
            preferences.put("language", language);
            preferences.put("theme", theme);
            rebuild();
        }
    }

    private JPanel fact(String value, String label, Color text, Color muted) {
        RoundedPanel panel = new RoundedPanel(new Color(91, 240, 196, 18));
        panel.setLayout(new GridLayout(2, 1));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(text);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 13f));
        JLabel info = new JLabel(label);
        info.setForeground(muted);
        info.setFont(info.getFont().deriveFont(10f));
        panel.add(valueLabel);
        panel.add(info);
        return panel;
    }

    private JButton secondaryButton(String label, Color text) {
        JButton button = new JButton(label);
        button.setForeground(text);
        button.setBackground(new Color(91, 240, 196, 20));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 14, 11, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static JPanel transparent(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private static String html(String text, int width) {
        return "<html><body style='width:" + width + "px'>" + text + "</body></html>";
    }

    private String t(String key) {
        return TRANSLATIONS.getOrDefault(language, TRANSLATIONS.get("en")).getOrDefault(key, key);
    }

    private static final Map<String, Map<String, String>> TRANSLATIONS = translations();

    private static Map<String, Map<String, String>> translations() {
        Map<String, Map<String, String>> all = new LinkedHashMap<>();
        all.put("en", Map.ofEntries(
                Map.entry("edition", "PRIVATE PASSWORD STUDIO · 2.0"), Map.entry("settings", "Settings"),
                Map.entry("eyebrow", "ZERO-KNOWLEDGE GENERATOR"), Map.entry("title", "Strong passwords. Quietly generated."),
                Map.entry("intro", "Build unique credentials on your device. Nothing is sent, saved or observed."),
                Map.entry("local", "ON DEVICE"), Map.entry("secure", "SECURE RANDOM"), Map.entry("tracking", "TRACKING"),
                Map.entry("password", "YOUR NEW PASSWORD"), Map.entry("copy", "Copy"), Map.entry("copied", "Copied ✓"),
                Map.entry("length", "Length"), Map.entry("uppercase", "Uppercase"), Map.entry("lowercase", "Lowercase"),
                Map.entry("numbers", "Numbers"), Map.entry("symbols", "Symbols"), Map.entry("ambiguous", "Avoid ambiguous"),
                Map.entry("strength", "ESTIMATED STRENGTH"), Map.entry("generate", "Generate secure password"),
                Map.entry("weak", "Weak"), Map.entry("fair", "Fair"), Map.entry("strong", "Strong"), Map.entry("exceptional", "Exceptional"),
                Map.entry("language", "Language"), Map.entry("appearance", "Appearance"), Map.entry("dark", "Night"), Map.entry("light", "Daylight")));
        all.put("de", Map.ofEntries(
                Map.entry("edition", "PRIVATES PASSWORT-STUDIO · 2.0"), Map.entry("settings", "Einstellungen"),
                Map.entry("eyebrow", "LOKALER SICHERHEITSGENERATOR"), Map.entry("title", "Starke Passwörter. Diskret erzeugt."),
                Map.entry("intro", "Erstelle einzigartige Zugangsdaten auf deinem Gerät. Nichts wird gesendet oder gespeichert."),
                Map.entry("local", "AUF DEM GERÄT"), Map.entry("secure", "SICHERER ZUFALL"), Map.entry("tracking", "TRACKING"),
                Map.entry("password", "DEIN NEUES PASSWORT"), Map.entry("copy", "Kopieren"), Map.entry("copied", "Kopiert ✓"),
                Map.entry("length", "Länge"), Map.entry("uppercase", "Großbuchstaben"), Map.entry("lowercase", "Kleinbuchstaben"),
                Map.entry("numbers", "Zahlen"), Map.entry("symbols", "Sonderzeichen"), Map.entry("ambiguous", "Ähnliche vermeiden"),
                Map.entry("strength", "GESCHÄTZTE STÄRKE"), Map.entry("generate", "Sicheres Passwort erzeugen"),
                Map.entry("weak", "Schwach"), Map.entry("fair", "Solide"), Map.entry("strong", "Stark"), Map.entry("exceptional", "Außergewöhnlich"),
                Map.entry("language", "Sprache"), Map.entry("appearance", "Darstellung"), Map.entry("dark", "Nacht"), Map.entry("light", "Tageslicht")));
        return all;
    }

    private static final class GradientRoot extends JPanel {
        private final boolean light;
        private GradientRoot(boolean light) { this.light = light; setOpaque(true); }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            Color top = light ? new Color(235, 247, 242) : NIGHT;
            Color bottom = light ? new Color(209, 231, 228) : new Color(8, 31, 40);
            g.setPaint(new GradientPaint(0, 0, top, getWidth(), getHeight(), bottom));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(91, 240, 196, light ? 38 : 24));
            g.fillOval(-120, -180, 560, 430);
            g.setColor(new Color(255, 190, 92, light ? 35 : 18));
            g.fillOval(getWidth() - 360, getHeight() - 300, 520, 440);
            g.dispose();
        }
    }

    private static final class RoundedPanel extends JPanel {
        private final Color fill;
        private RoundedPanel(Color fill) { this.fill = fill; setOpaque(false); }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class BrandMark extends JComponent {
        private BrandMark() { setPreferredSize(new Dimension(42, 42)); }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(91, 240, 196, 45));
            g.fillOval(1, 1, 40, 40);
            g.setStroke(new BasicStroke(2f));
            g.setColor(MINT);
            g.drawArc(8, 10, 26, 20, 12, 310);
            g.setColor(AMBER);
            g.fillOval(18, 17, 7, 7);
            g.dispose();
        }
    }
}
