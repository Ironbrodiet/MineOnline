package gg.codie.mineonline;

import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.utils.FileUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MineOnlineLauncherFrame extends JFrame {
    private JButton launchGameButton;
    private JButton launchAppletButton;
    private JTextField usernameTextField;
    private JButton loginButton;
    private JTextField mppassTextField;
    private JButton getMppassButton;
    private JTextField jarPathTextField;
    private JButton browseButton;
    private JPanel formPanel;
    private JLabel username;
    private JCheckBox useLocalProxyCheckBox;
    private JTextField apiDomainTextField;
    private JTextField serverPortTextField;
    private JTextField sessionIdTextField;
    private JTextField serverIPTextField;
    private JPasswordField passwordField;
    private JCheckBox hasPaidCheckBox;
    private JTextField appletClassNameTextField;
    private JTextField gameClassNameTextField;
    private JCheckBox connectToServerCheckBox;
    private JButton openJoinURLButton;
    private JLabel needAccountLabel;
    private JTextField baseURLTextField;
    private JFileChooser fileChooser = new JFileChooser();

    int proxyPort;

    public MineOnlineLauncherFrame(){
        super("MineOnline Launcher (Prototype)");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(formPanel);
        setSize(600, 575);
        setResizable(false);

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return FileUtils.getExtension(f) != null && FileUtils.getExtension(f).equals("jar");
            }

            @Override
            public String getDescription() {
                return "Minecraft game file (.jar)";
            }
        });

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal =  fileChooser.showOpenDialog(MineOnlineLauncherFrame.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    jarPathTextField.setText(file.getAbsolutePath());
                }
            }
        });

        jarPathTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void findMainClasses() {
                try {
                    appletClassNameTextField.setText("");
                    gameClassNameTextField.setText("");

                    if(jarPathTextField.getText().isEmpty())
                        return;

                    Properties.properties.put("jarFilePath", jarPathTextField.getText());
                    Properties.saveProperties();

                    JarFile jarFile = new JarFile(jarPathTextField.getText());
                    Enumeration allEntries = jarFile.entries();
                    while (allEntries.hasMoreElements()) {
                        JarEntry entry = (JarEntry) allEntries.nextElement();
                        String classCanonicalName = entry.getName();

                        if(!classCanonicalName.contains(".class"))
                            continue;

                        classCanonicalName = classCanonicalName.replace("/", ".");
                        classCanonicalName = classCanonicalName.replace(".class", "");

                        String className = classCanonicalName;
                        if(classCanonicalName.lastIndexOf(".") > -1) {
                            className = classCanonicalName.substring(classCanonicalName.lastIndexOf(".") + 1);
                        }

                        if(className.equals("minecraftApplet")) {
                            appletClassNameTextField.setText(classCanonicalName);
                        } else if(className.equals("MiencraftLauncher")) {
                            gameClassNameTextField.setText(classCanonicalName);
                        } else if(className.equals("Minecraft")) {
                            gameClassNameTextField.setText(classCanonicalName);
                        }
                    }
                } catch (IOException ex) {

                }
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                findMainClasses();
            }

            public void removeUpdate(DocumentEvent e) {
                findMainClasses();
            }
            public void insertUpdate(DocumentEvent e) {
                findMainClasses();
            }

        });

        launchAppletButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<String> args = new ArrayList();

                    String premiumArgument = "-demo";
                    if(hasPaidCheckBox.isSelected()) {
                        premiumArgument = "-paid";
                    }
                    args.add(premiumArgument);

                    if(!usernameTextField.getText().isEmpty())
                    {
                        args.add("-login");
                        args.add(usernameTextField.getText());

                        if(!sessionIdTextField.getText().isEmpty())
                        {
                            args.add(sessionIdTextField.getText());
                        }
                    }

                    if(connectToServerCheckBox.isSelected() && !serverIPTextField.getText().isEmpty())
                    {
                        args.add("-server");
                        args.add(serverIPTextField.getText());
                        if(!serverPortTextField.getText().isEmpty())
                        {
                            args.add(serverPortTextField.getText());

                            if(!mppassTextField.getText().isEmpty())
                            {
                                args.add(mppassTextField.getText());
                            }
                        }
                    }

                    MineOnlineLauncher.launch(jarPathTextField.getText(), ELaunchType.Applet, appletClassNameTextField.getText(),
                            args.toArray(new String[0]),
                            proxyPort);

                    setVisible(false);
                    while(MineOnlineLauncher.gameProcess.isAlive()) {

                    }
                    System.exit(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to launch applet.");
                }
            }
        });

        launchGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<String> args = new ArrayList();

                    if(!usernameTextField.getText().isEmpty())
                    {
                        args.add(usernameTextField.getText());

                        if(!sessionIdTextField.getText().isEmpty())
                        {
                            args.add(sessionIdTextField.getText());
                        }
                    }

                    MineOnlineLauncher.launch(jarPathTextField.getText(), ELaunchType.Game, gameClassNameTextField.getText(),
                            args.toArray(new String[0]),
                            proxyPort);

                    setVisible(false);
                    while(MineOnlineLauncher.gameProcess.isAlive()) {

                    }
                    System.exit(0);
                    setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to launch game.");
                }
            }
        });

        useLocalProxyCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(useLocalProxyCheckBox.isSelected()) {
                    Properties.properties.put("useLocalProxy", true);
                } else {
                    Properties.properties.put("useLocalProxy", false);
                }
                Properties.saveProperties();
                useLocalProxyUpdated();
            }
        });

        hasPaidCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(hasPaidCheckBox.isSelected()) {
                    Properties.properties.put("isPremium", true);
                } else {
                    Properties.properties.put("isPremium", false);
                }
                Properties.saveProperties();
            }
        });


        // Todo: Make this one function for all props.

        apiDomainTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void onChange() {
                if(useLocalProxyCheckBox.isSelected())
                    startProxy();
                Properties.properties.put("apiDomainName", apiDomainTextField.getText());
                Properties.saveProperties();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }
        });


        serverIPTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void onChange() {
                Properties.properties.put("serverIP", serverIPTextField.getText());
                Properties.saveProperties();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }
        });

        serverPortTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void onChange() {
                Properties.properties.put("serverPort", serverPortTextField.getText());
                Properties.saveProperties();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }
        });

        DocumentListener loginButtonEnableListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent evt) {
                if(passwordField.getPassword().length > 0 && !usernameTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty() || !useLocalProxyCheckBox.isSelected())) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if(passwordField.getPassword().length > 0 && !usernameTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty() || !useLocalProxyCheckBox.isSelected())) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }
            public void insertUpdate(DocumentEvent e) {
                if(passwordField.getPassword().length > 0 && !usernameTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty() || !useLocalProxyCheckBox.isSelected())) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }
        };

        passwordField.getDocument().addDocumentListener(loginButtonEnableListener);
        apiDomainTextField.getDocument().addDocumentListener(loginButtonEnableListener);
        usernameTextField.getDocument().addDocumentListener(loginButtonEnableListener);

        appletClassNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent evt) {
                if(!jarPathTextField.getText().isEmpty() && !appletClassNameTextField.getText().isEmpty())
                    launchAppletButton.setEnabled(true);
                else
                    launchAppletButton.setEnabled(false);
            }

            public void removeUpdate(DocumentEvent e) {
                if(!jarPathTextField.getText().isEmpty() && !appletClassNameTextField.getText().isEmpty())
                    launchAppletButton.setEnabled(true);
                else
                    launchAppletButton.setEnabled(false);
            }
            public void insertUpdate(DocumentEvent e) {
                if(!jarPathTextField.getText().isEmpty() && !appletClassNameTextField.getText().isEmpty())
                    launchAppletButton.setEnabled(true);
                else
                    launchAppletButton.setEnabled(false);
            }
        });

        gameClassNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent evt) {
                if(!jarPathTextField.getText().isEmpty() && !gameClassNameTextField.getText().isEmpty())
                    launchGameButton.setEnabled(true);
                else
                    launchGameButton.setEnabled(false);
            }

            public void removeUpdate(DocumentEvent e) {
                if(!jarPathTextField.getText().isEmpty() && !gameClassNameTextField.getText().isEmpty())
                    launchGameButton.setEnabled(true);
                else
                    launchGameButton.setEnabled(false);
            }
            public void insertUpdate(DocumentEvent e) {
                if(!jarPathTextField.getText().isEmpty() && !gameClassNameTextField.getText().isEmpty())
                    launchGameButton.setEnabled(true);
                else
                    launchGameButton.setEnabled(false);
            }
        });

        DocumentListener mpPassButtonDocumentListner = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent evt) {
                if(!serverIPTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty() || !useLocalProxyCheckBox.isSelected()))
                    getMppassButton.setEnabled(true);
                else
                    getMppassButton.setEnabled(false);
            }

            public void removeUpdate(DocumentEvent e) {
                if(!serverIPTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty() || !useLocalProxyCheckBox.isSelected()))
                    getMppassButton.setEnabled(true);
                else
                    getMppassButton.setEnabled(false);
            }
            public void insertUpdate(DocumentEvent e) {
                if(!serverIPTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty() || !useLocalProxyCheckBox.isSelected()))
                    getMppassButton.setEnabled(true);
                else
                    getMppassButton.setEnabled(false);
            }
        };

        sessionIdTextField.getDocument().addDocumentListener(mpPassButtonDocumentListner);
        serverIPTextField.getDocument().addDocumentListener(mpPassButtonDocumentListner);
        apiDomainTextField.getDocument().addDocumentListener(mpPassButtonDocumentListner);

        needAccountLabel.setForeground(Color.BLUE.darker());
        needAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        needAccountLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://" + apiDomainTextField.getText() + "/register.jsp"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // the mouse has entered the label
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // the mouse has exited the label
            }
        });


        hasPaidCheckBox.setSelected(Properties.properties.getBoolean("isPremium"));
        jarPathTextField.setText(Properties.properties.getString("jarFilePath"));
        apiDomainTextField.setText(Properties.properties.getString("apiDomainName"));
        useLocalProxyCheckBox.setSelected(Properties.properties.getBoolean("useLocalProxy"));
        useLocalProxyUpdated();
        serverIPTextField.setText(Properties.properties.getString("serverIP"));
        serverPortTextField.setText(Properties.properties.getString("serverPort"));
        joinServerUpdated();

        if(!usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty() && !MinecraftAPI.checkSession(usernameTextField.getText(), sessionIdTextField.getText())){
            sessionIdTextField.setText("");
        }
    }

    private void joinServerUpdated() {
        serverIPTextField.setEnabled(connectToServerCheckBox.isSelected());
        serverIPTextField.setEditable(connectToServerCheckBox.isSelected());
        serverPortTextField.setEnabled(connectToServerCheckBox.isSelected());
        serverPortTextField.setEditable(connectToServerCheckBox.isSelected());
        mppassTextField.setEnabled(connectToServerCheckBox.isSelected());
        mppassTextField.setEditable(connectToServerCheckBox.isSelected());
        openJoinURLButton.setEnabled(connectToServerCheckBox.isSelected());
        getMppassButton.setEnabled(connectToServerCheckBox.isSelected() && !serverIPTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty() || !useLocalProxyCheckBox.isSelected()));
    }

    private void useLocalProxyUpdated() {
        if(Properties.properties.getBoolean("useLocalProxy"))
            startProxy();
        else
            killProxy();
        apiDomainTextField.setEnabled(useLocalProxyCheckBox.isSelected());
        apiDomainTextField.setEditable(useLocalProxyCheckBox.isSelected());
    }

    private void startProxy() {
        try {
            killProxy();

            System.getProperties().put("http.proxyHost", "0.0.0.0");
            System.getProperties().put("http.proxyPort", Proxy.getProxyPort());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to start proxy.");
            useLocalProxyCheckBox.setSelected(false);
        }
    }

    private void killProxy() {
        Proxy.stopProxy();
    }

    public static void main(String[] args) {
        Properties.loadProperties();

        JFrame frame = new MineOnlineLauncherFrame();
        frame.setVisible(true);
    }
}
