package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ServerGUI {

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private Map<SocketChannel, String> subscriptions;

    private JFrame frame;
    private JTextArea logArea;
    private JTextField topicField;
    private JTextField messageField;

    public ServerGUI() {
        subscriptions = new HashMap<>();

        frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        frame.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());
        frame.add(controlPanel, BorderLayout.SOUTH);

        JPanel topicPanel = new JPanel();
        JLabel topicLabel = new JLabel("Topic:");
        topicField = new JTextField(10);
        topicPanel.add(topicLabel);
        topicPanel.add(topicField);
        controlPanel.add(topicPanel, BorderLayout.WEST);

        JButton subscribeButton = new JButton("Subscribe");
        subscribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subscribe(topicField.getText());
                topicField.setText("");
            }
        });
        controlPanel.add(subscribeButton, BorderLayout.CENTER);

        JButton unsubscribeButton = new JButton("Unsubscribe");
        unsubscribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unsubscribe();
            }
        });
        controlPanel.add(unsubscribeButton, BorderLayout.EAST);

        JPanel messagePanel = new JPanel();
        JLabel messageLabel = new JLabel("Message:");
        messageField = new JTextField(20);
        messagePanel.add(messageLabel);
        messagePanel.add(messageField);
        controlPanel.add(messagePanel, BorderLayout.NORTH);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                messageField.setText("");
            }
        });
        controlPanel.add(sendButton, BorderLayout.SOUTH);

        frame.setVisible(true);
        log("Server started.");

        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(12345));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (key.isAcceptable()) {
                    acceptClient();
                } else if (key.isReadable()) {
                    readMessage(key);
                }
            }
        }
    }

    private void acceptClient() throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        log("New client connected: " + clientChannel.getRemoteAddress());
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            key.cancel();
            clientChannel.close();
            log("Client disconnected: " + clientChannel.getRemoteAddress());
            return;
        }

        buffer.flip();
        CharBuffer charBuffer = Charset.defaultCharset().decode(buffer);
        String message = charBuffer.toString();
        log("Received from client " + clientChannel.getRemoteAddress() + ": " + message);

        if (message.startsWith("SUBSCRIBE:")) {
            String topic = message.substring(10).trim();
            subscriptions.put(clientChannel, topic);
            log("Client subscribed to topic: " + topic);
        } else if (message.equals("UNSUBSCRIBE")) {
            unsubscribe(clientChannel);
            log("Client unsubscribed.");
        } else {
            forwardMessage(message);
        }
    }

    private void forwardMessage(String message) throws IOException {
        for (Map.Entry<SocketChannel, String> entry : subscriptions.entrySet()) {
            SocketChannel clientChannel = entry.getKey();
            String topic = entry.getValue();
            if (topic.equals("") || message.contains(topic)) {
                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                clientChannel.write(buffer);
            }
        }
    }

    private void subscribe(String topic) {
        try {
            for (SocketChannel clientChannel : subscriptions.keySet()) {
                ByteBuffer buffer = ByteBuffer.wrap(("SUBSCRIBE: " + topic).getBytes());
                clientChannel.write(buffer);
            }
            log("Subscribed to topic: " + topic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unsubscribe() {
        try {
            for (SocketChannel clientChannel : subscriptions.keySet()) {
                ByteBuffer buffer = ByteBuffer.wrap("UNSUBSCRIBE".getBytes());
                clientChannel.write(buffer);
            }
            subscriptions.clear();
            log("Unsubscribed from all topics.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unsubscribe(SocketChannel clientChannel) {
        subscriptions.remove(clientChannel);
    }

    private void sendMessage(String message) {
        try {
            for (SocketChannel clientChannel : subscriptions.keySet()) {
                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                clientChannel.write(buffer);
            }
            log("Message sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}

