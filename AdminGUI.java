package zad1;

import javax.swing.*;
import java.awt.*;
import java.nio.channels.SocketChannel;
import java.util.List;

public class AdminGUI {
    public AdminGUI(SocketChannel socketChannel){
        JFrame frame = new JFrame("Admin");


        JPanel jPanel1 = new JPanel(new FlowLayout());
        frame.add(jPanel1);

        JComboBox<JLabel> comboBox = new JComboBox();
        ClientGUI clientGUI = new ClientGUI(socketChannel);
        List<JLabel> listaTematow= clientGUI.getTematy();
        for (JLabel temat : listaTematow) {
            System.out.print(temat.getText());
            comboBox.addItem(temat);
        }

        jPanel1.add(comboBox);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setVisible(true);
    }

}
