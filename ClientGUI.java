package zad1;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI {

    private List<JLabel> listaTematow;

    public ClientGUI(SocketChannel socketChannel) {
        JFrame jframe = new JFrame();
        jframe.setTitle("Klient");

        listaTematow = new ArrayList<>();

        JPanel jpanel = new JPanel();
        jpanel.setLayout(new GridBagLayout());
        jframe.add(jpanel);

        // GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Marginesy wokół komponentów
        gbc.fill = GridBagConstraints.HORIZONTAL; // Rozciąganie komponentów na szerokość

        // przycisk dodaj
        JButton jbutton = new JButton("dodaj");
        gbc.gridx = 0; // kolumna 0
        gbc.gridy = 0; // wiersz 0
        jpanel.add(jbutton, gbc);

        // przycisk zrezygnuj
        JButton jbutton1 = new JButton("zrezygnuj");
        gbc.gridx = 1; // kolumna 1
        gbc.gridy = 0; // wiersz 0
        jpanel.add(jbutton1, gbc);

        // pole tekstowe
        JTextField jtextfield = new JTextField();
        gbc.gridx = 0; // kolumna 0
        gbc.gridy = 1; // wiersz 1
        gbc.gridwidth = 2; // rozciąganie na 2 kolumny
        jpanel.add(jtextfield, gbc);

        //panel z tematami
        JPanel topicsPanel = new JPanel();
        topicsPanel.setLayout(new BoxLayout(topicsPanel, BoxLayout.Y_AXIS));

        Font titleFont = new Font("Arial", Font.BOLD, 18);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Twoje tematy");
        titledBorder.setTitleFont(titleFont);
        topicsPanel.setBorder(titledBorder);

//        // Dodawanie przykładowych tematów
//        topicsPanel.add(new JLabel("pilka"));
//        topicsPanel.add(new JLabel("koszykowka"));
//        topicsPanel.add(new JLabel("siatkowka"));

        // Dodawanie topicsPanel do głównego panelu
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH; // Rozciąganie panelu w obu kierunkach
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        jpanel.add(topicsPanel, gbc);

        //artykuł
        JTextArea articleTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(articleTextArea);
        gbc.gridy = 3;
        gbc.weighty = 3.0; // Większa waga dla miejsca na artykuł
        jpanel.add(scrollPane, gbc);


        //implementacja przycisku dodaj
        jbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String temat = jtextfield.getText();
                if (!temat.isEmpty()) {
                    JLabel topicLabel = new JLabel(temat);
                    listaTematow.add(topicLabel);
                    topicsPanel.add(topicLabel);
                    topicsPanel.revalidate();
                    topicsPanel.repaint();
                    jtextfield.setText(""); // Wyczyść pole tekstowe po dodaniu
                }
            }
        });


        // Implementacja przycisku zrezygnuj
        jbutton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String temat = jtextfield.getText();
                if (!temat.isEmpty()) {
                    JLabel labelToRemove = null;
                    for (JLabel label : listaTematow) {
                        if (label.getText().equals(temat)) {
                            labelToRemove = label;
                            break;
                        }
                    }
                    if (labelToRemove != null) {
                        topicsPanel.remove(labelToRemove); // Usunięcie etykiety z panelu
                        listaTematow.remove(labelToRemove); // Usunięcie etykiety z listy
                        topicsPanel.revalidate(); // Przeliczenie układu komponentów
                        topicsPanel.repaint(); // Odświeżenie panelu
                        jtextfield.setText(""); // Wyczyść pole tekstowe po usunięciu
                    } else {
                        JOptionPane.showMessageDialog(jframe, "Temat nie istnieje");
                    }
                }
            }
        });



        jframe.setSize(400, 600);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setVisible(true);
    }

    public List<JLabel> getTematy(){
        return listaTematow;
    }

}
