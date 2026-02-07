import apply.StaticQuackify;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuackifyUI {

    private static final String[] AUDIO_URLS = {
        "https://github.com/csvistool/1332_misc/raw/refs/heads/main/audio/song_1.wav",
        "https://github.com/csvistool/1332_misc/raw/refs/heads/main/audio/song_2.wav",
        "https://github.com/csvistool/1332_misc/raw/refs/heads/main/audio/song_3.wav",
        "https://github.com/csvistool/1332_misc/raw/refs/heads/main/audio/song_4.wav",
        "https://github.com/csvistool/1332_misc/raw/refs/heads/main/audio/song_5.wav",
        "https://github.com/csvistool/1332_misc/raw/refs/heads/main/audio/song_6.wav",
        "https://github.com/csvistool/1332_misc/raw/refs/heads/main/audio/song_7.wav"
    };

    private static final Map<String, Integer> songAudioMap = new HashMap<>();
    private static int nextAudioIndex = ThreadLocalRandom.current().nextInt(AUDIO_URLS.length);
    private static Clip currentClip = null;

    private static final Map<Integer, byte[]> audioCache = new HashMap<>();

    public static void main(String[] args) {
        preloadAudioCache();
        SwingUtilities.invokeLater(QuackifyUI::createAndShowGUI);
    }

    /**
     * Pre-loads all audio files into memory cache in background.
     */
    private static void preloadAudioCache() {
        new Thread(() -> {
            for (int i = 0; i < AUDIO_URLS.length; i++) {
                try {
                    URL url = new URL(AUDIO_URLS[i]);
                    byte[] audioData = url.openStream().readAllBytes();
                    synchronized (audioCache) {
                        audioCache.put(i, audioData);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to cache audio " + i + ": " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Gets the audio index for a song. Each unique song is assigned a persistent
     * audio clip index (0-6) that stays the same even if the playlist changes.
     */
    private static int getAudioIndexForSong(String song) {
        if (!songAudioMap.containsKey(song)) {
            songAudioMap.put(song, nextAudioIndex);
            nextAudioIndex = (nextAudioIndex + 1) % AUDIO_URLS.length;
        }
        return songAudioMap.get(song);
    }

    /**
     * Plays the audio clip associated with the given song in a loop.
     */
    private static void playAudioForSong(String song) {
        stopAudio();
        int audioIndex = getAudioIndexForSong(song);

        new Thread(() -> {
            try {
                byte[] audioData;
                synchronized (audioCache) {
                    audioData = audioCache.get(audioIndex);
                }

                AudioInputStream audioStream;
                if (audioData != null) {
                    // Use cached data (instant)
                    audioStream = AudioSystem.getAudioInputStream(
                        new java.io.ByteArrayInputStream(audioData));
                } else {
                    // Fallback: download if not cached yet
                    URL url = new URL(AUDIO_URLS[audioIndex]);
                    audioStream = AudioSystem.getAudioInputStream(url);
                }

                Clip newClip = AudioSystem.getClip();
                newClip.open(audioStream);
                
                // Synchronized block to safely swap clips
                synchronized (QuackifyUI.class) {
                    stopAudio(); // Stop again in case another clip started
                    currentClip = newClip;
                    currentClip.loop(Clip.LOOP_CONTINUOUSLY);
                    currentClip.start();
                }
            } catch (Exception e) {
                System.err.println("Could not play audio: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Stops the currently playing audio clip.
     */
    private static void stopAudio() {
        synchronized (QuackifyUI.class) {
            if (currentClip != null) {
                currentClip.stop();
                currentClip.close();
                currentClip = null;
            }
        }
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JFrame frame = new JFrame("Quackify - CS 1332 Music Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        try {
            java.net.URL iconURL = new java.net.URL("https://csvistool.com/favicon.png");
            ImageIcon icon = new ImageIcon(iconURL);
            frame.setIconImage(icon.getImage());
        } catch (Exception ignored) {
        }


        DefaultListModel<String> listModel = new DefaultListModel<>();
        StaticQuackify quackify = Main.getQuackifyInstance();
        JList<String> playlistView = new JList<>(listModel);
        JScrollPane listScroll = new JScrollPane(playlistView);

        JLabel currentLabel = new JLabel("Now Playing: <none>");
        currentLabel.setFont(currentLabel.getFont().deriveFont(Font.BOLD, 16f));

        JLabel infoLabel = new JLabel(" ");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.PLAIN, 12f));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 8, 8));
        infoLabel.setForeground(Color.DARK_GRAY);


        JLabel playLight = new JLabel();
        playLight.setOpaque(true);
        playLight.setBackground(Color.RED);
        playLight.setPreferredSize(new Dimension(12, 12));
        JLabel playStatusLabel = new JLabel("Music Stopped");
        playStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));


        ConfettiPanel confetti = new ConfettiPanel();
        confetti.setOpaque(false);
        confetti.setPreferredSize(new Dimension(800, 200));


        JPanel controls = new JPanel();
        controls.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JTextField songField = new JTextField();
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; controls.add(songField, c);

        JButton addBtn = new JButton("Add Song");
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1; controls.add(addBtn, c);

        JButton removeBtn = new JButton("Remove Song");
        c.gridx = 1; c.gridy = 1; controls.add(removeBtn, c);

        final int[] currentIdx = new int[] { -1 };

        JButton playBtn = new JButton("Play");
        c.gridx = 0; c.gridy = 2; controls.add(playBtn, c);

        JButton stopBtn = new JButton("Stop");
        c.gridx = 1; c.gridy = 2; controls.add(stopBtn, c);

        JButton nextBtn = new JButton("Next");
        c.gridx = 0; c.gridy = 3; controls.add(nextBtn, c);

        JButton randomSongBtn = new JButton("randomSong");
        c.gridx = 1; c.gridy = 3; controls.add(randomSongBtn, c);

        JButton undoBtn = new JButton("Undo");
        c.gridx = 0; c.gridy = 4; controls.add(undoBtn, c);

        JButton redoBtn = new JButton("Redo");
        c.gridx = 1; c.gridy = 4; controls.add(redoBtn, c);

        JButton reverseBtn = new JButton("Reverse Playlist");
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2; controls.add(reverseBtn, c);
        c.gridwidth = 1;


        Dimension btnSize = new Dimension(120, 30);
        for (Component comp : new Component[]{addBtn, removeBtn, playBtn, stopBtn, nextBtn, randomSongBtn, undoBtn, redoBtn, reverseBtn}) {
            if (comp instanceof JButton) {
                ((JButton) comp).setPreferredSize(btnSize);
            }
        }


        try {
            String cur = quackify.currentSong();
            currentIdx[0] = listModel.indexOf(cur);
        } catch (Exception ignore) {
            currentIdx[0] = -1;
        }
        playlistView.repaint();

        controls.setBorder(BorderFactory.createTitledBorder("Controls"));
        controls.setBackground(new Color(0,0,0,0));
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setPreferredSize(new Dimension(260, 0));
        leftWrapper.add(controls, BorderLayout.NORTH);


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        topPanel.add(currentLabel, BorderLayout.NORTH);
        topPanel.add(infoLabel, BorderLayout.SOUTH);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        statusPanel.setOpaque(false);
        statusPanel.add(playLight);
        statusPanel.add(playStatusLabel);
        topPanel.add(statusPanel, BorderLayout.EAST);

        frame.add(leftWrapper, BorderLayout.WEST);
        frame.add(listScroll, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);

        frame.add(confetti, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);


        listScroll.setBorder(BorderFactory.createTitledBorder("Playlist"));


        Runnable refresh = () -> {
            listModel.clear();
            int n = quackify.size();
            int i = 0;
            for (String s : quackify.getPlaylist()) {
                if (i++ >= n) break;
                listModel.addElement(s);
            }
        };

        addBtn.addActionListener(e -> {
            String song = songField.getText().trim();
            try {
                quackify.addSong(song);
                listModel.insertElementAt(song, 0);

                songField.setText("");
                songField.requestFocusInWindow();
                try {
                    if (quackify.isPalindrome()) {
                        confetti.splash();
                        showInfo(infoLabel, "Added: " + song + " - Palindrome playlist!", false);
                    } else {
                        showInfo(infoLabel, "Added: " + song, false);
                    }
                } catch (Exception ex) {
                    showInfo(infoLabel, "Added: " + song, false);
                }
            } catch (IllegalStateException | IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showInfo(infoLabel, ex.getMessage(), true);
            }
        });


        songField.addActionListener(e -> addBtn.doClick());

        removeBtn.addActionListener(e -> {
            String song = songField.getText().trim();
            try {
                quackify.removeSong(song);
                listModel.removeElement(song);
                try {
                    if (quackify.isPalindrome()) {
                        confetti.splash();
                        showInfo(infoLabel, "Removed: " + song + " - Palindrome playlist!", false);
                    } else {
                        showInfo(infoLabel, "Removed: " + song, false);
                    }
                } catch (Exception ex) {
                    showInfo(infoLabel, "Removed: " + song, false);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showInfo(infoLabel, ex.getMessage(), true);
            }
        });

        playBtn.addActionListener(e -> {
            try {
                quackify.play();
                try {
                    String cur = quackify.currentSong();
                    currentLabel.setText("Now Playing: " + cur);
                    currentIdx[0] = findNextIndex(listModel, cur, 0);
                    playAudioForSong(cur); // Play audio for current song
                    setPlayIndicator(playLight, playStatusLabel, true);
//                    currentIdx[0] = findNextIndex(listModel, cur, currentIdx[0] + 1);
                } catch (Exception ex) {
                    currentLabel.setText("Now Playing: <none>");
                    currentIdx[0] = -1;
                }
                playlistView.repaint();
                showInfo(infoLabel, "Playback started", false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showInfo(infoLabel, ex.getMessage(), true);
            }
        });

        stopBtn.addActionListener(e -> {
            try {
                quackify.stop();
                stopAudio(); // Stop audio playback
                setPlayIndicator(playLight, playStatusLabel, false);
                currentLabel.setText("Now Playing: <none>");

                try {
                    String cur = quackify.currentSong();
                    currentIdx[0] = findNextIndex(listModel, cur, 0);
                } catch (Exception ex) {
                    currentIdx[0] = -1;
                }

                currentIdx[0] = -1;
                playlistView.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showInfo(infoLabel, ex.getMessage(), true);
            }
        });

        nextBtn.addActionListener(e -> {
            try {
                String next = quackify.nextSong();
                currentLabel.setText("Now Playing: " + next);
                currentIdx[0] = findNextIndex(listModel, next, currentIdx[0] + 1);
                playAudioForSong(next); // Play audio for next song
                playlistView.repaint();
                showInfo(infoLabel, "Advanced to next song", false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showInfo(infoLabel, ex.getMessage(), true);
            }
        });

        randomSongBtn.addActionListener(e -> {
            try {
                String song = quackify.randomSong();
                currentLabel.setText("Now Playing: " + song);

                currentIdx[0] = findNextIndex(listModel, song, currentIdx[0] + 1);
                playAudioForSong(song); // Play audio for randomSongd song
                playlistView.repaint();
                showInfo(infoLabel, "randomSongd", false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showInfo(infoLabel, ex.getMessage(), true);
            }
        });

        undoBtn.addActionListener(e -> {
            try {
                quackify.undo();
                try {
                    refresh.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    showInfo(infoLabel, ex.getMessage(), true);
                }
                try {
                    String cur = quackify.currentSong();
                    currentIdx[0] = findNextIndex(listModel, cur, 0);
                } catch (Exception ex) {
                    currentIdx[0] = -1;
                }
                playlistView.repaint();
                showInfo(infoLabel, "Undo", false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showInfo(infoLabel, ex.getMessage(), true);
            }
        });

        redoBtn.addActionListener(e -> {
            try {
                quackify.redo();
                try {
                    refresh.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    showInfo(infoLabel, ex.getMessage(), true);
                }
                try {
                    String cur = quackify.currentSong();
                    currentIdx[0] = findNextIndex(listModel, cur, 0);
                } catch (Exception ex) {
                    currentIdx[0] = -1;
                }
                playlistView.repaint();
                showInfo(infoLabel, "Redo", false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showInfo(infoLabel, ex.getMessage(), true);
            }
        });

        reverseBtn.addActionListener(e -> {
            try {
                quackify.reverse();
                refresh.run();
                try {
                    String cur = quackify.currentSong();
                    currentIdx[0] = findNextIndex(listModel, cur, 0);
                } catch (Exception ex) {
                    currentIdx[0] = -1;
                }
                playlistView.repaint();
                try {
                    if (quackify.isPalindrome()) {
                        confetti.splash();
                        showInfo(infoLabel, "Reversed - Palindrome playlist!", false);
                    } else {
                        showInfo(infoLabel, "Reversed", false);
                    }
                } catch (Exception ex) {
                    showInfo(infoLabel, "Reversed", false);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                showInfo(infoLabel, ex.getMessage(), true);
            }
        });


        playlistView.setFont(playlistView.getFont().deriveFont(14f));
        playlistView.setFixedCellHeight(28);

        playlistView.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {

            }

            @Override
            public void addSelectionInterval(int index0, int index1) {

            }
        });


        playlistView.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, false, false);
                if (currentIdx[0] == index) {
                    lbl.setBackground(new Color(200, 255, 200));
                    lbl.setOpaque(true);
                } else {
                    lbl.setOpaque(false);
                }
                lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                return lbl;
            }
        });
        setPlayIndicator(playLight, playStatusLabel, false);

        frame.setVisible(true);
    }


    private static void showInfo(JLabel infoLabel, String message, boolean isError) {
        SwingUtilities.invokeLater(() -> {
            infoLabel.setText(message);
            infoLabel.setForeground(isError ? Color.RED.darker() : Color.DARK_GRAY);
        });

        Timer t = new Timer(4000, e -> SwingUtilities.invokeLater(() -> infoLabel.setText(" ")));
        t.setRepeats(false);
        t.start();
    }

    private static void setPlayIndicator(JLabel light, JLabel label, boolean playing) {
        SwingUtilities.invokeLater(() -> {
            light.setBackground(playing ? Color.GREEN.brighter() : Color.RED);
            label.setText(playing ? "Music Playing" : "Music Stopped");
        });
    }

    /**
     * Find the next index of value in listModel starting at fromIndex (inclusive). Returns -1 if not found.
     * This helper advances through the list so duplicate song names will move the highlight forward.
     */
    private static int findNextIndex(DefaultListModel<String> model, String value, int fromIndex) {
        if (value == null || model == null || model.size() == 0) return -1;
        int n = model.size();
        int start = Math.max(0, fromIndex % n);
        for (int i = 0; i < n; i++) {
            int idx = (start + i) % n;
            if (value.equals(model.get(idx))) return idx;
        }
        return -1;
    }


    static class ConfettiPanel extends JPanel {
        private volatile boolean active = false;

        public void splash() {
            if (active) return;
            active = true;
            Thread t = new Thread(() -> {
                long end = System.currentTimeMillis() + 1000;
                while (System.currentTimeMillis() < end) {
                    repaint();
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                }
                active = false;
                repaint();
            });
            t.setDaemon(true);
            t.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!active) return;
            Graphics2D g2 = (Graphics2D) g.create();
            for (int i = 0; i < 80; i++) {
                g2.setColor(new Color(ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom.current().nextInt(256), 200));
                int x = ThreadLocalRandom.current().nextInt(Math.max(1, getWidth()));
                int y = ThreadLocalRandom.current().nextInt(Math.max(1, getHeight()));
                int size = ThreadLocalRandom.current().nextInt(4, 24);
                g2.fillOval(x, y, size, size);
            }
            
            g2.setColor(Color.MAGENTA);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String message = "Playlist is a palindrome";
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(message);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() - 20;
            g2.drawString(message, x, y);
            
            g2.dispose();
        }
    }
}
